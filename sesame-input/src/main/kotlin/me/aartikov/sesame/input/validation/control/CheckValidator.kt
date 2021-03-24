package me.aartikov.sesame.input.validation.control

import me.aartikov.sesame.input.CheckControl
import me.aartikov.sesame.localizedstring.LocalizedString

class CheckValidator constructor(
    override val control: CheckControl,
    private val validation: (Boolean) -> ValidationResult,
    private val showError: ((LocalizedString) -> Unit)? = null
) : ControlValidator<CheckControl> {

    override fun validate(displayResult: Boolean): ValidationResult {
        return getValidationResult().also {
            if (displayResult) displayValidationResult(it)
        }
    }

    private fun getValidationResult(): ValidationResult {
        if (control.skipInValidation) {
            return ValidationResult.Skipped
        }

        return validation(control.value)
    }

    private fun displayValidationResult(validationResult: ValidationResult) = when (validationResult) {
        ValidationResult.Valid, ValidationResult.Skipped -> control.error = null
        is ValidationResult.Invalid -> {
            control.error = validationResult.errorMessage
            showError?.invoke(validationResult.errorMessage)
        }
    }
}