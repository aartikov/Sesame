package me.aartikov.lib.loading.simple

import me.aartikov.lib.loading.simple.Loading.State

data class LoadingUiState<T>(
    val data: T?,
    val error: Throwable?,
    val emptyVisible: Boolean,
    val loadingVisible: Boolean,
    val refreshVisible: Boolean,
    val refreshEnabled: Boolean
)

val <T> State<T>.uiState: LoadingUiState<T>
    get() = LoadingUiState(
        data = when (this) {
            is State.Data -> this.data
            is State.Refresh -> this.data
            else -> null
        },
        error = when (this) {
            is State.EmptyError -> this.throwable
            else -> null
        },
        emptyVisible = this is State.Empty,
        loadingVisible = this is State.EmptyLoading,
        refreshVisible = this is State.Refresh,
        refreshEnabled = this is State.Data || this is State.Refresh
    )

fun <T> LoadingUiState<T>.setToView(
    setData: (T) -> Unit = {},
    setDataVisible: (Boolean) -> Unit = {},
    setError: (Throwable) -> Unit = {},
    setErrorVisible: (Boolean) -> Unit = {},
    setEmptyVisible: (Boolean) -> Unit = {},
    setLoadingVisible: (Boolean) -> Unit = {},
    setRefreshVisible: (Boolean) -> Unit = {},
    setRefreshEnabled: (Boolean) -> Unit = {}
) {

    if (data != null) {
        setDataVisible(true)
        setData(data)
    } else {
        setDataVisible(false)
    }

    if (error != null) {
        setErrorVisible(true)
        setError(error)
    } else {
        setErrorVisible(false)
    }

    setLoadingVisible(loadingVisible)
    setRefreshVisible(refreshVisible)
    setRefreshEnabled(refreshEnabled)
}