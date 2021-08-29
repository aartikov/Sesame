package me.aartikov.sesamecomposesample.dialogs

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope

class RealDialogsComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, DialogsComponent {

    private val coroutineScope = componentCoroutineScope()

    override val dialog = DialogControl<LocalizedString, Unit>()
    override val dialogForResult = DialogControl<LocalizedString, DialogResult>()

    override fun onShowDialogButtonClick() {
        dialog.show(LocalizedString.resource(R.string.dialog_message))
    }

    override fun onShowForResultButtonClick() {
        coroutineScope.launch {
            val result = dialogForResult.showForResult(LocalizedString.resource(R.string.dialog_message_for_result))
                ?: DialogResult.Cancel

            // TODO: how to show messages?
            if (result == DialogResult.Ok) {
                //showMessage(LocalizedString.resource(R.string.common_ok))
            } else {
                //showMessage(LocalizedString.resource(R.string.common_cancel))
            }
        }
    }
}