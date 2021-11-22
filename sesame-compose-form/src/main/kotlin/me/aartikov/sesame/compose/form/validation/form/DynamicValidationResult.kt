package me.aartikov.sesame.compose.form.validation.form

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.compose.form.control.ValidatableControl

/**
 * Validates a form dynamically and emits validation result. Validation called whenever a value or skipInValidation
 * of some control is changed.
 */
fun CoroutineScope.dynamicValidationResult(formValidator: FormValidator): State<FormValidationResult> {
    val result = mutableStateOf(formValidator.validate(displayResult = false))
    formValidator.validators.forEach { (control, _) ->
        callWhenControlEdited(this, control) {
            result.value = formValidator.validate(displayResult = false)
        }
    }
    return result
}

private fun callWhenControlEdited(
    coroutineScope: CoroutineScope,
    control: ValidatableControl<*>,
    callback: () -> Unit
) {
    control.valueChangeEvent
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)

    control.skipInValidationChangeEvent
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)
}