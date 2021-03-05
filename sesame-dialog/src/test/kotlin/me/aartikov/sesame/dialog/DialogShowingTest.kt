package me.aartikov.sesame.dialog

import android.app.Dialog
import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import me.aartikov.sesame.dialog.utils.MainDispatcherRule
import me.aartikov.sesame.dialog.utils.TestDialogObserver
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DialogShowingTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows dialog when lifecycle is started and show is called`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dialogControl.show()

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, never()).dismiss()
    }

    @Test
    fun `doesn't show dialog when lifecycle is stopped and show is called`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        dialogControl.show()

        verifyZeroInteractions(mockedDialog)
    }

    @Test
    fun `shows only last dialog when show is called several times and then lifecycle is started`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Int, Unit>()
        val values = mutableListOf<Int>()
        with(dialogObserver) {
            dialogControl bind { data, _ ->
                values.add(data)
                mockedDialog
            }
        }

        with(dialogObserver.dialogObserverLifecycleOwner) {
            moveToState(Lifecycle.State.CREATED)
            repeat(3) { dialogControl.show(it) }
            moveToState(Lifecycle.State.STARTED)
        }

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, never()).dismiss()
        assertEquals(listOf(2), values)
    }

    @Test
    fun `closes dialog when lifecycle is started and dismiss is called`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dialogControl.show()
        dialogControl.dismiss()

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, times(1)).dismiss()
    }

    @Test
    fun `doesn't closes dialog when lifecycle is stopped and dismiss is called`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dialogControl.show()
        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        dialogControl.dismiss()

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, never()).dismiss()
    }

    @Test
    fun `closes dialog when dismiss is called and then lifecycle is started`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dialogControl.show()
        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        dialogControl.dismiss()
        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, times(1)).dismiss()
    }

    @Test
    fun `show dialog when lifecycle is started and showForResult is called`() = runBlockingTest {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        val job = launch { dialogControl.showForResult() }

        verify(mockedDialog, times(1)).show()
        job.cancel()
    }

    @Test
    fun `doesn't show dialog when lifecycle is stopped and showForResult is called`() = runBlockingTest {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, Unit>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.CREATED)
        val job = launch { dialogControl.showForResult() }

        verifyZeroInteractions(mockedDialog)
        job.cancel()
    }

    @Test
    fun `closes dialog when sendResult is called`() {
        val dialogObserver = TestDialogObserver()
        val mockedDialog = mock<Dialog>()
        val dialogControl = DialogControl<Unit, String>()
        with(dialogObserver) { dialogControl bind { _, _ -> mockedDialog } }

        dialogObserver.dialogObserverLifecycleOwner.moveToState(Lifecycle.State.STARTED)
        dialogControl.show()
        dialogControl.sendResult("Result")

        verify(mockedDialog, times(1)).show()
        verify(mockedDialog, times(1)).dismiss()
    }
}
