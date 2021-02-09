package me.aartikov.androidarchitecture.movies.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.aartikov.androidarchitecture.movies.domain.Movie
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MoviesGateway @Inject constructor() {

    companion object {
        private const val PAGE_VOLUME = 20
        private const val DELAY_MS = 2000L
    }

    private var counter = 0

    suspend fun getMovies(page: Int): List<Movie> = withContext(Dispatchers.IO) {
        delay(DELAY_MS)
        val success = counter++ % 2 == 0
        if (success)
            generateMovies(page)
        else
            throw RuntimeException("Emulated failure. Please, try again.")
    }

    private fun generateMovies(page: Int): List<Movie> =
        (page * PAGE_VOLUME until (page + 1) * PAGE_VOLUME).map { Movie(id = it) }
}