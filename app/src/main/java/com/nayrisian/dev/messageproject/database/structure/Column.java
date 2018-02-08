package com.nayrisian.dev.messageproject.database.structure;

/**
 * Abstract representation of a column in a database.
 */
public class Column<T> {
    // Variables
    private String mName;
    private Class<T> mType;
    private Table mParent;
    private T mValue;
    private boolean mIsPrimaryKey = false;
    private boolean mIsAutoIncrement = false;
    private boolean mIsNullable = false;

    // Constructors
    public Column(String name, Table parent, Class<T> type) {
        this(name, parent, type, false, false);
    }

    public Column(String name, Table parent, Class<T> type, boolean isPrimaryKey) {
        this(name, parent, type, isPrimaryKey, true);
    }

    public Column(String name, Table parent, Class<T> type, boolean isPrimaryKey, boolean isAutoIncrement) {
        mName = name;
        mParent = parent;
        mType = type;
        mIsPrimaryKey = isPrimaryKey;
        mIsAutoIncrement = isAutoIncrement;
        mIsNullable = isPrimaryKey;
        parent.add(this);
    }

    public Column(String name, Table parent, Class<T> type, T value) {
        this(name, parent, type, value, false, false);
    }

    public Column(String name, Table parent, Class<T> type, T value, boolean isPrimaryKey) {
        this(name, parent, type, value, isPrimaryKey, true);
    }

    public Column(String name, Table parent, Class<T> type, T value, boolean isPrimaryKey, boolean isAutoIncrement) {
        this(name, parent, type, isPrimaryKey, isAutoIncrement);
        mValue = value;
    }

    // Methods
    public String getName() {
        return mParent.getName() + "." + mName;
    }

    public String getSimpleName() {
        return mName;
    }

    public Table getParent() {
        return mParent;
    }

    public Class<T> getType() {
        return mType;
    }

    public T getValue() {
        return mValue;
    }

    public T setValue(T value) {
        return mValue = value;
    }

    public boolean getPrimaryKey() {
        return mIsPrimaryKey;
    }

    public boolean getAutoIncrement() {
        return mIsAutoIncrement;
    }

    public boolean getNullable() {
        return mIsNullable;
    }

    @Override
    public String toString() {
        return getName();
    }
}