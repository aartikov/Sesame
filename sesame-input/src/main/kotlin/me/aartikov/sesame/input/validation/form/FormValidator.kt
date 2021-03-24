package me.aartikov.sesame.input.validation.form

import me.aartikov.sesame.input.Control
import me.aartikov.sesame.input.validation.control.ControlValidator
import me.aartikov.sesame.input.validation.control.ValidationResult

class FormValidator(
    val validators: Map<Control<*>, ControlValidator<*>>
) {

    fun validate(displayResult: Boolean = true): FormValidationResult {
        val results = mutableMapOf<Control<*>, ValidationResult>()
        validators.forEach { (control, validator) ->
            results[control] = validator.validate(displayResult)
        }
        return FormValidationResult(results)
    }
}