package me.aartikov.sesamecomposesample.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.aartikov.sesamecomposesample.theme.AppTheme

enum class CounterIcon(val symbol: Char) {
    Minus('–'),
    Plus('+')
}

@Composable
fun CounterButton(
    icon: CounterIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = modifier.size(42.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = icon.symbol.toString(),
            fontSize = 24.sp
        )
    }
}

@Preview
@Composable
fun CounterButtonsPreview() {
    AppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CounterButton(CounterIcon.Minus, onClick = {}, enabled = false)
            CounterButton(CounterIcon.Plus, onClick = {}, enabled = true)
        }
    }
}