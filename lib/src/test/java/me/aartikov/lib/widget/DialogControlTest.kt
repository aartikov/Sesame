package me.aartikov.lib.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.widget.dialog_control.DialogControl
import me.aartikov.lib.widget.dialog_control.dialogControl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DialogControlTest : ViewModel(), WidgetHost {

    companion object {
        private const val TEST_SHOW_DIALOG = "Test show dialog"
    }

    private lateinit var dialogControl: DialogControl<String, String>

    override val widgetHostScope get() = viewModelScope

    @Before
    fun `set up`() {
        dialogControl = dialogControl()
    }

    @Test
    fun `displayed on show`() = runBlockingTest {
        val job = launch {
            dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        assertTrue(dialogControl.displayed.value is DialogControl.Display.Displayed<*>)

        job.cancel()
    }

    @Test
    fun `removed on result`() = runBlockingTest {
        val job = launch {
            dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        dialogControl.sendResult("Value")

        assertTrue(dialogControl.displayed.value === DialogControl.Display.Absent)
    }

    @Test
    fun `removed on dismiss`() = runBlockingTest {
        val job = launch {
            dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        dialogControl.dismiss()

        assertTrue(dialogControl.displayed.value === DialogControl.Display.Absent)

        job.cancel()
    }

    @Test
    fun `accept one result`() = runBlockingTest {
        var expected: String? = null

        val job = launch {
            expected = dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        dialogControl.sendResult("Value 1")
        dialogControl.sendResult("Value 2")

        assertEquals(expected, "Value 1")

        job.cancel()
    }
}
