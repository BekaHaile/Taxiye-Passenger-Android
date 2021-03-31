package product.clicklabs.jugnoo.utils

import android.content.Context
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

object UtilsKt {

    private var phoneNumberUtil: PhoneNumberUtil? = null

    @JvmStatic
    fun splitCountryCodeAndPhoneNumber(context: Context, number: String):CountryCodePhoneNo {
        if(phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        }


        var number1 = number
        number1 = number1.replace(" ", "")
        number1 = if (number1.startsWith("0")) number1.replaceFirst("0", "") else number1

        if (number1.startsWith("+")) {

            val phoneNumber = try {
                phoneNumberUtil?.parse(number1, null)
            } catch (e: NumberParseException) {
                Log.e("PhoneNumberUtil", "error during parsing a number $number1")
                null
            }
            if (phoneNumber == null) {
                number1 = Utils.retrievePhoneNumberTenChars(number1, "")
                return CountryCodePhoneNo("", "", number1)
            } else {
                val countryIso = phoneNumberUtil?.getRegionCodeForCountryCode(phoneNumber.countryCode) ?: ""
                val countryCode = "+".plus(phoneNumber.countryCode)
                number1 = Utils.retrievePhoneNumberTenChars(number1, countryCode)
                return CountryCodePhoneNo(countryCode, countryIso, number1)
            }
        } else {
            number1 = Utils.retrievePhoneNumberTenChars(number1, "")
            return CountryCodePhoneNo("", "", number1)
        }
    }


    private fun getCountryCodeFromNumber(context: Context, number: String): String? {
        if(phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        }
        val validatedNumber = if (number.startsWith("+")) number else "+$number"

        val phoneNumber = try {
            phoneNumberUtil?.parse(validatedNumber, null)
        } catch (e: NumberParseException) {
            Log.e("PhoneNumberUtil", "error during parsing a number")
            null
        }
        if(phoneNumber == null) return null

        return phoneNumber.countryCode.toString()
//        return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
    }
}


class CountryCodePhoneNo(val countryCode:String, val countryIso:String, val phoneNo:String)