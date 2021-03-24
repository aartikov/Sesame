package me.aartikov.sesame.input

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.state

class CheckControl(
    override val propertyHostScope: CoroutineScope,
    initialChecked: Boolean
) : Control<Boolean>, PropertyHost {

    var checked: Boolean by state(initialChecked)
    override var error: LocalizedString? by state(null)
    override var skipInValidation by state(false)   // TODO: computed from visible and enabled

    override val value by ::checked

    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }
}

fun PropertyHost.CheckControl(initialChecked: Boolean = false) =
    CheckControl(propertyHostScope, initialChecked)
