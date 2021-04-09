package me.aartikov.sesame.form.validation.control

import androidx.annotation.StringRes
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.localizedstring.LocalizedString

class InputValidatorBuilder(
    private val inputControl: InputControl,
    private val required: Boolean
) {

    private val validations = mutableListOf<(String) -> ValidationResult>()

    /**
     * Adds an arbitrary validation. Validations are processed sequentially until first error.
     */
    fun validation(validation: (String) -> ValidationResult) {
        validations.add(validation)
    }

    fun build(): InputValidator {
        return InputValidator(inputControl, required, validations)
    }
}

/**
 * Adds an arbitrary validation. Validations are processed sequentially until first error.
 */
fun InputValidatorBuilder.validation(isValid: (String) -> Boolean, errorMessage: LocalizedString) {
    validation {
        if (isValid(it)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errorMessage)
        }
    }
}

/**
 * Adds an arbitrary validation. Validations are processed sequentially until first error.
 */
fun InputValidatorBuilder.validation(isValid: (String) -> Boolean, @StringRes errorMessageRes: Int) {
    validation(isValid, LocalizedString.resource(errorMessageRes))
}

/**
 * Adds a validation that checks that an input is not blank.
 */
fun InputValidatorBuilder.isNotBlank(errorMessage: LocalizedString) {
    validation(
        isValid = { it.isNotBlank() },
        errorMessage
    )
}

/**
 * Adds a validation that checks that an input is not blank.
 */
fun InputValidatorBuilder.isNotBlank(@StringRes errorMessageRes: Int) {
    isNotBlank(LocalizedString.resource(errorMessageRes))
}

/**
 * Adds a validation that checks that an input matches [regex].
 */
fun InputValidatorBuilder.regex(regex: Regex, errorMessage: LocalizedString) {
    validation(
        isValid = { regex.matches(it) },
        errorMessage
    )
}

/**
 * Adds a validation that checks that an input matches [regex].
 */
fun InputValidatorBuilder.regex(regex: Regex, @StringRes errorMessageRes: Int) {
    regex(regex, LocalizedString.resource(errorMessageRes))
}

/**
 * Adds a validation that checks that an input has at least given number of symbols.
 */
fun InputValidatorBuilder.minLength(length: Int, errorMessage: LocalizedString) {
    validation(
        isValid = { it.length >= length },
        errorMessage
    )
}

/**
 * Adds a validation that checks that an input has at least given number of symbols.
 */
fun InputValidatorBuilder.minLength(length: Int, @StringRes errorMessageRes: Int) {
    minLength(length, LocalizedString.resource(errorMessageRes))
}

/**
 * Adds a validation that checks that an input equals to an input of another input control.
 */
fun InputValidatorBuilder.equalsTo(inputControl: InputControl, errorMessage: LocalizedString) {
    validation(
        isValid = { it == inputControl.text },
        errorMessage
    )
}

/**
 * Adds a validation that checks that an input equals to an input of another input control.
 */
fun InputValidatorBuilder.equalsTo(inputControl: InputControl, errorMessageRes: Int) {
    equalsTo(inputControl, LocalizedString.resource(errorMessageRes))
}