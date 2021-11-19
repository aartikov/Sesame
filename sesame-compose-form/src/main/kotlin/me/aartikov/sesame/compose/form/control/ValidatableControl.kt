package me.aartikov.sesame.compose.form.control

import androidx.compose.runtime.MutableState
import me.aartikov.sesame.localizedstring.LocalizedString

/**
 * Control that can be validated.
 * @param ValueT type of value managed by a control.
 *
 * @see: [InputControl]
 * @see: [CheckControl]
 */
interface ValidatableControl<ValueT> {

    /**
     * Control value.
     */
    val value: ValueT

    /**
     * Displayed error.
     */
    var error: MutableState<LocalizedString?>

    /**
     * Is control should be skipped during validation.
     */
    val skipInValidation: Boolean
}