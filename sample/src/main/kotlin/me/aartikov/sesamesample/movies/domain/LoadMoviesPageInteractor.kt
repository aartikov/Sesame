package me.aartikov.sesamesample.movies.domain

import me.aartikov.sesame.loading.paged.Page
import me.aartikov.sesamesample.movies.data.MoviesGateway
import javax.inject.Inject

class LoadMoviesPageInteractor @Inject constructor(
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