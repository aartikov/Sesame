package me.aartikov.lib.loading.paged.internal

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.lib.loading.paged.PagedLoader
import me.aartikov.lib.loading.paged.PagedLoading
import me.aartikov.lib.loading.paged.PagedLoading.Event
import me.aartikov.lib.loading.paged.PagedLoading.State

internal class PagedLoadingImpl<T : Any>(
    loader: PagedLoader<T>,
    initialState: State<T>
) : PagedLoading<T> {

    private val mutableStateFlow = MutableStateFlow(initialState)

    private val mutableEventFlow = MutableSharedFlow<PagedLoading.Event>(
        extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loop: PagedLoadingLoop<T> = PagedLoadingLoop(
        initialState = initialState.toInternalState(),
        reducer = PagedLoadingReducer(),
        actionSources = emptyList(),
        effectHandlers = listOf(
            PagedLoadingEffectHandler(loader),
            EventEffectHandler { event -> mutableEventFlow.tryEmit(event) }
        )
    )

    override val stateFlow: StateFlow<State<T>>
        get() = mutableStateFlow

    override val eventFlow: Flow<Event>
        get() = mutableEventFlow

    override suspend fun start(fresh: Boolean) = coroutineScope {
        launch {
            loop.stateFlow.collect {
                mutableStateFlow.value = it.toPublicState()
            }
        }
        loop.dispatch(Action.Load(fresh))
        loop.start()
    }

    override fun refresh() {
        loop.dispatch(Action.Refresh)
    }

    override fun loadMore() {
        loop.dispatch(Action.LoadMore)
    }

    override fun restart(fresh: Boolean) {
        loop.dispatch(Action.Restart(fresh))
    }
}