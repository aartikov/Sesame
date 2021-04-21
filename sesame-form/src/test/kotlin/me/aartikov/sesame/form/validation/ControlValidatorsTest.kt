package me.aartikov.sesame.form.validation

import me.aartikov.sesame.form.control.CheckControl
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.form.utils.TestPropertyHost
import me.aartikov.sesame.form.validation.control.equalsTo
import me.aartikov.sesame.form.validation.control.isNotBlank
import me.aartikov.sesame.form.validation.control.minLength
import me.aartikov.sesame.form.validation.control.regex
import me.aartikov.sesame.form.validation.form.checked
import me.aartikov.sesame.form.validation.form.formValidator
import me.aartikov.sesame.localizedstring.LocalizedString
import org.junit.Assert.assertEquals
import org.junit.Test

class ControlValidatorsTest {

    companion object {
        private val ERROR = LocalizedString.raw("error")
        private val ERROR1 = LocalizedString.raw("error1")
        private val ERROR2 = LocalizedString.raw("error2")
        private val ERROR3 = LocalizedString.raw("error3")
    }

    @Test
    fun `is not blank`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            input(inputControl) {
                isNotBlank(ERROR)
            }
        }

        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = " "
        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "123"
        formValidator.validate()
        assertEquals(null, inputControl.error)
    }

    @Test
    fun regex() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            input(inputControl) {
                regex("a[bc]*".toRegex(), ERROR)
            }
        }

        inputControl.text = "abcd"
        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "abbbccb"
        formValidator.validate()
        assertEquals(null, inputControl.error)
    }

    @Test
    fun `not required input`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            input(inputControl, required = false) {
                regex("a[bc]*".toRegex(), ERROR)
            }
        }

        formValidator.validate()
        assertEquals(null, inputControl.error)
    }

    @Test
    fun `min length`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            input(inputControl) {
                minLength(5, ERROR)
            }
        }

        inputControl.text = "123"
        formValidator.validate()
        assertEquals(ERROR, inputControl.error)

        inputControl.text = "12345"
        formValidator.validate()
        assertEquals(null, inputControl.error)
    }

    @Test
    fun `equals to`() = with(TestPropertyHost()) {
        val inputControl1 = InputControl("text")
        val inputControl2 = InputControl()
        val formValidator = formValidator {
            input(inputControl2) {
                equalsTo(inputControl1, ERROR)
            }
        }

        inputControl2.text = "123"
        formValidator.validate()
        assertEquals(ERROR, inputControl2.error)

        inputControl2.text = "text"
        formValidator.validate()
        assertEquals(null, inputControl2.error)
    }

    @Test
    fun `multiple input validations`() = with(TestPropertyHost()) {
        val inputControl = InputControl()
        val formValidator = formValidator {
            input(inputControl) {
                isNotBlank(ERROR1)
                regex("a[bc]*".toRegex(), ERROR2)
                minLength(5, ERROR3)
            }
        }

        formValidator.validate()
        assertEquals(ERROR1, inputControl.error)

        inputControl.text = "b"
        formValidator.validate()
        assertEquals(ERROR2, inputControl.error)

        inputControl.text = "abc"
        formValidator.validate()
        assertEquals(ERROR3, inputControl.error)

        inputControl.text = "abccc"
        formValidator.validate()
        assertEquals(null, inputControl.error)
    }

    @Test
    fun checked() = with(TestPropertyHost()) {
        val checkControl = CheckControl()
        val formValidator = formValidator {
            checked(checkControl, ERROR)
        }

        formValidator.validate()
        assertEquals(ERROR, checkControl.error)

        checkControl.checked = true
        formValidator.validate()
        assertEquals(null, checkControl.error)
    }
}