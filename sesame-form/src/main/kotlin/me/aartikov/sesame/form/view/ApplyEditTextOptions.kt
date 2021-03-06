package me.aartikov.sesame.form.view

import android.text.InputFilter
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import me.aartikov.sesame.form.control.*

fun EditText.applyOptions(
    singleLine: Boolean,
    maxLength: Int,
    filter: SymbolFilter?,
    formatter: InputFormatter?,
    keyboardOptions: KeyboardOptions
) {

    inputType = getInputType(singleLine, keyboardOptions)
    imeOptions = getImeOptions(keyboardOptions)

    if (filter != null) {
        filters += SymbolInputFilter(filter)
    }

    formatter?.install(this)

    if (maxLength != Int.MAX_VALUE) {
        filters += InputFilter.LengthFilter(maxLength)
    }
}

private fun getInputType(singleLine: Boolean, keyboardOptions: KeyboardOptions): Int {

    val typeFlags = when (keyboardOptions.keyboardType) {
        KeyboardType.Ascii -> InputType.TYPE_CLASS_TEXT    // is set as imeOptions
        KeyboardType.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        KeyboardType.Number -> InputType.TYPE_CLASS_NUMBER
        KeyboardType.SignedNumber -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        KeyboardType.DecimalNumber -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        KeyboardType.DecimalSignedNumber -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        KeyboardType.NumberPassword -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        KeyboardType.Password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        KeyboardType.VisiblePassword -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        KeyboardType.Phone -> InputType.TYPE_CLASS_PHONE
        KeyboardType.Text -> InputType.TYPE_CLASS_TEXT
        KeyboardType.Uri -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
    }

    val isTextClass = typeFlags and InputType.TYPE_MASK_CLASS == InputType.TYPE_CLASS_TEXT

    val multiLineFlags = if (!singleLine && isTextClass) InputType.TYPE_TEXT_FLAG_MULTI_LINE else 0

    val capitalizationFlags = if (isTextClass) {
        when (keyboardOptions.capitalization) {
            KeyboardCapitalization.None -> 0
            KeyboardCapitalization.Characters -> InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            KeyboardCapitalization.Words -> InputType.TYPE_TEXT_FLAG_CAP_WORDS
            KeyboardCapitalization.Sentences -> InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
    } else {
        0
    }

    return typeFlags or multiLineFlags or capitalizationFlags
}

private fun getImeOptions(keyboardOptions: KeyboardOptions): Int {

    val actionFlags = when (keyboardOptions.imeAction) {
        ImeAction.Default -> EditorInfo.IME_ACTION_UNSPECIFIED
        ImeAction.Done -> EditorInfo.IME_ACTION_DONE
        ImeAction.Go -> EditorInfo.IME_ACTION_GO
        ImeAction.Next -> EditorInfo.IME_ACTION_NEXT
        ImeAction.None -> EditorInfo.IME_ACTION_NONE
        ImeAction.Previous -> EditorInfo.IME_ACTION_PREVIOUS
        ImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
        ImeAction.Send -> EditorInfo.IME_ACTION_SEND
    }

    val asciiFlags = if (keyboardOptions.keyboardType == KeyboardType.Ascii) {
        EditorInfo.IME_FLAG_FORCE_ASCII
    } else {
        0
    }

    return actionFlags or asciiFlags
}