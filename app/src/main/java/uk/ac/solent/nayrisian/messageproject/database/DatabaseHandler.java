package uk.ac.solent.nayrisian.messageproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import uk.ac.solent.nayrisian.messageproject.database.tables.Accounts;

import static uk.ac.solent.nayrisian.messageproject.database.tables.Accounts.*;
import static uk.ac.solent.nayrisian.messageproject.database.tables.Messages.*;

/**
 * Singleton handler of the SQLite Android database system.
 * TODO: Create a separate SQLite handler system externally.
 * Created by Nayrisian on 09/10/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ams.db";

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

    public void addAccount(Accounts account) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, account.getUsername());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ACCOUNTS, null, contentValues);
        db.close();
    }

    public void delAccount(String username) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ACCOUNTS +
                " WHERE " + COLUMN_USERNAME + "=\"" + username + "\";");
    }

    public void delAccount(int userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ACCOUNTS +
                " WHERE " + COLUMN_USERID + "=\"" + userID + "\";");
    }

    public Accounts getAccount() { return null; }

    // Debugging
    public String getDatabaseInfo() {
        String dbInfo = "";
        getColumnInfo(TABLE_ACCOUNTS, COLUMN_USERID);
        return dbInfo;
    }

    private String getColumnInfo(String table, String column) {
        SQLiteDatabase db = getWritableDatabase();
        String dbInfo = "";
        Cursor cursor = db.rawQuery("SELECT *" +
                " FROM " + table +
                " WHERE 1;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
            if (cursor.getString(cursor.getColumnIndex(column)) != null)
                dbInfo += cursor.getString(cursor.getColumnIndex(column)) + "\n";
        cursor.close();
        db.close();
        return dbInfo;
    }
}