package me.aartikov.sesame.loading.simple.internal

import kotlinx.coroutines.flow.Flow
import me.aartikov.sesame.loading.simple.isEmpty
import me.aartikov.sesame.loop.ActionSource

internal class LoadingActionSource<T : Any>(private val dataFlow: Flow<T?>) : ActionSource<Action<T>> {

    override suspend fun start(actionConsumer: (Action<T>) -> Unit) {
        dataFlow.collect { data ->
            if (data == null || isEmpty(data)) {
                actionConsumer(Action.EmptyDataObserved)
            } else {
                actionConsumer(Action.DataObserved(data))
            }
        }
    }
}