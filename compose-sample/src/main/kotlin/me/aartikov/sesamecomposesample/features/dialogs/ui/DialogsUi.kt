package me.aartikov.sesamecomposesample.features.dialogs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.theme.AppTheme
import me.aartikov.sesamecomposesample.core.utils.ShowDialog
import me.aartikov.sesamecomposesample.core.utils.resolve
import me.aartikov.sesamecomposesample.features.dialogs.ui.widgets.DialogText
import me.aartikov.sesamecomposesample.features.dialogs.ui.widgets.DialogTitle
import me.aartikov.sesamecomposesample.features.menu.ui.MenuButton

@Composable
fun DialogsUi(
    component: DialogsComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box(modifier = Modifier.padding(32.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(IntrinsicSize.Max)
            ) {
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.show_dialog_button_text),
                    onClick = component::onShowDialogButtonClick
                )
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.show_for_result_button_text),
                    onClick = component::onShowForResultButtonClick
                )
            }
        }

        Dialog(component.dialog)

        DialogForResult(component.dialogForResult)
    }
}

@Composable
fun Dialog(dialog: DialogControl<LocalizedString, Unit>) {
    ShowDialog(dialog) { message ->
        AlertDialog(
            title = {
                DialogTitle(stringResource(R.string.dialog_title))
            },
            text = {
                DialogText(message.resolve())
            },
            confirmButton = {
                DialogButton(
                    text = stringResource(R.string.common_ok),
                    onClick = dialog::dismiss
                )
            },
            onDismissRequest = dialog::dismiss
        )
    }
}

@Composable
fun DialogForResult(dialog: DialogControl<LocalizedString, DialogResult>) {
    ShowDialog(dialog) { message ->
        AlertDialog(
            title = {
                DialogTitle(stringResource(R.string.dialog_for_result_title))
            },
            text = {
                DialogText(message.resolve())
            },
            confirmButton = {
                DialogButton(
                    text = stringResource(R.string.common_ok),
                    onClick = {
                        dialog.sendResult(DialogResult.Ok)
                    }
                )
            },
            dismissButton = {
                DialogButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = {
                        dialog.sendResult(DialogResult.Cancel)
                    }
                )
            },
            onDismissRequest = dialog::dismiss
        )
    }
}

@Preview
@Composable
fun MenuUiPreview() {
    AppTheme {
        DialogsUi(FakeDialogsComponent())
    }
}

class FakeDialogsComponent : DialogsComponent {

    override val dialog = DialogControl<LocalizedString, Unit>()

    override val dialogForResult = DialogControl<LocalizedString, DialogResult>()

    override fun onShowDialogButtonClick() {}

    override fun onShowForResultButtonClick() {}
}