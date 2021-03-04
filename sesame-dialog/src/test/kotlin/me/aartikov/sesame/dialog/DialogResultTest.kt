package me.aartikov.sesame.dialog

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DialogResultTest {

    @Test
    fun `returns a result when sendResult is called`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var result: String? = null
        val job = launch {
            result = dialogControl.showForResult("Anything")
        }
        dialogControl.sendResult("Value")

        assertEquals("Value", result)
        job.cancel()
    }

    @Test
    fun `returns null as a result when dismiss is called`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var result: String? = null
        val job = launch {
            result = dialogControl.showForResult("Anything")
        }
        dialogControl.dismiss()

        assertEquals(null, result)
        job.cancel()
    }

    @Test
    fun `returns null as a result when other dialog is shown`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var result: String? = null
        val job = launch {
            result = dialogControl.showForResult("Anything")
        }
        dialogControl.show("Anything")

        assertEquals(null, result)
        job.cancel()
    }

    @Test
    fun `skips a result when showForResult was not called`() = runBlockingTest {
        val dialogControl = dialogControl<String, String>()

        var result: String? = null
        dialogControl.sendResult("Value 1") // skipped
        val job = launch {
            result = dialogControl.showForResult("Anything")
        }
        dialogControl.sendResult("Value 2")

        assertEquals("Value 2", result)
        job.cancel()
    }
}
