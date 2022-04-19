package me.aartikov.sesamecomposesample.features.root.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.Children
import com.arkivanov.decompose.router.RouterState
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.core.theme.AppTheme
import me.aartikov.sesamecomposesample.core.utils.createFakeRouterState
import me.aartikov.sesamecomposesample.core.utils.resolve
import me.aartikov.sesamecomposesample.features.counter.ui.CounterUi
import me.aartikov.sesamecomposesample.features.dialogs.ui.DialogsUi
import me.aartikov.sesamecomposesample.features.form.ui.FormUi
import me.aartikov.sesamecomposesample.features.menu.ui.FakeMenuComponent
import me.aartikov.sesamecomposesample.features.menu.ui.MenuUi
import me.aartikov.sesamecomposesample.features.movies.ui.MoviesUi
import me.aartikov.sesamecomposesample.features.profile.ui.ProfileUi

@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBar(component.title)
        }
    ) {
        Content(component.routerState)
    }
}

@Composable
private fun TopBar(title: LocalizedString) {
    TopAppBar(
        title = {
            Text(title.resolve())
        }
    )
}

@Composable
private fun Content(routerState: RouterState<*, RootComponent.Child>) {
    Children(routerState) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Menu -> MenuUi(instance.component)
            is RootComponent.Child.Counter -> CounterUi(instance.component)
            is RootComponent.Child.Dialogs -> DialogsUi(instance.component)
            is RootComponent.Child.Profile -> ProfileUi(instance.component)
            is RootComponent.Child.Movies -> MoviesUi(instance.component)
            is RootComponent.Child.Form -> FormUi(instance.component)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RootUiPreview() {
    AppTheme {
        RootUi(FakeRootComponent())
    }
}

class FakeRootComponent : RootComponent {

    override val routerState = createFakeRouterState(
        RootComponent.Child.Menu(FakeMenuComponent())
    )

    override val title = LocalizedString.raw("Sesame compose sample")
}