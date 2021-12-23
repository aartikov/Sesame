package me.aartikov.sesamecomposesample.movies.data

import me.aartikov.sesamecomposesample.movies.domain.Movie

interface MoviesGateway {

    suspend fun loadMovies(offset: Int, limit: Int): List<Movie>
}