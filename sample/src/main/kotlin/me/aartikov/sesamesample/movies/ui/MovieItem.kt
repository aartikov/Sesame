package me.aartikov.sesamesample.movies.ui

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.databinding.ItemMovieBinding
import me.aartikov.sesamesample.movies.domain.Movie


data class MovieItem(private val movie: Movie) : BindableItem<ItemMovieBinding>() {

    override fun getLayout() = R.layout.item_movie

    override fun initializeViewBinding(view: View): ItemMovieBinding {
        return ItemMovieBinding.bind(view)
    }

    override fun getId(): Long = movie.id.toLong()

    override fun bind(binding: ItemMovieBinding, position: Int) = with(binding) {
        title.text = movie.title
        overview.text = movie.overview
    }
}

fun List<Movie>.toGroupieItems(): List<MovieItem> = this.map { MovieItem(it) }