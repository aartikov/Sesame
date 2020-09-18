package me.aartikov.androidarchitecture.list.ui

data class MovieListItem(
    val title: String,
    val id: Int,
    val overview: String,
    val posterUrl: String,
    val releaseDate: String,
    val voteAverage: Double
)