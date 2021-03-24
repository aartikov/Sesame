package me.aartikov.sesame.input

import me.aartikov.sesame.localizedstring.LocalizedString

interface Control<ValueT> {

    val value: ValueT

    val error: LocalizedString?

    val skipInValidation: Boolean
}