package me.aartikov.sesame.compose.form.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import me.aartikov.sesame.localizedstring.LocalizedString

class CheckControl(
    initialChecked: Boolean = false
) : ValidatableControl<Boolean> {

    /**
     * Is control checked.
     */
    var checked: MutableState<Boolean> = mutableStateOf(initialChecked)

    /**
     * Is control visible.
     */
    var visible: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Is control enabled.
     */
    var enabled: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Displayed error.
     */
    override var error: MutableState<LocalizedString?> = mutableStateOf(null)

    override val value by derivedStateOf { checked.value }

    override val skipInValidation by derivedStateOf { !visible.value || !enabled.value }

    /**
     * Called automatically when checked is changed on a view side.
     */
    fun onCheckedChanged(checked: Boolean) {
        this.checked.value = checked
    }
}
