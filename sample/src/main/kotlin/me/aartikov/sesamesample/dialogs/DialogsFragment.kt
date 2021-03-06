package me.aartikov.sesamesample.dialogs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentDialogsBinding

@AndroidEntryPoint
class DialogsFragment : BaseFragment<DialogsViewModel>(R.layout.fragment_dialogs, DialogsViewModel::class) {

    override val titleRes: Int = R.string.dialogs_title

    private val binding by viewBinding(FragmentDialogsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            showDialogButton.setOnClickListener { vm.onShowDialogButtonClicked() }
            showForResultButton.setOnClickListener { vm.onShowForResultButtonClicked() }

            vm.dialog bind { message, dc ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_title)
                    .setMessage(message.resolve(requireContext()))
                    .setPositiveButton(R.string.common_ok) { _, _ ->
                        dc.dismiss()
                    }
                    .create()
            }

            vm.dialogForResult bind { message, dc ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_for_result_title)
                    .setMessage(message.resolve(requireContext()))
                    .setPositiveButton(R.string.common_ok) { _, _ ->
                        dc.sendResult(DialogResult.Ok)
                    }
                    .setNegativeButton(R.string.common_cancel) { _, _ ->
                        dc.sendResult(DialogResult.Cancel)
                    }
                    .create()
            }

            vm.showMessage bind {
                Toast.makeText(requireContext(), it.resolve(requireContext()), Toast.LENGTH_SHORT).show()
            }
        }
    }
}