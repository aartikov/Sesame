package me.aartikov.sesame.loading.paged.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import me.aartikov.sesame.loading.paged.DataMerger
import me.aartikov.sesame.loading.paged.PagedLoader
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.PagedLoading.Event
import me.aartikov.sesame.loading.paged.PagedLoading.State
import me.aartikov.sesame.loop.startIn

internal class PagedLoadingImpl<T : Any>(
    scope: CoroutineScope,
    loader: PagedLoader<T>,
    initialState: State<T>,
    dataMerger: DataMerger<T>
) : PagedLoading<T> {

    private val mutableEventFlow = MutableSharedFlow<Event<T>>(
        extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val loop: PagedLoadingLoop<T> = PagedLoadingLoop(
        initialState = initialState,
        reducer = PagedLoadingReducer(dataMerger),
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
        loop.startIn(scope)
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

    override fun mutateData(transform: (List<T>) -> List<T>) {
        loop.dispatch(Action.MutateData(transform))
    }
}