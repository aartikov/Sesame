package me.aartikov.sesame.compose.form.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import me.aartikov.sesame.compose.form.validation.form.FormValidator
import me.aartikov.sesame.localizedstring.LocalizedString

class CheckControl(
    initialChecked: Boolean = false
) : ValidatableControl<Boolean> {

    /**
     * Is control checked. Observable property.
     */
    var checked: MutableState<Boolean> = mutableStateOf(initialChecked)

    /**
     * Is control visible. Observable property.
     */
    var visible: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Is control enabled. Observable property.
     */
    var enabled: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Displayed error. Observable property.
     */
    override var error: MutableState<LocalizedString?> = mutableStateOf(null)

    /**
     * A command to scroll to this control.
     */
   /* val scrollToIt = command<Unit>()*/

    override val value by derivedStateOf { checked.value }

    override val skipInValidation by derivedStateOf { !visible.value || !enabled.value }

    /**
     * Called automatically when checked is changed on a view side.
     */
    fun onCheckedChanged(checked: Boolean) {
        this.checked.value = checked
    }

    override fun requestFocus() {
        //scrollToIt()
    }
}
