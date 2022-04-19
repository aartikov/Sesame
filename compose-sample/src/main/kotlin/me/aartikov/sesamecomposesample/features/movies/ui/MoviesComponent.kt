package me.aartikov.sesamecomposesample.features.movies.ui

import me.aartikov.sesame.loading.paged.PagedLoading
import me.aartikov.sesamecomposesample.features.movies.domain.Movie

interface MoviesComponent {

    val moviesState: PagedLoading.State<Movie>

    fun onPullToRefresh()

    fun onRetryClicked()

    fun onLoadMore()
}