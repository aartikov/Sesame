package me.aartikov.sesamecomposesample.features.root.ui

import com.arkivanov.decompose.router.RouterState
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.features.counter.ui.CounterComponent
import me.aartikov.sesamecomposesample.features.dialogs.ui.DialogsComponent
import me.aartikov.sesamecomposesample.features.form.ui.FormComponent
import me.aartikov.sesamecomposesample.features.menu.ui.MenuComponent
import me.aartikov.sesamecomposesample.features.movies.ui.MoviesComponent
import me.aartikov.sesamecomposesample.features.profile.ui.ProfileComponent

interface RootComponent {

    val routerState: RouterState<*, Child>

    val title: LocalizedString

    sealed interface Child {
        class Menu(val component: MenuComponent) : Child
        class Counter(val component: CounterComponent) : Child
        class Dialogs(val component: DialogsComponent) : Child
        class Profile(val component: ProfileComponent) : Child
        class Movies(val component: MoviesComponent) : Child
        class Form(val component: FormComponent) : Child
    }
}