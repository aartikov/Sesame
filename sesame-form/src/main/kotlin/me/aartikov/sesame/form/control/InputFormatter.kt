package me.aartikov.sesame.form.control

import android.widget.EditText

/**
 * Allows to add additional options related to formatting for a given [EditText].
 * For example, to configure formatting with https://github.com/TinkoffCreditSystems/decoro library.
 */
interface InputFormatter {

    fun install(editText: EditText)
}