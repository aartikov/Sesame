package me.aartikov.androidarchitecture.list.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_list.*
import kotlinx.android.synthetic.main.screen_profile.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.profile.ProfileViewModel
import me.aartikov.lib.loading.paged.setToView

@AndroidEntryPoint
class ListScreen : BaseScreen<ListViewModel>(R.layout.screen_list, ListViewModel::class) {

    private val adapter = ListDelegationAdapter(
        movieAdapterDelegate()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsList.adapter = adapter
        itemsList.layoutManager = LinearLayoutManager(requireContext())

        vm::moviesUiState bind { state ->
            state.setToView(
                setData = adapter::setItems,
                setDataVisible = itemsList::isVisible::set,
                setError = {

                },
                setErrorVisible = {

                },
                setEmptyVisible = {

                },
                setLoadingVisible = {

                },
                setRefreshVisible = {

                },
                setRefreshEnabled = {

                },
                setLoadMoreVisible = {

                },
                setLoadMoreEnabled = {

                }
            )


        }

    }

}