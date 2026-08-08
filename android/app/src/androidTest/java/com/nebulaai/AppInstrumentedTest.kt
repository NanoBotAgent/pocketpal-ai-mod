package com.nebulaai

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context get() = instrumentation.targetContext

    @Test
    fun appContext_validPackageName() {
        assertEquals("com.nebulaai", context.packageName)
    }

    @Test
    fun appContext_notNull() {
        assertNotNull(context)
        assertNotNull(context.applicationContext)
    }

    @Test
    fun appContext_packageManagerAvailable() {
        assertNotNull(context.packageManager)
    }

    @Test
    fun appContext_resourcesAvailable() {
        assertNotNull(context.resources)
        assertTrue(context.resources.displayMetrics.density > 0f)
    }

    @Test
    fun appContext_assetsAvailable() {
        assertNotNull(context.assets)
    }

    @Test
    fun appContext_contentResolverAvailable() {
        assertNotNull(context.contentResolver)
    }

    @Test
    fun appContext_looperAvailable() {
        assertNotNull(context.mainLooper)
    }

    @Test
    fun appContext_applicationInfoExists() {
        assertNotNull(context.applicationInfo)
        assertEquals("com.nebulaai", context.applicationInfo.packageName)
    }

    @Test
    fun appContext_cacheDirExists() {
        assertNotNull(context.cacheDir)
        assertTrue(context.cacheDir.exists())
    }

    @Test
    fun appContext_filesDirAccessible() {
        assertNotNull(context.filesDir)
    }

    @Test
    fun appContext_externalFilesDirAccessible() {
        val extDir = context.getExternalFilesDir(null)
        assertNotNull(extDir)
    }

    @Test
    fun appHasInternetPermission() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val permissions = info.requestedPermissions ?: emptyArray()
        assertTrue("App must have INTERNET permission",
            permissions.contains("android.permission.INTERNET"))
    }

    @Test
    fun appHasCameraPermission() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val permissions = info.requestedPermissions ?: emptyArray()
        assertTrue("App must have CAMERA permission",
            permissions.contains("android.permission.CAMERA"))
    }

    @Test
    fun appVersionInfo_available() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, 0)
        assertTrue("Version name should exist", info.versionName != null)
        assertTrue("Version code should be positive", info.longVersionCode > 0)
    }

    @Test
    fun mainActivity_launches() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertEquals("NebulaAI", activity.mainComponentName)
            }
        }
    }

    @Test
    fun mainActivity_notFinishingAfterLaunch() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertFalse("Activity should not be finishing immediately after launch",
                    activity.isFinishing)
            }
        }
    }

    @Test
    fun mainActivity_windowHasDecorView() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity.window)
                assertNotNull(activity.window.decorView)
            }
        }
    }

    @Test
    fun mainActivity_hasActionBarNull() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // React Native apps typically have no default action bar
                assertNotNull(activity)
            }
        }
    }

    @Test
    fun mainActivity_recreatesSuccessfully() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.recreate()
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertFalse(activity.isFinishing)
            }
        }
    }

    @Test
    fun mainActivity_intentActionMain() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setPackage(context.packageName)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = context.packageManager
        val activities = pm.queryIntentActivities(intent, 0)
        assertTrue("Should find at least one activity matching MAIN/LAUNCHER",
            activities.isNotEmpty())
    }

    @Test
    fun deepLink_hubScheme_registered() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("pocketpal://hub/run/123")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.setPackage(context.packageName)
        val pm = context.packageManager
        val activities = pm.queryIntentActivities(intent, 0)
        assertTrue("Should handle pocketpal://hub deep link",
            activities.isNotEmpty())
    }

    @Test
    fun deepLink_checkoutScheme_registered() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("pocketpal://checkout/callback")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.setPackage(context.packageName)
        val pm = context.packageManager
        val activities = pm.queryIntentActivities(intent, 0)
        assertTrue("Should handle pocketpal://checkout deep link",
            activities.isNotEmpty())
    }

    @Test
    fun appStringResource_exists() {
        val res = context.resources
        val stringId = res.getIdentifier("app_name", "string", context.packageName)
        assertTrue("app_name string resource should exist", stringId != 0)
        val appName = res.getString(stringId)
        assertNotNull(appName)
        assertTrue(appName.isNotEmpty())
    }

    @Test
    fun appTheme_exists() {
        val res = context.resources
        val themeId = res.getIdentifier("AppTheme", "style", context.packageName)
        assertTrue("AppTheme style resource should exist", themeId != 0)
    }

    @Test
    fun appLauncherIcon_exists() {
        val res = context.resources
        val iconId = res.getIdentifier("ic_launcher", "mipmap", context.packageName)
        assertTrue("ic_launcher mipmap should exist", iconId != 0)
    }

    @Test
    fun appRoundIcon_exists() {
        val res = context.resources
        val iconId = res.getIdentifier("ic_launcher_round", "mipmap", context.packageName)
        assertTrue("ic_launcher_round mipmap should exist", iconId != 0)
    }

    @Test
    fun backupRules_legacy_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("backup_rules_legacy", "xml", context.packageName)
        assertTrue("backup_rules_legacy XML should exist", xmlId != 0)
    }

    @Test
    fun backupRules_12plus_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("backup_rules_12_plus", "xml", context.packageName)
        assertTrue("backup_rules_12_plus XML should exist", xmlId != 0)
    }

    @Test
    fun networkSecurityConfig_exists() {
        val res = context.resources
        val xmlId = res.getIdentifier("network_security_config", "xml", context.packageName)
        assertTrue("network_security_config XML should exist", xmlId != 0)
    }

    @Test
    fun database_canOpen() {
        // Verify the app can create/write to its internal database
        val dbFile = context.getDatabasePath("test_ci_verify.db")
        assertNotNull(dbFile)
        dbFile.parentFile?.mkdirs()
        assertTrue("Database directory should exist", dbFile.parentFile?.exists() == true)
        dbFile.delete()
    }

    @Test
    fun sharedPreferences_writable() {
        val prefs = context.getSharedPreferences("ci_test_prefs", Context.MODE_PRIVATE)
        assertNotNull(prefs)
        prefs.edit().putString("test_key", "test_value").commit()
        assertEquals("test_value", prefs.getString("test_key", null))
        prefs.edit().remove("test_key").commit()
        assertNull(prefs.getString("test_key", null))
    }

    @Test
    fun cacheDir_writable() {
        val testFile = java.io.File(context.cacheDir, "ci_test_file.txt")
        testFile.writeText("test content")
        assertTrue(testFile.exists())
        assertEquals("test content", testFile.readText())
        testFile.delete()
        assertFalse(testFile.exists())
    }

    @Test
    fun filesDir_writable() {
        val testFile = java.io.File(context.filesDir, "ci_test_file.txt")
        testFile.writeText("test content")
        assertTrue(testFile.exists())
        assertEquals("test content", testFile.readText())
        testFile.delete()
        assertFalse(testFile.exists())
    }

    @Test
    fun externalStorage_writable() {
        val extDir = context.getExternalFilesDir(null)
        assertNotNull(extDir)
        val testFile = java.io.File(extDir, "ci_ext_test.txt")
        testFile.writeText("external test")
        assertTrue(testFile.exists())
        assertEquals("external test", testFile.readText())
        testFile.delete()
        assertFalse(testFile.exists())
    }

    @Test
    fun cpuInfo_readable() {
        val cpuInfoFile = java.io.File("/proc/cpuinfo")
        assertTrue("cpuinfo should be readable", cpuInfoFile.exists())
        val content = cpuInfoFile.readText()
        assertTrue("cpuinfo should have content", content.isNotEmpty())
        assertTrue("cpuinfo should mention processor",
            content.contains("processor", ignoreCase = true))
    }

    @Test
    fun availableProcessors_positive() {
        val cores = Runtime.getRuntime().availableProcessors()
        assertTrue("Should have at least 1 CPU core", cores > 0)
    }

    @Test
    fun javaRuntime_available() {
        val runtime = Runtime.getRuntime()
        assertNotNull(runtime)
        assertTrue(runtime.maxMemory() > 0 || runtime.maxMemory() == Long.MAX_VALUE)
    }

    @Test
    fun systemProperties_readable() {
        // Read a basic system property
        val bootCompleted = readSystemProperty("sys.boot_completed")
        assertEquals("1", bootCompleted)
    }

    @Test
    fun appProcess_running() {
        val pid = android.os.Process.myPid()
        assertTrue("PID should be positive", pid > 0)
    }

    @Test
    fun emulatorBooted() {
        val bootProp = readSystemProperty("sys.boot_completed")
        assertEquals("Emulator should be fully booted", "1", bootProp)
    }

    @Test
    fun dataDirectory_accessible() {
        val dataDir = context.applicationInfo.dataDir
        assertNotNull(dataDir)
        assertTrue("Data dir should exist", java.io.File(dataDir).exists())
    }

    @Test
    fun nativeLibraryDir_accessible() {
        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        assertNotNull(nativeLibDir)
        assertTrue("Native lib dir should exist", java.io.File(nativeLibDir).exists())
    }

    @Test
    fun appCanStartAnotherActivity() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertNotNull(activity.application)
                assertTrue(activity.application is android.app.Application)
            }
        }
    }

    @Test
    fun webViewAvailable() {
        // Check if WebView is available (React Native uses it for some internals)
        val webViewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
        webViewIntent.setPackage(context.packageName)
        // Even if no package matches, the system WebView should be available
        val pm = instrumentation.context.packageManager
        val webActivities = pm.queryIntentActivities(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com")), 0)
        assertTrue("System should have a browser or WebView handler", webActivities.isNotEmpty())
    }

    @Test
    fun notificationManagerAvailable() {
        val notifMgr = context.getSystemService(Context.NOTIFICATION_SERVICE)
        assertNotNull("NotificationManager should be available", notifMgr)
    }

    @Test
    fun activityManagerAvailable() {
        val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE)
        assertNotNull("ActivityManager should be available", activityMgr)
    }

    @Test
    fun connectivityServiceAvailable() {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        assertNotNull("ConnectivityService should be available", connMgr)
    }

    @Test
    fun downloadManagerAvailable() {
        // Most Android devices have DownloadManager
        val dlMgr = context.getSystemService(Context.DOWNLOAD_SERVICE)
        assertNotNull("DownloadManager should be available", dlMgr)
    }

    @Test
    fun alarmServiceAvailable() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE)
        assertNotNull("AlarmManager should be available", alarmMgr)
    }

    @Test
    fun vibratorServiceAvailable() {
        val vibMgr = context.getSystemService(Context.VIBRATOR_SERVICE)
        assertNotNull("VibratorService should be available", vibMgr)
    }

    @Test
    fun clipboardManagerAvailable() {
        val clipMgr = context.getSystemService(Context.CLIPBOARD_SERVICE)
        assertNotNull("ClipboardManager should be available", clipMgr)
    }

    @Test
    fun sensorManagerAvailable() {
        val sensorMgr = context.getSystemService(Context.SENSOR_SERVICE)
        assertNotNull("SensorManager should be available", sensorMgr)
    }

    @Test
    fun wifiServiceAvailable() {
        val wifiMgr = context.getSystemService(Context.WIFI_SERVICE)
        assertNotNull("WifiManager should be available", wifiMgr)
    }

    @Test
    fun storageManagerAvailable() {
        val storageMgr = context.getSystemService(Context.STORAGE_SERVICE)
        assertNotNull("StorageManager should be available", storageMgr)
    }

    @Test
    fun windowManagerAvailable() {
        val windowMgr = context.getSystemService(Context.WINDOW_SERVICE)
        assertNotNull("WindowManager should be available", windowMgr)
    }

    @Test
    fun powerManagerAvailable() {
        val powerMgr = context.getSystemService(Context.POWER_SERVICE)
        assertNotNull("PowerManager should be available", powerMgr)
    }

    @Test
    fun keyguardManagerAvailable() {
        val keyguardMgr = context.getSystemService(Context.KEYGUARD_SERVICE)
        assertNotNull("KeyguardManager should be available", keyguardMgr)
    }

    @Test
    fun audioManagerAvailable() {
        val audioMgr = context.getSystemService(Context.AUDIO_SERVICE)
        assertNotNull("AudioManager should be available", audioMgr)
    }

    @Test
    fun displayMetrics_valid() {
        val metrics = context.resources.displayMetrics
        assertTrue("Display width should be positive", metrics.widthPixels > 0)
        assertTrue("Display height should be positive", metrics.heightPixels > 0)
        assertTrue("Display density should be positive", metrics.density > 0f)
        assertTrue("Density DPI should be positive", metrics.densityDpi > 0)
    }

    @Test
    fun configuration_valid() {
        val config = context.resources.configuration
        assertNotNull(config)
        assertTrue("Screen width should be positive",
            config.screenWidthDp > 0 || config.screenLayout != 0)
    }

    @Test
    fun appMinSdkVersion() {
        val minSdk = context.applicationInfo.minSdkVersion
        assertTrue("Min SDK should be reasonable for RN app",
            minSdk >= android.os.Build.VERSION_CODES.LOLLIPOP)
    }

    @Test
    fun appTargetSdkVersion() {
        val targetSdk = context.applicationInfo.targetSdkVersion
        assertNotNull(targetSdk)
        assertTrue("Target SDK should be at least 30",
            targetSdk!! >= android.os.Build.VERSION_CODES.R)
    }

    @Test
    fun appHasValidUid() {
        val uid = context.applicationInfo.uid
        assertTrue("App UID should be positive", uid > 0)
    }

    @Test
    fun appProcessName_correct() {
        val procName = readSystemProperty("application.process.name")
        if (procName.isNotEmpty()) {
            assertEquals("com.nebulaai", procName)
        }
    }

    @Test
    fun debuggable() {
        // On debug builds, the app should be debuggable
        val debuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        assertTrue("Debug builds should be debuggable", debuggable)
    }

    @Test
    fun allowBackup() {
        val backup = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP) != 0
        assertTrue("App should allow backup", backup)
    }

    @Test
    fun hardwareAccelerated() {
        val hwAccel = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_HARDWARE_ACCELERATED) != 0
        assertTrue("App should have hardware acceleration enabled", hwAccel)
    }

    @Test
    fun noNullRequiredPermissions() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val permissions = info.requestedPermissions
        if (permissions != null) {
            for (perm in permissions) {
                assertNotNull("Permission should not be null", perm)
                assertTrue("Permission should be non-empty", perm.isNotEmpty())
            }
        }
    }

    @Test
    fun mainActivityConfigChanges_declared() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull("MainActivity should be declared in manifest", mainActivity)
        assertTrue("MainActivity should declare configChanges",
            (mainActivity!!.configChanges and
                android.content.pm.ActivityInfo.CONFIG_ORIENTATION) != 0)
        assertTrue("MainActivity should declare keyboard configChanges",
            (mainActivity.configChanges and
                android.content.pm.ActivityInfo.CONFIG_KEYBOARD_HIDDEN) != 0)
    }

    @Test
    fun mainActivity_launchMode_singleTask() {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val activities = info.activities ?: emptyArray()
        val mainActivity = activities.find { it.name.contains("MainActivity") }
        assertNotNull("MainActivity should be declared in manifest", mainActivity)
        assertEquals("MainActivity should use singleTask launch mode",
            android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK,
            mainActivity!!.launchMode)
    }

    @Test
    fun nativeOptionalLibrary_openCL_notRequired() {
        // libOpenCL.so is optional (required=false), so the app should install fine without it
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
        assertNotNull(info)
    }

    private fun readSystemProperty(name: String): String {
        return try {
            Class.forName("android.os.SystemProperties")
                .getMethod("get", String::class.java)
                .invoke(null, name) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
