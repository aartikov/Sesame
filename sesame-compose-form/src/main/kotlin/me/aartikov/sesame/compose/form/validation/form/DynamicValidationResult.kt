package me.aartikov.sesame.compose.form.validation.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.compose.form.control.ValidatableControl

/**
 * Validates a form dynamically and emits validation result. Validation called whenever a value or skipInValidation
 * of some control is changed.
 */
fun CoroutineScope.dynamicValidationResult(formValidator: FormValidator): FormValidationResult {
    val result = MutableStateFlow(formValidator.validate(displayResult = false))
    formValidator.validators.forEach { (control, _) ->
        callWhenControlEdited(this, control) {
            result.value = formValidator.validate(displayResult = false)
        }
    }
    val state by derivedStateOf { result.asStateFlow() }
    return state.value
}

private fun callWhenControlEdited(
    coroutineScope: CoroutineScope,
    control: ValidatableControl<*>,
    callback: () -> Unit
) {
    val value = MutableStateFlow(control::value)
    value.asStateFlow()
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)

    val skipInValidation = MutableStateFlow(control::skipInValidation)
    skipInValidation
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)
}
