package com.nayrisian.dev.messageproject.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nayrisian.dev.messageproject.R;
import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.database.DatabaseHelper;
import com.nayrisian.dev.messageproject.database.type.Account;
import com.nayrisian.dev.messageproject.database.type.Contact;
import com.nayrisian.dev.messageproject.database.type.Message;
import com.nayrisian.dev.messageproject.database.type.Receiver;
import com.nayrisian.dev.messageproject.database.type.Sender;
import com.nayrisian.dev.messageproject.event.ChatEventListener;
import com.nayrisian.dev.messageproject.event.ChatReceiveEvent;

import java.sql.Timestamp;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChatActivity extends BaseActivity implements ChatCallback, ChatEventListener {
    private DatabaseHelper mDBHelper;
    private Contact mContact;

    private LinearLayout mChatLayout;
    private EditText mTxtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // UI references.
        mChatLayout = (LinearLayout) findViewById(R.id.layoutChat);
        mTxtMessage = (EditText) findViewById(R.id.txtMessage);
        Button mBtnMessageSend = (Button) findViewById(R.id.btnSend);
        // Get database helper instance.
        mDBHelper = DatabaseHelper.get(this);
        // Set other things
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mContact = null;
            } else {
                mContact = new Contact(extras.getLong("Contact_ID"),
                        extras.getString("Contact_Email"),
                        extras.getString("Contact_Name"));
            }
        } else {
            mContact = new Contact(savedInstanceState.getLong("Contact_ID"),
                    savedInstanceState.getString("Contact_Email"),
                    savedInstanceState.getString("Contact_Name"));
        }
        mBtnMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = Setting.getAccount();
                Message message = new Message(mTxtMessage.getText().toString(),
                        new Sender(account.getID(), account.getEmail(), account.getUsername()),
                        new Receiver(mContact.getID(), mContact.getEmail(), mContact.getUsername()),
                        new Timestamp(System.currentTimeMillis()));
                mDBHelper.addMessage(ChatActivity.this, message);
                generateMessage(message);
                mTxtMessage.getText().clear();
            }
        });
        // Generate recent chat
        generateChat();
        ChatReceiveEvent.register(this);
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------------------- Chat methods ----------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public void run(Message message) {
        if (message.getSender().getID() == mContact.getID()) {
            generateMessage(message);
        }
    }

    @Override
    public void updateChat(List<Message> messages) {
        // Create chat objects
        for (Message message : messages) {
            generateMessage(message);
        }
    }

    public void generateMessage(Message message) {
        // Set message.
        TextView lblMessage = new TextView(this);
        lblMessage.setText(message.getMessage());
        lblMessage.setPadding(5, 5, 5, 5);
        if (message.getReceiver().getID() == Setting.getAccount().getID()) {
            lblMessage.setGravity(Gravity.START);
            System.out.println("Receiver is this account, setting gravity to left.");
        } else if (message.getSender().getID() == Setting.getAccount().getID()) {
            lblMessage.setGravity(Gravity.END);
            System.out.println("Sender is this account, setting gravity to right.");
        } else {
            lblMessage.setGravity(Gravity.CENTER_HORIZONTAL);
            System.out.println("ERROR - CANNOT DECIDE MESSAGE OWNER ID");
        }
        System.out.println("Message: " + message.getMessage());
        System.out.println("    Receiver ID: " + message.getReceiver().getID());
        System.out.println("    Sender ID: " + message.getSender().getID());
        System.out.println("    Account ID: " + Setting.getAccount().getID());
        System.out.println("    Contact ID: " + mContact.getID());
        // Add to layout.
        mChatLayout.addView(lblMessage);
    }

    /**
     * Generates the latest 10/20/50 messages when called.
     */
    public void generateChat() {
        List<Message> messages = mDBHelper.getRecentMessages(this, Setting.getAccount(), mContact, 50);
        updateChat(messages);
    }
}