package me.aartikov.sesame.form.validation.form

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.form.ValidatableControl
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.StateDelegate
import me.aartikov.sesame.property.flow
import me.aartikov.sesame.property.stateFromFlow

fun PropertyHost.dynamicValidationResult(formValidator: FormValidator): StateDelegate<FormValidationResult> {
    val result = MutableStateFlow(formValidator.validate(displayResult = false))
    formValidator.validators.forEach { (control, _) ->
        callWhenControlEdited(propertyHostScope, control) {
            result.value = formValidator.validate(displayResult = false)
        }
    }
    return stateFromFlow(result.asStateFlow())
}

private fun callWhenControlEdited(
    coroutineScope: CoroutineScope,
    control: ValidatableControl<*>,
    callback: () -> Unit
) {
    control::value.flow
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)

    control::skipInValidation.flow
        .drop(1)
        .onEach {
            callback()
        }
        .launchIn(coroutineScope)
}