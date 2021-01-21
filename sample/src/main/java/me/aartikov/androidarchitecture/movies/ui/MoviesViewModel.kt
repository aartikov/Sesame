package me.aartikov.androidarchitecture.movies.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.movies.data.MoviesGateway
import me.aartikov.androidarchitecture.movies.domain.Movie
import me.aartikov.lib.core.loading.paged.PagedLoading
import me.aartikov.lib.core.loading.paged.handleErrors
import me.aartikov.lib.core.loading.paged.startIn
import me.aartikov.lib.property.stateFromFlow


class MoviesViewModel @ViewModelInject constructor(
    private val moviesGateway: MoviesGateway
) : BaseViewModel() {

    private val moviesLoading = PagedLoading<Movie>(
        loadPage = { moviesGateway.getMovies(it.loadedPageCount) }
    )

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
