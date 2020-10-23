package me.aartikov.lib.loading.simple.internal

import me.aartikov.lib.loading.simple.dataIsEmpty
import me.aartikov.lib.state_machine.EffectHandler
import java.util.concurrent.CancellationException

internal class LoadingEffectHandler<T : Any>(private val loader: suspend (fresh: Boolean) -> T) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.Load -> load(effect.fresh, actionConsumer)
            is Effect.Refresh -> load(fresh = true, actionConsumer)
        }
    }

    private suspend fun load(fresh: Boolean, actionConsumer: (Action<T>) -> Unit) {
        try {
            val data = loader(fresh)
            if (dataIsEmpty(data)) {
                actionConsumer(Action.EmptyDataLoaded)
            } else {
                actionConsumer(Action.DataLoaded(data))
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                actionConsumer(Action.LoadingError(e))
            }
        }
    }
}