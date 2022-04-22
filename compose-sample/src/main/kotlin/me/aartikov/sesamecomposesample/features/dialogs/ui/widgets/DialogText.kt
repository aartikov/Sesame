package me.aartikov.sesamecomposesample.features.dialogs.ui.widgets

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DialogText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.body2,
        modifier = modifier
    )
}