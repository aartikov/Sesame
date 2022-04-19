package me.aartikov.sesamecomposesample.features.root

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.root.ui.RealRootComponent
import me.aartikov.sesamecomposesample.features.root.ui.RootComponent
import org.koin.core.component.get

fun ComponentFactory.createRootComponent(componentContext: ComponentContext): RootComponent {
    return RealRootComponent(componentContext, get())
}
