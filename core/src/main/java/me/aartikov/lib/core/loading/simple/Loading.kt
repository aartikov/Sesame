package me.aartikov.lib.core.loading.simple

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface Loading<T : Any> {

    sealed class State<out T> {
        object Empty : State<Nothing>()
        object Loading : State<Nothing>()
        data class Error(val throwable: Throwable) : State<Nothing>()
        data class Data<T>(val data: T, val refreshing: Boolean = false) : State<T>()
    }

    sealed class Event {
        data class Error(val throwable: Throwable, val hasData: Boolean) : Event()
    }

    val stateFlow: StateFlow<State<T>>

    val eventFlow: Flow<Event>

    suspend fun start(fresh: Boolean = true)

    fun refresh()

    fun restart(fresh: Boolean = true)
}

val <T : Any> Loading<T>.state: Loading.State<T> get() = stateFlow.value

fun <T : Any> Loading<T>.startIn(scope: CoroutineScope, fresh: Boolean = true): Job {
    return scope.launch {
        start(fresh)
    }
}

fun <T : Any> Loading<T>.handleErrors(
    scope: CoroutineScope,
    handler: (Loading.Event.Error) -> Unit
): Job {
    return eventFlow.filterIsInstance<Loading.Event.Error>()
        .onEach {
            handler(it)
        }
        .launchIn(scope)
}