package me.aartikov.sesame.form.validation.form

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.aartikov.sesame.form.Control
import me.aartikov.sesame.form.validation.control.ControlValidator
import me.aartikov.sesame.form.validation.control.InputValidator
import me.aartikov.sesame.property.flow

interface FormValidationFeature {

    fun install(coroutineScope: CoroutineScope, formValidator: FormValidator)
}

object HideErrorOnValueChanged : FormValidationFeature {

    override fun install(coroutineScope: CoroutineScope, formValidator: FormValidator) {
        formValidator.validators.forEach { (control, _) ->
            hideErrorOnValueChanged(coroutineScope, control)
        }
    }

    private fun hideErrorOnValueChanged(coroutineScope: CoroutineScope, control: Control<*>) {
        control::value.flow
            .drop(1)
            .onEach {
                control.error = null
            }
            .launchIn(coroutineScope)
    }
}

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
