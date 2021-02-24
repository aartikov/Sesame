package me.aartikov.androidarchitecture.menu

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.databinding.ScreenMenuBinding

@AndroidEntryPoint
class MenuScreen : BaseScreen<MenuViewModel>(R.layout.screen_menu, MenuViewModel::class) {

    private val binding by viewBinding(ScreenMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            counterButton.setOnClickListener { vm.onCounterButtonClicked() }
            profileButton.setOnClickListener { vm.onProfileButtonClicked() }
            dialogsButton.setOnClickListener { vm.onDialogsButtonClicked() }
            moviesButton.setOnClickListener { vm.onMoviesButtonClicked() }
            clockButton.setOnClickListener { vm.onClockButtonClicked() }
        }
    }
}

