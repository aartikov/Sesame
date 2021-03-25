package me.aartikov.sesame.form.validation.control

import me.aartikov.sesame.form.Control

interface ControlValidator<ControlT : Control<*>> {

    val control: ControlT

    fun validate(displayResult: Boolean = true): ValidationResult
}