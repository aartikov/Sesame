package me.aartikov.lib.dialog

import android.app.Dialog
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.lib.utils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DialogBindingTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `doesn't show dialog when not started`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogControl.show()

        verifyZeroInteractions(mockedDialog)
    }

    @Test
    fun `shows dialog when started`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.onStart()
        dialogControl.show()

        verify(mockedDialog).show()
        verify(mockedDialog, never()).dismiss()
    }

    @Test
    fun `closes dialog when started`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.onStart()
        dialogControl.show()
        dialogControl.dismiss()

        verify(mockedDialog).show()
        verify(mockedDialog).dismiss()
    }

    @Test
    fun `shows only last dialog when restarted`()  {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Int, Unit>()
        val values = mutableListOf<Int>()
        with(dialogObserver) {
            dialogControl bind { data, _ ->
                values.add(data)
                mockedDialog
            }
        }

        with(dialogObserver.dialogObserverLifecycleOwner) {
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
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Int, Unit>()
        val values = mutableListOf<Int>()
        with(dialogObserver) {
            dialogControl bind { data, _ ->
                values.add(data)
                mockedDialog
            }
        }

        with(dialogObserver.dialogObserverLifecycleOwner) {
            repeat(3) { dialogControl.show(it) }
            onStart()
        }

        verify(mockedDialog).show()
        verify(mockedDialog, never()).dismiss()
        assertEquals(listOf(2), values)
    }

    @Test
    fun `doesn't show dialog when stopped`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.onStop()
        dialogControl.show()

        verifyZeroInteractions(mockedDialog)
    }

    @Test
    fun `show result dialog when started`() = runBlockingTest {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.onStart()
        val job = launch { dialogControl.showForResult() }

        verify(mockedDialog).show()
        job.cancel()
    }

    @Test
    fun `doesn't show result dialog when stopped`() = runBlockingTest {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = dialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.onStop()
        val job = launch { dialogControl.showForResult() }

        verifyZeroInteractions(mockedDialog)
        job.cancel()
    }
}
