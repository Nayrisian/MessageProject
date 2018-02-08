package com.nayrisian.dev.messageproject.utility;

import android.content.Context;
import android.util.Log;

import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.database.DatabaseHelper;

/**
 * Error handling class - Manages and displays data on errors.
 * Created by Nayrisian on 17/10/2016.
 */
public class Error {
    public static void log(Exception ex, Context context) {
        System.out.println(ex.getMessage());
        System.out.println(Log.getStackTraceString(ex));
        DatabaseHelper dbHelper = DatabaseHelper.get(context);
        StackTraceElement stackElement = ex.getStackTrace()[0];
        dbHelper.addError(context, Setting.getAccount(), Log.getStackTraceString(ex),
                stackElement.getClassName() + "::" + stackElement.getMethodName(),
                stackElement.getLineNumber(), dbHelper.getVersion());
    }
}