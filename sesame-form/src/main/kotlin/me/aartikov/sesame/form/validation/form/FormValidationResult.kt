package me.aartikov.sesame.form.validation.form

import me.aartikov.sesame.form.Control
import me.aartikov.sesame.form.validation.control.ValidationResult

data class FormValidationResult(
    val controlResults: Map<Control<*>, ValidationResult>
) {

    val isValid get() = controlResults.values.none { it is ValidationResult.Invalid }
}