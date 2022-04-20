package me.aartikov.sesame.dialog

import android.app.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

/**
 * Allows to bind a [DialogControl] to [Dialog] and send dialog results.
 * In MVVM architecture [DialogObserver] is View.
 */
interface DialogObserver {

    /**
     * A [LifecycleOwner] to provide a lifecycle for bindings.
     */
    val dialogObserverLifecycleOwner: LifecycleOwner

    /**
     * Binds [DialogControl] to a function that creates [Dialog].
     * To send a dialog result call [DialogControl.sendResult] from a dialog listener.
     */
    infix fun <T : Any, R : Any> DialogControl<T, R>.bind(createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {
        dialogObserverLifecycleOwner.lifecycleScope.launchWhenStarted {
            var dialog: Dialog? = null
            val closeDialog = {
                dialog?.setOnDismissListener(null)
                dialog?.dismiss()
                dialog = null
            }

            try {
                stateFlow.collect { state ->
                    when (state) {
                        is DialogControl.State.Shown<T> -> {
                            closeDialog()
                            dialog = createDialog(state.data, this@bind)
                            dialog?.setOnDismissListener { dismiss() }
                            dialog?.show()
                        }

                        is DialogControl.State.Hidden -> {
                            closeDialog()
                        }
                    }
                }
            } finally {
                closeDialog()
            }
        }
    }
}