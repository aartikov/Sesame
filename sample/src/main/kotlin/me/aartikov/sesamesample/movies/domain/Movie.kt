package me.aartikov.sesamesample.movies.domain

data class Movie(
    val id: Int,
    val title: String = "Movie ${id + 1}",
    val overview: String = "Some overview"
)