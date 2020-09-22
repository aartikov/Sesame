package me.aartikov.androidarchitecture.list.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_list.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.list.utils.doAfterScrollToEnd
import me.aartikov.lib.loading.paged.setToView


@AndroidEntryPoint
class ListScreen : BaseScreen<ListViewModel>(R.layout.screen_list, ListViewModel::class) {

    private val movieAdapter = GroupAdapter<GroupieViewHolder>()
    private val listSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        listSwipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }

        vm::moviesUiState bind { state ->
            state.setToView(
                setData = { movies ->
                    // TODO: calculate list diffs asynchronously (movieAdapter..updateAsync()
                    listSection.update(movies.toGroupieItems())
                },
                setDataVisible = itemsList::isVisible::set,
                setError = { listErrorMessage.text = it.message },
                setErrorVisible = listErrorView::isVisible::set,
                setLoadingVisible = listLoadingView::isVisible::set,
                setRefreshVisible = listSwipeRefresh::setRefreshing,
                setRefreshEnabled = listSwipeRefresh::setEnabled,
                setLoadMoreVisible = { visible ->
                    if (visible) {
                        listSection.setFooter(LoadingItem())
                    } else {
                        listSection.removeFooter()
                    }
                },
                setLoadMoreEnabled = { }
            )
        }
    }

    private fun initRecyclerView() {
        movieAdapter.add(listSection)
        with(itemsList) {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
            doAfterScrollToEnd {
                vm.onLoadMore()
            }
        }
    }
}