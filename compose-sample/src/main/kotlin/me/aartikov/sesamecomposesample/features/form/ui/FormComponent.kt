package me.aartikov.sesamecomposesample.features.form.ui

import kotlinx.coroutines.flow.Flow
import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesame.compose.form.control.InputControl

interface FormComponent {

    val nameInput: InputControl

    val emailInput: InputControl

    val phoneInput: InputControl

    val passwordInput: InputControl

    val confirmPasswordInput: InputControl

    val termsCheckBox: CheckControl

    val submitButtonState: SubmitButtonState

    val dropKonfettiEvent: Flow<Unit>

    fun onSubmitClicked()
}