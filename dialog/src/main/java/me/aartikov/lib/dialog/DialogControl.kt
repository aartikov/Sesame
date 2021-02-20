package me.aartikov.lib.dialog

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class DialogControl<T : Any, R : Any> {

    internal val data = MutableStateFlow<T?>(null)
    private val resultChannel = Channel<R?>(Channel.RENDEZVOUS)

    fun show(data: T) {
        this.data.value = data
    }

    suspend fun showForResult(data: T): R? {
        this.data.value = data
        return resultChannel.receive()
    }

    fun sendResult(result: R) {
        data.value = null
        this.resultChannel.offer(result)
    }

    fun dismiss() {
        if (data.value != null) {
            data.value = null
            resultChannel.offer(null)
        }
    }
}

fun <R : Any> DialogControl<Unit, R>.show() = show(Unit)

suspend fun <R : Any> DialogControl<Unit, R>.showForResult(): R? = showForResult(Unit)

fun <T : Any, R : Any> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}