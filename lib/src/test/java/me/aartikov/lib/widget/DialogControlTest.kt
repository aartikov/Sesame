package me.aartikov.lib.widget

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DialogControlTest {

    @Test
    fun `accepts result`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var actual: String? = null
        val job = launch {
            actual = dialogControl.showForResult("Anything")
        }
        dialogControl.sendResult("Value 1")

        assertEquals( "Value 1", actual)
        job.cancel()
    }

    @Test
    fun `skips result when not requested`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var actual: String? = null
        dialogControl.sendResult("Value 1")
        val job = launch {
            actual = dialogControl.showForResult("Anything")
        }
        dialogControl.sendResult("Value 2")

        assertEquals("Value 2", actual)
        job.cancel()
    }
}
