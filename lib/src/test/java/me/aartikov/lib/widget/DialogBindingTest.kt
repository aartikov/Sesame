package me.aartikov.lib.widget

import android.app.Dialog
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.DispatchersTestRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DialogBindingTest {

    @get:Rule
    val coroutinesTestRule = DispatchersTestRule()

    @Test
    fun `doesn't show dialog when not started`() {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(widgetObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogControl.show(Unit)

        verifyZeroInteractions(mockedDialog)
    }

    @Test
    fun `shows dialog when started`() {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(widgetObserver) { dialogControl bind { _, _ -> mockedDialog } }

        widgetObserver.widgetObserverLifecycleOwner.onStart()
        dialogControl.show(Unit)
        dialogControl.dismiss()

        verify(mockedDialog).show()
        verify(mockedDialog).dismiss()
    }

    @Test
    fun `shows only last dialog when restarted`()  {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Int, Unit>()
        val values = mutableListOf<Int>()
        with(widgetObserver) {
            dialogControl bind { data, _ ->
                values.add(data)
                mockedDialog
            }
        }

        with(widgetObserver.widgetObserverLifecycleOwner) {
            onStop()
            repeat(3) { dialogControl.show(it) }
            onStart()
        }

        verify(mockedDialog).show()
        verify(mockedDialog, never()).dismiss()
        assertEquals(listOf(2), values)
    }

    @Test
    fun `shows only last dialog when not started`()  {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Int, Unit>()
        val values = mutableListOf<Int>()
        with(widgetObserver) {
            dialogControl bind { data, _ ->
                values.add(data)
                mockedDialog
            }
        }

        with(widgetObserver.widgetObserverLifecycleOwner) {
            repeat(3) { dialogControl.show(it) }
            onStart()
        }

        verify(mockedDialog).show()
        verify(mockedDialog, never()).dismiss()
        assertEquals(listOf(2), values)
    }

    @Test
    fun `doesn't show dialog when stopped`() {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(widgetObserver) { dialogControl bind { _, _ -> mockedDialog } }

        widgetObserver.widgetObserverLifecycleOwner.onStop()
        dialogControl.show(Unit)

        verifyZeroInteractions(mockedDialog)
    }

    @Test
    fun `show result dialog when started`() = runBlockingTest {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(widgetObserver) { dialogControl bind { _, _ -> mockedDialog } }

        widgetObserver.widgetObserverLifecycleOwner.onStart()
        val job = launch { dialogControl.showForResult(Unit) }

        verify(mockedDialog).show()
        job.cancel()
    }

    @Test
    fun `doesn't show result dialog when stopped`() = runBlockingTest {
        val widgetObserver = TestWidgetObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(widgetObserver) { dialogControl bind { _, _ -> mockedDialog } }

        widgetObserver.widgetObserverLifecycleOwner.onStop()
        val job = launch { dialogControl.showForResult(Unit) }

        verifyZeroInteractions(mockedDialog)
        job.cancel()
    }
}