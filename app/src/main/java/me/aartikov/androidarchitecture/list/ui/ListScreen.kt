package me.aartikov.androidarchitecture.list.ui

import android.os.Bundle
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

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val section = Section()
    private val loadingFooter = LoadingItem()
    private val layoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        listSwipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }

        vm::moviesUiState bind { state ->
            state.setToView(
                setData = { movies ->
                    // TODO: properly add section
                    adapter.updateAsync(movies.toGroupieItems())
                },
                setDataVisible = itemsList::isVisible::set,
                setError = { listErrorMessage.text = it.message },
                setErrorVisible = listErrorView::isVisible::set,
                setLoadingVisible = listLoadingView::isVisible::set,
                setRefreshVisible = listSwipeRefresh::setRefreshing,
                setRefreshEnabled = listSwipeRefresh::setEnabled,
                setLoadMoreVisible = { visible ->
                    // TODO : add proper footer
                    if (visible)
                        section.setFooter(loadingFooter)
                    else
                        section.removeFooter()
                }
            )
        }
    }

    private fun initRecyclerView() {
        adapter.add(section)
        itemsList.adapter = adapter
        itemsList.layoutManager = layoutManager
        itemsList.doAfterScrollToEnd(layoutManager) { vm.onLoadMore() }
    }
}