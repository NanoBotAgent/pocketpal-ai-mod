package com.nebulaai

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.nebulaai", context.packageName)
    }

    @Test
    fun appIsInstalled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context)
        assertNotNull(context.packageManager)
    }

    @Test
    fun mainActivityLaunches() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertTrue(activity.isActivityRunning || !activity.isFinishing)
            }
        }
    }
}
