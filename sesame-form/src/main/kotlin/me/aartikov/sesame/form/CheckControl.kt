package me.aartikov.sesame.form

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state

class CheckControl(
    override val propertyHostScope: CoroutineScope,
    initialChecked: Boolean
) : ValidatableControl<Boolean>, PropertyHost {

    var checked: Boolean by state(initialChecked)
    var visible by state(true)
    var enabled by state(true)
    override var error: LocalizedString? by state(null)

    val scrollTo = command<Unit>()

    override val value by ::checked

    override val skipInValidation by computed(::visible, ::enabled) { visible, enabled ->
        !visible || !enabled
    }

    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }

    override fun requestFocus() {
        scrollTo()
    }
}

fun PropertyHost.CheckControl(initialChecked: Boolean = false) =
    CheckControl(propertyHostScope, initialChecked)
