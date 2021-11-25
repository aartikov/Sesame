package me.aartikov.sesame.compose.form.control

import androidx.compose.runtime.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import me.aartikov.sesame.localizedstring.LocalizedString

class CheckControl(
    initialChecked: Boolean = false
) : ValidatableControl<Boolean> {

    /**
     * Is control checked.
     */
    var checked: Boolean by mutableStateOf(initialChecked)

    /**
     * Is control visible.
     */
    var visible: Boolean by mutableStateOf(true)

    /**
     * Is control enabled.
     */
    var enabled: Boolean by mutableStateOf(true)

    /**
     * Displayed error.
     */
    override var error: LocalizedString? by mutableStateOf(null)

    override val value by ::checked

    override val skipInValidation by derivedStateOf { !visible || !enabled }

    private val scrollToItChannel = Channel<Unit>(Channel.UNLIMITED)

    val scrollToItEvent = scrollToItChannel.receiveAsFlow()

    override fun requestFocus() {
        scrollToItChannel.trySend(Unit)
    }

    /**
     * Called automatically when checked is changed on a view side.
     */
    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }
}
