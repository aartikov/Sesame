package me.aartikov.sesame.loading.simple.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.isEmpty
import me.aartikov.sesame.loop.EffectHandler
import java.util.concurrent.CancellationException

internal class LoadingEffectHandler<T : Any>(private val load: suspend (fresh: Boolean) -> T?) :
    EffectHandler<Effect<T>, Action<T>> {

    private var job: Job? = null

    override suspend fun handleEffect(effect: Effect<T>, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.Load -> loadData(effect.fresh, actionConsumer)
            is Effect.CancelLoading -> cancelLoading()
        }
    }

    private suspend fun loadData(
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

    private fun cancelLoading() {
        job?.cancel()
        job = null
    }
}