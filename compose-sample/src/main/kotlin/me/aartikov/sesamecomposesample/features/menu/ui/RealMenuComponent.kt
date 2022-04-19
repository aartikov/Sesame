package me.aartikov.sesamecomposesample.features.menu.ui

import com.arkivanov.decompose.ComponentContext

class RealMenuComponent(
    componentContext: ComponentContext,
    val output: (MenuComponent.Output) -> Unit
) : ComponentContext by componentContext, MenuComponent {

    override fun onMenuItemClick(item: MenuItem) {
        output(MenuComponent.Output.MenuItemSelected(item))
    }
}