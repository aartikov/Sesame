package me.aartikov.sesame.form.validation.control

import me.aartikov.sesame.form.InputControl

class InputValidator constructor(
    override val control: InputControl,
    private val required: Boolean = true,
    private val validations: List<(String) -> ValidationResult>
) : ControlValidator<InputControl> {

    override fun validate(displayResult: Boolean): ValidationResult {
        return getValidationResult().also {
            if (displayResult) displayValidationResult(it)
        }
    }

    private fun getValidationResult(): ValidationResult {
        if (control.skipInValidation) {
            return ValidationResult.Skipped
        }

        if (control.text.isBlank() && !required) {
            return ValidationResult.Valid
        }

        validations.forEach { validation ->
            val result = validation(control.text)
            if (result is ValidationResult.Invalid) {
                return result
            }
        }

        return ValidationResult.Valid
    }

    private fun displayValidationResult(validationResult: ValidationResult) = when (validationResult) {
        ValidationResult.Valid, ValidationResult.Skipped -> control.error = null
        is ValidationResult.Invalid -> control.error = validationResult.errorMessage
    }
}