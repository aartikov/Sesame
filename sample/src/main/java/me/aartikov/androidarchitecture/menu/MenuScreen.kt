package me.aartikov.androidarchitecture.menu

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_menu.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen

@AndroidEntryPoint
class MenuScreen : BaseScreen<MenuViewModel>(R.layout.screen_menu, MenuViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        counterButton.setOnClickListener { vm.onCounterButtonClicked() }
        profileButton.setOnClickListener { vm.onProfileButtonClicked() }
        dialogsButton.setOnClickListener { vm.onDialogsButtonClicked() }
        moviesButton.setOnClickListener { vm.onMoviesButtonClicked() }
    }
}

