package com.nayrisian.dev.messageproject.database.structure;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract representation of a database with tables and columns.
 * Created by Nayrisian on 15/11/2016.
 */
public class Database {
    // Variables
    private String mName;
    private List<Table> mTables;

    // Constructors
    public Database(String name) {
        mName = name;
        mTables = new LinkedList<>();
    }

    // Methods
    public String getName() {
        return mName;
    }

    public Table get(Table table) {
        for (int i = 0; i < mTables.size(); i++)
            if (mTables.equals(table))
                return mTables.get(i);
        return null;
    }

    public List<Table> getAll() {
        return mTables;
    }

    public Table add(Table table) {
        if (mTables.add(table))
            return table;
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
