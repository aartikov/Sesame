package me.aartikov.sesame.form.validation.form

import me.aartikov.sesame.form.control.ValidatableControl
import me.aartikov.sesame.form.validation.control.ValidationResult

/**
 * Represents result of form validation.
 */
data class FormValidationResult(
    val controlResults: Map<ValidatableControl<*>, ValidationResult>
) {

    val isValid get() = controlResults.values.none { it is ValidationResult.Invalid }
}