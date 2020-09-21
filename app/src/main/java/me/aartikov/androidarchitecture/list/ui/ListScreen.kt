package me.aartikov.androidarchitecture.list.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_list.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.lib.loading.paged.setToView

@AndroidEntryPoint
class ListScreen : BaseScreen<ListViewModel>(R.layout.screen_list, ListViewModel::class) {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val section = Section()
    private val loadingFooter = LoadingItem()
    private val layoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //adapter.add(section)
        itemsList.adapter = adapter
        itemsList.layoutManager = layoutManager
        itemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.childCount + layoutManager.findFirstVisibleItemPosition()
                    == layoutManager.itemCount
                ) {
                    vm.onLoadMore()
                }
            }
        })

        listSwipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }

        vm::moviesUiState bind { state ->
            state.setToView(
                setData = {movies ->
                    val temp = movies.map { MovieItem(it) }
                    adapter.addAll(temp)
                    //section.addAll(temp)
                },
                setDataVisible = itemsList::isVisible::set,
                setError = { listErrorMessage.text = it.message },
                setErrorVisible = listErrorView::isVisible::set,
                setEmptyVisible = {
                    // Нужен ли вообще здесь этот обработчик?

                },
                setLoadingVisible = listLoadingView::isVisible::set,
                setRefreshVisible = listSwipeRefresh::setRefreshing,
                setRefreshEnabled = listSwipeRefresh::setEnabled,
                setLoadMoreVisible = { visible ->
                    if (visible)
                        section.setFooter(loadingFooter)
                    else
                        section.removeFooter()
                },
                setLoadMoreEnabled = { enabled ->
                    if (enabled) {
                        /*
                    * Когда loadMoreEnabled == false (то есть в состоянии Refresh, LoadingMore, FullData)
                    * дергать не надо.
                    Вернее, это можно делать и ничего плохого не случится, н
                    * о ты будешь с большой частотой закидывать в стейтмашину бесполезные экшены
                    * */
                    }
                }
            )
        }

    }

}