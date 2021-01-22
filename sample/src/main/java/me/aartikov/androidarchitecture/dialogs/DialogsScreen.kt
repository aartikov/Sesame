package me.aartikov.androidarchitecture.dialogs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_for_result.view.*
import kotlinx.android.synthetic.main.screen_dialogs.*
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen

@AndroidEntryPoint
class DialogsScreen : BaseScreen<DialogsViewModel>(R.layout.screen_dialogs, DialogsViewModel::class) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showDialogButton.setOnClickListener { vm.onShowDialogButtonClicked() }
        showForResultButton.setOnClickListener { vm.onShowForResultButtonClicked() }

        vm.dialog bind { text, dc ->
            AlertDialog.Builder(requireContext())
                .setTitle(text)
                .setNegativeButton(R.string.cancel_button) { _, _ ->
                    dc.dismiss()
                }
                .create()
        }

        vm.dialogForResult bind { text, dc ->
            val dialogView = LayoutInflater.from(context).inflate(
                R.layout.dialog_for_result,
                null,
                false
            )

            dialogView.title.text = text

            AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel_button) { _, _ ->
                    dc.dismiss()
                }
                .setPositiveButton(R.string.send_button) { _, _ ->
                    dc.sendResult(dialogView.resultInput.text.toString())
                }
                .create()
        }

        vm.showMessage bind { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
    }
}