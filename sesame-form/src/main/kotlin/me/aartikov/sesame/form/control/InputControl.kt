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
 * Logical representation of an input field. It allows to configure an input field and manage its state from ViewModel.
 * To connect InputControl to UI use [ControlObserver.bind].
 * InputControl can be used for form validation, @see [FormValidator].
 */
class InputControl(
    override val propertyHostScope: CoroutineScope,
    initialText: String = "",
    val singleLine: Boolean = true,
    val maxLength: Int = Int.MAX_VALUE,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val filter: SymbolFilter? = null,
    val formatter: InputFormatter? = null
) : ValidatableControl<String>, PropertyHost {

    /**
     * Current text. Observable property.
     */
    var text: String by state(initialText)

    /**
     * Is control visible. Observable property.
     */
    var visible: Boolean by state(true)

    /**
     * Is control enabled. Observable property.
     */
    var enabled: Boolean by state(true)

    /**
     * Is control has focus. Observable property.
     */
    var hasFocus: Boolean by state(false)

    /**
     * Displayed error. Observable property.
     */
    override var error: LocalizedString? by state(null)

    /**
     * A command to scroll to this control.
     */
    val scrollToIt = command<Unit>()

    override val value by ::text

    override val skipInValidation by computed(::visible, ::enabled) { visible, enabled ->
        !visible || !enabled
    }

    /**
     * Called automatically when text is changed on a view side.
     */
    fun onTextChanged(text: String) {
        this.text = text
    }

    /**
     * Called automatically when focus is changed on a view side.
     */
    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus = hasFocus
    }

    override fun requestFocus() {
        scrollToIt()
        hasFocus = true
    }
}

/**
 * Creates [InputControl].
 */
fun PropertyHost.InputControl(
    initialText: String = "",
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    filter: SymbolFilter? = null,
    formatter: InputFormatter? = null
): InputControl {

    return InputControl(
        propertyHostScope,
        initialText,
        singleLine,
        maxLength,
        keyboardOptions,
        filter,
        formatter
    )
}