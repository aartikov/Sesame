package me.aartikov.sesamecomposesample.features.movies.domain

import me.aartikov.sesame.loading.paged.Page
import me.aartikov.sesamecomposesample.features.movies.data.MoviesGateway

class LoadMoviesPageInteractor(
    private val moviesGateway: MoviesGateway
) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    suspend fun execute(offset: Int): Page<Movie> {
        val movies = moviesGateway.loadMovies(offset, PAGE_SIZE)
        return Page(movies, hasNextPage = movies.size >= PAGE_SIZE)
    }
}