package me.aartikov.lib.dialog

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class DialogControl<T : Any, R : Any> internal constructor() {

    internal sealed class State<out T : Any> {
        data class Shown<T : Any>(val data: T, val forResult: Boolean) : State<T>()
        object Hidded : State<Nothing>()
    }

    internal val state = MutableStateFlow<State<T>>(State.Hidded)
    private val resultChannel = Channel<R?>(Channel.RENDEZVOUS)

    fun show(data: T) {
        if (isShownForResult()) {
            resultChannel.offer(null)
        }
        state.value = State.Shown(data, forResult = false)
    }

    suspend fun showForResult(data: T): R? {
        if (isShownForResult()) {
            resultChannel.offer(null)
        }
        state.value = State.Shown(data, forResult = true)
        return resultChannel.receive()
    }

    fun sendResult(result: R) {
        state.value = State.Hidded
        this.resultChannel.offer(result)
    }

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

fun <R : Any> DialogControl<Unit, R>.show() = show(Unit)

suspend fun <R : Any> DialogControl<Unit, R>.showForResult(): R? = showForResult(Unit)

fun <T : Any, R : Any> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}