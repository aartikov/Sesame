package me.aartikov.sesamesample.form

import android.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.form.*
import me.aartikov.sesame.form.validation.control.*
import me.aartikov.sesame.form.validation.form.*
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.command
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor() : BaseViewModel() {

    companion object {
        private const val NAME_MAX_LENGTH = 100
        private const val PASSWORD_MIN_SYMBOLS = 6
        private const val RUS_PHONE_DIGIT_COUNT = 11
    }

    val nameInput = InputControl(
        maxLength = NAME_MAX_LENGTH,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    )

    val emailInput = InputControl(
        keyboardOptions = KeyboardOptions(KeyboardType.Email)
    )

    val phoneInput = InputControl(
        keyboardOptions = KeyboardOptions(KeyboardType.Phone),
        formatter = PhoneNumberFormatter
    )

    val passwordInput = InputControl(
        keyboardOptions = KeyboardOptions(KeyboardType.Password)
    )

    val confirmPasswordInput = InputControl(
        keyboardOptions = KeyboardOptions(KeyboardType.Password)
    )

    val termsCheckBox = CheckControl()

    val dropKonfetti = command<Unit>()

    private val formValidator = formValidator {

        features = listOf(ValidateOnFocusLost, RevalidateOnValueChanged, FocusOnFirstInvalidControlAfterValidation)

        input(nameInput) {
            isNotBlank(R.string.field_is_blank_error_message)
        }

        input(emailInput, required = false) {
            regex(Patterns.EMAIL_ADDRESS.toRegex(), R.string.invalid_email_error_message)
        }

        input(phoneInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            validation(
                { str -> str.count { it.isDigit() } == RUS_PHONE_DIGIT_COUNT },
                R.string.invalid_phone_error_message
            )
        }

        input(passwordInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            minSymbols(
                PASSWORD_MIN_SYMBOLS,
                LocalizedString.resource(R.string.minimum_symbols_error_message, PASSWORD_MIN_SYMBOLS)
            )
            validation(
                { str -> str.any { it.isDigit() } },
                LocalizedString.resource(R.string.must_contain_digit_error_message)
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
            dropKonfetti()
        }
    }
}