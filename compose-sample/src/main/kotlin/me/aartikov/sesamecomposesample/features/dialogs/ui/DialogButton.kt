package me.aartikov.sesamecomposesample.features.dialogs.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.aartikov.sesamecomposesample.core.theme.AppTheme

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text.uppercase())
    }
}

@Preview
@Composable
fun CounterButtonsPreview() {
    AppTheme {
        DialogButton("Cancel", onClick = {})
    }
}