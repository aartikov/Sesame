package me.aartikov.sesame.compose.form.validation.form

import kotlinx.coroutines.CoroutineScope

/**
 * High level feature for [FormValidator].
 */
interface FormValidationFeature {

    fun install(coroutineScope: CoroutineScope, formValidator: FormValidator)
}