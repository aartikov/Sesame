package me.aartikov.sesamecomposesample.features.counter.ui

interface CounterComponent {

    val count: Int

    val minusButtonEnabled: Boolean

    val plusButtonEnabled: Boolean

    fun onMinusButtonClick()

    fun onPlusButtonClick()
}