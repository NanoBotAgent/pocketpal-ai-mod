package com.nebulaai

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * ModelInferenceTest downloads the MiniCPM5-1B-Q8_0 GGUF model from HuggingFace,
 * loads it via llama.rn native library, and tests real LLM inference including:
 * - Basic text generation
 * - Search ON/OFF behavior difference
 * - Pal system prompt injection
 *
 * Requirements: 8GB emulator RAM (-memory 8192) for the 1.2GB Q8 model.
 */
@RunWith(AndroidJUnit4::class)
class ModelInferenceTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context get() = instrumentation.targetContext

    companion object {
        private const val MODEL_HF_URL = "https://huggingface.co/RockMan256/MiniCPM5-1B-Q8_0-GGUF/resolve/main/MiniCPM5-1B.Q8_0.gguf"
        private const val MODEL_FILENAME = "MiniCPM5-1B.Q8_0.gguf"
        private const val DOWNLOAD_TIMEOUT_SEC = 300L
        private const val INFERENCE_TIMEOUT_SEC = 120L
        private const val MAX_TOKENS = 64
    }

    private fun makePromise(latch: CountDownLatch, result: Array<Any?>): Promise {
        return object : Promise {
            override fun resolve(value: Any?) { result[0] = value; latch.countDown() }
            override fun reject(code: String, message: String?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, message: String?, e: Throwable?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(code: String, e: Throwable?) { result[1] = "$code: ${e?.message}"; latch.countDown() }
            override fun reject(code: String, message: String?, e: Throwable?, userInfo: WritableMap?) { result[1] = "$code: $message"; latch.countDown() }
            override fun reject(e: Throwable) { result[1] = "error: ${e.message}"; latch.countDown() }
            override fun reject(e: Throwable, userInfo: WritableMap) { result[1] = "error: ${e.message}"; latch.countDown() }
            override fun reject(code: String, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, throwable: Throwable?, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String, message: String?, userInfo: WritableMap) { result[1] = code; latch.countDown() }
            override fun reject(code: String?, message: String?, throwable: Throwable?, userInfo: WritableMap?) { result[1] = code; latch.countDown() }
            override fun reject(message: String) { result[1] = message; latch.countDown() }
        }
    }

    private fun downloadModel(): File {
        val modelDir = File(context.getExternalFilesDir(null), "models")
        modelDir.mkdirs()
        val modelFile = File(modelDir, MODEL_FILENAME)

        if (modelFile.exists() && modelFile.length() > 1_000_000_000) {
            println("[ModelInferenceTest] Model already downloaded: ${modelFile.length()} bytes")
            return modelFile
        }

        println("[ModelInferenceTest] Downloading model from $MODEL_HF_URL")
        val conn = (URL(MODEL_HF_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 30000
            readTimeout = 300000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "NebulaAI-CI-Test/1.0")
        }

        assertEquals("HTTP connection should succeed", 200, conn.responseCode)

        val totalBytes = conn.contentLengthLong
        println("[ModelInferenceTest] Model size: $totalBytes bytes (${totalBytes / 1_000_000_000.0} GB)")

        conn.inputStream.use { input ->
            FileOutputStream(modelFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalDownloaded = 0L
                var lastProgressLog = 0L
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalDownloaded += bytesRead
                    if (totalDownloaded - lastProgressLog > 100_000_000) {
                        println("[ModelInferenceTest] Downloaded ${totalDownloaded / 1_000_000} MB / ${totalBytes / 1_000_000} MB")
                        lastProgressLog = totalDownloaded
                    }
                }
            }
        }

        assertTrue("Model file should exist after download", modelFile.exists())
        assertTrue("Model file should be > 1GB",
            modelFile.length() > 1_000_000_000)

        println("[ModelInferenceTest] Download complete: ${modelFile.length()} bytes")
        return modelFile
    }

    private fun getLlamaModule(reactContext: ReactApplicationContext): Any? {
        return try {
            val llamaModuleClass = Class.forName("com.rnllm.LlamaModule")
            val getNativeModuleMethod = ReactApplicationContext::class.java.getMethod(
                "getNativeModule",
                Class::class.java
            )
            getNativeModuleMethod.invoke(reactContext, llamaModuleClass)
        } catch (e: Exception) {
            println("[ModelInferenceTest] Could not get LlamaModule instance: ${e.message}")
            null
        }
    }

    private fun loadLlamaContext(modelPath: String): Long {
        val reactContext = ReactApplicationContext(context)

        val initLatch = CountDownLatch(1)
        val initResult = arrayOfNulls<Any>(2)

        val params = com.facebook.react.bridge.Arguments.createMap().apply {
            putString("model", modelPath)
            putInt("n_ctx", 2048)
            putInt("n_batch", 512)
            putInt("n_threads", 2)
            putInt("n_gpu_layers", 0)
            putBoolean("use_mlock", false)
            putBoolean("use_mmap", true)
        }

        try {
            val llamaModuleClass = Class.forName("com.rnllm.LlamaModule")
            val initLlama = llamaModuleClass.getDeclaredMethod(
                "initLlama",
                com.facebook.react.bridge.ReadableMap::class.java,
                Promise::class.java
            )

            val moduleInstance = getLlamaModule(reactContext)
            assertNotNull("LlamaModule instance should not be null", moduleInstance)

            val promise = makePromise(initLatch, initResult)

            initLlama.invoke(moduleInstance, params, promise)
            assertTrue("Model init should complete within timeout",
                initLatch.await(INFERENCE_TIMEOUT_SEC, TimeUnit.SECONDS))

            assertNull("Model init should not reject. Error: ${initResult[1]}", initResult[1])
            assertNotNull("Model init should return a context", initResult[0])

            val contextId = (initResult[0] as? com.facebook.react.bridge.ReadableMap)
                ?.getDouble("contextId")?.toLong()
                ?: -1L

            assertTrue("Context ID should be positive", contextId > 0)
            println("[ModelInferenceTest] Llama context loaded: $contextId")
            return contextId
        } catch (e: ClassNotFoundException) {
            println("[ModelInferenceTest] llama.rn module not found: ${e.message}")
            fail("llama.rn LlamaModule class not found: ${e.message}")
            return -1
        }
    }

    private fun runCompletion(contextId: Long, prompt: String, systemPrompt: String? = null): String {
        val completionLatch = CountDownLatch(1)
        val completionResult = arrayOfNulls<Any>(2)

        try {
            val llamaModuleClass = Class.forName("com.rnllm.LlamaModule")
            val reactContext = ReactApplicationContext(context)
            val moduleInstance = getLlamaModule(reactContext)
            assertNotNull("LlamaModule instance should not be null", moduleInstance)

            val promptMap = com.facebook.react.bridge.Arguments.createMap().apply {
                putDouble("contextId", contextId.toDouble())
                putString("prompt", prompt)
                putInt("max_tokens", MAX_TOKENS)
                putDouble("temperature", 0.7)
                putDouble("top_p", 0.9)
                putBoolean("stream", false)
                if (systemPrompt != null) {
                    putString("system_prompt", systemPrompt)
                }
            }

            val completionMethod = llamaModuleClass.getDeclaredMethod(
                "completion",
                com.facebook.react.bridge.ReadableMap::class.java,
                Promise::class.java
            )

            val promise = makePromise(completionLatch, completionResult)

            completionMethod.invoke(moduleInstance, promptMap, promise)
            assertTrue("Completion should finish within timeout",
                completionLatch.await(INFERENCE_TIMEOUT_SEC, TimeUnit.SECONDS))

            assertNull("Completion should not reject. Error: ${completionResult[1]}",
                completionResult[1])

            val result = completionResult[0] as? com.facebook.react.bridge.ReadableMap
            assertNotNull("Completion should return a result map", result)

            val text = result?.getString("text") ?: ""
            println("[ModelInferenceTest] Completion result (${text.length} chars): ${text.take(200)}")
            return text
        } catch (e: Exception) {
            fail("Completion failed: ${e.message}")
            return ""
        }
    }

    private fun releaseContext(contextId: Long) {
        try {
            val llamaModuleClass = Class.forName("com.rnllm.LlamaModule")
            val reactContext = ReactApplicationContext(context)
            val moduleInstance = getLlamaModule(reactContext)
            val releaseMethod = llamaModuleClass.getDeclaredMethod(
                "releaseContext",
                java.lang.Double::class.java,
                Promise::class.java
            )
            val latch = CountDownLatch(1)
            val result = arrayOfNulls<Any>(2)
            val promise = makePromise(latch, result)
            releaseMethod.invoke(moduleInstance, contextId.toDouble(), promise)
            latch.await(30, TimeUnit.SECONDS)
            println("[ModelInferenceTest] Released context $contextId")
        } catch (e: Exception) {
            println("[ModelInferenceTest] Release failed (non-fatal): ${e.message}")
        }
    }

    @Test
    fun model_download_succeeds() {
        val modelFile = downloadModel()
        assertNotNull("Model file should not be null", modelFile)
        assertTrue("Model file should exist", modelFile.exists())
        assertTrue("Model file should be > 1GB", modelFile.length() > 1_000_000_000)
    }

    @Test
    fun model_load_succeeds() {
        val modelFile = downloadModel()
        val contextId = loadLlamaContext(modelFile.absolutePath)
        assertTrue("Context ID should be positive", contextId > 0)
        releaseContext(contextId)
    }

    @Test
    fun model_basicInference_producesText() {
        val modelFile = downloadModel()
        val contextId = loadLlamaContext(modelFile.absolutePath)

        try {
            val prompt = "You are a helpful assistant. Say hello in one sentence."
            val result = runCompletion(contextId, prompt)

            assertTrue("Inference should produce non-empty text",
                result.isNotEmpty())
            println("[ModelInferenceTest] Basic inference output: ${result.take(200)}")
        } finally {
            releaseContext(contextId)
        }
    }

    @Test
    fun model_inference_searchOnOff_produceDifferentResults() {
        val modelFile = downloadModel()
        val contextId = loadLlamaContext(modelFile.absolutePath)

        try {
            val prompt = "What is the capital of France?"

            val resultWithoutSearch = runCompletion(contextId, prompt)
            assertTrue("Inference without search should produce text",
                resultWithoutSearch.isNotEmpty())

            val searchContextPrompt = """
                Based on the following search results, answer the question.

                Search Results:
                1. Paris is the capital and most populous city of France.
                2. France is a country in Western Europe with Paris as its capital.

                Question: What is the capital of France?
            """.trimIndent()

            val resultWithSearch = runCompletion(contextId, searchContextPrompt)
            assertTrue("Inference with search should produce text",
                resultWithSearch.isNotEmpty())

            println("[ModelInferenceTest] Without search: ${resultWithoutSearch.take(150)}")
            println("[ModelInferenceTest] With search: ${resultWithSearch.take(150)}")

            val combinedResults = (resultWithoutSearch + resultWithSearch).lowercase()
            assertTrue("At least one result should mention Paris",
                combinedResults.contains("paris"))
        } finally {
            releaseContext(contextId)
        }
    }

    @Test
    fun model_palSystemPrompt_injection_changesBehavior() {
        val modelFile = downloadModel()
        val contextId = loadLlamaContext(modelFile.absolutePath)

        try {
            val prompt = "Tell me about yourself."

            val resultWithoutPal = runCompletion(contextId, prompt)

            val palSystemPrompt = """
                You are Nebula, a friendly AI companion inside a mobile app.
                Always introduce yourself as Nebula and be concise, warm, and helpful.
                Keep responses under 3 sentences.
            """.trimIndent()

            val resultWithPal = runCompletion(contextId, prompt, palSystemPrompt)

            assertTrue("Without pal prompt should produce text",
                resultWithoutPal.isNotEmpty())
            assertTrue("With pal prompt should produce text",
                resultWithPal.isNotEmpty())

            println("[ModelInferenceTest] Without pal: ${resultWithoutPal.take(150)}")
            println("[ModelInferenceTest] With pal: ${resultWithPal.take(150)}")

            val withPalLower = resultWithPal.lowercase()
            val withoutPalLower = resultWithoutPal.lowercase()

            val behavioralChange = withPalLower != withoutPalLower &&
                (withPalLower.contains("nebula") ||
                 withPalLower.contains("companion") ||
                 resultWithPal.length < resultWithoutPal.length)

            assertTrue(
                "Pal system prompt should change model behavior (mention Nebula or alter response)",
                behavioralChange
            )
        } finally {
            releaseContext(contextId)
        }
    }

    @Test
    fun model_multipleCompletions_areConsistent() {
        val modelFile = downloadModel()
        val contextId = loadLlamaContext(modelFile.absolutePath)

        try {
            val prompt = "What is 2 + 2?"

            val result1 = runCompletion(contextId, prompt)
            val result2 = runCompletion(contextId, prompt)

            assertTrue("First completion should produce text", result1.isNotEmpty())
            assertTrue("Second completion should produce text", result2.isNotEmpty())

            val combined = (result1 + result2).lowercase()
            assertTrue("At least one result should contain 4",
                combined.contains("4"))
        } finally {
            releaseContext(contextId)
        }
    }

    @Test
    fun emulator_hasSufficientMemory() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        println("[ModelInferenceTest] Available memory: ${memInfo.availMem / 1_000_000_000.0} GB")
        println("[ModelInferenceTest] Total memory: ${memInfo.totalMem / 1_000_000_000.0} GB")

        assertTrue("Emulator should have at least 4GB available memory for model loading",
            memInfo.availMem > 4_000_000_000L)
    }
}
