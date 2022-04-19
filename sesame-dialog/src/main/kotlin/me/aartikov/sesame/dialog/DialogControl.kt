package me.aartikov.sesame.dialog

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Helps to manage a dialog state in View Model.
 * Use [DialogObserver] to display a dialog.
 */
class DialogControl<T : Any, R : Any> {

    sealed class State<out T : Any> {
        data class Shown<T : Any>(val data: T, val forResult: Boolean) : State<T>()
        object Hidden : State<Nothing>()
    }

    private val mutableStateFlow = MutableStateFlow<State<T>>(State.Hidden)
    private val resultChannel = Channel<R?>(Channel.RENDEZVOUS)

    val stateFlow: StateFlow<State<T>>
        get() = mutableStateFlow

    /**
     * Shows a dialog.
     * @param data custom data
     */
    fun show(data: T) {
        if (isShownForResult()) {
            resultChannel.trySend(null)
        }
        mutableStateFlow.value = State.Shown(data, forResult = false)
    }

    /**
     * Shows a dialog for result. It suspends until a result will be received.
     * @param data custom data
     * @return result or null if a dialog was dismissed
     */
    suspend fun showForResult(data: T): R? {
        if (isShownForResult()) {
            resultChannel.trySend(null)
        }
        mutableStateFlow.value = State.Shown(data, forResult = true)
        return resultChannel.receive()
    }

    /**
     * Sends result for a dialog shown for result. Should be called from a view side (See: [DialogObserver.bind]).
     */
    fun sendResult(result: R) {
        mutableStateFlow.value = State.Hidden
        this.resultChannel.trySend(result)
    }

    /**
     * Hides a dialog. If a dialog was shown for result than null will be returned as a result.
     */
    fun dismiss() {
        if (mutableStateFlow.value == State.Hidden) {
            return
        }

        val wasShownForResult = isShownForResult()
        mutableStateFlow.value = State.Hidden
        if (wasShownForResult) {
            resultChannel.trySend(null)
        }
    }

    private fun isShownForResult(): Boolean {
        return (mutableStateFlow.value as? State.Shown<T>)?.forResult == true
    }
}

/**
 * A shortcut to show a dialog without custom data.
 */
fun <R : Any> DialogControl<Unit, R>.show() = show(Unit)

/**
 * A shortcut to show a dialog for result without custom data.
 */
suspend fun <R : Any> DialogControl<Unit, R>.showForResult(): R? = showForResult(Unit)

/**
 * Returns [DialogControl.State.Shown.data] if it is available or null otherwise
 */
val <T : Any> DialogControl.State<T>.dataOrNull
    get() = (this as? DialogControl.State.Shown)?.data