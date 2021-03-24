package me.aartikov.sesame.input.validation.form

import me.aartikov.sesame.input.Control
import me.aartikov.sesame.input.validation.control.ValidationResult

data class FormValidationResult(
    val controlResults: Map<Control<*>, ValidationResult>
) {

    val isValid get() = controlResults.values.none { it is ValidationResult.Invalid }
}