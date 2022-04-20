package me.aartikov.sesamecomposesample.features.movies.data

import me.aartikov.sesamecomposesample.features.movies.domain.Movie

interface MoviesGateway {

    suspend fun loadMovies(offset: Int, limit: Int): List<Movie>
}