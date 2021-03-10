package me.aartikov.sesame.input

import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.state

class InputControl(
    override val propertyHostScope: CoroutineScope,
    initialText: String
) : PropertyHost {

    var text: String by state(initialText)
    var hasFocus: Boolean by state(false)
    var error: String? by state(null)

    fun onTextChanged(text: String) {
        this.text = text
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus = hasFocus
    }
}

fun PropertyHost.InputControl(initialText: String = "") =
    InputControl(propertyHostScope, initialText)