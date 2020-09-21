package me.aartikov.androidarchitecture.list.domain

data class Movie(
    val title: String = "test title",
    val id: Int = 999,
    val overview: String = "test overview",
    val posterUrl: String = "test url",
    val releaseDate: String = "12.12.2012",
    val voteAverage: Double = 9.9
)