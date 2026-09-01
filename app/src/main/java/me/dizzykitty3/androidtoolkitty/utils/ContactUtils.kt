package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract

object ContactUtils {
    private const val TEST = "test"
    private const val TEL_MOBILE = "+86 100 0000 00"
    private const val MAIL_HOME = "@gmail.com"
    private var count = 0

    fun createContact(activity: Activity?) {
        createContactImpl(activity, count)
        count++
    }

    private fun createContactImpl(activity: Activity?, number: Int) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            // name
            putExtra(ContactsContract.Intents.Insert.NAME, "$TEST $number")

            // number
            if (number >= 10) {
                putExtra(ContactsContract.Intents.Insert.PHONE, "$TEL_MOBILE$number")
            } else {
                putExtra(ContactsContract.Intents.Insert.PHONE, "${TEL_MOBILE}0$number")
            }
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )

            // email
            putExtra(ContactsContract.Intents.Insert.EMAIL, "$TEST$count$MAIL_HOME")
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_HOME
            )
        }

        activity?.startActivityForResult(intent, 1001)
    }
}