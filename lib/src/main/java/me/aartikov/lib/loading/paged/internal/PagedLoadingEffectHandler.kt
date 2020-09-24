package me.aartikov.lib.loading.paged.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.lib.loading.paged.PagedLoader
import me.aartikov.lib.loading.paged.PagingInfo
import me.aartikov.lib.state_machine.EffectHandler
import java.util.concurrent.CancellationException

internal class PagedLoadingEffectHandler<T : Any>(private val loader: PagedLoader<T>) :
    EffectHandler<Effect<T>, Action<T>> {

    private var loadNextPageJob: Job? = null

    override suspend fun handleEffect(effect: Effect<T>, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.LoadFirstPage -> loadFirstPage(effect.fresh, actionConsumer)
            is Effect.LoadNextPage -> loadNextPage(effect.pagingInfo, actionConsumer)
        }
    }

    private suspend fun loadFirstPage(fresh: Boolean, actionConsumer: (Action<T>) -> Unit) {
        try {
            loadNextPageJob?.cancel()
            val data = loader.loadFirstPage(fresh)
            if (data.isEmpty()) {
                actionConsumer(Action.EmptyPage)
            } else {
                actionConsumer(Action.NewPage(data))
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                actionConsumer(Action.Error(e))
            }
        }
    }

    private suspend fun loadNextPage(
        pagingInfo: PagingInfo<T>,
        actionConsumer: (Action<T>) -> Unit
    ) = coroutineScope {
        loadNextPageJob = launch {
            try {
                val data = loader.loadNextPage(pagingInfo)
                if (data.isEmpty()) {
                    actionConsumer(Action.EmptyPage)
                } else {
                    actionConsumer(Action.NewPage(data))
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    actionConsumer(Action.Error(e))
                }
            }
        }
    }
}