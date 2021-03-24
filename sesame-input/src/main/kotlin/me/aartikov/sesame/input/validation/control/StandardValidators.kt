package me.aartikov.sesame.input.validation.control

import androidx.annotation.StringRes
import me.aartikov.sesame.input.CheckControl
import me.aartikov.sesame.input.InputControl
import me.aartikov.sesame.input.validation.form.FormValidatorBuilder
import me.aartikov.sesame.localizedstring.LocalizedString


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

fun InputValidatorBuilder.isNotBlank(errorMessage: LocalizedString) {
    validation(
        isValid = { it.isNotBlank() },
        errorMessage
    )
}

fun InputValidatorBuilder.isNotBlank(@StringRes errorMessageRes: Int) {
    isNotBlank(LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.regex(regex: Regex, errorMessage: LocalizedString) {
    validation(
        isValid = { regex.matches(it) },
        errorMessage
    )
}

fun InputValidatorBuilder.regex(regex: Regex, @StringRes errorMessageRes: Int) {
    regex(regex, LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.minSymbols(number: Int, errorMessage: LocalizedString) {
    validation(
        isValid = { it.length >= number },
        errorMessage
    )
}

fun InputValidatorBuilder.minSymbols(number: Int, @StringRes errorMessageRes: Int) {
    minSymbols(number, LocalizedString.resource(errorMessageRes))
}

fun InputValidatorBuilder.equalsTo(input: InputControl, errorMessage: LocalizedString) {
    validation(
        isValid = { it == input.text },
        errorMessage
    )
}

fun InputValidatorBuilder.equalsTo(input: InputControl, errorMessageRes: Int) {
    equalsTo(input, LocalizedString.resource(errorMessageRes))
}