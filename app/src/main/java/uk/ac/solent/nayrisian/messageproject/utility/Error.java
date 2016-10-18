package uk.ac.solent.nayrisian.messageproject.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Error handling class - Manages and displays data on errors.
 * Created by Nayrisian on 17/10/2016.
 */
public class Error {
    @SuppressWarnings("all")
    public static void log(String message) {
        // TODO: Log for errors and storing them. (Database task?)
    }

    @SuppressWarnings("all")
    public static void display(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
