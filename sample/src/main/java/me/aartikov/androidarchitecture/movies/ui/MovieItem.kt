package me.aartikov.androidarchitecture.movies.ui

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_movie.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.movies.domain.Movie


class MovieItem(private val movie: Movie) : Item() {

    override fun getLayout() = R.layout.item_movie

    override fun getId(): Long = movie.id.toLong()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) = with(viewHolder) {
        id.text = movie.id.toString()
        title.text = movie.title
        overview.text = movie.overview
    }
}

fun List<Movie>.toGroupieItems() : List<MovieItem>
        = this.map { MovieItem(it) }