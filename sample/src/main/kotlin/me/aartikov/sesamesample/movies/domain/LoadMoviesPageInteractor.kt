package me.aartikov.sesamesample.movies.domain

import me.aartikov.sesamesample.movies.data.MoviesGateway
import javax.inject.Inject

class LoadMoviesPageInteractor @Inject constructor(
    private val moviesGateway: MoviesGateway
) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    suspend fun execute(offset: Int): List<Movie> {
        return moviesGateway.loadMovies(offset, PAGE_SIZE)
    }
}