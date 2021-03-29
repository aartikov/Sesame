package me.aartikov.sesamesample.form

import android.widget.EditText
import me.aartikov.sesame.form.InputFormatter
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


object RussianPhoneNumberFormatter : InputFormatter {

    override fun install(editText: EditText) {
        val slots = PredefinedSlots.RUS_PHONE_NUMBER.copyOf()
        slots[1].setValueInterpreter { character ->
            if (character == '8') '7' else character    //replace 8 to 7 in "+7"
        }

        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true       // allow to erase "+7("

        MaskFormatWatcher(mask).installOn(editText)
    }
}