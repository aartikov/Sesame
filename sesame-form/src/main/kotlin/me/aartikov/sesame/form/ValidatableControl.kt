package me.aartikov.sesame.form

import me.aartikov.sesame.localizedstring.LocalizedString

interface ValidatableControl<ValueT> {

    val value: ValueT

    var error: LocalizedString?

    val skipInValidation: Boolean

    fun requestFocus()
}