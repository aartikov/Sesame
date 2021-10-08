package me.aartikov.sesamecomposesample.movies.ui

import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesame.loading.paged.handleErrors
import me.aartikov.sesame.loading.paged.refresh
import me.aartikov.sesamecomposesample.movies.domain.LoadMoviesPageInteractor
import me.aartikov.sesamecomposesample.movies.domain.Movie
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope
import me.aartikov.sesamecomposesample.utils.toComposeState

class RealMoviesComponent(
    componentContext: ComponentContext,
    private val loadMoviesPageInteractor: LoadMoviesPageInteractor
) : ComponentContext by componentContext, MoviesComponent {

    private val coroutineScope = componentCoroutineScope()

    private val moviesLoading = PagedLoading<Movie>(
        coroutineScope,
        loadPage = { loadMoviesPageInteractor.execute(it.loadedData.size) }
    )

    override val moviesState by moviesLoading.stateFlow.toComposeState(coroutineScope)

    init {
        moviesLoading.handleErrors(coroutineScope) { error ->
            if (error.hasData) {
                // TODO: Show toast
            }
        }

        moviesLoading.refresh()
    }

    override fun onPullToRefresh() {
        moviesLoading.refresh()
    }

    override fun onRetryClicked() {
        moviesLoading.refresh()
    }

    override fun onLoadMore() {
        moviesLoading.loadMore()
    }
}