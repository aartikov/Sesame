package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import me.aartikov.lib.state_machine.EffectHandler
import me.aartikov.lib.loading.simple.FlowLoader
import me.aartikov.lib.loading.simple.dataIsEmpty

internal class FlowLoadingEffectHandler<T : Any>(private val loader: FlowLoader<T>) :
    EffectHandler<Effect, Action<T>> {

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.Load -> load(effect.fresh, actionConsumer)
            is Effect.Refresh -> refresh()
        }
    }

    private suspend fun load(fresh: Boolean, actionConsumer: (Action<T>) -> Unit) {
        try {
            loader.load(fresh).collect { event ->
                actionConsumer(eventToAction(event))
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                throw RuntimeException("Invalid implementation of FlowLoader. Load can't throw exceptions.", e)
            }
        }
    }

    private suspend fun refresh() {
        try {
            loader.refresh()
        } catch (e: Exception) {
            if (e !is CancellationException) {
                throw RuntimeException("Invalid implementation of FlowLoader. Refresh can't throw exceptions.", e)
            }
        }
    }

    private fun eventToAction(event: FlowLoader.Event<T>) = when (event) {
        is FlowLoader.Event.Loading -> Action.LoadingStarted

        is FlowLoader.Event.Data -> when (event.origin) {
            FlowLoader.Origin.FRESH -> when {
                dataIsEmpty(event.data) -> Action.FreshEmptyData
                else -> Action.FreshData(event.data)
            }
            FlowLoader.Origin.CACHE -> when {
                dataIsEmpty(event.data) -> Action.CachedEmptyData
                else -> Action.CachedData(event.data)
            }
        }

        is FlowLoader.Event.Error -> when (event.origin) {
            FlowLoader.Origin.FRESH -> Action.LoadingError(event.throwable)
            FlowLoader.Origin.CACHE -> Action.CacheError(event.throwable)
        }

        is FlowLoader.Event.DataRemoved -> Action.CachedEmptyData
    }
}