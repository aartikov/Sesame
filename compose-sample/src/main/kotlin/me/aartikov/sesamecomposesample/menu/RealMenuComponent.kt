package me.aartikov.sesamecomposesample.menu

import com.arkivanov.decompose.ComponentContext

class RealMenuComponent(
    componentContext: ComponentContext,
    val output: (MenuComponent.Output) -> Unit
) : ComponentContext by componentContext, MenuComponent {

    override fun onMenuItemClicked(item: MenuItem) {
        output(MenuComponent.Output.OpenScreen(item))
    }
}