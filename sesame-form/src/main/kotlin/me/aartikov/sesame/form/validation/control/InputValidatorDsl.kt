package me.aartikov.sesame.form.validation.control

import androidx.annotation.StringRes
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.localizedstring.LocalizedString

class InputValidatorBuilder(
    private val inputControl: InputControl,
    private val required: Boolean
) {

    private val validations = mutableListOf<(String) -> ValidationResult>()

    fun validation(validation: (String) -> ValidationResult) {
        validations.add(validation)
    }

    fun build(): InputValidator {
        return InputValidator(inputControl, required, validations)
    }
}

fun InputValidatorBuilder.validation(isValid: (String) -> Boolean, errorMessage: LocalizedString) {
    validation {
        if (isValid(it)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errorMessage)
        }
    }
}

fun InputValidatorBuilder.validation(isValid: (String) -> Boolean, @StringRes errorMessageRes: Int) {
    validation(isValid, LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.isNotBlank(errorMessage: LocalizedString) {
    validation(
        isValid = { it.isNotBlank() },
        errorMessage
    )
}

fun InputValidatorBuilder.isNotBlank(@StringRes errorMessageRes: Int) {
    isNotBlank(LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.regex(regex: Regex, errorMessage: LocalizedString) {
    validation(
        isValid = { regex.matches(it) },
        errorMessage
    )
}

fun InputValidatorBuilder.regex(regex: Regex, @StringRes errorMessageRes: Int) {
    regex(regex, LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.minSymbols(number: Int, errorMessage: LocalizedString) {
    validation(
        isValid = { it.length >= number },
        errorMessage
    )
}

fun InputValidatorBuilder.minSymbols(number: Int, @StringRes errorMessageRes: Int) {
    minSymbols(number, LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.equalsTo(input: InputControl, errorMessage: LocalizedString) {
    validation(
        isValid = { it == input.text },
        errorMessage
    )
}

fun InputValidatorBuilder.equalsTo(input: InputControl, errorMessageRes: Int) {
    equalsTo(input, LocalizedString.resource(errorMessageRes))
}