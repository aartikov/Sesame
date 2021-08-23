package me.aartikov.sesame.loading.paged.internal

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.paged.PagedLoader
import me.aartikov.sesame.loading.paged.PagingInfo
import me.aartikov.sesame.loop.EffectHandler
import kotlin.coroutines.cancellation.CancellationException

internal class PagedLoadingEffectHandler<T : Any>(private val loader: PagedLoader<T>) :
    EffectHandler<Effect<T>, Action<T>> {

    private var job: Job? = null

    override suspend fun handleEffect(effect: Effect<T>, actionConsumer: (Action<T>) -> Unit) {
        when (effect) {
            is Effect.LoadFirstPage -> loadFirstPage(effect.fresh, actionConsumer)
            is Effect.LoadNextPage -> loadNextPage(effect.pagingInfo, actionConsumer)
            is Effect.CancelLoading -> cancelLoading()
        }
    }

    private suspend fun loadFirstPage(
        fresh: Boolean,
        actionConsumer: (Action<T>) -> Unit
    ) = coroutineScope {
        job?.cancel()
        job = launch {
            try {
                val page = loader.loadFirstPage(fresh)
                if (isActive) {
                    if (page.data.isEmpty()) {
                        actionConsumer(Action.EmptyPageLoaded)
                    } else {
                        actionConsumer(Action.NewPageLoaded(page))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isActive) {
                    actionConsumer(Action.LoadingError(e))
                }
            }
        }
    }

    private suspend fun loadNextPage(
        pagingInfo: PagingInfo<T>,
        actionConsumer: (Action<T>) -> Unit
    ) = coroutineScope {
        job?.cancel()
        job = launch {
            try {
                val page = loader.loadNextPage(pagingInfo)
                if (isActive) {
                    if (page.data.isEmpty()) {
                        actionConsumer(Action.EmptyPageLoaded)
                    } else {
                        actionConsumer(Action.NewPageLoaded(page))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isActive) {
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