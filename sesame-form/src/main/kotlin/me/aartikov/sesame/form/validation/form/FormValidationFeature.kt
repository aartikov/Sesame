package me.aartikov.sesame.form.validation.form

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.aartikov.sesame.form.control.ValidatableControl
import me.aartikov.sesame.form.validation.control.ControlValidator
import me.aartikov.sesame.form.validation.control.InputValidator
import me.aartikov.sesame.form.validation.control.ValidationResult
import me.aartikov.sesame.property.flow

/**
 * High level feature for [FormValidator].
 */
interface FormValidationFeature {

    fun install(coroutineScope: CoroutineScope, formValidator: FormValidator)
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
        control::value.flow
            .drop(1)
            .onEach {
                control.error = null
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
        control::value.flow
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
        inputControl::hasFocus.flow
            .drop(1)
            .filter { !it }
            .onEach {
                inputValidator.validate()
            }
            .launchIn(coroutineScope)
    }
}

/**
 * Sets focus on a first invalid control after form validation has been processed.
 */
object SetFocusOnFirstInvalidControlAfterValidation : FormValidationFeature {

    override fun install(coroutineScope: CoroutineScope, formValidator: FormValidator) {
        formValidator.validatedEventFlow
            .onEach {
                if (it.displayResult) {
                    focusOnFirstInvalidControl(it.result)
                }
            }
            .launchIn(coroutineScope)
    }

    private fun focusOnFirstInvalidControl(validationResult: FormValidationResult) {
        val firstInvalidControl = validationResult.controlResults.entries
            .firstOrNull { it.value is ValidationResult.Invalid }?.key

        firstInvalidControl?.requestFocus()
    }
}