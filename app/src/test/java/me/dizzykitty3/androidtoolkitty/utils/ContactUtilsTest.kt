package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ContactUtilsTest {

    @Test
    fun createContact_startsPrepopulatedMobileContactInsertIntent() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        ContactUtils.createContact(activity)

        val intent = shadowOf(activity).nextStartedActivity
        val name = requireNotNull(intent.getStringExtra(ContactsContract.Intents.Insert.NAME))
        val index = name.removePrefix("test ")
        assertEquals(Intent.ACTION_INSERT, intent.action)
        assertEquals(ContactsContract.Contacts.CONTENT_TYPE, intent.type)
        assertTrue(index.all(Char::isDigit))
        assertEquals("+86 100 0000 00${index.padStart(2, '0')}", intent.getStringExtra(ContactsContract.Intents.Insert.PHONE))
        assertEquals("test$index@gmail.com", intent.getStringExtra(ContactsContract.Intents.Insert.EMAIL))
        assertEquals(
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            intent.getIntExtra(ContactsContract.Intents.Insert.PHONE_TYPE, -1),
        )
        assertEquals(
            ContactsContract.CommonDataKinds.Email.TYPE_HOME,
            intent.getIntExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, -1),
        )
    }

    @Test
    fun createContact_incrementsTheGeneratedContactIndex() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        ContactUtils.createContact(activity)
        val firstName = requireNotNull(
            shadowOf(activity).nextStartedActivity
                .getStringExtra(ContactsContract.Intents.Insert.NAME),
        )
        ContactUtils.createContact(activity)
        val secondName = requireNotNull(
            shadowOf(activity).nextStartedActivity
                .getStringExtra(ContactsContract.Intents.Insert.NAME),
        )

        val firstIndex = firstName.removePrefix("test ").toInt()
        val secondIndex = secondName.removePrefix("test ").toInt()
        assertEquals(firstIndex + 1, secondIndex)
    }
}
