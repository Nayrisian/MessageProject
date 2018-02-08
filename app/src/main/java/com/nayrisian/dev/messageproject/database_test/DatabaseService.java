package com.nayrisian.dev.messageproject.database_test;

import com.nayrisian.dev.messageproject.database_test.structure.Database;

import java.util.HashMap;
import java.util.Map;

/**
 * The interfacing service
 * Created by Nayrisian on 10/12/2016.
 */
public class DatabaseService {
    // Default database to reference to.
    public static final String MASTER_DATABASE = "master";
    // Mapping of databases currently loaded.
    private static Map<String, Database> mDatabases = new HashMap<>(4);
    // Current pointer/key for the database in use/last used.
    private static String mCurrentDatabase = MASTER_DATABASE;

    /**
     *
     */
    public static void getMessages() {

    }

    /**
     * Set the current database in the map of databases using the database name.
     * @param databaseName The key referencing to the database name.
     */
    public static void setCurrentDatabase(String databaseName) {
        if (!databaseName.isEmpty() && mDatabases.containsKey(databaseName)) {
            mCurrentDatabase = databaseName;
        }
    }

    /**
     * Add a database to the map of databases to be able to use and connect to.
     * @param database The map of databases.
     */
    public static void addDatabase(Database database) {
        mDatabases.put(database.NAME, database);
    }
}