package me.aartikov.sesamecomposesample.features.form.ui.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorText(
    errorText: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp)
) {
    Text(
        modifier = modifier.padding(paddingValues),
        text = errorText,
        style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.error)
    )
}