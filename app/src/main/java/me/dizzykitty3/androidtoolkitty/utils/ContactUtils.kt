package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract

object ContactUtils {
    private const val CONTACT_NAME_PREFIX = "test"
    private const val MOBILE_NUMBER_PREFIX = "+86 100 0000 00"
    private const val HOME_EMAIL_DOMAIN = "@gmail.com"
    private var count = 0

    fun createContact(activity: Activity?) {
        createContactImpl(activity, count)
        count++
    }

    private fun createContactImpl(activity: Activity?, number: Int) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            // name
            putExtra(ContactsContract.Intents.Insert.NAME, "$CONTACT_NAME_PREFIX $number")

            // number
            val formattedNumber = number.toString().padStart(2, '0')
            putExtra(
                ContactsContract.Intents.Insert.PHONE,
                "$MOBILE_NUMBER_PREFIX$formattedNumber"
            )
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )

            // email
            putExtra(
                ContactsContract.Intents.Insert.EMAIL,
                "$CONTACT_NAME_PREFIX$count$HOME_EMAIL_DOMAIN"
            )
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_HOME
            )
        }

        activity?.startActivityForResult(intent, 1001)
    }
}