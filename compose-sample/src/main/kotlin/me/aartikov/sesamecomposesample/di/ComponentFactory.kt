package me.aartikov.sesamecomposesample.di

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.counter.RealCounterComponent
import me.aartikov.sesamecomposesample.dialogs.RealDialogsComponent
import me.aartikov.sesamecomposesample.menu.MenuComponent
import me.aartikov.sesamecomposesample.menu.RealMenuComponent
import me.aartikov.sesamecomposesample.movies.ui.RealMoviesComponent
import me.aartikov.sesamecomposesample.profile.ui.RealProfileComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ComponentFactory : KoinComponent {

    fun createProfileComponent(componentContext: ComponentContext) = RealProfileComponent(
        componentContext,
        get()
    )

    fun createDialogsComponent(componentContext: ComponentContext) = RealDialogsComponent(
        componentContext
    )

    fun createCounterComponent(componentContext: ComponentContext) = RealCounterComponent(
        componentContext
    )

    fun createMenuComponent(
        componentContext: ComponentContext,
        output: (MenuComponent.Output) -> Unit
    ) = RealMenuComponent(
        componentContext,
        output
    )

    fun createMoviesComponent(componentContext: ComponentContext) = RealMoviesComponent(
        componentContext,
        get()
    )
}