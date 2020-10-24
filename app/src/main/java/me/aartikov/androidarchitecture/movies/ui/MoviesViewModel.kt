package me.aartikov.androidarchitecture.movies.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.movies.domain.Movie
import me.aartikov.lib.data_binding.stateFromFlow
import me.aartikov.lib.loading.paged.PagedLoading
import me.aartikov.lib.loading.paged.handleErrors
import me.aartikov.lib.loading.paged.startIn


class MoviesViewModel @ViewModelInject constructor(
    private val moviesLoading: PagedLoading<Movie>
) : BaseViewModel() {

    val moviesState by stateFromFlow(moviesLoading.stateFlow)

    init {
        moviesLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData)
                showError(error.throwable)
        }
        moviesLoading.startIn(viewModelScope)
    }

    fun onPullToRefresh() = moviesLoading.refresh()

    fun onRetryClicked() = moviesLoading.refresh()

    fun onLoadMore() {
        moviesLoading.loadMore()
    }
}
