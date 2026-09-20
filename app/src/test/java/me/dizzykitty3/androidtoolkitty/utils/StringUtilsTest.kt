package me.dizzykitty3.androidtoolkitty.utils

import me.dizzykitty3.androidtoolkitty.utils.StringUtils.dropSpaces
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.isInvalidUsername
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.isValidUsername
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.removeTrailingPeriod
import me.dizzykitty3.androidtoolkitty.utils.StringUtils.toASCII
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class StringUtilsTest {

    @Test
    fun dropSpaces_removesAsciiAndFullWidthWhitespace() {
        assertEquals("ToolKitty", " Tool\tKitty　".dropSpaces())
    }

    @Test
    fun usernameValidation_acceptsOnlyLettersNumbersAndUnderscores() {
        assertTrue("theo_123".isValidUsername())
        assertTrue("".isValidUsername())
        assertTrue("theo kitty".isInvalidUsername())
        assertFalse("theo-kitty".isValidUsername())
    }

    @Test
    fun unicodeConversions_handleValidAndInvalidInput() {
        assertEquals("00410042", StringUtils.characterToUnicode("AB"))
        assertEquals("AB", StringUtils.unicodeToCharacter("00410042"))
        assertThrows(IllegalArgumentException::class.java) {
            StringUtils.unicodeToCharacter("004")
        }
        assertThrows(IllegalArgumentException::class.java) {
            StringUtils.unicodeToCharacter("zzzz")
        }
    }

    @Test
    fun basicStringFormatting_isStable() {
        assertEquals("release", "release...".removeTrailingPeriod())
        assertEquals("65, 55357, 56369", "A🐱".toASCII())
    }

    @Test
    fun systemLanguageFlags_classifySupportedAndCjkLocales() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertTrue(StringUtils.sysLangSupported)
            assertTrue(StringUtils.sysLangFullyTranslated)
            assertFalse(StringUtils.sysLangCJK)

            Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
            assertTrue(StringUtils.sysLangSupported)
            assertFalse(StringUtils.sysLangFullyTranslated)
            assertTrue(StringUtils.sysLangCJK)

            Locale.setDefault(Locale.FRENCH)
            assertTrue(StringUtils.sysLangNotSupported)
            assertTrue(StringUtils.sysLangNotFullyTranslated)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }
}
