package me.aartikov.sesame.form.control

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.form.validation.form.FormValidator
import me.aartikov.sesame.form.view.ControlObserver
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state

/**
 * Logical representation of a control with checkable state (CheckBox, Switch, etc). It allows to manage checked state from ViewModel.
 * To connect CheckControl to UI use [ControlObserver.bind].
 * CheckControl can be used for form validation, @see [FormValidator].
 */
class CheckControl(
    override val propertyHostScope: CoroutineScope,
    initialChecked: Boolean = false
) : ValidatableControl<Boolean>, PropertyHost {

    /**
     * Is control checked. Observable property.
     */
    var checked: Boolean by state(initialChecked)

    /**
     * Is control visible. Observable property.
     */
    var visible: Boolean by state(true)

    /**
     * Is control enabled. Observable property.
     */
    var enabled: Boolean by state(true)

    /**
     * Displayed error. Observable property.
     */
    override var error: LocalizedString? by state(null)

    /**
     * A command to scroll to this control.
     */
    val scrollToIt = command<Unit>()

    override val value by ::checked

    override val skipInValidation by computed(::visible, ::enabled) { visible, enabled ->
        !visible || !enabled
    }

    /**
     * Called automatically when checked is changed on a view side.
     */
    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }

    override fun requestFocus() {
        scrollToIt()
    }
}

/**
 * Creates [CheckControl]
 */
fun PropertyHost.CheckControl(initialChecked: Boolean = false) =
    CheckControl(propertyHostScope, initialChecked)
