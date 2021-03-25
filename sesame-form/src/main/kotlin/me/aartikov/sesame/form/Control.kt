package me.aartikov.sesame.form

import me.aartikov.sesame.localizedstring.LocalizedString

interface Control<ValueT> {

    val value: ValueT

    val error: LocalizedString?

    val skipInValidation: Boolean
}