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
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.layout_loading_view.*
import kotlinx.android.synthetic.main.screen_movies.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.movies.utils.doAfterScrollToEnd
import me.aartikov.lib.loading.paged.setToView


@AndroidEntryPoint
class MoviesScreen : BaseScreen<MoviesViewModel>(R.layout.screen_movies, MoviesViewModel::class) {

    private val movieAdapter = GroupAdapter<GroupieViewHolder>()
    private val listSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        swipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }
        retryButton.setOnClickListener { vm.onRetryClicked() }

        vm::moviesUiState bind { state ->
            state.setToView(
                setData = { movies ->
                    // TODO: calculate list diffs asynchronously (movieAdapter..updateAsync()
                    listSection.update(movies.toGroupieItems())
                },
                setEmptyVisible = emptyPlaceholder::isVisible::set,
                setDataVisible = list::isVisible::set,
                setError = { errorMessage.text = it.message },
                setErrorVisible = errorView::isVisible::set,
                setLoadingVisible = loadingView::isVisible::set,
                setRefreshVisible = swipeRefresh::setRefreshing,
                setRefreshEnabled = swipeRefresh::setEnabled,
                setLoadMoreVisible = { visible ->
                    val item = LoadingItem()
                    if (visible)
                        listSection.setFooter(item)
                    else
                        listSection.removeFooter()
                }
            )
        }
    }

    private fun initRecyclerView() {
        movieAdapter.add(listSection)
        with(list) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            doAfterScrollToEnd {
                vm.onLoadMore()
            }
        }
    }
}