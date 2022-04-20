package me.aartikov.sesame.compose.form.control

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.aartikov.sesame.localizedstring.LocalizedString

/**
 * Logical representation of an input field. It allows to configure an input field and manage its state from ViewModel.
 */
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
    var hasFocus: Boolean by mutableStateOf(false)

    /**
     * Displayed error.
     */
    override var error: LocalizedString? by mutableStateOf(null)

    override val value by ::text

    override val skipInValidation by derivedStateOf { !visible || !enabled }

    private val mutableScrollToItEventFlow = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val scrollToItEvent get() = mutableScrollToItEventFlow.asSharedFlow()

    override fun requestFocus() {
        this.hasFocus = true
        mutableScrollToItEventFlow.tryEmit(Unit)
    }

    /**
     * Should be called when text is changed on a view side.
     */
    fun onTextChanged(text: String) {
        this.text = text
    }

    fun onFocusChanged(hasFocus: Boolean) {
        this.hasFocus = hasFocus
    }

    private fun correctText(text: String): String {
        val transformedText = textTransformation?.transform(text) ?: text
        return transformedText.take(maxLength)
    }
}
