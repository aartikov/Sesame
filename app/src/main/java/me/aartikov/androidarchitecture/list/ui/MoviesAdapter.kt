package me.aartikov.androidarchitecture.list.ui

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_movie.*
import me.aartikov.androidarchitecture.R

fun movieAdapterDelegate() = adapterDelegateLayoutContainer<MovieListItem, MovieListItem>(R.layout.item_movie) {

    bind { movies ->
        title.text = item.title
        overview.text = item.overview

    }
}