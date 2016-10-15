package uk.ac.solent.nayrisian.messageproject.database.tables;

/**
 * Represents fields within a column, as well as the overall column structure.
 * Created by Nayrisian on 11/10/2016.
 */

public class Accounts implements ITable {
    public static final String TABLE_ACCOUNTS = "accounts",
            COLUMN_USERID = "_id",
            COLUMN_EMAIL = "email",
            COLUMN_USERNAME = "username",
            COLUMN_PASSWORD = "password";

    protected int _id;
    protected String _email, _username, _password;

    public Accounts(int id, String email, String username, String password) {
        _id = id;
        _email = email;
        _username = username;
        _password = password;
    }

    public int getID() {
        return _id;
    }

    public void setID(int id) {
        _id = id;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }
}