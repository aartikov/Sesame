package me.aartikov.sesame.form.validation.control

import me.aartikov.sesame.form.ValidatableControl

interface ControlValidator<ControlT : ValidatableControl<*>> {

    val control: ControlT

    fun validate(displayResult: Boolean = true): ValidationResult
}