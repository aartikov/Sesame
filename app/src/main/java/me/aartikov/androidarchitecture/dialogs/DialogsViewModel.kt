package me.aartikov.androidarchitecture.dialogs

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.widget.dialogControl

class DialogsViewModel : BaseViewModel() {

    val showMessage = command<String>()

    val dialog = dialogControl<String, String>()
    val dialogForResult = dialogControl<Unit, String>()

    fun onShowDialogButtonClicked() {
        dialog.show("Test show dialog")
    }

    fun onShowForResultButtonClicked() {
        viewModelScope.launch {
            val result = dialogForResult.showForResult(Unit)

            if(result != null && result.isNotEmpty()) {
                showMessage.send(result)
            } else {
                showMessage.send("Canceled")
            }
        }
    }
}