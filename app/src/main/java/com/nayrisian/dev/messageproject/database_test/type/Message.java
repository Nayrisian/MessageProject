package com.nayrisian.dev.messageproject.database_test.type;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * Created by Nayrisian on 26/11/2016.
 */

public class Message implements Serializable {
    private String mMessage;
    private Sender mSender;
    private Receiver mReceiver;
    private Timestamp mTime;

    public Message(String message, Sender sender, Receiver receiver, Timestamp time) {
        mMessage = message;
        mSender = sender;
        mReceiver = receiver;
        mTime = time;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Sender getSender() {
        return mSender;
    }

    public Receiver getReceiver() {
        return mReceiver;
    }

    public Timestamp getTime() {
        return mTime;
    }
}