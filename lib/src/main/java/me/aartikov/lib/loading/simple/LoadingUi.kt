package me.aartikov.lib.loading.simple

import me.aartikov.lib.loading.simple.Loading.State

data class LoadingUiState<D, E>(
    val data: D?,
    val error: E?,
    val emptyVisible: Boolean,
    val loadingVisible: Boolean,
    val refreshVisible: Boolean
)

fun <T, D, E> State<T>.toUiState(dataMapper: (T) -> D, errorMapper: (Throwable) -> E): LoadingUiState<D, E> =
    LoadingUiState(
        data = when (this) {
            is State.Data -> dataMapper(this.data)
            is State.Refresh -> dataMapper(this.data)
            else -> null
        },
        error = when (this) {
            is State.EmptyError -> errorMapper(this.throwable)
            else -> null
        },
        emptyVisible = this is State.Empty,
        loadingVisible = this is State.EmptyLoading,
        refreshVisible = this is State.Refresh
    )

fun <T, D> State<T>.toUiState(dataMapper: (T) -> D): LoadingUiState<D, Throwable> = toUiState(dataMapper, { it })

fun <T> State<T>.toUiState(): LoadingUiState<T, Throwable> = toUiState({ it }, { it })

fun <D, E> LoadingUiState<D, E>.setToView(
    setData: (D) -> Unit = {},
    setDataVisible: (Boolean) -> Unit = {},
    setError: (E) -> Unit = {},
    setErrorVisible: (Boolean) -> Unit = {},
    setEmptyVisible: (Boolean) -> Unit = {},
    setLoadingVisible: (Boolean) -> Unit = {},
    setRefreshVisible: (Boolean) -> Unit = {}
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

    setEmptyVisible(emptyVisible)
    setLoadingVisible(loadingVisible)
    setRefreshVisible(refreshVisible)
}