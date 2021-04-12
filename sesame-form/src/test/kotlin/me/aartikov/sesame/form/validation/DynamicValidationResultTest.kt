package me.aartikov.sesame.form.validation

import me.aartikov.sesame.form.control.CheckControl
import me.aartikov.sesame.form.control.InputControl
import me.aartikov.sesame.form.utils.TestPropertyHost
import me.aartikov.sesame.form.validation.control.ValidationResult
import me.aartikov.sesame.form.validation.control.equalsTo
import me.aartikov.sesame.form.validation.control.minLength
import me.aartikov.sesame.form.validation.form.FormValidationResult
import me.aartikov.sesame.form.validation.form.checked
import me.aartikov.sesame.form.validation.form.dynamicValidationResult
import me.aartikov.sesame.form.validation.form.formValidator
import me.aartikov.sesame.localizedstring.LocalizedString
import org.junit.Assert.assertEquals
import org.junit.Test

class DynamicValidationResultTest {

    companion object {
        private val ERROR1 = LocalizedString.raw("error1")
        private val ERROR2 = LocalizedString.raw("error2")
        private val ERROR3 = LocalizedString.raw("error3")
    }

    @Test
    fun `dynamic validation result`() = with(TestPropertyHost()) {
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

        val validationResult by dynamicValidationResult(formValidator)

        // One valid
        val expected1 = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Invalid(ERROR1),
                inputControl2 to ValidationResult.Valid,
                checkControl to ValidationResult.Invalid(ERROR3)
            )
        )
        assertEquals(expected1, validationResult)
        assert(!validationResult.isValid)
        assertEquals(null, inputControl1.error)   // does not display result on controls

        // All invalid
        inputControl1.text = "123"
        val expected2 = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Invalid(ERROR1),
                inputControl2 to ValidationResult.Invalid(ERROR2),
                checkControl to ValidationResult.Invalid(ERROR3)
            )
        )
        assertEquals(expected2, validationResult)
        assert(!validationResult.isValid)

        // All valid
        inputControl1.text = "12345"
        inputControl2.text = "12345"
        checkControl.checked = true
        val expected3 = FormValidationResult(
            mapOf(
                inputControl1 to ValidationResult.Valid,
                inputControl2 to ValidationResult.Valid,
                checkControl to ValidationResult.Valid
            )
        )
        assertEquals(expected3, validationResult)
        assert(validationResult.isValid)
    }
}