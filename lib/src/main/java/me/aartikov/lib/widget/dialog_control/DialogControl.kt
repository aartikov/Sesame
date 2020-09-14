package me.aartikov.lib.widget.dialog_control

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

class DialogControl<T: Any, R: Any> {

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
        this.resultChannel.offer(result)
    }

    fun dismiss() {
        data.value = null
        resultChannel.offer(null)
    }
}

fun <T: Any, R: Any> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}