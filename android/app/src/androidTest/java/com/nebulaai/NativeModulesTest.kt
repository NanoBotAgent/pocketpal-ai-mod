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

    private fun makePromise(latch: java.util.concurrent.CountDownLatch, result: Array<Any?>): Promise {
        return object : Promise {
            override fun resolve(value: Any?) { result[0] = value?.toString(); latch.countDown() }
            override fun reject(code: String, message: String?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?) { result[1] = code; latch.countDown() }
            override fun reject(code: String, message: String?, throwable: Throwable?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(throwable: Throwable) { result[1] = throwable.message; latch.countDown() }
            override fun reject(throwable: Throwable, userInfo: WritableMap) { result[1] = throwable.message; latch.countDown() }
            override fun reject(code: String, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?, userInfo: WritableMap) { result[1] = "$code: $throwable"; latch.countDown() }
            override fun reject(code: String, message: String?, userInfo: WritableMap) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String?, message: String?, throwable: Throwable?, userInfo: WritableMap?) { result[1] = "$code: $message"; latch.countDown() }
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
        val result = arrayOfNulls<Any?>(2)
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
        val result = arrayOfNulls<Any?>(2)
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
        val result = handleIntent.invoke(module, null)
        assertEquals("handleIntent with null should return false", false, result)
    }

    @Test
    fun externalContentLinkModule_prepareExternalLink_rejectsWithoutActivity() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.ExternalContentLinkModule", reactContext)
            ?: fail("ExternalContentLinkModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<Any?>(2)
        val prepareExternalLink = module.javaClass.getMethod("prepareExternalLink", String::class.java, Promise::class.java)
        prepareExternalLink.invoke(module, "https://example.com/checkout", makePromise(latch, result))
        assertTrue("Promise should settle", await(latch))
        assertEquals("error", result[1])
    }

    @Test
    fun externalContentLinkModule_isExternalContentLinkAvailable_resolvesBoolean() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.ExternalContentLinkModule", reactContext)
            ?: fail("ExternalContentLinkModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val isAvailable = module.javaClass.getMethod("isExternalContentLinkAvailable", Promise::class.java)
        isAvailable.invoke(module, makePromise(latch, result))
        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        assertTrue("Result should be Boolean", result[0] is Boolean)
    }

    @Test
    fun externalContentLinkModule_reportExternalContentLink_resolves() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.ExternalContentLinkModule", reactContext)
            ?: fail("ExternalContentLinkModule should instantiate")
        val latch = java.util.concurrent.CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val report = module.javaClass.getMethod("reportExternalContentLink", String::class.java, String::class.java, Promise::class.java)
        report.invoke(module, "purchase123", "token456", makePromise(latch, result))
        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
    }

    @Test
    fun downloadModule_canInstantiate() {
        val reactContext = ReactApplicationContext(context)
        val module = tryInstantiateModule("com.nebulaai.download.DownloadModule", reactContext)
        assertNotNull("DownloadModule should instantiate", module)
    }
}
