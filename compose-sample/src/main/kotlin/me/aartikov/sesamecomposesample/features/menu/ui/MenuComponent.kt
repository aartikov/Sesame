package me.aartikov.sesamecomposesample.features.menu.ui

interface MenuComponent {

    fun onMenuItemClick(item: MenuItem)

    sealed interface Output {
        data class MenuItemSelected(val menuItem: MenuItem) : Output
    }
}