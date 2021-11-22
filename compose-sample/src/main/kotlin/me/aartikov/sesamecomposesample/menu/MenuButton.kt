package me.aartikov.sesamecomposesample.menu

import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import me.aartikov.sesamecomposesample.theme.AppTheme

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = colors
    ) {
        Text(
            text = text.uppercase(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun CounterButtonsPreview() {
    AppTheme {
        MenuButton(text = "Menu item", onClick = {})
    }
}