package me.aartikov.sesamesample.form

import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.input.CheckControl
import me.aartikov.sesame.input.InputControl
import me.aartikov.sesame.input.KeyboardOptions
import me.aartikov.sesame.input.KeyboardType
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor() : BaseViewModel() {

    val nameInput = InputControl(
        maxLength = 100,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )

    val emailInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
    )

    val phoneInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        )
    )

    val passwordInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )

    val confirmPasswordInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )

    val termsCheckBox = CheckControl()
}