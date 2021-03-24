package me.aartikov.sesame.input.validation.control

import me.aartikov.sesame.input.Control

interface ControlValidator<ControlT : Control<*>> {

    val control: ControlT

    fun validate(displayResult: Boolean = true): ValidationResult
}