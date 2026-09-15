package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.os.Build
import androidx.annotation.CheckResult
import java.util.Locale

object StringUtils {
    private val whitespaceRegex = Regex("\\s")
    private val validUsernameRegex = Regex("^[a-zA-Z0-9_]*$")
    private val supportedLocaleRegex = Regex("en|Hans|zh_CN|zh_SG|ja")
    private val englishLocaleRegex = Regex("en")
    private val cjkLocaleRegex = Regex("Hans|Hant|zh|ja|ko")

    // ----- string processing -----//

    /**
     * Drop spaces, including full-width ones.
     */
    fun String.dropSpaces(): String = replace(whitespaceRegex, "")

    /**
     * Allows for letters, numbers, or underscores.
     */
    fun String.isValidUsername(): Boolean = matches(validUsernameRegex)

    fun String.isInvalidUsername(): Boolean = !this.isValidUsername()

    fun String.removeTrailingPeriod(): String =
        dropLastWhile { it == '.' }

    fun String.toASCII(): String = this.map { it.code }.joinToString(", ")

    @Throws(IllegalArgumentException::class)
    fun unicodeToCharacter(unicode: String): String {
        val length = unicode.length
        require(length % 4 == 0) { "The length of the input is not a multiple of 4" }

        try {
            val stringBuilder = StringBuilder()
            var i = 0
            while (i < length) {
                val hexValue = unicode.substring(i, i + 4)
                val decimalValue = hexValue.toInt(16)
                stringBuilder.append(decimalValue.toChar())
                i += 4
            }
            return stringBuilder.toString()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Unicode string format: ", e)
        }
    }

    @Throws(IllegalArgumentException::class)
    fun characterToUnicode(characters: String): String {
        require(characters.isNotEmpty()) { "Input string is empty" }
        return characters
            .map { it.code.toString(16).padStart(4, '0') }
            .joinToString("")
    }

    // ----- system language setting ----- //

    val sysLocale: String @CheckResult get() = Locale.getDefault().toString()

    val sysLangSupported: Boolean @CheckResult get() = sysLocale.contains(supportedLocaleRegex)

    val sysLangNotSupported: Boolean @CheckResult get() = !sysLangSupported

    val sysLangFullyTranslated: Boolean @CheckResult get() = sysLocale.contains(englishLocaleRegex)

    val sysLangNotFullyTranslated: Boolean @CheckResult get() = !sysLangFullyTranslated

    val sysLangCJK: Boolean @CheckResult get() = sysLocale.contains(cjkLocaleRegex)

    // ----- device and app info ----- //

    val manufacturer: String
        get() = Build.MANUFACTURER

    val model: String
        get() = Build.MODEL

    val device: String
        get() = Build.DEVICE

    val osVer: String
        get() = "Android ${Build.VERSION.RELEASE} (${Build.getMajorSdkVersion(Build.VERSION.SDK_INT_FULL)}.${
            Build.getMinorSdkVersion(
                Build.VERSION.SDK_INT_FULL
            )
        })"

    // BuildConfig.VERSION_NAME may not have the updated value at compile time. (I guess)
    val Context.versionName: String
        get() = this.packageManager.getPackageInfo(this.packageName, 0).versionName.toString()
}