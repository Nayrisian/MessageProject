package com.nayrisian.dev.messageproject.database.type;

import java.io.Serializable;

/**
 * Java type of the database account table.
 * Created by Nayrisian on 15/11/2016.
 */
public class Account extends Contact implements Serializable {
    private String mPassword;
    private String mHashtype;
    private String mSalt;

    public Account(long id, String email, String username, String password, String hashtype) {
        this(id, email, username, password, hashtype, "");
    }

    public Account(long id, String email, String username, String password, String hashtype, String salt) {
        super(id, email, username);
        mPassword = password;
        mHashtype = hashtype;
        mSalt = salt;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getHashtype() {
        return mHashtype;
    }

    public String getSalt() {
        return mSalt;
    }

    public boolean isValid() {
        return true;
    }
}