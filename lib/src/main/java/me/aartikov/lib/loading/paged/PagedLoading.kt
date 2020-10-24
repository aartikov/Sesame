package me.aartikov.lib.loading.paged

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.aartikov.lib.loading.paged.internal.PagedLoadingImpl

interface PagedLoader<T : Any> {

    suspend fun loadFirstPage(fresh: Boolean): List<T>

    suspend fun loadNextPage(pagingInfo: PagingInfo<T>): List<T>
}

interface PagedLoading<T : Any> {

    sealed class State<out T> {
        object Empty : State<Nothing>()
        object Loading : State<Nothing>()
        data class Error(val throwable: Throwable) : State<Nothing>()
        data class Data<T>(
            val pageCount: Int,
            val data: List<T>,
            val status: DataStatus = DataStatus.NORMAL
        ) : State<T>() {
            val refreshing: Boolean get() = status == DataStatus.REFRESHING
            val loadingMore: Boolean get() = status == DataStatus.LOADING_MORE
            val fullData: Boolean get() = status == DataStatus.FULL_DATA
        }
    }

    enum class DataStatus {
        NORMAL, REFRESHING, LOADING_MORE, FULL_DATA
    }

    sealed class Event {
        data class Error(val throwable: Throwable, val hasData: Boolean) : Event()
    }

    val stateFlow: StateFlow<State<T>>

    val eventFlow: Flow<Event>

    suspend fun start(fresh: Boolean = true)

    fun refresh()

    fun loadMore()

}

fun <T : Any> PagedLoading(
    loader: PagedLoader<T>,
    initialState: PagedLoading.State<T> = PagedLoading.State.Empty
): PagedLoading<T> {
    return PagedLoadingImpl(loader, initialState)
}

val <T : Any> PagedLoading<T>.state: PagedLoading.State<T> get() = stateFlow.value

fun <T : Any> PagedLoading<T>.startIn(scope: CoroutineScope, fresh: Boolean = true): Job {
    return scope.launch {
        start(fresh)
    }
}

fun <T : Any> PagedLoading<T>.handleErrors(
    scope: CoroutineScope,
    handler: (PagedLoading.Event.Error) -> Unit
): Job {
    return eventFlow.filterIsInstance<PagedLoading.Event.Error>()
        .onEach {
            handler(it)
        }
        .launchIn(scope)
}