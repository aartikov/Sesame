package me.aartikov.sesame.compose.form.validation.form

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.compose.form.control.ValidatableControl
import me.aartikov.sesame.compose.form.validation.control.ControlValidator
import me.aartikov.sesame.compose.form.validation.control.InputValidator

/**
 * High level feature for [FormValidator].
 */
interface FormValidationFeature {

    fun install(coroutineScope: CoroutineScope, formValidator: FormValidator)
}

/**
 * Validates a control whenever it loses a focus.
 */
object ValidateOnFocusLost : FormValidationFeature {

    override fun install(coroutineScope: CoroutineScope, formValidator: FormValidator) {
        formValidator.validators.forEach { (_, validator) ->
            if (validator is InputValidator) {
                validateOnFocusLost(coroutineScope, validator)
            }
        }
    }

    private fun validateOnFocusLost(coroutineScope: CoroutineScope, inputValidator: InputValidator) {
        val inputControl = inputValidator.control
        inputControl.hasFocus
            .drop(1)
            .filter { !it }
            .onEach {
                inputValidator.validate()
            }
            .launchIn(coroutineScope)
    }
}

/**
 * Validates control again whenever its value is changed and it already displays an error.
 */
object RevalidateOnValueChanged : FormValidationFeature {

    override fun install(coroutineScope: CoroutineScope, formValidator: FormValidator) {
        formValidator.validators.forEach { (_, validator) ->
            revalidateOnValueChanged(coroutineScope, validator)
        }
    }

    private fun revalidateOnValueChanged(coroutineScope: CoroutineScope, validator: ControlValidator<*>) {
        val control = validator.control
        control.valueChangeEvent
            .drop(1)
            .onEach {
                if (control.error != null) {
                    validator.validate()
                }
            }
            .launchIn(coroutineScope)
    }
}

/**
 * Hides an error on a control whenever some value is entered to it.
 */
object HideErrorOnValueChanged : FormValidationFeature {

    override fun install(coroutineScope: CoroutineScope, formValidator: FormValidator) {
        formValidator.validators.forEach { (control, _) ->
            hideErrorOnValueChanged(coroutineScope, control)
        }
    }

    private fun hideErrorOnValueChanged(coroutineScope: CoroutineScope, control: ValidatableControl<*>) {
        control.valueChangeEvent
            .drop(1)
            .onEach {
                control.error = null
            }
            .launchIn(coroutineScope)
    }
}