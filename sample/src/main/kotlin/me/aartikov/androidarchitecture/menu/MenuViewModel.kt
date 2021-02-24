package me.aartikov.androidarchitecture.menu

import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.androidarchitecture.*
import me.aartikov.androidarchitecture.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : BaseViewModel() {

    fun onCounterButtonClicked() {
        navigate(OpenCounterScreen)
    }

    fun onProfileButtonClicked() {
        navigate(OpenProfileScreen)
    }

    fun onDialogsButtonClicked() {
        navigate(OpenDialogsScreen)
    }

    fun onMoviesButtonClicked() {
        navigate(OpenMoviesScreen)
    }

    fun onClockButtonClicked() {
        navigate(OpenClockScreen)
    }
}