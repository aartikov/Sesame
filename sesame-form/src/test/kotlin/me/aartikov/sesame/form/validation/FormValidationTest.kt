package me.aartikov.sesame.form.validation

import me.aartikov.sesame.form.control.CheckControl
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.form.utils.TestPropertyHost
import me.aartikov.sesame.form.validation.control.ValidationResult
import me.aartikov.sesame.form.validation.control.equalsTo
import me.aartikov.sesame.form.validation.control.isNotBlank
import me.aartikov.sesame.form.validation.control.minLength
import me.aartikov.sesame.form.validation.form.FormValidationResult
import me.aartikov.sesame.form.validation.form.checked
import me.aartikov.sesame.form.validation.form.formValidator
import me.aartikov.sesame.localizedstring.LocalizedString
import org.junit.Assert.assertEquals
import org.junit.Test

class FormValidationTest {

    companion object {
        private val ERROR1 = LocalizedString.raw("error1")
        private val ERROR2 = LocalizedString.raw("error2")
        private val ERROR3 = LocalizedString.raw("error3")
    }

    @Test
    fun `multiple controls`() = with(TestPropertyHost()) {
        val inputControl1 = InputControl()
        val inputControl2 = InputControl()
        val checkControl = CheckControl()

        val formValidator = formValidator {
            input(inputControl1) {
                minLength(5, ERROR1)
            }

            input(inputControl2) {
                equalsTo(inputControl1, ERROR2)
            }

            checked(checkControl, ERROR3)
        }

        // All invalid
        inputControl1.text = "123"
        val result1 = formValidator.validate()
        val expectedResult1 = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Invalid(ERROR1),
                inputControl2 to ValidationResult.Invalid(ERROR2),
                checkControl to ValidationResult.Invalid(ERROR3)

            )
        )
        assertEquals(expectedResult1, result1)
        assert(!result1.isValid)

        // All valid
        inputControl1.text = "12345"
        inputControl2.text = "12345"
        checkControl.checked = true
        val result2 = formValidator.validate()
        val expectedResult2 = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Valid,
                inputControl2 to ValidationResult.Valid,
                checkControl to ValidationResult.Valid

            )
        )
        assertEquals(expectedResult2, result2)
        assert(result2.isValid)
    }

    @Test
    fun `skips hidden control`() = with(TestPropertyHost()) {
        val inputControl1 = InputControl()
        val inputControl2 = InputControl()
        inputControl2.visible = false

        val formValidator = formValidator {
            input(inputControl1) {
                isNotBlank(ERROR1)
            }

            input(inputControl2) {
                isNotBlank(ERROR2)
            }
        }

        val result = formValidator.validate()
        val expectedResult = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Invalid(ERROR1),
                inputControl2 to ValidationResult.Skipped
            )
        )
        assertEquals(expectedResult, result)
    }
}