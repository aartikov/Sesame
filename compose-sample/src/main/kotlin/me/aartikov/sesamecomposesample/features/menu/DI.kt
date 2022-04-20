package me.aartikov.sesamecomposesample.features.menu

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.features.menu.ui.MenuComponent
import me.aartikov.sesamecomposesample.features.menu.ui.RealMenuComponent

fun ComponentFactory.createMenuComponent(
    componentContext: ComponentContext,
    output: (MenuComponent.Output) -> Unit
): MenuComponent {
    return RealMenuComponent(componentContext, output)
}