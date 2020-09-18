package me.aartikov.androidarchitecture.list.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_list.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.profile.ProfileViewModel

@AndroidEntryPoint
class ListScreen : BaseScreen<ListViewModel>(R.layout.screen_list, ListViewModel::class) {

    private val adapter = ListDelegationAdapter<List<MovieListItem>>(
        movieAdapterDelegate()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsList.adapter = adapter
        itemsList.layoutManager = LinearLayoutManager(requireContext())


    }

}