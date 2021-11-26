package me.aartikov.sesamecomposesample.menu

interface MenuComponent {

    fun onMenuItemClick(item: MenuItem)

    sealed interface Output {
        data class OpenScreen(val menuItem: MenuItem) : Output
    }
}