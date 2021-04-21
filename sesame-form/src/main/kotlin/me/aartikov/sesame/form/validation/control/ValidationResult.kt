package me.aartikov.sesame.form.validation.control

import me.aartikov.sesame.localizedstring.LocalizedString

/**
 * Represents a result of validation.
 */
sealed class ValidationResult {

    /**
     * An input is valid.
     */
    object Valid : ValidationResult()

    /**
     * Validation was skipped.
     */
    object Skipped : ValidationResult()

    /**
     * An input is invalid.
     */
    data class Invalid(val errorMessage: LocalizedString) : ValidationResult()
}