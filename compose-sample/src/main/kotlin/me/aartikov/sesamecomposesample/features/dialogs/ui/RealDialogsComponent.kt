package me.aartikov.sesamecomposesample.features.dialogs.ui

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.message.MessageService
import me.aartikov.sesamecomposesample.core.utils.componentCoroutineScope

class RealDialogsComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService
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

            if (result == DialogResult.Ok) {
                messageService.showMessage(LocalizedString.resource(R.string.common_ok))
            } else {
                messageService.showMessage(LocalizedString.resource(R.string.common_cancel))
            }
        }
    }
}