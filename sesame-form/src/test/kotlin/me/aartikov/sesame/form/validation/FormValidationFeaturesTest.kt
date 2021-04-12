package me.aartikov.sesame.form.validation

import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.form.utils.TestPropertyHost
import me.aartikov.sesame.form.validation.control.minLength
import me.aartikov.sesame.form.validation.form.*
import me.aartikov.sesame.localizedstring.LocalizedString
import org.junit.Assert.assertEquals
import org.junit.Test

class FormValidationFeaturesTest {

    companion object {
        private val ERROR = LocalizedString.raw("error")
    }

    @Test
    fun `hide error on value changed`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            features = listOf(HideErrorOnValueChanged)

            input(inputControl) {
                minLength(5, ERROR)
            }
        }

        inputControl.text = "123"
        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "1234"
        assertEquals(null, inputControl.error)
    }

    @Test
    fun `revalidate on value changed`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            features = listOf(RevalidateOnValueChanged)

            input(inputControl) {
                minLength(5, ERROR)
            }
        }

        inputControl.text = "123"
        assertEquals(null, inputControl.error)

        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "1234"
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "12345"
        assertEquals(null, inputControl.error)

        inputControl.text = "1234"
        assertEquals(null, inputControl.error)
    }

    @Test
    fun `validate on focus lost`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        formValidator {
            features = listOf(ValidateOnFocusLost)

            input(inputControl) {
                minLength(5, ERROR)
            }
        }

        inputControl.hasFocus = true
        inputControl.text = "123"
        assertEquals(null, inputControl.error)

        inputControl.hasFocus = false
        assertEquals(ERROR, inputControl.error)
    }

    @Test
    fun `set focus on first invalid control after validation`() = with(TestPropertyHost()) {
        val inputControl1 = InputControl()
        val inputControl2 = InputControl()
        val formValidator = formValidator {
            features = listOf(SetFocusOnFirstInvalidControlAfterValidation)

            input(inputControl1) {
                minLength(5, ERROR)
            }

            input(inputControl2) {
                minLength(5, ERROR)
            }
        }

        inputControl1.text = "12345"
        inputControl2.text = "123"
        formValidator.validate()
        assertEquals(false, inputControl1.hasFocus)
        assertEquals(true, inputControl2.hasFocus)
    }
}