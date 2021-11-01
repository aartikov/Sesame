package me.aartikov.sesame.compose.form.control

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.VisualTransformation
import me.aartikov.sesame.localizedstring.LocalizedString

class InputControl(
    initialText: String = "",
    val singleLine: Boolean = true,
    val maxLength: Int = Int.MAX_VALUE,
    val keyboardOptions: KeyboardOptions,
    val ignoreRegex: Regex? = null,
    val transformer: VisualTransformation = VisualTransformation.None
) : ValidatableControl<String> {

    /**
     * Current text.
     */
    var text: MutableState<String> = mutableStateOf(initialText)

    /**
     * Is control visible.
     */
    var visible: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Is control enabled.
     */
    var enabled: MutableState<Boolean> = mutableStateOf(true)

    /**
     * Is control has focus.
     */
    var hasFocus: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Displayed error.
     */
    override var error: MutableState<LocalizedString?> = mutableStateOf(null)

    override val value by derivedStateOf { text.value }

    override val skipInValidation by derivedStateOf { !visible.value || !enabled.value }

    /**
     * Called automatically when text is changed on a view side.
     */
    fun onTextChanged(text: String) {
        if ((ignoreRegex == null || !text.contains(ignoreRegex)) && text.length < maxLength) {
            this.text.value =  text
        }
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus.value = hasFocus
    }

    override fun requestFocus() {
        hasFocus.value = true
    }
}
