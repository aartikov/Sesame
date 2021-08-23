package me.aartikov.sesamesample.movies.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.aartikov.sesamesample.movies.domain.Movie
import java.lang.Integer.min
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MoviesGateway @Inject constructor() {

    companion object {
        private const val TOTAL_COUNT = 95
    }

    private var counter = 0

    suspend fun loadMovies(offset: Int, limit: Int): List<Movie> = withContext(Dispatchers.IO) {
        delay(1000)
        val success = counter % 4 != 0
        counter++

        if (success)
            generateMovies(offset, limit)
        else
            throw RuntimeException("Emulated failure. Please, try again.")
    }

    private fun generateMovies(offset: Int, limit: Int): List<Movie> {
        return (offset until min(offset + limit, TOTAL_COUNT))
            .map { Movie(id = it) }
    }
}