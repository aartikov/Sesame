package me.aartikov.sesamecomposesample.features.dialogs.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.aartikov.sesamecomposesample.core.theme.AppTheme

data class ButtonDescriptor(
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun SimpleDialog(
    title: String,
    text: String,
    buttons: List<ButtonDescriptor>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = {
            Text(text = title)
        },

        text = {
            Text(text = text)
        },

        buttons = {
            Row(
                modifier = Modifier
                    .padding(end = 12.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                buttons.forEach { descriptor ->
                    DialogButton(
                        text = descriptor.text,
                        onClick = descriptor.onClick
                    )
                }
            }
        },

        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}

@Preview
@Composable
fun SimpleDialogPreview() {
    AppTheme {
        SimpleDialog(
            title = "Title",
            text = "Some message",
            buttons = listOf(
                ButtonDescriptor(
                    text = "Cancel",
                    onClick = {}
                ),
                ButtonDescriptor(
                    text = "Ok",
                    onClick = {}
                )
            ),
            onDismissRequest = {}
        )
    }
}