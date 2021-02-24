package me.aartikov.androidarchitecture.movies.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.androidarchitecture.movies.data.MoviesGateway
import me.aartikov.androidarchitecture.movies.domain.Movie
import me.aartikov.lib.loading.paged.PagedLoading
import me.aartikov.lib.loading.paged.handleErrors
import me.aartikov.lib.loading.paged.refresh
import me.aartikov.lib.property.stateFromFlow
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
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
        moviesLoading.attach(viewModelScope)
        moviesLoading.refresh()
    }

    fun onPullToRefresh() = moviesLoading.refresh()

    fun onRetryClicked() = moviesLoading.refresh()

    fun onLoadMore() {
        moviesLoading.loadMore()
    }
}
