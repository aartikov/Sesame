package me.aartikov.sesame.compose.form.validation.form

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.aartikov.sesame.compose.form.control.ValidatableControl
import me.aartikov.sesame.compose.form.validation.control.ControlValidator
import me.aartikov.sesame.compose.form.validation.control.InputValidator
import me.aartikov.sesame.compose.form.validation.control.ValidationResult

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

    private fun hideErrorOnValueChanged(
        coroutineScope: CoroutineScope,
        control: ValidatableControl<*>
    ) {
        val state = MutableStateFlow(control.value)
        state.asStateFlow()
            .drop(1)
            .onEach {
                control.error = mutableStateOf(null)
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

    private fun revalidateOnValueChanged(
        coroutineScope: CoroutineScope,
        validator: ControlValidator<*>
    ) {
        val state = MutableStateFlow(validator.control.value)
        state.asStateFlow()
            .drop(1)
            .onEach {
                if (validator.control.error.value != null) {
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

    private fun validateOnFocusLost(
        coroutineScope: CoroutineScope,
        inputValidator: InputValidator
    ) {
        val state = MutableStateFlow(inputValidator.control.hasFocus)
        state.asStateFlow()
            .drop(1)
            .filter { !it.value }
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