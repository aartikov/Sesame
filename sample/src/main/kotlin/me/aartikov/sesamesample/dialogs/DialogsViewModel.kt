package me.aartikov.sesamesample.dialogs

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.command
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DialogsViewModel @Inject constructor() : BaseViewModel() {

    val showMessage = command<LocalizedString>()

    val dialog = DialogControl<LocalizedString, Unit>()
    val dialogForResult = DialogControl<LocalizedString, DialogResult>()

    fun onShowDialogButtonClicked() {
        dialog.show(LocalizedString.resource(R.string.dialog_message))
    }

    fun onShowForResultButtonClicked() {
        viewModelScope.launch {
            val result = dialogForResult.showForResult(LocalizedString.resource(R.string.dialog_message_for_result))
                ?: DialogResult.Cancel

            if (result == DialogResult.Ok) {
                showMessage(LocalizedString.resource(R.string.common_ok))
            } else {
                showMessage(LocalizedString.resource(R.string.common_cancel))
            }
        }
    }
}