package me.aartikov.lib.activable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Converts [Flow] to an activable flow.
 * An activable flow collects the [originalFlow] and emits elements further when the [activable] is active. It stops collecting and emitting when the [activable] is inactive.
 */
fun <T> activableFlow(originalFlow: Flow<T>, activable: Activable, scope: CoroutineScope): Flow<T> {
    val flow = MutableSharedFlow<T>()
    var job: Job? = null

    activable.activeFlow
        .onEach { active ->
            if (active) {
                job = originalFlow
                    .onEach { flow.emit(it) }
                    .launchIn(scope)
            } else {
                job?.cancel()
                job = null
            }
        }
        .launchIn(scope)

    return flow
}