package me.aartikov.sesamecomposesample.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.toComposeState(coroutineScope: CoroutineScope, startState: T = this.value): State<T> {
    val profileState: MutableState<T> = mutableStateOf(startState)
    coroutineScope.launch {
        this@toComposeState.collect {
            profileState.value = it
        }
    }
    return profileState
}