package me.aartikov.androidarchitecture.list

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.profile.ProfileViewModel

@AndroidEntryPoint
class ListScreen : BaseScreen<ProfileViewModel>(R.layout.screen_list, ProfileViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}