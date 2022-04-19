package me.aartikov.sesame.compose.form.control

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.aartikov.sesame.localizedstring.LocalizedString

/**
 * Logical representation of a control with checkable state (CheckBox, Switch, etc). It allows to manage checked state from ViewModel.
 */
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

    private val mutableScrollToItEventFlow = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val scrollToItEvent get() = mutableScrollToItEventFlow.asSharedFlow()

    override fun requestFocus() {
        mutableScrollToItEventFlow.tryEmit(Unit)
    }

    /**
     * Called automatically when checked is changed on a view side.
     */
    fun onCheckedChanged(checked: Boolean) {
        this.checked = checked
    }
}
