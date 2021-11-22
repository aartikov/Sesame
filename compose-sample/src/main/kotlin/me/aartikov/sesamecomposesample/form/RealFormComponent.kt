package me.aartikov.sesamecomposesample.form

import android.util.Patterns
import androidx.annotation.ColorRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.arkivanov.decompose.ComponentContext
import me.aartikov.sesame.compose.form.control.*
import me.aartikov.sesame.compose.form.validation.control.*
import me.aartikov.sesame.compose.form.validation.form.*
import me.aartikov.sesame.compose.form.validation.form.ValidateOnFocusLost
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.utils.componentCoroutineScope

enum class SubmitButtonState(@ColorRes val color: Int) {
    Valid(R.color.green),
    Invalid(R.color.red)
}

enum class KonfettiState {
    Shown,
    Hidden
}

class RealFormComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, FormComponent {

    companion object {
        private const val NAME_MAX_LENGTH = 100
        private const val PHONE_MAX_LENGTH = 11
        private const val PASSWORD_MIN_LENGTH = 6
        private const val RUS_PHONE_DIGIT_COUNT = 10
    }

    private val coroutineScope = componentCoroutineScope()

    override val nameInput = InputControl(
        maxLength = NAME_MAX_LENGTH,
        textTransformation = {
            it.replace(Regex("[1234567890+=]"), "")
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    )

    override val emailInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
    )

    override val phoneInput = InputControl(
        maxLength = PHONE_MAX_LENGTH,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        visualTransformation = RussianPhoneNumberVisualTransformation
    )

    override val passwordInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )

    override val confirmPasswordInput = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )

    override var konfettiState by mutableStateOf(KonfettiState.Hidden)

    override val termsCheckBox = CheckControl()

    private val formValidator = coroutineScope.formValidator {

        features = listOf(ValidateOnFocusLost, RevalidateOnValueChanged)

        input(nameInput) {
            isNotBlank(R.string.field_is_blank_error_message)
        }

        input(emailInput, required = false) {
            isNotBlank(R.string.field_is_blank_error_message)
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
            equalsTo(passwordInput, R.string.passwords_do_not_match)
        }

        checked(termsCheckBox, R.string.terms_are_accepted_error_message)
    }

    private val dynamicResult = coroutineScope.dynamicValidationResult(formValidator)

    override val submitButtonState by derivedStateOf {
        if (dynamicResult.value.isValid) SubmitButtonState.Valid else SubmitButtonState.Invalid
    }

    override fun onSubmitClicked() {
        val result = formValidator.validate()
        konfettiState = if (result.isValid) KonfettiState.Shown else KonfettiState.Hidden
    }
}