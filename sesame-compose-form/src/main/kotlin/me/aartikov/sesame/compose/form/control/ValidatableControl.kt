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
     * Control value. Observable property.
     */
    val value: ValueT

    /**
     * Displayed error. Observable property.
     */
    var error: MutableState<LocalizedString?>

    /**
     * Is control should be skipped during validation. Observable property.
     */
    val skipInValidation: Boolean

    /**
     * Moves focus to a control.
     */
    fun requestFocus()
}