package com.rsmnm.BaseClasses

import android.app.Activity
import android.content.Intent
import com.google.android.gms.location.places.ui.PlacePicker
import com.rsmnm.Interfaces.ContactPickedInterface
import android.provider.ContactsContract


/**
 * Created by saqib on 9/12/2018.
 */
abstract class ContactPickerFragment : BaseFragment() {
    lateinit private var contactPickedInterface: ContactPickedInterface
    var CONTACT_PICKER_REQUEST = 2015;

    fun pickContact(contactPickedInterface: ContactPickedInterface) {
        this.contactPickedInterface = contactPickedInterface
        val i = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(i, CONTACT_PICKER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val contactUri = data?.getData()
                val cursor = fragmentActivity.getContentResolver().query(contactUri, null, null, null, null)
                cursor.moveToFirst()
                val number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                contactPickedInterface.onContactSelected(cursor.getString(name),cursor.getString(number))
            }
        }
    }
}