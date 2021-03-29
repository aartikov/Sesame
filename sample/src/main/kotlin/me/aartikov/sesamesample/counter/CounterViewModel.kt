package me.aartikov.sesamesample.counter

import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor() : BaseViewModel() {

    companion object {
        private const val MAX_COUNT = 10
    }

    var count by state(0)
        private set

    val minusButtonEnabled by computed(::count) { it > 0 }
    val plusButtonEnabled by computed(::count) { it < MAX_COUNT }

    val showMessage = command<LocalizedString>()

    fun onMinusButtonClicked() {
        if (minusButtonEnabled) {
            count--
        }
    }

    fun onPlusButtonClicked() {
        if (plusButtonEnabled) {
            count++
        }

        if (count == MAX_COUNT) {
            showMessage(LocalizedString.resource(R.string.overflow_message))
        }
    }
}