package com.nayrisian.dev.messageproject.database.type;

import java.io.Serializable;

/**
 * Java type of the database contact table.
 * Created by Nayrisian on 21/11/2016.
 */
public class Contact implements Serializable {
    private long mID;
    private String mEmail;
    private String mUsername;

    public Contact(long id, String email, String username) {
        mID = id;
        mEmail = email;
        mUsername = username;
    }

    public long getID() {
        return mID;
    }

    public void setID(long id) {
        mID = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}