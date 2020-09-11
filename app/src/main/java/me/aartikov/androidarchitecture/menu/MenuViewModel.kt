package me.aartikov.androidarchitecture.menu

import androidx.hilt.lifecycle.ViewModelInject
import me.aartikov.androidarchitecture.OpenCounterScreen
import me.aartikov.androidarchitecture.OpenProfileScreen
import me.aartikov.androidarchitecture.base.BaseViewModel
import me.aartikov.lib.widget.dialog_control.dialogControl

class MenuViewModel @ViewModelInject constructor() : BaseViewModel() {

    val dialog = dialogControl<String, String>()

    fun onCounterButtonClicked() {
        navigate(OpenCounterScreen)
    }

    fun onProfileButtonClicked() {
        navigate(OpenProfileScreen)
    }

    fun showDialog() {
        dialog.show("Test show dialog")
    }
}