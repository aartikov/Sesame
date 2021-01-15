package me.aartikov.androidarchitecture.dialogs

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.widget.dialogControl

class DialogsViewModel : BaseViewModel() {

    val showMessage = command<String>()

    val dialog = dialogControl<String, Unit>()
    val dialogForResult = dialogControl<String, String>()

    fun onShowDialogButtonClicked() {
        dialog.show("Dialog")
    }

    fun onShowForResultButtonClicked() {
        viewModelScope.launch {
            val result = dialogForResult.showForResult("Dialog for result")

            if (result != null) {
                showMessage(if (result.isEmpty()) "Empty" else result)
            } else {
                showMessage("Canceled")
            }
        }
    }
}