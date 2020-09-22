package me.aartikov.androidarchitecture.list.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.list.domain.Movie
import me.aartikov.lib.data_binding.computed
import me.aartikov.lib.data_binding.stateFromFlow
import me.aartikov.lib.loading.paged.PagedLoading
import me.aartikov.lib.loading.paged.handleErrors
import me.aartikov.lib.loading.paged.startIn
import me.aartikov.lib.loading.paged.uiState

class ListViewModel @ViewModelInject constructor(
    private val moviesLoading: PagedLoading<Movie>
) : BaseViewModel() {

    private val moviesState by stateFromFlow(moviesLoading.stateFlow)
    val moviesUiState = moviesState.uiState

    init {
        moviesLoading.handleErrors(viewModelScope) {error ->
            if (error.hasData)
                showError(error.throwable)
        }
        moviesLoading.startIn(viewModelScope)
    }


    fun onPullToRefresh() = moviesLoading.refresh()

    fun onLoadMore() {
        if (moviesUiState.loadMoreEnabled)
            moviesLoading.loadMore()
    }


}