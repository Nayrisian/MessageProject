package com.nayrisian.dev.messageproject.database.type;

import java.io.Serializable;

/**
 * Used primarily in |database.tables.Message| for storing user data.
 * Created by Nayrisian on 15/10/2016.
 */
public class Sender extends Contact implements Serializable {
    public Sender(long id, String email, String name) {
        super(id, email, name);
    }

    @Override
    public String toString() {
        return getEmail() + ":" + getUsername();
    }
}
