package me.aartikov.sesame.form.validation.form

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.aartikov.sesame.form.Control
import me.aartikov.sesame.form.validation.control.ControlValidator
import me.aartikov.sesame.form.validation.control.ValidationResult

class FormValidator(
    val validators: Map<Control<*>, ControlValidator<*>>
) {

    private val mutableValidatedEventFlow = MutableSharedFlow<FormValidatedEvent>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val validatedEventFlow get() = mutableValidatedEventFlow.asSharedFlow()

    fun validate(displayResult: Boolean = true): FormValidationResult {
        val results = mutableMapOf<Control<*>, ValidationResult>()
        validators.forEach { (control, validator) ->
            results[control] = validator.validate(displayResult)
        }

        return FormValidationResult(results).also {
            mutableValidatedEventFlow.tryEmit(FormValidatedEvent(it, displayResult))
        }
    }
}