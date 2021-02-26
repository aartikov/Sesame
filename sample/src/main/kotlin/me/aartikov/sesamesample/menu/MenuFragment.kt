package me.aartikov.sesamesample.menu

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentMenuBinding

@AndroidEntryPoint
class MenuFragment : BaseFragment<MenuViewModel>(R.layout.fragment_menu, MenuViewModel::class) {

    private val binding by viewBinding(FragmentMenuBinding::bind)

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

