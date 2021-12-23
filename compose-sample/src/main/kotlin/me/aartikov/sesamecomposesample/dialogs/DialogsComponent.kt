package me.aartikov.sesamecomposesample.dialogs

import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.localizedstring.LocalizedString

interface DialogsComponent {

    val dialog: DialogControl<LocalizedString, Unit>

    val dialogForResult: DialogControl<LocalizedString, DialogResult>

    fun onShowDialogButtonClick()

    fun onShowForResultButtonClick()
}