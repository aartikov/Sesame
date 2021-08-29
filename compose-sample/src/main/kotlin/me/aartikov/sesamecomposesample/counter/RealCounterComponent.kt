package me.aartikov.sesamecomposesample.counter

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext

class RealCounterComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, CounterComponent {

    companion object {
        private const val MAX_COUNT = 10
    }

    override var count by mutableStateOf(0)
        private set

    override val minusButtonEnabled by derivedStateOf { count > 0 }

    override val plusButtonEnabled by derivedStateOf { count < MAX_COUNT - 1 }

    override fun onMinusButtonClick() {
        if (minusButtonEnabled) {
            count--
        }
    }

    override fun onPlusButtonClick() {
        if (plusButtonEnabled) {
            count++
        }

        if (count == MAX_COUNT) {
            // TODO: how to show toasts?
            // showMessage(LocalizedString.resource(R.string.overflow_message))
        }
    }
}