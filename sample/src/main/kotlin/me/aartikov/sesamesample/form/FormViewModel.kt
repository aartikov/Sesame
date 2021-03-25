package me.aartikov.sesamesample.form

import android.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.input.*
import me.aartikov.sesame.input.validation.control.*
import me.aartikov.sesame.input.validation.form.checked
import me.aartikov.sesame.input.validation.form.formValidator
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.command
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val phoneUtil: PhoneUtil
) : BaseViewModel() {

    companion object {
        private const val PASSWORD_MIN_SYMBOLS = 6
    }

    val showMessage = command<LocalizedString>()

    val nameInput = InputControl(
        maxLength = 100,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
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

    private val formValidator = formValidator {
        input(nameInput) {
            isNotBlank(R.string.field_is_blank_error_message)
        }

        input(emailInput, required = false) {
            regex(Patterns.EMAIL_ADDRESS.toRegex(), R.string.invalid_email_error_message)
        }

        input(phoneInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            validation(phoneUtil::isValidPhone, R.string.invalid_phone_error_message)
        }

        input(passwordInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            minSymbols(
                PASSWORD_MIN_SYMBOLS,
                LocalizedString.resource(R.string.minimum_symbols_error_message, PASSWORD_MIN_SYMBOLS)
            )
        }

        input(confirmPasswordInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            equalsTo(passwordInput, R.string.passwords_do_not_match)
        }

        checked(termsCheckBox, R.string.terms_are_accepted_error_message)
    }

    fun onSubmitClicked() {
        val result = formValidator.validate()
        if (result.isValid) {
            showMessage(LocalizedString.resource(R.string.form_is_valid))
        }
    }
}