package me.aartikov.sesamesample.form

import com.google.i18n.phonenumbers.PhoneNumberUtil
import javax.inject.Inject

private const val MAX_PHONE_LENGTH = 17

fun onlyPhone(phoneNumberString: String): String {
    return "+${phoneNumberString.onlyDigits()}"
}

fun String.onlyDigits() = this.replace("\\D".toRegex(), "")

class PhoneUtil @Inject constructor() {

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    fun formatPhoneNumber(phoneNumberString: String): String {
        val phoneNumber = onlyPhone(phoneNumberString).take(MAX_PHONE_LENGTH)
        var formattedPhone: String = phoneNumber

        val asYouTypeFormatter =
            phoneNumberUtil.getAsYouTypeFormatter(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY)

        for (ch in phoneNumber.toCharArray()) {
            formattedPhone = asYouTypeFormatter.inputDigit(ch)
        }

        return formattedPhone.trim()
    }

    fun isValidPhone(phoneNumber: String): Boolean {
        return try {
            phoneNumberUtil.isValidNumber(
                phoneNumberUtil.parse(onlyPhone(phoneNumber), null)
            )
        } catch (e: Exception) {
            false
        }
    }
}