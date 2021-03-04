package me.aartikov.sesame.dialog

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Helps to manage a dialog state in View Model.
 * Use [dialogControl] to create it.
 * Use [DialogObserver] to display a dialog.
 */
class DialogControl<T : Any, R : Any> internal constructor() {

    internal sealed class State<out T : Any> {
        data class Shown<T : Any>(val data: T, val forResult: Boolean) : State<T>()
        object Hidded : State<Nothing>()
    }

    internal val state = MutableStateFlow<State<T>>(State.Hidded)
    private val resultChannel = Channel<R?>(Channel.RENDEZVOUS)

    /**
     * Shows a dialog.
     * @param data custom data
     */
    fun show(data: T) {
        if (isShownForResult()) {
            resultChannel.offer(null)
        }
        state.value = State.Shown(data, forResult = false)
    }

    /**
     * Shows a dialog for result. It suspends until a result will be received.
     * @param data custom data
     * @return result or null if a dialog was dismissed
     */
    suspend fun showForResult(data: T): R? {
        if (isShownForResult()) {
            resultChannel.offer(null)
        }
        state.value = State.Shown(data, forResult = true)
        return resultChannel.receive()
    }

    /**
     * Sends result for a dialog shown for result. Should be called from a view side (See: [DialogObserver.bind]).
     */
    fun sendResult(result: R) {
        state.value = State.Hidded
        this.resultChannel.offer(result)
    }

    /**
     * Hides a dialog. If a dialog was shown for result than null will be returned as a result.
     */
    fun dismiss() {
        if (state.value == State.Hidded) {
            return
        }

        val wasShownForResult = isShownForResult()
        state.value = State.Hidded
        if (wasShownForResult) {
            resultChannel.offer(null)
        }
    }

    private fun isShownForResult(): Boolean {
        return (state.value as? State.Shown<T>)?.forResult == true
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
 * Creates [DialogControl].
 */
fun <T : Any, R : Any> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}