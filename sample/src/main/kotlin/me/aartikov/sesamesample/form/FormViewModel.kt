package me.aartikov.sesamesample.form

import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.input.CheckControl
import me.aartikov.sesame.input.InputControl
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor() : BaseViewModel() {

    val nameInput = InputControl()
    val emailInput = InputControl()
    val phoneInput = InputControl()
    val passwordInput = InputControl()
    val confirmPasswordInput = InputControl()
    val termsCheckBox = CheckControl()
}