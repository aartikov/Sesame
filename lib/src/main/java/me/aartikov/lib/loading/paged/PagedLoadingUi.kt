package me.aartikov.lib.loading.paged

import me.aartikov.lib.loading.paged.PagedLoading.State


data class PagedLoadingUiState<T>(
    val data: List<T>,
    val error: Throwable?,
    val emptyVisible: Boolean,
    val loadingVisible: Boolean,
    val refreshVisible: Boolean,
    val loadMoreVisible: Boolean,
    val loadMoreEnabled: Boolean
)

val <T> State<T>.uiState: PagedLoadingUiState<T>
    get() = PagedLoadingUiState(
        data = when (this) {
            is State.Data -> this.data
            is State.Refresh -> this.data
            is State.LoadingMore -> this.data
            is State.FullData -> this.data
            else -> emptyList()
        },
        error = when (this) {
            is State.EmptyError -> this.throwable
            else -> null
        },
        emptyVisible = this is State.Empty,
        loadingVisible = this is State.EmptyLoading,
        refreshVisible = this is State.Refresh,
        loadMoreVisible = this is State.LoadingMore,
        loadMoreEnabled = this is State.Data
    )

fun <T> PagedLoadingUiState<T>.setToView(
    setData: (List<T>) -> Unit = {},
    setDataVisible: (Boolean) -> Unit = {},
    setError: (Throwable) -> Unit = {},
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