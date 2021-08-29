package me.aartikov.sesamecomposesample.root

import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.value.Value
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.counter.CounterComponent
import me.aartikov.sesamecomposesample.menu.MenuComponent

interface RootComponent {

    val routerState: Value<RouterState<*, Child>>

    val title: LocalizedString

    sealed interface Child {
        class Menu(val component: MenuComponent) : Child
        class Counter(val component: CounterComponent) : Child
    }
}