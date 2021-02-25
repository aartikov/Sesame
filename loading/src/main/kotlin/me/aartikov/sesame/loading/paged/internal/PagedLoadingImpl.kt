package me.aartikov.sesame.loading.paged.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.paged.PagedLoader
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.PagedLoading.Event
import me.aartikov.sesame.loading.paged.PagedLoading.State

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

    override fun attach(scope: CoroutineScope): Job = scope.launch {
        coroutineScope {
            launch {
                loop.stateFlow.collect {
                    mutableStateFlow.value = it.toPublicState()
                }
            }

            launch {
                loop.start()
            }
        }
    }

    override fun loadFirstPage(fresh: Boolean, dropData: Boolean) {
        loop.dispatch(Action.LoadFirstPage(fresh, dropData))
    }

    override fun loadMore() {
        loop.dispatch(Action.LoadMore)
    }
}