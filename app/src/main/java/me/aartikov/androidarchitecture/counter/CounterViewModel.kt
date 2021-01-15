package me.aartikov.androidarchitecture.counter

import androidx.hilt.lifecycle.ViewModelInject
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.lib.data_binding.command
import me.aartikov.lib.data_binding.computed
import me.aartikov.lib.data_binding.state

class CounterViewModel @ViewModelInject constructor() : BaseViewModel() {

    companion object {
        private const val MAX_COUNT = 10
    }

    var count by state(0)
        private set

    val minusButtonEnabled by computed(::count) { it > 0 }
    val plusButtonEnabled by computed(::count) { it < MAX_COUNT }

    val showMessage = command<String>()

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
            showMessage("It's enough!")
        }
    }
}