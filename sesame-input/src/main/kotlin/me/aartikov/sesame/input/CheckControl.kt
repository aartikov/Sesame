package me.aartikov.sesame.input

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.state

class CheckControl(
    override val propertyHostScope: CoroutineScope,
    initialChecked: Boolean
) : PropertyHost {

    var checked: Boolean by state(initialChecked)

    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }
}

fun PropertyHost.CheckControl(initialChecked: Boolean = false) =
    CheckControl(propertyHostScope, initialChecked)
