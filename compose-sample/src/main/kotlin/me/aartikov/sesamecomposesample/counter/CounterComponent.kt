package me.aartikov.sesamecomposesample.counter

interface CounterComponent {

    val count: Int

    val minusButtonEnabled: Boolean

    val plusButtonEnabled: Boolean

    fun onMinusButtonClicked()

    fun onPlusButtonClicked()
}