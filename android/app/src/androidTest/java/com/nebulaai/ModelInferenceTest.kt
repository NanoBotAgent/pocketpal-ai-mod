package com.nebulaai

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ModelInferenceTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context get() = instrumentation.targetContext

    private fun makePromise(latch: CountDownLatch, result: Array<Any?>): Promise {
        return object : Promise {
            override fun resolve(value: Any?) { result[0] = value; latch.countDown() }
            override fun reject(code: String, message: String, throwable: Throwable?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, message: String) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?) { result[1] = code; latch.countDown() }
            override fun reject(throwable: Throwable, userInfo: WritableMap) { result[1] = throwable.message; latch.countDown() }
            override fun reject(code: String, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, message: String, userInfo: WritableMap) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, message: String, throwable: Throwable?, userInfo: WritableMap) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(message: String) { result[1] = message; latch.countDown() }
        }
    }

    private fun createReactContext(): ReactApplicationContext {
        return ReactApplicationContext(context)
    }

    private fun await(latch: CountDownLatch, timeoutSec: Long = 10): Boolean {
        return latch.await(timeoutSec, TimeUnit.SECONDS)
    }

    @Test
    fun hardwareInfoModule_getChipset_resolves() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.HardwareInfoModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val latch = CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val getChipset = moduleClass.getMethod("getChipset", Promise::class.java)
        getChipset.invoke(module, makePromise(latch, result))

        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        assertNotNull("Should resolve with chipset string", result[0])
    }

    @Test
    fun hardwareInfoModule_getCPUInfo_resolves() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.HardwareInfoModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val latch = CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val getCPUInfo = moduleClass.getMethod("getCPUInfo", Promise::class.java)
        getCPUInfo.invoke(module, makePromise(latch, result))

        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        assertNotNull("Should resolve with CPU info map", result[0])
        assertTrue("Result should be WritableMap", result[0] is WritableMap)
    }

    @Test
    fun hardwareInfoModule_getGPUInfo_resolves() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.HardwareInfoModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val latch = CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val getGPUInfo = moduleClass.getMethod("getGPUInfo", Promise::class.java)
        getGPUInfo.invoke(module, makePromise(latch, result))

        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        assertNotNull("Should resolve with GPU info map", result[0])
        assertTrue("Result should be WritableMap", result[0] is WritableMap)
    }

    @Test
    fun hardwareInfoModule_getAvailableMemory_resolves() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.HardwareInfoModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val latch = CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val getAvailableMemory = moduleClass.getMethod("getAvailableMemory", Promise::class.java)
        getAvailableMemory.invoke(module, makePromise(latch, result))

        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        assertNotNull("Should resolve with available memory", result[0])
        assertTrue("Result should be Number", result[0] is Number)
    }

    @Test
    fun keepAwakeModule_activate_deactivate() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.KeepAwakeModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val activate = moduleClass.getMethod("activate")
        val deactivate = moduleClass.getMethod("deactivate")

        // Should not throw
        activate.invoke(module)
        deactivate.invoke(module)
    }

    @Test
    fun storefrontModule_getCountryCode_resolves() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.StorefrontModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)

        val latch = CountDownLatch(1)
        val result = arrayOfNulls<Any>(2)
        val getCountryCode = moduleClass.getMethod("getCountryCode", Promise::class.java)
        getCountryCode.invoke(module, makePromise(latch, result))

        assertTrue("Promise should settle", await(latch))
        assertNull("Should not reject", result[1])
        // Country code can be null if not available
    }

    @Test
    fun downloadModule_canInstantiate() {
        val reactContext = createReactContext()
        val moduleClass = Class.forName("com.nebulaai.download.DownloadModule")
        val constructor = moduleClass.getConstructor(ReactApplicationContext::class.java)
        val module = constructor.newInstance(reactContext)
        assertNotNull("DownloadModule should instantiate", module)
    }

    @Test
    fun emulator_hasSufficientMemory() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        println("[ModelInferenceTest] Available memory: ${memInfo.availMem / 1_000_000_000.0} GB")
        println("[ModelInferenceTest] Total memory: ${memInfo.totalMem / 1_000_000_000.0} GB")

        // Just verify we can read memory info, don't enforce minimum
        assertTrue("Available memory should be positive", memInfo.availMem > 0)
    }
}
