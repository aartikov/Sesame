package me.aartikov.sesamecomposesample.di

import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesamecomposesample.counter.RealCounterComponent
import me.aartikov.sesamecomposesample.dialogs.RealDialogsComponent
import me.aartikov.sesamecomposesample.form.FormComponent
import me.aartikov.sesamecomposesample.form.RealFormComponent
import me.aartikov.sesamecomposesample.menu.MenuComponent
import me.aartikov.sesamecomposesample.menu.RealMenuComponent
import me.aartikov.sesamecomposesample.movies.ui.RealMoviesComponent
import me.aartikov.sesamecomposesample.profile.ui.RealProfileComponent
import me.aartikov.sesamecomposesample.root.RealRootComponent
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ComponentFactory(private val localKoin: Koin) : KoinComponent {

    override fun getKoin(): Koin = localKoin

    fun createProfileComponent(componentContext: ComponentContext) = RealProfileComponent(
        componentContext,
        get(),
        get()
    )

    fun createDialogsComponent(componentContext: ComponentContext) = RealDialogsComponent(
        componentContext,
        get()
    )

    fun createCounterComponent(componentContext: ComponentContext) = RealCounterComponent(
        componentContext,
        get()
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
        get(),
        get()
    )

    fun createRootComponent(componentContext: ComponentContext) = RealRootComponent(
        componentContext,
        get()
    )

    fun createFormComponent(componentContext: ComponentContext) = RealFormComponent(
        componentContext
    )
}