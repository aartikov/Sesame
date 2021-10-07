package me.aartikov.sesamecomposesample.root

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.arkivanov.decompose.value.Value
import kotlinx.parcelize.Parcelize
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.di.ComponentFactory
import me.aartikov.sesamecomposesample.menu.MenuComponent
import me.aartikov.sesamecomposesample.menu.MenuItem

class RealRootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, RootComponent {

    private val componentFactory = ComponentFactory()

    private val router = router<ChildConfig, RootComponent.Child>(
        initialConfiguration = ChildConfig.Menu,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override val routerState: Value<RouterState<*, RootComponent.Child>> = router.state

    override var title by mutableStateOf(getTitle(router.state.value))

    init {
        routerState.subscribe { state ->
            title = getTitle(state)
        }
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
        }

    private fun onMenuOutput(output: MenuComponent.Output): Unit = when (output) {
        is MenuComponent.Output.OpenScreen -> when (output.menuItem) {
            MenuItem.Counter -> router.push(ChildConfig.Counter)
            MenuItem.Dialogs -> router.push(ChildConfig.Dialogs)
            MenuItem.Profile -> router.push(ChildConfig.Profile)
        }
    }

    private fun getTitle(routerState: RouterState<*, RootComponent.Child>): LocalizedString =
        when (routerState.activeChild.instance) {
            is RootComponent.Child.Menu -> LocalizedString.resource(R.string.app_name)
            is RootComponent.Child.Counter -> LocalizedString.resource(R.string.counter_title)
            is RootComponent.Child.Dialogs -> LocalizedString.resource(R.string.dialogs_title)
            is RootComponent.Child.Profile -> LocalizedString.resource(R.string.profile_title)
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
    }
}

