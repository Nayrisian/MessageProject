package com.nayrisian.dev.messageproject.database.structure;

import android.content.Context;

import com.nayrisian.dev.messageproject.utility.Error;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Nayrisian on 20/11/2016.
 */
public class Record {
    private Map<String, Column<?>> mValues;

    public Record() {
        mValues = new HashMap<>();
    }

    public <T> Record setValue(JSONObject jsonObject, Column<T> column, Context context) {
        try {
            String name = column.getSimpleName();
            Object value;
            if (jsonObject.getString(name).matches("[0-9]+\\.[0-9]+"))
                value = jsonObject.getDouble(name);
            else if (jsonObject.getString(name).matches("[0-9]+"))
                value = jsonObject.getLong(name);
            else
                value = jsonObject.getString(name);
            column.setValue(column.getType().cast(value));
            mValues.put(name, column);
        } catch (JSONException ex) {
            Error.log(ex, context);
        }
        return this;
    }

    public <T> Column<T> getValue(Column<T> column) {
        for (Column<?> value : mValues.values())
            if (column.equals(value))
                return column.getClass().cast(value);
        return null;
    }
}