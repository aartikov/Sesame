package me.aartikov.lib.loading.simple.internal

import me.aartikov.lib.state_machine.EffectHandler
import me.aartikov.lib.loading.simple.OrdinaryLoader
import me.aartikov.lib.loading.simple.dataIsEmpty

internal class OrdinaryLoadingEffectHandler<T : Any>(private val loader: OrdinaryLoader<T>) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.Load -> load(effect.fresh, actionConsumer)
            is Effect.Refresh -> load(fresh = true, actionConsumer)
        }
    }

    private suspend fun load(fresh: Boolean, actionConsumer: (Action<T>) -> Unit) {
        try {
            actionConsumer(Action.LoadingStarted)
            val data = loader.load(fresh)
            if (dataIsEmpty(data)) {
                actionConsumer(Action.FreshEmptyData)
            } else {
                actionConsumer(Action.FreshData(data))
            }
        } catch (e: Exception) {
            actionConsumer(Action.LoadingError(e))
        }
    }
}