package me.aartikov.sesamecomposesample.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.dialog.dataOrNull

@Composable
fun <T : Any, R : Any> ShowDialog(
    dialogControl: DialogControl<T, R>,
    dialog: @Composable (data: T) -> Unit
) {
    val state by dialogControl.stateFlow.collectAsState()
    state.dataOrNull?.let { data ->
        dialog(data)
    }
}