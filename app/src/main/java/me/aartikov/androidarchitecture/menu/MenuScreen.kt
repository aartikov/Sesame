package me.aartikov.androidarchitecture.menu

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
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
        dialogButton.setOnClickListener { vm.onDialogButtonClicked() }

        vm.dialog bind { text, dc ->
            AlertDialog.Builder(requireContext())
                .setTitle(text)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel_button) { _, _ ->
                    dc.dismiss()
                }
                .create()
        }
    }
}

