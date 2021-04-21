package me.aartikov.sesame.loading.paged.internal

import me.aartikov.sesame.loading.paged.PagedLoading

internal fun <T> State<T>.toPublicState(): PagedLoading.State<T> = when (this) {
    State.Empty -> PagedLoading.State.Empty
    State.Loading -> PagedLoading.State.Loading
    is State.Error -> PagedLoading.State.Error(this.throwable)
    is State.Data -> PagedLoading.State.Data(pageCount, this.data)
    is State.Refresh -> PagedLoading.State.Data(pageCount, this.data, PagedLoading.DataStatus.Refreshing)
    is State.LoadingMore -> PagedLoading.State.Data(pageCount, this.data, PagedLoading.DataStatus.LoadingMore)
    is State.FullData -> PagedLoading.State.Data(pageCount, this.data, PagedLoading.DataStatus.FullData)
}

internal fun <T> PagedLoading.State<T>.toInternalState() = when (this) {
    PagedLoading.State.Empty -> State.Empty
    PagedLoading.State.Loading -> State.Loading
    is PagedLoading.State.Error -> State.Error(this.throwable)
    is PagedLoading.State.Data -> when (this.status) {
        PagedLoading.DataStatus.Normal -> State.Data(pageCount, this.data)
        PagedLoading.DataStatus.Refreshing -> State.Refresh(pageCount, this.data)
        PagedLoading.DataStatus.LoadingMore -> State.LoadingMore(pageCount, this.data)
        PagedLoading.DataStatus.FullData -> State.FullData(pageCount, this.data)
    }
}