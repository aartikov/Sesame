package me.aartikov.sesamecomposesample.movies.ui

import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesamecomposesample.movies.domain.Movie

interface MoviesComponent {

    val moviesState: PagedLoading.State<Movie>

    fun onPullToRefresh()

    fun onRetryClicked()

    fun onLoadMore()
}