package me.aartikov.sesamecomposesample.menu

interface MenuComponent {

    fun onMenuItemClicked(item: MenuItem)

    sealed interface Output {
        data class OpenScreen(val menuItem: MenuItem) : Output
    }
}