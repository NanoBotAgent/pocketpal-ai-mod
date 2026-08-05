package com.nebulaai.pocketpal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.modules.core.PermissionListener
import java.io.File
import java.util.*

class PocketPalModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {

    override fun getName(): String = "PocketPalModule"

    private val POCKETPAL_PACKAGE = "com.pocketpalai"
    private val POCKETPAL_E2E_PACKAGE = "com.pocketpalai.e2e"
    private val REQUEST_CODE_PICK_DIRECTORY = 1001

    private var promise: Promise? = null

    init {
        reactContext.addActivityEventListener(this)
    }

    @ReactMethod
    fun isPocketPalInstalled(promise: Promise) {
        val context = reactApplicationContext
        val packageManager = context.packageManager

        try {
            // Check for both prod and e2e versions
            val prodInstalled = isPackageInstalled(packageManager, POCKETPAL_PACKAGE)
            val e2eInstalled = isPackageInstalled(packageManager, POCKETPAL_E2E_PACKAGE)

            val writableMap = Arguments.createMap()
            writableMap.putBoolean("installed", prodInstalled || e2eInstalled)
            writableMap.putBoolean("prodInstalled", prodInstalled)
            writableMap.putBoolean("e2eInstalled", e2eInstalled)

            promise.resolve(writableMap)
        } catch (e: Exception) {
            promise.reject("ERROR", "Failed to check PocketPal installation", e)
        }
    }

    private fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
        try {
            packageManager.getPackageInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    @ReactMethod
    fun pickPocketPalModelsDirectory(promise: Promise) {
        this.promise = promise

        val activity = getCurrentActivity()
        if (activity == null) {
            promise.reject("ERROR", "No activity available")
            return
        }

        // Check if PocketPal is installed first
        val packageManager = activity.packageManager
        val prodInstalled = isPackageInstalled(packageManager, POCKETPAL_PACKAGE)
        val e2eInstalled = isPackageInstalled(packageManager, POCKETPAL_E2E_PACKAGE)

        if (!prodInstalled && !e2eInstalled) {
            promise.reject("NOT_INSTALLED", "PocketPal AI is not installed")
            return
        }

        // Use Storage Access Framework to let user pick a directory
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )

        // Try to start from PocketPal's directory if possible
        val pocketPalUri = if (prodInstalled) {
            getPocketPalModelsUri(POCKETPAL_PACKAGE)
        } else {
            getPocketPalModelsUri(POCKETPAL_E2E_PACKAGE)
        }
        pocketPalUri?.let { intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, it) }

        activity.startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY, null)
    }

    private fun getPocketPalModelsUri(packageName: String): Uri? {
        // PocketPal stores models in its internal files directory under "models"
        // We can't directly access it, but we can suggest the Documents folder
        // where PocketPal might have exported models
        return Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADocuments")
    }

    @ReactMethod
    fun scanPocketPalModelsDirectory(treeUriString: String, promise: Promise) {
        val context = reactApplicationContext
        val treeUri = Uri.parse(treeUriString)

        try {
            // Take persistable URI permission
            context.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val models = scanDirectoryForModels(context, treeUri)
            val writableArray = Arguments.createArray()

            for (model in models) {
                val modelMap = Arguments.createMap()
                modelMap.putString("name", model.name)
                modelMap.putString("uri", model.uri.toString())
                modelMap.putString("path", model.path)
                modelMap.putDouble("size", model.size.toDouble())
                writableArray.pushMap(modelMap)
            }

            promise.resolve(writableArray)
        } catch (e: Exception) {
            promise.reject("ERROR", "Failed to scan directory", e)
        }
    }

    private data class ModelFile(
        val name: String,
        val uri: Uri,
        val path: String,
        val size: Long
    )

    private fun scanDirectoryForModels(context: Context, treeUri: Uri): List<ModelFile> {
        val models = mutableListOf<ModelFile>()
        val documentId = DocumentsContract.getTreeDocumentId(treeUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_FLAGS
        )

        try {
            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val documentId = cursor.getString(0)
                    val displayName = cursor.getString(1)
                    val mimeType = cursor.getString(2)
                    val size = cursor.getLong(3)
                    val flags = cursor.getInt(4)

                    val childUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)

                    if (isModelFile(displayName, mimeType)) {
                        models.add(ModelFile(displayName, childUri, documentId, size))
                    } else if (isDirectory(context, documentId, treeUri)) {
                        // Recursively scan subdirectories
                        models.addAll(scanDirectoryForModels(context, childUri))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PocketPalModule", "Error scanning directory", e)
        }

        return models
    }

    private fun isModelFile(displayName: String?, mimeType: String?): Boolean {
        if (displayName == null) return false
        val lowerName = displayName.lowercase()
        return lowerName.endsWith(".gguf") ||
               lowerName.endsWith(".bin") ||
               lowerName.endsWith(".safetensors") ||
               lowerName.endsWith(".pt") ||
               lowerName.endsWith(".pth")
    }

    private fun isDirectory(context: Context, documentId: String, treeUri: Uri): Boolean {
        val childUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
        val projection = arrayOf(DocumentsContract.Document.COLUMN_MIME_TYPE)
        return context.contentResolver.query(childUri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val mimeType = cursor.getString(0)
                mimeType == DocumentsContract.Document.MIME_TYPE_DIR
            } else {
                false
            }
        } ?: false
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && promise != null) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val treeUri = data.data
                if (treeUri != null) {
                    // Take persistable URI permission
                    val context = reactApplicationContext
                    context.contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val models = scanDirectoryForModels(context, treeUri)
                    val writableArray = Arguments.createArray()

                    for (model in models) {
                        val modelMap = Arguments.createMap()
                        modelMap.putString("name", model.name)
                        modelMap.putString("uri", model.uri.toString())
                        modelMap.putString("path", model.path)
                        modelMap.putDouble("size", model.size.toDouble())
                        writableArray.pushMap(modelMap)
                    }

                    promise!!.resolve(writableArray)
                } else {
                    promise!!.reject("ERROR", "No directory selected")
                }
            } else {
                promise!!.reject("CANCELLED", "User cancelled directory selection")
            }
            promise = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        // Not used
    }
}
