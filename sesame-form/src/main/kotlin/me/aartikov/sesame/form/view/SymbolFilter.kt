package me.aartikov.sesame.form.view

import android.text.InputFilter
import android.text.Spanned

internal class SymbolFilter(
    private val filter: (Char) -> Boolean
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var keepOriginal = true
        val sb = StringBuilder(end - start)

        for (i in start until end) {
            val c = source[i]
            if (filter(c)) {
                sb.append(c)
            } else {
                keepOriginal = false
            }
        }

        return if (keepOriginal) {
            null
        } else {
            sb
        }
    }
}