package me.aartikov.sesame.form.validation.form

import androidx.annotation.StringRes
import me.aartikov.sesame.form.CheckControl
import me.aartikov.sesame.form.Control
import me.aartikov.sesame.form.InputControl
import me.aartikov.sesame.form.validation.control.CheckValidator
import me.aartikov.sesame.form.validation.control.ControlValidator
import me.aartikov.sesame.form.validation.control.InputValidatorBuilder
import me.aartikov.sesame.form.validation.control.ValidationResult
import me.aartikov.sesame.localizedstring.LocalizedString

class FormValidatorBuilder {

    private val validators = mutableMapOf<Control<*>, ControlValidator<*>>()

    fun validator(validator: ControlValidator<*>) {
        val control = validator.control
        if (validators.containsKey(control)) {
            throw IllegalArgumentException("Validator for $control is already added.")
        }
        validators[control] = validator
    }

    fun check(
        checkControl: CheckControl,
        validation: (Boolean) -> ValidationResult,
        showError: ((LocalizedString) -> Unit)? = null
    ) {
        val checkValidator = CheckValidator(checkControl, validation, showError)
        validator(checkValidator)
    }

    fun input(
        inputControl: InputControl,
        required: Boolean = true,
        buildBlock: InputValidatorBuilder.() -> Unit
    ) {
        val inputValidator = InputValidatorBuilder(inputControl, required)
            .apply(buildBlock)
            .build()
        validator(inputValidator)
    }

    fun build(): FormValidator {
        return FormValidator(validators)
    }
}

fun formValidator(buildBlock: FormValidatorBuilder.() -> Unit): FormValidator {
    return FormValidatorBuilder()
        .apply(buildBlock)
        .build()
}

fun FormValidatorBuilder.checked(
    checkControl: CheckControl,
    errorMessage: LocalizedString,
    showError: ((LocalizedString) -> Unit)? = null
) {
    this.check(
        checkControl,
        validation = {
            if (it) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
        },
        showError
    )
}

fun FormValidatorBuilder.checked(
    checkControl: CheckControl,
    @StringRes errorMessageRes: Int,
    showError: ((LocalizedString) -> Unit)? = null
) {
    this.check(
        checkControl,
        validation = {
            if (it) ValidationResult.Valid else ValidationResult.Invalid(LocalizedString.resource(errorMessageRes))
        },
        showError
    )
}