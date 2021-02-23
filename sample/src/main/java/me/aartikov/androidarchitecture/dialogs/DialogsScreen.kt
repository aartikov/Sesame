package me.aartikov.androidarchitecture.dialogs

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.androidarchitecture.R
import me.aartikov.androidarchitecture.base.BaseScreen
import me.aartikov.androidarchitecture.databinding.ScreenDialogsBinding

@AndroidEntryPoint
class DialogsScreen : BaseScreen<DialogsViewModel>(R.layout.screen_dialogs, DialogsViewModel::class) {

    private val binding by viewBinding(ScreenDialogsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            showDialogButton.setOnClickListener { vm.onShowDialogButtonClicked() }
            showForResultButton.setOnClickListener { vm.onShowForResultButtonClicked() }

            vm.dialog bind { message, dc ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok_button) { _, _ ->
                        dc.dismiss()
                    }
                    .create()
            }

            vm.dialogForResult bind { message, dc ->
                AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_for_result_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok_button) { _, _ ->
                        dc.sendResult(DialogResult.OK)
                    }
                    .setNegativeButton(R.string.cancel_button) { _, _ ->
                        dc.sendResult(DialogResult.CANCELED)
                    }
                    .create()
            }

            vm.showMessage bind { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }
}