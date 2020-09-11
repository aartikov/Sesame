package me.aartikov.lib.widget.dialog_control

import kotlinx.coroutines.flow.MutableStateFlow

class DialogControl<T, R> {

    internal val displayed = MutableStateFlow<Display<T>>(Display.Absent)

    fun show(data: T) {
        dismiss()
        displayed.value = Display.Displayed(data)
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