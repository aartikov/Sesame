package me.aartikov.sesamecomposesample.features.root.ui

import android.os.Parcelable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import kotlinx.parcelize.Parcelize
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.core.ComponentFactory
import me.aartikov.sesamecomposesample.core.utils.toComposeState
import me.aartikov.sesamecomposesample.features.counter.createCounterComponent
import me.aartikov.sesamecomposesample.features.dialogs.createDialogsComponent
import me.aartikov.sesamecomposesample.features.form.createFormComponent
import me.aartikov.sesamecomposesample.features.menu.createMenuComponent
import me.aartikov.sesamecomposesample.features.menu.ui.MenuComponent
import me.aartikov.sesamecomposesample.features.menu.ui.MenuItem
import me.aartikov.sesamecomposesample.features.movies.createMoviesComponent
import me.aartikov.sesamecomposesample.features.profile.createProfileComponent

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, RootComponent {

    private val router = router<ChildConfig, RootComponent.Child>(
        initialConfiguration = ChildConfig.Menu,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override val routerState: RouterState<*, RootComponent.Child> by router.state.toComposeState(lifecycle)

    override val title by derivedStateOf {
        getTitle(router.state.value)
    }

    private fun createChild(config: ChildConfig, componentContext: ComponentContext) =
        when (config) {
            is ChildConfig.Menu -> {
                RootComponent.Child.Menu(
                    componentFactory.createMenuComponent(componentContext, ::onMenuOutput)
                )
            }

            is ChildConfig.Counter -> {
                RootComponent.Child.Counter(
                    componentFactory.createCounterComponent(componentContext)
                )
            }

            is ChildConfig.Dialogs -> {
                RootComponent.Child.Dialogs(
                    componentFactory.createDialogsComponent(componentContext)
                )
            }

            is ChildConfig.Profile -> {
                RootComponent.Child.Profile(
                    componentFactory.createProfileComponent(componentContext)
                )
            }

            is ChildConfig.Movies -> {
                RootComponent.Child.Movies(
                    componentFactory.createMoviesComponent(componentContext)
                )
            }

            is ChildConfig.Form -> {
                RootComponent.Child.Form(
                    componentFactory.createFormComponent(componentContext)
                )
            }
        }

    private fun onMenuOutput(output: MenuComponent.Output): Unit = when (output) {
        is MenuComponent.Output.MenuItemSelected -> when (output.menuItem) {
            MenuItem.Counter -> router.push(ChildConfig.Counter)
            MenuItem.Dialogs -> router.push(ChildConfig.Dialogs)
            MenuItem.Profile -> router.push(ChildConfig.Profile)
            MenuItem.Movies -> router.push(ChildConfig.Movies)
            MenuItem.Form -> router.push(ChildConfig.Form)
        }
    }

    private fun getTitle(routerState: RouterState<*, RootComponent.Child>): LocalizedString =
        when (routerState.activeChild.instance) {
            is RootComponent.Child.Menu -> LocalizedString.resource(R.string.app_name)
            is RootComponent.Child.Counter -> LocalizedString.resource(R.string.counter_title)
            is RootComponent.Child.Dialogs -> LocalizedString.resource(R.string.dialogs_title)
            is RootComponent.Child.Profile -> LocalizedString.resource(R.string.profile_title)
            is RootComponent.Child.Movies -> LocalizedString.resource(R.string.movies_title)
            is RootComponent.Child.Form -> LocalizedString.resource(R.string.form_title)
        }

    private sealed interface ChildConfig : Parcelable {

        @Parcelize
        object Menu : ChildConfig

        @Parcelize
        object Counter : ChildConfig

        @Parcelize
        object Dialogs : ChildConfig

        @Parcelize
        object Profile : ChildConfig

        @Parcelize
        object Movies : ChildConfig

        @Parcelize
        object Form : ChildConfig
    }
}

