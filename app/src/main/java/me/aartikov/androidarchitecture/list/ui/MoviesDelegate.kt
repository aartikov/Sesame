package me.aartikov.androidarchitecture.list.ui

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_movie.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.list.domain.Movie

fun movieAdapterDelegate() = adapterDelegateLayoutContainer<Movie, Movie>(R.layout.item_movie) {

    bind { movies ->
        title.text = item.title
        overview.text = item.overview

    }
}