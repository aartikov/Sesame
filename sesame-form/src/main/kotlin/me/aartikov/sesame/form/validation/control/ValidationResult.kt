package me.aartikov.sesame.form.validation.control

import me.aartikov.sesame.localizedstring.LocalizedString

sealed class ValidationResult {

    object Valid : ValidationResult()

    object Skipped : ValidationResult()

    data class Invalid(val errorMessage: LocalizedString) : ValidationResult()
}