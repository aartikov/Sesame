package me.aartikov.sesame.compose.form.control

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import me.aartikov.sesame.localizedstring.LocalizedString

class InputControl(
    initialText: String = "",
    val singleLine: Boolean = true,
    val maxLength: Int = Int.MAX_VALUE,
    val keyboardOptions: KeyboardOptions,
    val textTransformation: TextTransformation? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None
) : ValidatableControl<String> {

    /**
     * Current text.
     */
    var text: String by mutableStateOf("")

    /**
     * Is control visible.
     */
    var visible: Boolean by mutableStateOf(true)

    /**
     * Is control enabled.
     */
    var enabled: Boolean by mutableStateOf(true)

    /**
     * Is control has focus.
     */
    var hasFocus = MutableStateFlow(false)

    /**
     * Displayed error.
     */
    override var error: LocalizedString? by mutableStateOf(null)

    override val value by ::text

    override val valueChangeEvent = MutableStateFlow(text)

    override val skipInValidationChangeEvent = MutableStateFlow(false)

    override val skipInValidation by derivedStateOf {
        val skip = !visible || !enabled
        skipInValidationChangeEvent.value = skip
        return@derivedStateOf skip
    }

    init {
        onTextChanged(initialText)
    }

    fun onTextChanged(text: String) {
        if (text.length < maxLength) {
            if (textTransformation != null) {
                this.text = textTransformation.transform(text = text)
            } else {
                this.text = text
            }
        }
        valueChangeEvent.value = this.text
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus.value = hasFocus
    }
}