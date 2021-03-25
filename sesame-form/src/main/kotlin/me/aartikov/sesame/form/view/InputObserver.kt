package me.aartikov.sesame.form.view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import me.aartikov.sesame.form.CheckControl
import me.aartikov.sesame.form.InputControl
import me.aartikov.sesame.form.applyOptions
import me.aartikov.sesame.property.PropertyObserver

interface InputObserver : PropertyObserver {

    infix fun InputControl.bind(textInputLayout: TextInputLayout) {
        val editText = textInputLayout.editText!!
        editText.applyOptions(this.singleLine, this.maxLength, this.keyboardOptions)
        bindText(this, editText)
        bindFocus(this, editText)
        bindError(this, textInputLayout)
        ::visible bind { textInputLayout.visibility = if (it) View.VISIBLE else View.GONE }
        ::enabled bind { textInputLayout.isEnabled = it }
    }

    infix fun CheckControl.bind(checkBox: CompoundButton) {
        bindChecked(this, checkBox)
        ::visible bind { checkBox.visibility = if (it) View.VISIBLE else View.GONE }
        ::enabled bind { checkBox.isEnabled = it }
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
            textInputLayout.error = it?.resolve(textInputLayout.context)
        }
    }

    private fun bindChecked(checkControl: CheckControl, checkBox: CompoundButton) {
        var updatingChecked = false

        checkControl::checked bind { checked ->
            if (checked != checkBox.isChecked) {
                updatingChecked = true
                checkBox.isChecked = checked
                updatingChecked = false
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (!updatingChecked) {
                checkControl.onCheckedChanged(isChecked)
            }
        }
    }
}