package me.aartikov.androidarchitecture.movies.domain

data class Movie(
    val id: Int,
    val title: String = "test title",
    val overview: String = "test overview"
)