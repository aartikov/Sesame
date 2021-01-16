package me.aartikov.lib.loading.simple.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.lib.loading.simple.isEmpty
import me.aartikov.lib.state_machine.EffectHandler
import java.util.concurrent.CancellationException

internal class LoadingEffectHandler<T : Any>(private val loader: suspend (fresh: Boolean) -> T?) :
    EffectHandler<Effect, Action<T>> {

    private var job: Job? = null

    override suspend fun handleEffect(effect: Effect, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.Load -> load(effect.fresh, actionConsumer)
        }
    }

    private suspend fun load(
        fresh: Boolean,
        actionConsumer: (Action<T>) -> Unit
    ) = coroutineScope {
        job?.cancel()
        job = launch {
            try {
                val data = loader(fresh)
                if (data == null || isEmpty(data)) {
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
}