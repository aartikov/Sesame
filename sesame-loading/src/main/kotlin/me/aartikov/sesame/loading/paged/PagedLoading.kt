package me.aartikov.sesame.loading.paged

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.loading.paged.internal.PagedLoadingImpl

/**
 * Data loader for [PagedLoading].
 */
interface PagedLoader<T : Any> {
    /**
     * Loads a first page.
     * @param fresh indicates that fresh data is required.
     * @return data for a first page. Empty list means missing/empty data.
     */
    suspend fun loadFirstPage(fresh: Boolean): List<T>

    /**
     * Loads the next page.
     * @param pagingInfo information about the already loaded pages. See: [PagingInfo].
     * @return data for the next page. Empty list means that the end of data is reached.
     */
    suspend fun loadNextPage(pagingInfo: PagingInfo<T>): List<T>
}

/**
 * Helps to load paged data and manage loading state.
 */
interface PagedLoading<T : Any> {

    /**
     * Loading state.
     */
    sealed class State<out T> {
        /**
         * Empty list is loaded or loading is not started yet.
         */
        object Empty : State<Nothing>()

        /**
         * Loading is in progress and there is no previously loaded data.
         */
        object Loading : State<Nothing>()

        /**
         * Loading error has occurred and there is no previously loaded data.
         */
        data class Error(val throwable: Throwable) : State<Nothing>()

        /**
         * Non-empty list has been loaded.
         * @property pageCount count of loaded pages.
         * @property data not empty list, sequentially merged data of the all loaded pages.
         * @property status see: [DataStatus].
         */
        data class Data<T>(
            val pageCount: Int,
            val data: List<T>,
            val status: DataStatus = DataStatus.Normal
        ) : State<T>() {
            val loadMoreEnabled: Boolean get() = status == DataStatus.Normal
            val refreshing: Boolean get() = status == DataStatus.Refreshing
            val loadingMore: Boolean get() = status == DataStatus.LoadingMore
            val fullData: Boolean get() = status == DataStatus.FullData
        }
    }

    enum class DataStatus {
        /**
         * Just a data, there is no in progress loading, the end of a list is not reached.
         */
        Normal,

        /**
         * First page reloading is in progress.
         */
        Refreshing,

        /**
         * Loading of a next page is in progress.
         */
        LoadingMore,

        /**
         * The end of a list is reached.
         */
        FullData
    }

    /**
     * Loading event.
     */
    sealed class Event<out T> {
        /**
         * An error occurred. [stateDuringLoading] a state that was during the failed loading.
         */
        data class Error<T>(val throwable: Throwable, val stateDuringLoading: State<T>) : Event<T>() {

            /**
             * Is true when there is previously loaded data. It is useful to not show an error dialog when a fullscreen error is already shown.
             */
            val hasData get() = stateDuringLoading is State.Data
        }
    }

    /**
     * Flow of loading states.
     */
    val stateFlow: StateFlow<State<T>>

    /**
     * Flow of loading events.
     */
    val eventFlow: Flow<Event<T>>

    /**
     * Initializes a [PagedLoading] object by providing a [CoroutineScope] to work in. Should be called once.
     */
    fun attach(scope: CoroutineScope): Job

    /**
     * Requests to load a first page.
     * @param fresh indicates that fresh data is required. See [PagedLoader.loadFirstPage].
     * @param reset if true than previously loaded data will be instantly dropped and in progress loading will be canceled.
     * Otherwise previously loaded data will be preserved until successful outcome, if another loadFirstPage request is in progress
     * than new one will be ignored, if loadMore request is in progress than it will be canceled.
     */
    fun loadFirstPage(fresh: Boolean, reset: Boolean = false)

    /**
     * Requests to load the next page. Loaded data will be added to the end of a previously loaded list.
     * The request will be ignored if another one (loadMore or loadFirstPage) is already in progress.
     */
    fun loadMore()

    /**
     * Requests to cancel in progress loading.
     * @param reset if true than state will be reset to [PagedLoading.State.Empty].
     */
    fun cancel(reset: Boolean = false)
}

/**
 * A shortcut for loadFirstPage(fresh = true, reset = false). Requests to load a fresh first page and preserve the old data until successful outcome.
 */
fun <T : Any> PagedLoading<T>.refresh() = loadFirstPage(fresh = true, reset = false)

