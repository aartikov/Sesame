package me.aartikov.androidarchitecture.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_menu.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.lib.widget.dialog_control.DialogControl

@AndroidEntryPoint
class MenuScreen : BaseScreen<MenuViewModel>(R.layout.screen_menu, MenuViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        counterButton.setOnClickListener { vm.onCounterButtonClicked() }
        profileButton.setOnClickListener { vm.onProfileButtonClicked() }
        dialogButton.setOnClickListener { vm.showDialog() }

        vm.dialog bind { text, _ ->
            AlertDialog.Builder(requireContext())
                .setTitle(text)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel_button) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }
    }
}

