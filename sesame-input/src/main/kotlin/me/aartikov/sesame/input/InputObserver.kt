package me.aartikov.sesame.input

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import me.aartikov.sesame.property.PropertyObserver

interface InputObserver : PropertyObserver {

    infix fun InputControl.bind(textInputLayout: TextInputLayout) {
        this bind textInputLayout.editText!!
        bindError(this, textInputLayout)
    }

    infix fun InputControl.bind(editText: EditText) {
        bindText(this, editText)
        bindFocus(this, editText)
    }

    private fun bindText(inputControl: InputControl, editText: EditText) {
        var updatingText = false

        inputControl::text bind { text ->
            val editable = editText.text
            if (!text.contentEquals(editable)) {
                updatingText = true
                editable.replace(0, editable.length, text)
                updatingText = false
            }
        }

        editText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!updatingText) {
                    inputControl.onTextChanged(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun bindFocus(inputControl: InputControl, editText: EditText) {
        var updatingFocus = false

        inputControl::hasFocus bind { hasFocus ->
            if (hasFocus != editText.hasFocus()) {
                updatingFocus = true
                if (hasFocus) {
                    editText.requestFocus()
                } else {
                    editText.clearFocus()
                }
                updatingFocus = false
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!updatingFocus) {
                inputControl.onFocusChanged(hasFocus)
            }
        }
    }

    private fun bindError(inputControl: InputControl, textInputLayout: TextInputLayout) {
        inputControl::error bind {
            textInputLayout.error = it
        }
    }
}