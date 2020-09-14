package me.aartikov.lib.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.widget.dialog_control.DialogControl
import me.aartikov.lib.widget.dialog_control.dialogControl
import org.junit.Assert.assertEquals
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
    fun `show dialog`() = runBlockingTest {
        val job = launch {
            dialogControl.show(TEST_SHOW_DIALOG)
        }

        assertEquals(dialogControl.data.value, TEST_SHOW_DIALOG)

        job.cancel()
    }

    @Test
    fun `show for result dialog`() = runBlockingTest {
        val job = launch {
            dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        assertEquals(dialogControl.data.value, TEST_SHOW_DIALOG)

        job.cancel()
    }

    @Test
    fun `removed on dismiss`() = runBlockingTest {
        val job = launch {
            dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        dialogControl.dismiss()

        assertEquals(dialogControl.data.value, null)

        job.cancel()
    }

    @Test
    fun `accept result`() = runBlockingTest {
        var expected: String? = null

        val job = launch {
            expected = dialogControl.showForResult(TEST_SHOW_DIALOG)
        }

        dialogControl.sendResult("Value 1")

        assertEquals(expected, "Value 1")

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
