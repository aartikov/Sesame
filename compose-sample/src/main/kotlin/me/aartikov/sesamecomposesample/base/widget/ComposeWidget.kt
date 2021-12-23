package me.aartikov.sesamecomposesample.base.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.aartikov.sesamecomposesample.R

@Composable
fun ProgressWidget() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun PlaceholderWidget(
    message: String,
    onRetry: () -> Unit
) {
    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.body2
            )
            TextButton(
                onClick = onRetry
            ) {
                Text(
                    text = stringResource(R.string.common_retry).uppercase()
                )
            }
        }
    }
}