package me.aartikov.lib.widget.dialog_control

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class DialogControl<T, R> {

    val displayed = MutableStateFlow<Display>(Display.Absent)

    fun show(data: T) {
        dismiss()
        displayed.value = Display.Displayed(data)
    }

    fun dismiss() {
        if (displayed.value is Display.Displayed<*>) {
            displayed.value = Display.Absent

        }
    }

    sealed class Display {
        data class Displayed<T>(val data: T) : Display()
        object Absent : Display()
    }
}

fun<T, R> dialogControl(): DialogControl<T, R> {
    return DialogControl()
}