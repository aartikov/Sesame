package me.aartikov.sesame.compose.form.control

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    private var _text by mutableStateOf(correctText(initialText))

    /**
     * Current text.
     */
    var text: String
        get() = _text
        set(value) {
            _text = correctText(value)
        }

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

    /**
     * Called automatically when text is changed on a view side.
     */
    fun onTextChanged(text: String) {
        this.text = text
        valueChangeEvent.value = this.text
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus.value = hasFocus
    }

    private fun correctText(text: String): String {
        val transformedText = textTransformation?.transform(text) ?: text
        return transformedText.take(maxLength)
    }
}
