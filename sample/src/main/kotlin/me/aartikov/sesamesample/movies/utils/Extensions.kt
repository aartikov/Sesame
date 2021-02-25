package me.aartikov.sesamesample.movies.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.doOnScrollToEnd(difference: Int = 10, listener: () -> Unit) =
    addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val lastVisibleItem =
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            if (dy > 0 && totalItemCount - lastVisibleItem < difference)
                recyclerView.post { listener() }
        }
    })