/**
 * A shortcut for loadFirstPage(fresh, reset = true). Requests to drop old data and load a first page.
 * @param fresh indicates that fresh data is required. See [PagedLoader.loadFirstPage].
 */
fun <T : Any> PagedLoading<T>.restart(fresh: Boolean = true) = loadFirstPage(fresh, reset = true)

/**
 * A shortcut for cancel(reset = true). Cancels loading and sets state to [PagedLoading.State.Empty].
 */
fun <T : Any> PagedLoading<T>.reset() = cancel(reset = true)

/**
 * Returns current [PagedLoading.State].
 */
val <T : Any> PagedLoading<T>.state: PagedLoading.State<T> get() = stateFlow.value

/**
 * Returns [PagedLoading.State.Data.data] if it is available or null otherwise
 */
val <T : Any> PagedLoading<T>.dataOrNull: List<T>? get() = state.dataOrNull

/**
 * Returns [PagedLoading.State.Error.throwable] if it is available or null otherwise
 */
val PagedLoading<*>.errorOrNull: Throwable? get() = state.errorOrNull

/**
 * Returns [PagedLoading.State.Data.data] if it is available or null otherwise
 */
val <T : Any> PagedLoading.State<T>.dataOrNull: List<T>? get() = (this as? PagedLoading.State.Data)?.data

/**
 * Returns [PagedLoading.State.Error.throwable] if it is available or null otherwise
 */
val PagedLoading.State<*>.errorOrNull: Throwable? get() = (this as? PagedLoading.State.Error)?.throwable

/**
 * A helper method to handle [PagedLoading.Event.Error].
 */
fun <T : Any> PagedLoading<T>.handleErrors(
    scope: CoroutineScope,
    handler: (PagedLoading.Event.Error<T>) -> Unit
): Job {
    return eventFlow.filterIsInstance<PagedLoading.Event.Error<T>>()
        .onEach {
            handler(it)
        }
        .launchIn(scope)
}

/**
 * Creates an implementation of [PagedLoading].
 */
fun <T : Any> PagedLoading(
    loader: PagedLoader<T>,
    initialState: PagedLoading.State<T> = PagedLoading.State.Empty
): PagedLoading<T> {
    return PagedLoadingImpl(loader, initialState)
}

/**
 * Creates an implementation of [PagedLoading].
 */
fun <T : Any> PagedLoading(
    loadFirstPage: suspend (fresh: Boolean) -> List<T>,
    loadNextPage: suspend (pagingInfo: PagingInfo<T>) -> List<T>,
    initialState: PagedLoading.State<T> = PagedLoading.State.Empty
): PagedLoading<T> {
    val loader = object : PagedLoader<T> {
        override suspend fun loadFirstPage(fresh: Boolean): List<T> = loadFirstPage(fresh)

        override suspend fun loadNextPage(pagingInfo: PagingInfo<T>): List<T> = loadNextPage(pagingInfo)
    }
    return PagedLoading(loader, initialState)
}

/**
 * Creates an implementation of [PagedLoading].
 */
fun <T : Any> PagedLoading(
    loadFirstPage: suspend () -> List<T>,
    loadNextPage: suspend (pagingInfo: PagingInfo<T>) -> List<T>,
    initialState: PagedLoading.State<T> = PagedLoading.State.Empty
): PagedLoading<T> {
    val loader = object : PagedLoader<T> {
        override suspend fun loadFirstPage(fresh: Boolean): List<T> = loadFirstPage()

        override suspend fun loadNextPage(pagingInfo: PagingInfo<T>): List<T> = loadNextPage(pagingInfo)
    }
    return PagedLoading(loader, initialState)
}

/**
 * Creates an implementation of [PagedLoading].
 */
fun <T : Any> PagedLoading(
    loadPage: suspend (pagingInfo: PagingInfo<T>) -> List<T>,
    initialState: PagedLoading.State<T> = PagedLoading.State.Empty
): PagedLoading<T> {
    val loader = object : PagedLoader<T> {
        override suspend fun loadFirstPage(fresh: Boolean): List<T> = loadPage(PagingInfo(0, emptyList<T>()))

        override suspend fun loadNextPage(pagingInfo: PagingInfo<T>): List<T> = loadPage(pagingInfo)
    }
    return PagedLoading(loader, initialState)
}