package me.aartikov.lib.widget.dialog_control

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

class DialogControl<T, R> {

    internal val displayed = MutableStateFlow<Display<T>>(Display.Absent)
    private val result = Channel<R>(Channel.UNLIMITED)

    fun show(data: T) {
        dismiss()
        displayed.value = Display.Displayed(data)
    }

    suspend fun showForResult(data: T): R? {

        dismiss()

        displayed.value = Display.Displayed(data)

        return result.receive()
    }

    fun sendResult(result: R) {
        this.result.offer(result)
        dismiss()
    }

    fun dismiss() {
        if (displayed.value is Display.Displayed) {
            displayed.value = Display.Absent
        }
    }

    sealed class Display<out T> {
        data class Displayed<T>(val data: T) : Display<T>()
        object Absent : Display<Nothing>()
    }
}

fun <T, R> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}