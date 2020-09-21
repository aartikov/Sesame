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
import me.aartikov.lib.loading.paged.setToView

@AndroidEntryPoint
class ListScreen : BaseScreen<ListViewModel>(R.layout.screen_list, ListViewModel::class) {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val section = Section()
    private val loadingFooter = LoadingItem()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsList.adapter = adapter
        itemsList.layoutManager = LinearLayoutManager(requireContext())

        listSwipeRefresh.setOnRefreshListener { vm.onPullToRefresh() }


        Log.d("ListScreen", "state = ${vm.moviesUiState}")
        vm::moviesUiState bind { state ->
            state.setToView(
                setData = {
                    section.addAll(it.toGroupieItems())
                    adapter.add(section)
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