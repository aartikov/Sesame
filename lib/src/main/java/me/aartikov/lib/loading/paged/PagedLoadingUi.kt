package me.aartikov.lib.loading.paged

import me.aartikov.lib.loading.paged.PagedLoading.State

data class PagedLoadingUiState<D, E>(
    val data: List<D>,
    val error: E?,
    val emptyVisible: Boolean,
    val loadingVisible: Boolean,
    val refreshVisible: Boolean,
    val loadMoreVisible: Boolean,
    val loadMoreEnabled: Boolean
)

fun <T, D, E> State<T>.toUiState(
    dataMapper: (List<T>) -> List<D>,
    errorMapper: (Throwable) -> E
): PagedLoadingUiState<D, E> = PagedLoadingUiState(
    data = when (this) {
        is State.Data -> dataMapper(this.data)
        is State.Refresh -> dataMapper(this.data)
        is State.LoadingMore -> dataMapper(this.data)
        is State.FullData -> dataMapper(this.data)
        else -> emptyList()
    },
    error = when (this) {
        is State.EmptyError -> errorMapper(this.throwable)
        else -> null
    },
    emptyVisible = this is State.Empty,
    loadingVisible = this is State.EmptyLoading,
    refreshVisible = this is State.Refresh,
    loadMoreVisible = this is State.LoadingMore,
    loadMoreEnabled = this is State.Data
)

fun <T, D> State<T>.toUiState(dataMapper: (List<T>) -> List<D>): PagedLoadingUiState<D, Throwable> =
    toUiState(dataMapper, { it })

fun <T> State<T>.toUiState(): PagedLoadingUiState<T, Throwable> = toUiState({ it }, { it })

fun <D, E> PagedLoadingUiState<D, E>.setToView(
    setData: (List<D>) -> Unit = {},
    setDataVisible: (Boolean) -> Unit = {},
    setError: (E) -> Unit = {},
    setErrorVisible: (Boolean) -> Unit = {},
    setEmptyVisible: (Boolean) -> Unit = {},
    setLoadingVisible: (Boolean) -> Unit = {},
    setLoadMoreVisible: (Boolean) -> Unit = {},
    setLoadMoreEnabled: (Boolean) -> Unit = {}
) {
    setData(data)
    setDataVisible(data.isNotEmpty())

    if (error != null) {
        setErrorVisible(true)
        setError(error)
    } else {
        setErrorVisible(false)
    }

    setEmptyVisible(emptyVisible)
    setLoadingVisible(loadingVisible)
    setLoadMoreVisible(loadMoreVisible)
    setLoadMoreEnabled(loadMoreEnabled)
}