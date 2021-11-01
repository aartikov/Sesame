package me.aartikov.sesame.compose.form.validation.control

import me.aartikov.sesame.compose.form.control.CheckControl
import me.aartikov.sesame.compose.form.validation.form.FormValidatorBuilder
import me.aartikov.sesame.compose.form.validation.form.checked
import me.aartikov.sesame.localizedstring.LocalizedString

/**
 * Validator for [CheckControl].
 * @param validation implements validation logic.
 * @param showError a callback that is called to show one-time error such as a toast. For permanent errors use [CheckControl.error] state.
 *
 * Use [FormValidatorBuilder.checked] to create it with a handy DSL.
 */
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
        ValidationResult.Valid, ValidationResult.Skipped -> control.error.value = null
        is ValidationResult.Invalid -> {
            control.error.value = validationResult.errorMessage
            showError?.invoke(validationResult.errorMessage)
        }
    }
}