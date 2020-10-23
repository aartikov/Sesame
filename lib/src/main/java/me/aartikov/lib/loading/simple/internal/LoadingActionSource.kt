package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import me.aartikov.lib.loading.simple.dataIsEmpty
import me.aartikov.lib.state_machine.ActionSource

internal class LoadingActionSource<T : Any>(private val dataFlow: Flow<T>) : ActionSource<Action<T>> {

    override suspend fun start(actionConsumer: (Action<T>) -> Unit) {
        dataFlow.collect { data ->
            if (dataIsEmpty(data)) {
                actionConsumer(Action.EmptyDataObserved)
            } else {
                actionConsumer(Action.DataObserved(data))
            }
        }
    }
}