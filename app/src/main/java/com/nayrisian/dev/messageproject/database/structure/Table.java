package com.nayrisian.dev.messageproject.database.structure;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract representation of a table in a database.
 */
public class Table {
    // Variables
    private String mName;
    private Database mParent;
    private List<Column> mColumns;

    // Constructors
    public Table(String name, Database parent) {
        mName = name;
        mParent = parent;
        mColumns = new LinkedList<>();
        parent.add(this);
    }

    // Methods
    public String getName() {
        return mName;
    }

    public Column get(Column column) {
        for (int i = 0; i < mColumns.size(); i++)
            if (mColumns.equals(column))
                return column;
        return null;
    }

    public List<Column> getAll() {
        return mColumns;
    }

    public Column add(Column column) {
        if (mColumns.add(column))
            return column;
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
