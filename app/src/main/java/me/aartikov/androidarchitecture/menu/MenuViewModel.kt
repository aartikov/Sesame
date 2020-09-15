package me.aartikov.androidarchitecture.menu

import androidx.hilt.lifecycle.ViewModelInject
import me.aartikov.androidarchitecture.OpenCounterScreen
import me.aartikov.androidarchitecture.OpenDialogsScreen
import me.aartikov.androidarchitecture.OpenProfileScreen
import me.aartikov.androidarchitecture.base.BaseViewModel

class MenuViewModel @ViewModelInject constructor() : BaseViewModel() {

    fun onCounterButtonClicked() {
        navigate(OpenCounterScreen)
    }

    fun onProfileButtonClicked() {
        navigate(OpenProfileScreen)
    }

    fun onDialogsButtonClicked() {
        navigate(OpenDialogsScreen)
    }
}