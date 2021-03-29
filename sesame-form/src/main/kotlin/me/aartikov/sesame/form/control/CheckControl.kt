package me.aartikov.sesame.form.control

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state

class CheckControl(
    override val propertyHostScope: CoroutineScope,
    initialChecked: Boolean = false
) : ValidatableControl<Boolean>, PropertyHost {

    var checked: Boolean by state(initialChecked)
    var visible: Boolean by state(true)
    var enabled: Boolean by state(true)
    override var error: LocalizedString? by state(null)

    val scrollToIt = command<Unit>()

    override val value by ::checked

    override val skipInValidation by computed(::visible, ::enabled) { visible, enabled ->
        !visible || !enabled
    }

    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }

    override fun requestFocus() {
        scrollToIt()
    }
}

fun PropertyHost.CheckControl(initialChecked: Boolean = false) =
    CheckControl(propertyHostScope, initialChecked)
