package me.aartikov.sesamecomposesample.form

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
import me.aartikov.sesamecomposesample.menu.FakeMenuComponent
import me.aartikov.sesamecomposesample.menu.MenuButton
import me.aartikov.sesamecomposesample.menu.MenuComponent
import me.aartikov.sesamecomposesample.menu.MenuItem
import me.aartikov.sesamecomposesample.theme.AppTheme

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
    }
}

@Preview
@Composable
fun FormUiPreview() {
    AppTheme {
        FormUi(FakeFormComponent())
    }
}

class FakeFormComponent : FormComponent