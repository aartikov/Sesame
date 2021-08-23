package me.aartikov.sesamesample.movies.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.handleErrors
import me.aartikov.sesame.loading.paged.refresh
import me.aartikov.sesame.property.stateFromFlow
import me.aartikov.sesamesample.base.BaseViewModel
import me.aartikov.sesamesample.movies.domain.LoadMoviesPageInteractor
import me.aartikov.sesamesample.movies.domain.Movie
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val loadMoviesPageInteractor: LoadMoviesPageInteractor
) : BaseViewModel() {

    private val moviesLoading = PagedLoading<Movie>(
        viewModelScope,
        loadPage = { loadMoviesPageInteractor.execute(it.loadedData.size) }
    )

    val moviesState by stateFromFlow(moviesLoading.stateFlow)

    init {
        moviesLoading.handleErrors(viewModelScope) { error ->
            if (error.hasData)
                showError(error.throwable)
        }

        moviesLoading.refresh()
    }

    fun onPullToRefresh() {
        moviesLoading.refresh()
    }

    fun onRetryClicked() {
        moviesLoading.refresh()
    }

    fun onLoadMore() {
        moviesLoading.loadMore()
    }
}
