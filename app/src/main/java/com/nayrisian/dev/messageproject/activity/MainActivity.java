package com.nayrisian.dev.messageproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nayrisian.dev.messageproject.R;
import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.database.DatabaseHelper;
import com.nayrisian.dev.messageproject.database.type.Contact;
import com.nayrisian.dev.messageproject.database.type.Message;
import com.nayrisian.dev.messageproject.event.ChatReceiveEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class MainActivity extends BaseActivity implements ContactCallback {
    // Database helper.
    private DatabaseHelper mDBHelper;

    // Data structures.
    private List<Contact> mContacts = new LinkedList<>();

    // UI references.
    private LinearLayout mLayoutContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Localised UI references.
        mLayoutContacts = (LinearLayout) findViewById(R.id.layoutContacts);
        Button mBtnAddContact = (Button) findViewById(R.id.btnAddContact);
        // Get an instance of the database helper.
        mDBHelper = DatabaseHelper.get(this);
        // Get all contacts of this user.
        List<Contact> contacts = mDBHelper.getContacts(this, Setting.getAccount(), 100);
        for (Contact contact : contacts) {
            addAccountToLayout(contact);
        }
        mBtnAddContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                ContactActivity.setCallback(MainActivity.this);
                ContactActivity.setContact(mContacts);
                startActivity(intent);
            }
        });
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run () {
                final List<Message> messages = mDBHelper.getNewMessages(MainActivity.this, Setting.getAccount(), 50);
                runOnUiThread(new Runnable() {
                    public void run() {
                        for (Message message : messages) {
                            ChatReceiveEvent.call(message);
                        }
                    }
                });
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Used by the ContactCallback interface to create a layout on main activity.
     * @param account Information on current user.
     */
    public void addContactCallback(Contact account) {
        Contact contact = new Contact(account.getID(), account.getEmail(), account.getUsername());
        if (mDBHelper.addContact(this, Setting.getAccount(), contact) > 0) {
            addAccountToLayout(contact);
        }
    }

    protected void addAccountToLayout(final Contact contact) {
        mContacts.add(contact);
        // Set pictures.
        ImageView accountImage = new ImageView(this);
        accountImage.setImageResource(R.mipmap.ic_launcher);
        // Set name.
        TextView accountName = new TextView(this);
        accountName.setText(contact.getUsername());
        // Set linear layout.
        LinearLayout layoutContact = new LinearLayout(this);
        layoutContact.setOrientation(LinearLayout.HORIZONTAL);
        layoutContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("Contact_ID", contact.getID());
                intent.putExtra("Contact_Email", contact.getEmail());
                intent.putExtra("Contact_Name", contact.getUsername());
                startActivity(intent);
            }
        });
        // Setting layouts.
        mLayoutContacts.addView(layoutContact,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layoutContact.addView(accountImage,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        layoutContact.addView(accountName,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
}