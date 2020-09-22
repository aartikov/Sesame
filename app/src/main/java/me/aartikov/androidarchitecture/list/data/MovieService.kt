package me.aartikov.androidarchitecture.list.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.aartikov.androidarchitecture.list.domain.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieService @Inject constructor() {

    suspend fun getMovies(page: Int): List<Movie> = withContext(Dispatchers.IO) {
        delay(1000L)
        if ((page + 1) % 3 != 0)
            generateMovies(page)
        else
            throw RuntimeException("No internet connection")
    }

    private fun generateMovies(page: Int): List<Movie> =
        (page*10 until (page + 1)*10).map { Movie(id = it) }
}