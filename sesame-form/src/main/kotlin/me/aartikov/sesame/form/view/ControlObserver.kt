package me.aartikov.sesame.form.view

import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import me.aartikov.sesame.form.control.CheckControl
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.property.PropertyObserver

/**
 * Gives access to binding methods for controls.
 */
interface ControlObserver : PropertyObserver {

    /**
     * Connects [InputControl] and [TextInputLayout] with two-way binding.
     */
    infix fun InputControl.bind(textInputLayout: TextInputLayout) {
        val editText = textInputLayout.editText!!
        editText.applyOptions(singleLine, maxLength, filter, formatter, keyboardOptions)
        bindText(this, editText)
        bindFocus(this, editText)
        bindError(this, textInputLayout)
        ::visible bind { textInputLayout.visibility = if (it) View.VISIBLE else View.GONE }
        ::enabled bind { textInputLayout.isEnabled = it }
        scrollToIt bind { scrollToView(textInputLayout) }
    }

    /**
     * Connects [CheckControl] and a [checkableView] with two-way binding.
     */
    infix fun CheckControl.bind(checkableView: CompoundButton) {
        bindChecked(this, checkableView)
        ::visible bind { checkableView.visibility = if (it) View.VISIBLE else View.GONE }
        ::enabled bind { checkableView.isEnabled = it }
        scrollToIt bind { scrollToView(checkableView) }
    }

    private fun bindText(inputControl: InputControl, editText: EditText) {
        var updating = false

        inputControl::text bind { text ->
            val editable = editText.text
            if (!text.contentEquals(editable)) {
                updating = true
                editable.replace(0, editable.length, text)
                updating = false
            }
        }

        editText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!updating) {
                    inputControl.onTextChanged(s.toString())
                }
            }
        })
    }

    private fun bindFocus(inputControl: InputControl, editText: EditText) {
        var updating = false

        inputControl::hasFocus bind { hasFocus ->
            if (hasFocus != editText.hasFocus()) {
                updating = true
                if (hasFocus) {
                    editText.requestFocus()
                } else {
                    editText.clearFocus()
                }
                updating = false
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!updating) {
                inputControl.onFocusChanged(hasFocus)
            }
        }
    }

    private fun bindError(inputControl: InputControl, textInputLayout: TextInputLayout) {
        inputControl::error bind {
            textInputLayout.error = it?.resolve(textInputLayout.context)
        }
    }

    private fun bindChecked(checkControl: CheckControl, checkableView: CompoundButton) {
        var updating = false

        checkControl::checked bind { checked ->
            if (checked != checkableView.isChecked) {
                updating = true
                checkableView.isChecked = checked
                updating = false
            }
        }

        checkableView.setOnCheckedChangeListener { _, isChecked ->
            if (!updating) {
                checkControl.onCheckedChanged(isChecked)
            }
        }
    }

    private fun scrollToView(view: View) {
        val rect = Rect(0, 0, view.width, view.height)
        view.requestRectangleOnScreen(rect, false)
    }
}