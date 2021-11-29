package me.aartikov.sesamecomposesample.form

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object RussianPhoneNumberVisualTransformation : VisualTransformation {
    private const val FIRST_HARDCODE_SLOT = "+7 ("
    private const val SECOND_HARDCODE_SLOT = ") "
    private const val DECORATE_HARDCODE_SLOT = "-"

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var output = ""
        if (text.text.isNotEmpty()) output += FIRST_HARDCODE_SLOT
        for (i in trimmed.indices) {
            output += trimmed[i]
            when (i) {
                2 -> output += SECOND_HARDCODE_SLOT
                5 -> output += DECORATE_HARDCODE_SLOT
                7 -> output += DECORATE_HARDCODE_SLOT
            }
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 2) return offset + 4
                if (offset <= 5) return offset + 6
                if (offset <= 7) return offset + 7
                if (offset <= 9) return offset + 8
                return 18
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 7) return offset - 4
                if (offset <= 11) return offset - 6
                if (offset <= 15) return offset - 7
                return 10
            }
        }

        return TransformedText(AnnotatedString(output), numberOffsetTranslator)
    }
}