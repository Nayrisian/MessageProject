package uk.ac.solent.nayrisian.messageproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.ac.solent.nayrisian.messageproject.database.tables.Account;

import static uk.ac.solent.nayrisian.messageproject.database.tables.Account.*;
import static uk.ac.solent.nayrisian.messageproject.database.tables.Message.*;

/**
 * Singleton handler of the SQLite Android database system.
 * TODO: Create a separate SQLite handler system externally.
 * Created by Nayrisian on 09/10/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ams.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNTS + "(" +
                COLUMN_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_EMAIL + " TEXT " +
                COLUMN_USERNAME + " TEXT " +
                COLUMN_PASSWORD + " TEXT " + ");");
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "(" +
                COLUMN_MESSAGEID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_MESSAGE + " TEXT " +
                COLUMN_SENDER + " TEXT " +
                COLUMN_RECEIVER + " TEXT " +
                COLUMN_TIME + " TIME " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public long addAccount(String email, String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long success;
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        success = db.insert(TABLE_ACCOUNTS, null, contentValues);
        db.close();
        return success;
    }
/*
    public void delAccount(String email) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ACCOUNTS +
                " WHERE " + COLUMN_EMAIL + "=\"" + email + "\";");
    }

    public void delAccount(int userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ACCOUNTS +
                " WHERE " + COLUMN_USERID + "=\"" + userID + "\";");
    }
*/
    public Account getAccount(String email) {
        SQLiteDatabase db = getWritableDatabase();
        Account account;
        Cursor cursor = db.rawQuery("SELECT *" +
                " FROM " + TABLE_ACCOUNTS +
                " WHERE " + COLUMN_EMAIL + " = ?", new String[] { email });
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)) != null) {
                    account = new Account(
                            cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
                    return account;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return null;
    }
}