package me.aartikov.sesamecomposesample.features.form.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesamecomposesample.core.utils.resolve

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckboxField(
    modifier: Modifier = Modifier,
    checkControl: CheckControl,
    label: String
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(modifier = modifier.fillMaxWidth()) {

        LaunchedEffect(key1 = checkControl) {
            checkControl.scrollToItEvent.collectLatest {
                bringIntoViewRequester.bringIntoView()
            }
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkControl.checked,
                onCheckedChange = { checkControl.onCheckedChanged(it) },
                enabled = checkControl.enabled
            )

            Text(text = label)
        }

        ErrorText(
            checkControl.error?.resolve() ?: "",
            paddingValues = PaddingValues(horizontal = 16.dp)
        )
    }
}