package me.aartikov.sesame.input.validation.control

import androidx.annotation.StringRes
import me.aartikov.sesame.input.InputControl
import me.aartikov.sesame.localizedstring.LocalizedString

class InputValidatorBuilder(
    private val inputControl: InputControl,
    private val required: Boolean
) {

    private val validations = mutableListOf<(String) -> ValidationResult>()

    fun validation(validation: (String) -> ValidationResult) {
        validations.add(validation)
    }

    fun validation(isValid: (String) -> Boolean, errorMessage: LocalizedString) {
        validation {
            if (isValid(it)) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errorMessage)
            }
        }
    }

    fun validation(isValid: (String) -> Boolean, @StringRes errorMessageRes: Int) {
        validation(isValid, LocalizedString.resource(errorMessageRes))
    }

    fun build(): InputValidator {
        return InputValidator(inputControl, required, validations)
    }
}