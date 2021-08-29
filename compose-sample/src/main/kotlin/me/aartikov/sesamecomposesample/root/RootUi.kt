package me.aartikov.sesamecomposesample.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.extensions.compose.jetpack.Children
import com.arkivanov.decompose.value.Value
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.counter.CounterUi
import me.aartikov.sesamecomposesample.menu.FakeMenuComponent
import me.aartikov.sesamecomposesample.menu.MenuUi
import me.aartikov.sesamecomposesample.theme.AppTheme
import me.aartikov.sesamecomposesample.utils.createFakeRouterStateValue
import me.aartikov.sesamecomposesample.utils.resolve

@Composable
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
private fun Content(routerState: Value<RouterState<*, RootComponent.Child>>) {
    Children(routerState) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Menu -> MenuUi(instance.component)
            is RootComponent.Child.Counter -> CounterUi(instance.component)
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

    override val routerState = createFakeRouterStateValue(
        RootComponent.Child.Menu(FakeMenuComponent())
    )

    override val title = LocalizedString.raw("Sesame compose sample")
}