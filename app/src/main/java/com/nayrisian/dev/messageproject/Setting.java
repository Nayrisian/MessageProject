package com.nayrisian.dev.messageproject;

import com.nayrisian.dev.messageproject.database.type.Account;

/**
 * Settings for the application.
 * Created by Nayrisian on 25/11/2016.
 */

public class Setting {
    private static Account mAccount = new Account(0, "", "", "", "", "");
    private static Style mStyle = Style.DARK;

    public static Account getAccount() {
        return mAccount;
    }

    public static Account setAccount(Account account) {
        return mAccount = account;
    }

    public static Style getStyle() {
        return mStyle;
    }

    public static void setStyle(Style style) {
        mStyle = style;
    }

    public static Style toggleStyle() {
        if (getStyle() == Style.LIGHT)
            setStyle(Style.DARK);
        else
            setStyle(Style.LIGHT);
        return mStyle;
    }

    public enum Style {
        LIGHT, DARK
    }
}