package me.aartikov.sesamecomposesample.features.counter.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.message.MessageService

class RealCounterComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService
) : ComponentContext by componentContext, CounterComponent {

    companion object {
        private const val MAX_COUNT = 10
    }

    override var count by mutableStateOf(0)
        private set

    override val minusButtonEnabled by derivedStateOf { count > 0 }

    override val plusButtonEnabled by derivedStateOf { count < MAX_COUNT }

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
            messageService.showMessage(LocalizedString.resource(R.string.overflow_message))
        }
    }
}