package com.nayrisian.dev.messageproject.database.type;

import java.io.Serializable;

/**
 * Used primarily in |database.tables.Message| for storing user data.
 * Created by Nayrisian on 15/10/2016.
 */
public class Receiver extends Contact implements Serializable {
    public Receiver(long id, String email, String name) {
        super(id, email, name);
    }

    @Override
    public String toString() { return getEmail() + ":" + getUsername(); }
}