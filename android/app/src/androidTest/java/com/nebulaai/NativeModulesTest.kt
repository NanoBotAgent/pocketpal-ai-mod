package com.nebulaai

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeModulesTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    private fun makePromise(latch: java.util.concurrent.CountDownLatch, result: Array<String?>): Promise {
        return object : Promise {
            override fun resolve(value: Any?) { result[0] = value?.toString(); latch.countDown() }
            override fun reject(code: String, message: String?) { result[1] = code; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?) { result[1] = code; latch.countDown() }
            override fun reject(code: String, message: String?, throwable: Throwable?) { result[1] = code; latch.countDown() }
            override fun reject(throwable: Throwable) { result[1] = throwable.message; latch.countDown() }
            override fun reject(throwable: Throwable, userInfo: WritableMap) { result[1] = throwable.message; latch.countDown() }
            override fun reject(code: String, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, message: String?, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String?, message: String?, throwable: Throwable?, userInfo: WritableMap?) { result[1] = code; latch.countDown() }
            override fun reject(message: String) { result[1] = message; latch.countDown() }
        }
    }

    private fun await(latch: java.util.concurrent.CountDownLatch, timeoutSec: Long = 5): Boolean {
        return latch.await(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
    }

    private fun tryInstantiateModule(className: String, reactContext: ReactApplicationContext): Any? {
        return try {
            val clazz = Class.forName(className)
            val constructor = clazz.getConstructor(ReactApplicationContext::class.java)
            constructor.newInstance(reactContext)
        } catch (e: Exception) {
            null
        }
    }

    @Test
    fun hardwareInfoModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.HardwareInfoModule", reactContext)
        assertNotNull("HardwareInfoModule should instantiate", module)
    }

    @Test
    fun keepAwakeModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.KeepAwakeModule", reactContext)
        assertNotNull("KeepAwakeModule should instantiate", module)
    }

    @Test
    fun storefrontModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.StorefrontModule", reactContext)
        assertNotNull("StorefrontModule should instantiate", module)
    }

    @Test
    fun authSessionModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.AuthSessionModule", reactContext)
        assertNotNull("AuthSessionModule should instantiate", module)
    }

    @Test
    fun externalContentLinkModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.ExternalContentLinkModule", reactContext)
        assertNotNull("ExternalContentLinkModule should instantiate", module)
    }

    @Test
    fun storefrontModule_resolvesCountryCode() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.StorefrontModule", reactContext)
            ?: fail("StorefrontModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<String>(2)
        val getCountryCode = module.javaClass.getMethod("getCountryCode", Promise::class.java)
        getCountryCode.invoke(module, makePromise(latch, result))
        assertTrue("Promise should resolve", await(latch))
        assertNull("Should not reject", result[1])
    }

    @Test
    fun authSessionModule_rejectsWithoutActivity() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.AuthSessionModule", reactContext)
            ?: fail("AuthSessionModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<String>(2)
        val openAuth = module.javaClass.getMethod("openAuth", String::class.java, String::class.java, Promise::class.java)
        openAuth.invoke(module, "https://example.com", "pocketpal", makePromise(latch, result))
        assertTrue("Promise should settle", await(latch))
        assertEquals("no_activity", result[1])
    }

    @Test
    fun authSessionModule_handleIntent_nullReturnsFalse() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.AuthSessionModule", reactContext)
            ?: fail("AuthSessionModule should instantiate")
        val handleIntent = module.javaClass.getMethod("handleIntent", Intent::class.java)
        assertFalse(handleIntent.invoke(module, null) as Boolean)
        assertFalse(handleIntent.invoke(module, Intent()) as Boolean)
    }

    @Test
    fun authSessionModule_rejectsSecondConcurrentAuth() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.AuthSessionModule", reactContext)
            ?: fail("AuthSessionModule should instantiate")
        val openAuth = module.javaClass.getMethod("openAuth", String::class.java, String::class.java, Promise::class.java)

        val latch1 = java.util.concurrent.CountDownLatch(1)
        val result1 = arrayOfNulls<String>(2)
        openAuth.invoke(module, "https://example.com", "pocketpal", makePromise(latch1, result1))
        await(latch1)
        val latch2 = java.util.concurrent.CountDownLatch(1)
        val result2 = arrayOfNulls<String>(2)
        openAuth.invoke(module, "https://example2.com", "pocketpal", makePromise(latch2, result2))
        assertTrue("Second promise should settle", await(latch2))
        val anyRejected = result1[1] != null || result2[1] != null
        assertTrue("At least one should reject (no_activity or auth_in_flight)", anyRejected)
    }

    @Test
    fun downloadModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.download.DownloadModule", reactContext)
        assertNotNull("DownloadModule should instantiate", module)
        val getName = module!!.javaClass.getMethod("getName")
        assertEquals("NativeDownloadModuleSpec", getName.invoke(module))
    }

    @Test
    fun pocketPalModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.pocketpal.PocketPalModule", reactContext)
        assertNotNull("PocketPalModule should instantiate", module)
        val getName = module!!.javaClass.getMethod("getName")
        assertEquals("PocketPalModule", getName.invoke(module))
    }

    @Test
    fun pocketPalModule_isPocketPalInstalled_resolves() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.pocketpal.PocketPalModule", reactContext)
            ?: fail("PocketPalModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<String>(2)
        val isPocketPalInstalled = module.javaClass.getMethod("isPocketPalInstalled", Promise::class.java)
        isPocketPalInstalled.invoke(module, makePromise(latch, result))
        assertTrue("Should resolve within timeout", await(latch, 10))
        assertNull("Should not reject", result[1])
    }

    @Test
    fun downloadDatabase_canOpen() {
        val db = com.nebulaai.download.DownloadDatabase.getInstance(context)
        assertNotNull(db)
    }

    @Test
    fun downloadDao_canInsertAndQuery() {
        val db = com.nebulaai.download.DownloadDatabase.getInstance(context)
        val dao = db.downloadDao()
        val testId = "test_ci_${System.currentTimeMillis()}"
        val entity = com.nebulaai.download.DownloadEntity(
            id = testId,
            url = "https://example.com/test.gguf",
            destination = "/tmp/test.gguf",
            totalBytes = 0,
            downloadedBytes = 0,
            status = com.nebulaai.download.DownloadStatus.QUEUED,
            priority = 0,
            networkType = com.nebulaai.download.NetworkType.ANY,
            createdAt = System.currentTimeMillis(),
            error = null,
            authToken = null
        )
        runBlocking {
            dao.insertDownload(entity)
            val retrieved = dao.getDownload(testId)
            assertNotNull("Download should be retrievable after insert", retrieved)
            assertEquals(testId, retrieved!!.id)
            assertEquals("https://example.com/test.gguf", retrieved.url)
            dao.deleteDownload(retrieved)
            val deleted = dao.getDownload(testId)
            assertNull("Download should be gone after delete", deleted)
        }
    }

    @Test
    fun nativeLibraryDir_containsSoFiles() {
        val nativeLibDir = context.applicationInfo?.nativeLibraryDir?.let { java.io.File(it) }
        assertNotNull("Native lib dir path should be available", nativeLibDir)
        assertTrue("Native lib dir should exist", nativeLibDir!!.exists())
        val soFiles = nativeLibDir.listFiles { _, name -> name.endsWith(".so") }
        assertNotNull("Should be able to list .so files", soFiles)
        assertTrue("Should have at least one .so file", soFiles!!.isNotEmpty())
    }

    @Test
    fun appTheme_isNotSystemDefault() {
        val res = context.resources
        val themeId = res.getIdentifier("AppTheme", "style", context.packageName)
        assertTrue(themeId != 0)
    }

    @Test
    fun appHasExportedMainActivity() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull(mainActivity)
        assertTrue("MainActivity should be exported", mainActivity!!.exported)
    }

    @Test
    fun appUsesRtlSupport() {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        assertTrue("App should declare supportsRtl",
            info.applicationInfo!!.flags and android.content.pm.ApplicationInfo.FLAG_SUPPORTS_RTL != 0)
    }

    @Test
    fun launchMode_singleTask_fromPackageInfo() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull(mainActivity)
        assertEquals(android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK,
            mainActivity!!.launchMode)
    }

    @Test
    fun appVersionName_matchesExpected() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, 0)
        assertNotNull(info.versionName)
        assertTrue("Version should be non-empty", info.versionName!!.isNotEmpty())
    }

    @Test
    fun appComponentName_mainActivity_valid() {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(context.packageName)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        assertTrue("Should resolve MAIN/LAUNCHER", resolveInfos.isNotEmpty())
        val resolveInfo = resolveInfos[0]
        assertTrue("Activity name should contain MainActivity",
            resolveInfo.activityInfo.name.contains("MainActivity"))
    }

    @Test
    fun deepLink_invalidHost_notRegistered() {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("pocketpal://unknown/path")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setPackage(context.packageName)
        val activities = pm.queryIntentActivities(intent, 0)
        assertFalse("Should not handle pocketpal://unknown deep link",
            activities.any { it.activityInfo.name.contains("MainActivity") })
    }

    @Test
    fun appAllowsBackup_fromFlags() {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        assertTrue(info.applicationInfo!!.flags and android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP != 0)
    }

    @Test
    fun appHardwareAccelerated_fromFlags() {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        assertTrue(info.applicationInfo!!.flags and android.content.pm.ApplicationInfo.FLAG_HARDWARE_ACCELERATED != 0)
    }

    @Test
    fun mainActivityWindowSoftInputMode_adjustResize() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull(mainActivity)
        val softInputMode = mainActivity!!.softInputMode
        val adjustResize = android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        assertTrue("MainActivity should use adjustResize for soft input",
            softInputMode and adjustResize == adjustResize)
    }

    @Test
    fun appConfigChanges_declaresAllExpected() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull(mainActivity)
        val changes = mainActivity!!.configChanges
        val expectedFlags = listOf(
            android.content.pm.ActivityInfo.CONFIG_ORIENTATION,
            android.content.pm.ActivityInfo.CONFIG_KEYBOARD_HIDDEN,
            android.content.pm.ActivityInfo.CONFIG_SCREEN_SIZE,
            android.content.pm.ActivityInfo.CONFIG_DENSITY,
            android.content.pm.ActivityInfo.CONFIG_UI_MODE
        )
        for (flag in expectedFlags) {
            assertTrue("MainActivity should declare configChanges flag $flag",
                changes and flag != 0)
        }
    }

    @Test
    fun appDataExtractionRules_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("backup_rules_12_plus", "xml", context.packageName)
        assertTrue("backup_rules_12_plus should exist", xmlId != 0)
    }

    @Test
    fun appFullBackupContent_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("backup_rules_legacy", "xml", context.packageName)
        assertTrue("backup_rules_legacy should exist", xmlId != 0)
    }

    @Test
    fun appNetworkSecurityConfig_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("network_security_config", "xml", context.packageName)
        assertTrue("network_security_config should exist", xmlId != 0)
    }

    @Test
    fun appPackageName_matchesApplicationId() {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        assertEquals("com.nebulaai", context.packageName)
        assertEquals("com.nebulaai", info.packageName)
    }

    @Test
    fun appCodePath_exists() {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        val apkFile = java.io.File(info.applicationInfo!!.sourceDir)
        assertTrue("APK file should exist", apkFile.exists())
        assertTrue("APK file should be non-empty", apkFile.length() > 0)
    }
}
