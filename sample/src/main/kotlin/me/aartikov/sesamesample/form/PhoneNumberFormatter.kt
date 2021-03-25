package me.aartikov.sesamesample.form

import android.widget.EditText
import me.aartikov.sesame.InputFormatter
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

object PhoneNumberFormatter : InputFormatter {

    override fun install(editText: EditText) {
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        MaskFormatWatcher(mask).installOn(editText)
    }
}