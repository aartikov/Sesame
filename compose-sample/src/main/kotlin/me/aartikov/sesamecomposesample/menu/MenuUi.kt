package me.aartikov.sesamecomposesample.menu

import androidx.compose.foundation.layout.*
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
        Box(modifier = Modifier.padding(32.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(IntrinsicSize.Max)
            ) {
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.counter_title),
                    onClick = { component.onMenuItemClick(MenuItem.Counter) }
                )
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.profile_title),
                    onClick = { component.onMenuItemClick(MenuItem.Profile) }
                )
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.dialogs_title),
                    onClick = { component.onMenuItemClick(MenuItem.Dialogs) }
                )
                MenuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.movies_title),
                    onClick = { component.onMenuItemClick(MenuItem.Movies) }
                )
            }
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
    override fun onMenuItemClick(item: MenuItem) {}
}