package me.aartikov.sesamecomposesample.features.counter

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.counter.ui.CounterComponent
import me.aartikov.sesamecomposesample.features.counter.ui.RealCounterComponent
import org.koin.core.component.get

fun ComponentFactory.createCounterComponent(componentContext: ComponentContext): CounterComponent {
    return RealCounterComponent(componentContext, get())
}