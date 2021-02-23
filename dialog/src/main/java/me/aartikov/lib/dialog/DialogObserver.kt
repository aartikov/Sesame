package me.aartikov.lib.dialog

import android.app.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect

interface DialogObserver {

    val dialogObserverLifecycleOwner: LifecycleOwner

    infix fun <T : Any, R : Any> DialogControl<T, R>.bind(createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {
        dialogObserverLifecycleOwner.lifecycleScope.launchWhenStarted {
            var dialog: Dialog? = null
            val closeDialog = {
                dialog?.setOnDismissListener(null)
                dialog?.dismiss()
                dialog = null
            }

            try {
                state.collect { state ->
                    when (state) {
                        is DialogControl.State.Shown<T> -> {
                            closeDialog()
                            dialog = createDialog(state.data, this@bind)
                            dialog?.setOnDismissListener { dismiss() }
                            dialog?.show()
                        }

                        is DialogControl.State.Hidded -> {
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