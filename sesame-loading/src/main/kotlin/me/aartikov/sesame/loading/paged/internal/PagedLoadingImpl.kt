package me.aartikov.sesame.loading.paged.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.paged.PagedLoader
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.PagedLoading.Event
import me.aartikov.sesame.loading.paged.PagedLoading.State

internal class PagedLoadingImpl<T : Any>(
    scope: CoroutineScope,
    loader: PagedLoader<T>,
    initialState: State<T>
) : PagedLoading<T> {

    private val mutableEventFlow = MutableSharedFlow<Event<T>>(
        extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loop: PagedLoadingLoop<T> = PagedLoadingLoop(
        initialState = initialState,
        reducer = PagedLoadingReducer(),
        effectHandlers = listOf(
            PagedLoadingEffectHandler(loader),
            EventEffectHandler { event -> mutableEventFlow.tryEmit(event) }
        )
    )

    override val stateFlow: StateFlow<State<T>>
        get() = loop.stateFlow

    override val eventFlow: Flow<Event<T>>
        get() = mutableEventFlow

    init {
        scope.launch {
            loop.start()
        }
    }

    override fun loadFirstPage(fresh: Boolean, reset: Boolean) {
        loop.dispatch(Action.LoadFirstPage(fresh, reset))
    }

    override fun loadMore() {
        loop.dispatch(Action.LoadMore)
    }

    override fun cancel(reset: Boolean) {
        loop.dispatch(Action.Cancel(reset))
    }
}