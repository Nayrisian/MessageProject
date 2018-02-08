package com.nayrisian.dev.messageproject.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nayrisian.dev.messageproject.R;
import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.database.DatabaseHelper;
import com.nayrisian.dev.messageproject.database.type.Account;
import com.nayrisian.dev.messageproject.database.type.Contact;

import java.util.LinkedList;
import java.util.List;

public class ContactActivity extends BaseActivity {
    private static ContactCallback mCallback;
    private static List<Contact> mContact = new LinkedList<>();

    private DatabaseHelper mDBHelper;
    private List<Account> mAccounts = new LinkedList<>();

    private LinearLayout mLayoutSearchResults;
    private EditText mTxtSearchID;
    private EditText mTxtSearchEmail;
    private EditText mTxtSearchUsername;

    public static void setCallback(ContactCallback callback) {
        mCallback = callback;
    }

    public static void setContact(List<Contact> contacts) {
        mContact = contacts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // UI references
        mLayoutSearchResults = (LinearLayout) findViewById(R.id.layoutSearchResults);
        mTxtSearchID = (EditText) findViewById(R.id.txtSearchID);
        mTxtSearchEmail = (EditText) findViewById(R.id.txtSearchEmail);
        mTxtSearchUsername = (EditText) findViewById(R.id.txtSearchUsername);
        Button mBtnSearchID = (Button) findViewById(R.id.btnSearchID);
        Button mBtnSearchEmail = (Button) findViewById(R.id.btnSearchEmail);
        Button mBtnSearchUsername = (Button) findViewById(R.id.btnSearchUsername);
        Button mBtnSearchClose = (Button) findViewById(R.id.btnSearchClose);
        mDBHelper = DatabaseHelper.get(this);

        mBtnSearchID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccounts.clear();
                if (mTxtSearchID != null) {
                    mAccounts.add(mDBHelper.getAccount(ContactActivity.this,
                            Long.parseLong(mTxtSearchID.getText().toString()), 1));
                }
                displayResults();
            }
        });
        mBtnSearchEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccounts.clear();
                mAccounts = mDBHelper.getAccounts(ContactActivity.this,
                        mTxtSearchEmail.getText().toString(), 100);
                displayResults();
            }
        });
        mBtnSearchUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccounts.clear();
                mAccounts = mDBHelper.getAccounts(ContactActivity.this,
                        mTxtSearchUsername.getText().toString(), false, 100);
                displayResults();
            }
        });
        mBtnSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void displayResults() {
        mTxtSearchID.setText("");
        mTxtSearchEmail.setText("");
        mTxtSearchUsername.setText("");
        mLayoutSearchResults.removeAllViews();
        OuterLoop:
        for (final Account account : mAccounts) {
            if (account == null)
                break;
            if (Setting.getAccount().getID() == account.getID())
                break;
            for (Contact contact : mContact)
                if (contact.getID() == account.getID())
                    break OuterLoop;
            // Set linear layout.
            LinearLayout layoutContact = new LinearLayout(this);
            layoutContact.setOrientation(LinearLayout.HORIZONTAL);
            layoutContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.addContactCallback(account);
                    finish();
                }
            });
            // Set pictures.
            ImageView accountImage = new ImageView(this);
            accountImage.setImageResource(R.mipmap.ic_launcher);
            accountImage.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            // Set name.
            TextView accountName = new TextView(this);
            accountName.setText(account.getUsername());
            accountName.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            // Setting layouts.
            mLayoutSearchResults.addView(layoutContact,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            layoutContact.addView(accountImage,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            layoutContact.addView(accountName,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }
}