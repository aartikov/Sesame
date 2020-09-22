package me.aartikov.androidarchitecture.list.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.doAfterScrollToEnd(layoutManager: LinearLayoutManager, onLoadMore: () -> Unit) =
    addOnScrollListener(object : RecyclerView.OnScrollListener() {

        private val visibleThreshold = 5
        private var previousTotal = 0
        private var isLoading = true

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemCount = recyclerView.childCount
            val totalItemCount = layoutManager.childCount
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (isLoading) {
                if (totalItemCount > previousTotal || totalItemCount == 0) {
                    isLoading = false
                    previousTotal = totalItemCount
                }
            }

            val isNeedLoading = (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)

            if (!isLoading && isNeedLoading)
                recyclerView.post { onLoadMore() }
        }

    })