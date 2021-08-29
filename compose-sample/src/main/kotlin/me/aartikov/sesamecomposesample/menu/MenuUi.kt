package me.aartikov.sesamecomposesample.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.theme.AppTheme

@Composable
fun MenuUi(
    component: MenuComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            MenuButton(
                text = stringResource(R.string.counter_title),
                onClick = { component.onMenuItemClicked(MenuItem.Counter) }
            )
        }
    }
}

@Preview
@Composable
fun MenuUiPreview() {
    AppTheme {
        MenuUi(FakeMenuComponent())
    }
}

class FakeMenuComponent : MenuComponent {
    override fun onMenuItemClicked(item: MenuItem) {}
}