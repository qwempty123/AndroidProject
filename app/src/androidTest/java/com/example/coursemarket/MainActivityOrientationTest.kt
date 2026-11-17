package com.example.coursemarket

import android.content.Context
import androidx.fragment.app.FragmentContainerView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MainActivityOrientationTest {

    @Test
    fun mainScreenCanBeRecreatedWithStableFragmentContainer() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        context.deleteDatabase(CourseMarketDatabase.DATABASE_NAME)

        val repository = CourseMarketRepository(context)
        val account = runBlocking {
            repository.initialize(emptyList())
            repository.register("rotation_test", "rotation123")
        }
        repository.close()

        context.getSharedPreferences("course_market", Context.MODE_PRIVATE)
            .edit()
            .putLong("session_user_id", account.id)
            .apply()

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        try {
            assertTrue("Main screen did not finish loading", awaitMainScreen(scenario, instrumentation))

            scenario.recreate()

            assertTrue(
                "Main screen was not restored after Activity recreation",
                awaitMainScreen(scenario, instrumentation)
            )
            scenario.onActivity { activity ->
                val container = activity.findViewById<FragmentContainerView>(R.id.screen_container)
                assertNotNull(container)
                assertEquals(R.id.screen_container, container.id)
                assertTrue(
                    activity.supportFragmentManager.findFragmentById(R.id.screen_container)
                        is CourseScreenFragment
                )
            }
        } finally {
            scenario.close()
            context.getSharedPreferences("course_market", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            context.deleteDatabase(CourseMarketDatabase.DATABASE_NAME)
        }
    }

    private fun awaitMainScreen(
        scenario: ActivityScenario<MainActivity>,
        instrumentation: android.app.Instrumentation
    ): Boolean {
        repeat(80) {
            instrumentation.waitForIdleSync()
            var found = false
            scenario.onActivity { activity ->
                found = activity.findViewById<FragmentContainerView>(R.id.screen_container) != null
            }
            if (found) return true
            Thread.sleep(100)
        }
        return false
    }
}
