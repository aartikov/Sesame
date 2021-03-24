package me.aartikov.sesame.input

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.localizedstring.LocalizedString
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.state

class InputControl(
    override val propertyHostScope: CoroutineScope,
    initialText: String = "",
    val singleLine: Boolean = true,
    val maxLength: Int = Int.MAX_VALUE,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) : Control<String>, PropertyHost {

    var text: String by state(initialText)
    var hasFocus: Boolean by state(false)
    override var error: LocalizedString? by state(null)
    override var skipInValidation by state(false)      // TODO: computed from visible and enabled

    override val value by ::text

    fun onTextChanged(text: String) {
        this.text = text
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus = hasFocus
    }
}

fun PropertyHost.InputControl(
    initialText: String = "",
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
): InputControl {

    return InputControl(
        propertyHostScope,
        initialText,
        singleLine,
        maxLength,
        keyboardOptions
    )
}