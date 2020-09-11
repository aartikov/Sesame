package me.aartikov.lib.widget

import android.app.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import me.aartikov.lib.widget.dialog_control.DialogControl

interface WidgetObserver : LifecycleOwner {

    infix fun <T, R> DialogControl<T, R>.bind(createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {
        lifecycleScope.launchWhenStarted {
            var dialog: Dialog? = null
            val closeDialog: () -> Unit ={
                dialog?.setOnDismissListener(null)
                dialog?.dismiss()
                dialog = null
            }
            
            displayed.collect {
                @Suppress("UNCHECKED_CAST")
                if (it is DialogControl.Display.Displayed<*>) {
                    dialog = createDialog(it.data as T, this@bind)
                    dialog?.setOnDismissListener { dismiss() }
                    dialog?.show()
                } else if (it === DialogControl.Display.Absent) {
                    closeDialog()
                }
            }
        }
    }
}