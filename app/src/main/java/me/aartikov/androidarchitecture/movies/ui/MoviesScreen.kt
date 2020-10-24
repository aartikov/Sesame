package me.aartikov.androidarchitecture.movies.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.screen_movies.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.movies.utils.doOnScrollToEnd
import me.aartikov.lib.loading.paged.PagedLoading


@AndroidEntryPoint
class MoviesScreen : BaseScreen<MoviesViewModel>(R.layout.screen_movies, MoviesViewModel::class) {

    private val movieAdapter = GroupAdapter<GroupieViewHolder>()
    private val listSection = Section()
    private var scrollToEndListenerEnabled: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
        retryButton.setOnClickListener { vm.onRetryClicked() }

        vm::moviesState bind { state ->
            swipeRefresh.isVisible = state is PagedLoading.State.Data
            emptyView.isVisible = state is PagedLoading.State.Empty
            loadingView.isVisible = state is PagedLoading.State.Loading
            errorView.isVisible = state is PagedLoading.State.Error

            when (state) {
                is PagedLoading.State.Data -> {
                    listSection.update(state.data.toGroupieItems())
                    swipeRefresh.isRefreshing = state.refreshing

                    if (state.loadingMore) {
                        listSection.setFooter(LoadingItem())
                    } else {
                        listSection.removeFooter()
                    }

                    scrollToEndListenerEnabled = !state.fullData
                }

                is PagedLoading.State.Error -> {
                    errorMessage.text = state.throwable.message
                }
            }
        }
    }

    private fun initRecyclerView() {
        movieAdapter.add(listSection)
        with(moviesList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            doOnScrollToEnd {
                if (scrollToEndListenerEnabled) vm.onLoadMore()
            }
        }
    }
}