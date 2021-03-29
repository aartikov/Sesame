package me.aartikov.sesamesample.menu

import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesamesample.*
import me.aartikov.sesamesample.base.BaseViewModel
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

    fun onFormButtonClicked() {
        navigate(OpenFormScreen)
    }
}