package com.nayrisian.dev.messageproject.activity;

import com.nayrisian.dev.messageproject.database.type.Contact;

/**
 * Callback used to send account data back to the main activity after adding a contact.
 * Created by Nayrisian on 27/11/2016.
 */
interface ContactCallback {
    void addContactCallback(Contact contact);
}
