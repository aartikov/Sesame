package me.aartikov.sesamecomposesample.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.aartikov.sesamecomposesample.theme.AppTheme

@Composable
fun CounterUi(
    component: CounterComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(56.dp, Alignment.CenterHorizontally)
        ) {
            CounterButton(
                icon = CounterIcon.Minus,
                onClick = component::onMinusButtonClick,
                enabled = component.minusButtonEnabled
            )
            Text(
                text = component.count.toString(),
                style = MaterialTheme.typography.h6,
            )
            CounterButton(
                icon = CounterIcon.Plus,
                onClick = component::onPlusButtonClick,
                enabled = component.plusButtonEnabled
            )
        }
    }
}

@Preview
@Composable
fun CounterUiPreview() {
    AppTheme {
        CounterUi(FakeCounterComponent())
    }
}

class FakeCounterComponent : CounterComponent {
    override val count = 10
    override val minusButtonEnabled = true
    override val plusButtonEnabled = false

    override fun onMinusButtonClick() {}
    override fun onPlusButtonClick() {}
}