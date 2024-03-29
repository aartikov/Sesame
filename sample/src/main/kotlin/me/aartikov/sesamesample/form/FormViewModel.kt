package me.aartikov.sesamesample.form

import android.util.Patterns
import dagger.hilt.android.lifecycle.HiltViewModel
import me.aartikov.sesame.form.control.*
import me.aartikov.sesame.form.validation.control.*
import me.aartikov.sesame.form.validation.form.*
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseViewModel
import javax.inject.Inject

enum class SubmitButtonState {
    Valid,
    Invalid
}

@HiltViewModel
class FormViewModel @Inject constructor() : BaseViewModel() {

    companion object {
        private const val NAME_MAX_LENGTH = 100
        private const val PASSWORD_MIN_LENGTH = 6
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
        formatter = RussianPhoneNumberFormatter
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

        features = listOf(ValidateOnFocusLost, RevalidateOnValueChanged, SetFocusOnFirstInvalidControlAfterValidation)

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
            minLength(
                PASSWORD_MIN_LENGTH,
                LocalizedString.resource(R.string.min_length_error_message, PASSWORD_MIN_LENGTH)
            )
            validation(
                { str -> str.any { it.isDigit() } },
                LocalizedString.resource(R.string.must_contain_digit_error_message)
            )
        }

        input(confirmPasswordInput) {
            isNotBlank(R.string.field_is_blank_error_message)
            equalsTo(passwordInput, R.string.passwords_do_not_match_error_message)
        }

        checked(termsCheckBox, R.string.terms_are_accepted_error_message)
    }

    private val validationResult by dynamicValidationResult(formValidator)

    val submitButtonState by computed(::validationResult) {
        if (it.isValid) SubmitButtonState.Valid else SubmitButtonState.Invalid
    }

    fun onSubmitClicked() {
        val result = formValidator.validate()
        if (result.isValid) {
            dropKonfetti()
        }
    }
}