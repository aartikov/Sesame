package me.aartikov.sesamecomposesample.menu

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.aartikov.sesamecomposesample.theme.AppTheme

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
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
        MenuButton("Menu item", onClick = {})
    }
}