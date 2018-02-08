package com.nayrisian.dev.messageproject.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.nayrisian.dev.messageproject.database.structure.Column;
import com.nayrisian.dev.messageproject.database.structure.Database;
import com.nayrisian.dev.messageproject.database.structure.Record;
import com.nayrisian.dev.messageproject.database.structure.Table;
import com.nayrisian.dev.messageproject.database.type.Account;
import com.nayrisian.dev.messageproject.database.type.Contact;
import com.nayrisian.dev.messageproject.database.type.Message;
import com.nayrisian.dev.messageproject.database.type.Receiver;
import com.nayrisian.dev.messageproject.database.type.Sender;
import com.nayrisian.dev.messageproject.encryption.Encrypt;
import com.nayrisian.dev.messageproject.encryption.Hash;
import com.nayrisian.dev.messageproject.encryption.Hashtype;
import com.nayrisian.dev.messageproject.utility.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Helper for handling database functions.
 * Created by Nayrisian on 15/11/2016.
 */
public class DatabaseHelper {
    private static DatabaseHelper mInstance = null;

    private Database mDBAndroid = new Database("andro-m00-u-080800");

    private Table mTableAccount = new Table("account", mDBAndroid);
    private Table mTableMessage = new Table("message", mDBAndroid);
    private Table mTableContact = new Table("contact", mDBAndroid);
    private Table mTableSync = new Table("sync", mDBAndroid);
    private Table mTableError = new Table("error", mDBAndroid);

    private Column<Long> mColumnAccountID = new Column<>("account_id", mTableAccount, Long.class, true);
    private Column<String> mColumnEmail = new Column<>("email", mTableAccount, String.class);
    private Column<String> mColumnUsername = new Column<>("username", mTableAccount, String.class);
    private Column<String> mColumnPassword = new Column<>("password", mTableAccount, String.class);
    private Column<String> mColumnHashType = new Column<>("hashtype", mTableAccount, String.class);
    private Column<String> mColumnSalt = new Column<>("salt", mTableAccount, String.class);

    private Column<Long> mColumnMessageID = new Column<>("message_id", mTableMessage, Long.class, true);
    private Column<String> mColumnMessage = new Column<>("message", mTableMessage, String.class);
    private Column<Long> mColumnSenderID = new Column<>("sender_id", mTableMessage, Long.class);
    private Column<String> mColumnSender = new Column<>("sender", mTableMessage, String.class);
    private Column<Long> mColumnReceiverID = new Column<>("receiver_id", mTableMessage, Long.class);
    private Column<String> mColumnReceiver = new Column<>("receiver", mTableMessage, String.class);
    private Column<String> mColumnTime = new Column<>("time", mTableMessage, String.class);

    private Column<Long> mColumnContactID = new Column<>("contact_id", mTableContact, Long.class, true);
    private Column<Long> mColumnContactAccountID = new Column<>("account_id", mTableContact, Long.class);
    private Column<Long> mColumnContactFriendID = new Column<>("friend_id", mTableContact, Long.class);

    private Column<Long> mColumnSyncID = new Column<>("sync_id", mTableSync, Long.class, true);
    private Column<Long> mColumnSyncAccountID = new Column<>("account_id", mTableSync, Long.class);
    private Column<Long> mColumnSyncMessageID = new Column<>("message_id", mTableSync, Long.class);

    private Column<Long> mColumnErrorID = new Column<>("error_id", mTableError, Long.class, true);
    private Column<Long> mColumnErrorAccountID = new Column<>("account_id", mTableError, Long.class);
    private Column<String> mColumnErrorMessage = new Column<>("message", mTableError, String.class);
    private Column<String> mColumnErrorLocation = new Column<>("location", mTableError, String.class);
    private Column<Long> mColumnErrorPosition = new Column<>("position", mTableError, Long.class);
    private Column<Long> mColumnErrorVersion = new Column<>("version", mTableError, Long.class);

    private DatabaseHelper() {

    }

    public static DatabaseHelper get(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper();
            mInstance.doCreate(context, mInstance.mDBAndroid);
        }
        return mInstance;
    }

    public long getVersion() {
        return 1;
    }

    public long addAccount(Context context, String email, String username, String password,
                           Hashtype hashtype) {
        try {
            String salt = Encrypt.saltString(Encrypt.generateSalt());
            return doInsert(context, mTableAccount,
                    new InsertPair<>(mColumnEmail, email),
                    new InsertPair<>(mColumnUsername, username),
                    new InsertPair<>(mColumnPassword, Hash.hash(password, hashtype)),
                    new InsertPair<>(mColumnHashType, hashtype.toString()),
                    new InsertPair<>(mColumnSalt, salt));
        } catch (GeneralSecurityException ex) {
            Error.log(ex, context);
            return -1;
        }
    }

    public Account getAccount(Context context, long condition, long limit) {
        JSONArray jsonArray = doSelect(context,
                new Column[] { /*An asterisk should be substituted.*/ },
                new Table[]{mTableAccount},
                new Condition[] {
                        new Condition<>(mColumnAccountID, Operator.EQUALS, condition)
                }, limit);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnAccountID, context)
                            .setValue(jsonObject, mColumnEmail, context)
                            .setValue(jsonObject, mColumnUsername, context)
                            .setValue(jsonObject, mColumnPassword, context)
                            .setValue(jsonObject, mColumnHashType, context)
                            .setValue(jsonObject, mColumnSalt, context);
                    long id = record.getValue(mColumnAccountID).getValue();
                    String email = record.getValue(mColumnEmail).getValue();
                    String username = record.getValue(mColumnUsername).getValue();
                    String password = record.getValue(mColumnPassword).getValue();
                    String hashtype = record.getValue(mColumnHashType).getValue();
                    String salt = record.getValue(mColumnSalt).getValue();
                    return new Account(id, email, username, password, hashtype, salt);
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        return null;
    }

    public Account getAccount(Context context, String condition, long limit) {
        return getAccount(context, condition, true, limit);
    }

    public Account getAccount(Context context, String condition, boolean useEmail, long limit) {
        JSONArray jsonArray;
        if (useEmail) {
            jsonArray = doSelect(context,
                    new Column[]{ /*An asterisk should be substituted.*/},
                    new Table[]{mTableAccount},
                    new Condition[]{
                            new Condition<>(mColumnEmail, Operator.EQUALS, condition)
                    }, limit);
        } else {
            jsonArray = doSelect(context,
                    new Column[]{ /*An asterisk should be substituted.*/},
                    new Table[]{mTableAccount},
                    new Condition[]{
                            new Condition<>(mColumnUsername, Operator.EQUALS, condition)
                    }, limit);
        }
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnAccountID, context)
                            .setValue(jsonObject, mColumnEmail, context)
                            .setValue(jsonObject, mColumnUsername, context)
                            .setValue(jsonObject, mColumnPassword, context)
                            .setValue(jsonObject, mColumnHashType, context)
                            .setValue(jsonObject, mColumnSalt, context);
                    long id = record.getValue(mColumnAccountID).getValue();
                    String email = record.getValue(mColumnEmail).getValue();
                    String username = record.getValue(mColumnUsername).getValue();
                    String password = record.getValue(mColumnPassword).getValue();
                    String hashtype = record.getValue(mColumnHashType).getValue();
                    String salt = record.getValue(mColumnSalt).getValue();
                    return new Account(id, email, username, password, hashtype, salt);
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        return null;
    }

    public List<Account> getAccounts(Context context, String condition, long limit) {
        return getAccounts(context, condition, true, limit);
    }

    public List<Account> getAccounts(Context context, String condition, boolean useEmail, long limit) {
        List<Account> accounts = new LinkedList<>();
        JSONArray jsonArray;
        if (useEmail) {
            jsonArray = doSelect(context,
                    new Column[]{ /*An asterisk should be substituted.*/},
                    new Table[]{mTableAccount},
                    new Condition[]{
                            new Condition<>(mColumnEmail, Operator.EQUALS, condition)
                    }, limit);
        } else {
            jsonArray = doSelect(context,
                    new Column[]{ /*An asterisk should be substituted.*/},
                    new Table[]{mTableAccount},
                    new Condition[]{
                            new Condition<>(mColumnUsername, Operator.EQUALS, condition)
                    }, limit);
        }
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnAccountID, context)
                            .setValue(jsonObject, mColumnEmail, context)
                            .setValue(jsonObject, mColumnUsername, context)
                            .setValue(jsonObject, mColumnPassword, context)
                            .setValue(jsonObject, mColumnHashType, context)
                            .setValue(jsonObject, mColumnSalt, context);
                    long id = record.getValue(mColumnAccountID).getValue();
                    String email = record.getValue(mColumnEmail).getValue();
                    String username = record.getValue(mColumnUsername).getValue();
                    String password = record.getValue(mColumnPassword).getValue();
                    String hashtype = record.getValue(mColumnHashType).getValue();
                    String salt = record.getValue(mColumnSalt).getValue();
                    accounts.add(new Account(id, email, username, password, hashtype, salt));
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        return accounts;
    }

    /* ==============================  Account functions  ============================== */

    /**
     * Adds a message to the message table - Then to the sync table.
     * @param context Used in displaying notifications on the activity.
     * @param message Used to save into the database tables.
     * @param receiver Used to save into the database tables.
     * @param sender Used to save into the database tables.
     * @param time Used to save into the database tables.
     * @return The position stored in the message table.
     */
    public long addMessage(Context context, String message, Receiver receiver, Sender sender,
                           Timestamp time) {
        return addMessage(context, new Message(message, sender, receiver, time));
    }

    /**
     * Adds a message to the message table - Then to the sync table.
     * @param context Used in displaying notifications on the activity.
     * @param message Used to save into the database tables.
     * @return The position stored in the message table.
     */
    public long addMessage(Context context, Message message) {
        long position = doInsert(context, mTableMessage,
                new InsertPair<>(mColumnMessage, message.getMessage()),
                new InsertPair<>(mColumnSenderID, message.getSender().getID()),
                new InsertPair<>(mColumnSender, message.getSender().toString()),
                new InsertPair<>(mColumnReceiverID, message.getReceiver().getID()),
                new InsertPair<>(mColumnReceiver, message.getReceiver().toString()),
                new InsertPair<>(mColumnTime, message.getTime()));
        doInsert(context, mTableSync,
                new InsertPair<>(mColumnSyncAccountID, message.getReceiver().getID()),
                new InsertPair<>(mColumnSyncMessageID, position));
        return position;
    }

    /**
     * Collects all messages in the sync table referencing the message table. - Then removes the
     * indexes to the message and receiver in the sync table.
     * @param context Used in displaying notifications on the activity.
     * @param account The account to search messages with.
     * @return A list containing messages.
     */
    public List<Message> getNewMessages(Context context, Account account, long limit) {
        List<Message> messages = new LinkedList<>();
        JSONArray jsonArray = doSelect(context,
                new Column[]{mColumnMessage, mColumnSenderID, mColumnSender, mColumnReceiverID, mColumnReceiver, mColumnTime},
                new Table[]{mTableSync, mTableMessage},
                new Condition[] {
                        new Condition<>(mColumnSyncAccountID, Operator.EQUALS, account.getID(), PostCondition.AND),
                        new Condition<>(mColumnSyncMessageID, Operator.EQUALS, mColumnMessageID)
                }, limit);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnMessage, context)
                            .setValue(jsonObject, mColumnSenderID, context)
                            .setValue(jsonObject, mColumnSender, context)
                            .setValue(jsonObject, mColumnReceiverID, context)
                            .setValue(jsonObject, mColumnReceiver, context)
                            .setValue(jsonObject, mColumnTime, context);
                    String message = record.getValue(mColumnMessage).getValue();
                    String[] senderInfo = record.getValue(mColumnSender).getValue().split(":");
                    Sender sender = new Sender(record.getValue(mColumnSenderID).getValue(),
                            senderInfo[0], senderInfo[1]);
                    String[] receiverInfo = record.getValue(mColumnReceiver).getValue().split(":");
                    Receiver receiver = new Receiver(record.getValue(mColumnReceiverID).getValue(),
                            receiverInfo[0], receiverInfo[1]);
                    Timestamp time;
                    try{
                        time = new Timestamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").parse(
                                record.getValue(mColumnTime).getValue()).getTime());
                    } catch(Exception e) {
                        time = null;
                    }
                    messages.add(new Message(message, sender, receiver, time));
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        long temp = doDelete(context,
                new Table[]{mTableSync},
                new Condition[] {
                        new Condition<>(mColumnSyncAccountID, Operator.EQUALS, account.getID())});
        return messages;
    }

    public List<Message> getRecentMessages(Context context, Account account, Contact contact, int limit) {
        List<Message> messages = new LinkedList<>();
        JSONArray jsonArray = doSelect(context,
                new Column[]{mColumnMessage, mColumnSenderID, mColumnSender, mColumnReceiverID, mColumnReceiver, mColumnTime},
                new Table[]{mTableMessage},
                new Condition[] {
                        new Condition<>(mColumnSenderID, Operator.EQUALS, account.getID(), PostCondition.AND),
                        new Condition<>(mColumnReceiverID, Operator.EQUALS, contact.getID(), PostCondition.OR),
                        new Condition<>(mColumnSenderID, Operator.EQUALS, contact.getID(), PostCondition.AND),
                        new Condition<>(mColumnReceiverID, Operator.EQUALS, account.getID())
                }, limit);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnMessage, context)
                            .setValue(jsonObject, mColumnSenderID, context)
                            .setValue(jsonObject, mColumnSender, context)
                            .setValue(jsonObject, mColumnReceiverID, context)
                            .setValue(jsonObject, mColumnReceiver, context)
                            .setValue(jsonObject, mColumnTime, context);
                    String message = record.getValue(mColumnMessage).getValue();
                    String[] senderInfo = record.getValue(mColumnSender).getValue().split(":");
                    Sender sender = new Sender(record.getValue(mColumnSenderID).getValue(),
                            senderInfo[0], senderInfo[1]);
                    String[] receiverInfo = record.getValue(mColumnReceiver).getValue().split(":");
                    Receiver receiver = new Receiver(record.getValue(mColumnReceiverID).getValue(),
                            receiverInfo[0], receiverInfo[1]);
                    Timestamp time;
                    try{
                        time = new Timestamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").parse(
                                record.getValue(mColumnTime).getValue()).getTime());
                    } catch(Exception e) {
                        time = null;
                    }
                    messages.add(new Message(message, sender, receiver, time));
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        return messages;
    }

    public long addContact(Context context, Account account, Contact contact) {
        return doInsert(context, mTableContact,
                new InsertPair<>(mColumnContactAccountID, account.getID()),
                new InsertPair<>(mColumnContactFriendID, contact.getID()));
    }

    public List<Contact> getContacts(Context context, Account account, long limit) {
        List<Contact> contacts = new LinkedList<>();
        JSONArray jsonArray = doSelect(context,
                new Column[]{mColumnAccountID, mColumnEmail, mColumnUsername},
                new Table[]{mTableContact, mTableAccount},
                new Condition[] {
                        new Condition<>(mColumnContactAccountID, Operator.EQUALS, account.getID(), PostCondition.AND),
                        new Condition<>(mColumnContactFriendID, Operator.EQUALS, mColumnAccountID)
                }, limit);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Record record = new Record()
                            .setValue(jsonObject, mColumnAccountID, context)
                            .setValue(jsonObject, mColumnEmail, context)
                            .setValue(jsonObject, mColumnUsername, context);
                    long id = record.getValue(mColumnAccountID).getValue();
                    String email = record.getValue(mColumnEmail).getValue();
                    String username = record.getValue(mColumnUsername).getValue();
                    contacts.add(new Contact(id, email, username));
                } catch (JSONException ex) {
                    Error.log(ex, context);
                }
            }
        }
        return contacts;
    }

    /* ==============================  Message functions  ============================== */

    public long addError(Context context, Account account, String message, String location, long position, long version) {
        return doInsert(context, mTableError,
                new InsertPair<>(mColumnErrorAccountID, account.getID()),
                new InsertPair<>(mColumnErrorMessage, message),
                new InsertPair<>(mColumnErrorLocation, location),
                new InsertPair<>(mColumnErrorPosition, position),
                new InsertPair<>(mColumnErrorVersion, version));
    }

    private JSONArray doSelect(Context context, Column[] columns, Table[] tables, Condition[] conditions, long limit) {
        long startTime = System.nanoTime();
        String columnNames = "";
        String tableNames = "";
        String conditionNames = "";
        if (columns.length == 0)
            columnNames = "*";
        else {
            for (int i = 0; i < columns.length - 1; i++)
                columnNames += columns[i] + ", ";
            columnNames += columns[columns.length - 1];
        }
        for (int i = 0; i < tables.length - 1; i++)
            tableNames += tables[i] + ", ";
        tableNames += tables[tables.length - 1];
        for (int i = 0; i < conditions.length - 1; i++)
            conditionNames += conditions[i];
        conditionNames += conditions[conditions.length - 1];
        String query = "SELECT " + columnNames + " FROM " + tableNames + " WHERE " + conditionNames + " LIMIT " + limit;
        try {
            String output = setupAsyncTask(context, "dbQuery", new KeyPair(
                    "query", query));
            if (!output.equals("0 results")) {
                System.out.println("doSelect(Context, Column[], Table[], Condition[], long) took: " +
                        ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
                return new JSONArray(output);
            }
        } catch (JSONException ex) {
            Error.log(ex, context);
        }
        System.out.println("doSelect(Context, Column[], Table[], Condition[], long) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return null;
    }

    private JSONArray doRawSelect(Context context, String raw) {
        long startTime = System.nanoTime();
        try {
            String output = setupAsyncTask(context, "dbQuery", new KeyPair(
                    "query", raw));
            if (!output.equals("0 results")) {
                System.out.println("doSelect(Context, String) took: " +
                        ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
                return new JSONArray(output);
            }
        } catch (JSONException ex) {
            Error.log(ex, context);
        }
        System.out.println("doSelect(Context, String) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return null;
    }

    private long doInsert(Context context, Table table, InsertPair ... insertPairs) {
        long startTime = System.nanoTime();
        String columns = " (";
        String values = " VALUES (";
        for (int i = 0; i < insertPairs.length - 1; i++) {
            columns += insertPairs[i].getColumn().getName() + ", ";
            values += insertPairs[i].getValue() + ", ";
        }
        InsertPair insertPair = insertPairs[insertPairs.length - 1];
        switch (insertPair.getType().getSimpleName()) {
            case "Timestamp":
                columns += insertPairs[insertPairs.length - 1].getColumn().getName() + ")";
                values += "CURRENT_TIME())";
                break;
            default:
                columns += insertPairs[insertPairs.length - 1].getColumn().getName() + ")";
                values += insertPairs[insertPairs.length - 1].getValue() + ")";
                break;
        }
        String output = setupAsyncTask(context, "dbExec", new KeyPair(
                "exec", "INSERT INTO " + table + columns + values));
        if (output.matches("Success: [0-9]+")) {
            output = output.substring(9);
            System.out.println("doInsert(Context, Table, InsertPair[]) took: " +
                    ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
            return Long.parseLong(output);
        }
        System.out.println("doInsert(Context, Table, InsertPair[]) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return -1;
    }

    /* ==============================  Contact functions  ============================== */

    private long doDelete(Context context, Table[] tables, Condition[] conditions) {
        long startTime = System.nanoTime();
        String tableNames = "";
        String conditionNames = "";
        for (int i = 0; i < tables.length - 1; i++)
            tableNames += tables[i] + ", ";
        tableNames += tables[tables.length - 1];
        for (int i = 0; i < conditions.length - 1; i++)
            conditionNames += conditions[i];
        conditionNames += conditions[conditions.length - 1];
        String output = setupAsyncTask(context, "dbExec", new KeyPair(
                "exec", "DELETE FROM " + tableNames + " WHERE " + conditionNames));
        if (output.matches("Success: [0-9]+")) {
            output = output.substring(9);
            return Long.parseLong(output);
        }
        System.out.println("doDelete(Context, Table[], Condition[]) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return -1;
    }

    private boolean doCreate(Context context, Database database) {
        long startTime = System.nanoTime();
        String output = "";
        for (Table table : database.getAll()) {
            String query = "CREATE TABLE IF NOT EXISTS " + table.getName() + " (";
            boolean firstIndex = true;
            for (Column column : table.getAll()) {
                if (firstIndex) {
                    query += column.getName() + " ";
                    firstIndex = false;
                } else {
                    query += ", " + column.getName() + " ";
                }
                switch (column.getType().getSimpleName()) {
                    case "String":
                        query += "text";
                        break;
                    case "Integer":
                        query += "int(11)";
                        break;
                    case "Long":
                        query += "int(11)";
                        break;
                    case "Timestamp":
                        query += "datetime";
                        break;
                    case "Sender":
                        query += "text";
                        break;
                    case "Receiver":
                        query += "text";
                        break;
                    default:
                        query += "text";
                }
                if (column.getPrimaryKey()) {
                    query += " PRIMARY KEY";
                } else if (!column.getNullable()) {
                    query += " NOT NULL";
                }
                if (column.getAutoIncrement()) {
                    query += " AUTO_INCREMENT";
                }
            }
            query += ")";
            output += setupAsyncTask(context, "dbExec", new KeyPair("exec", query));
        }
        boolean temp = !output.matches(".*Error: .*");
        System.out.println("doCreate(Context, Database) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return temp;
    }

    /* ===============================  Error functions  =============================== */

    private boolean doUpdate(Context context, Database database) {
        long startTime = System.nanoTime();
        String output = "";
        for (Table table : database.getAll())
            output += setupAsyncTask(context, "dbExec",
                    new KeyPair("exec", "DROP TABLE IF EXISTS " + table));
        boolean temp = doCreate(context, mDBAndroid) && !output.matches(".*Error: .*");
        System.out.println("doUpdate(Context, Database) took: " +
                ((System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        return temp;
    }

    /* ============================== Database Operations ============================== */

    private String setupAsyncTask(Context context, String file, KeyPair ... keyPairs) {
        String output;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // UI Thread -> Begin AsyncTask and wait.
            try {
                DatabaseTask task = new DatabaseTask(context, file);
                output = task.execute(keyPairs).get();
            } catch (InterruptedException | ExecutionException ex) {
                Error.log(ex, context);
                output = "null";
            }
        } else {
            // Worker Thread -> Continue with task
            output = execute(context, file, keyPairs);
        }
        return output;
    }

    private String execute(final Context context, String file, KeyPair ... keyPairs) {
        try {
            // Create URL reference.
            URL url = file.endsWith(".php") ? new URL("http://www.tau-network.co.uk/php/" + file)
                    : new URL("http://www.tau-network.co.uk/php/" + file + ".php");

            // Create parameters for POST request.
            JSONObject postDataParams = new JSONObject();
            for (KeyPair keyPair : keyPairs)
                postDataParams.put(keyPair.getKey(), keyPair.getValue());

            // Set connection settings.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setReadTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Setup output stream then output connection to URL.
            try (OutputStream os = connection.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
            }

            // Get response code then proceed to setup input stream if HTTP:OK
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder sb = new StringBuilder("");
                    String line;
                    while ((line = in.readLine()) != null)
                        sb.append(line);
                    in.close();
                    return sb.toString();
                }
            } else
                return "Error: " + responseCode;
        } catch (IOException | JSONException ex) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Error.log(ex, context);
                }
            });
            return "Error: " + ex.getClass().getName();
        }
    }

    private String getPostDataString(JSONObject params) throws JSONException, UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()){
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    private enum Operator {
        EQUALS, MORETHAN, LESSTHAN, MORETHANEQUALS, LESSTHANEQUALS,
        NOTEQUALS, NOTMORETHAN, NOTLESSTHAN, LIKE
    }

    private enum PostCondition {
        AND, OR, NULL
    }

    private final class InsertPair<T> {
        private Column mColumn;
        private T mValue;
        private Class<T> mType;

        @SuppressWarnings("unchecked")
        private InsertPair(Column column, T value) {
            mColumn = column;
            mType = (Class<T>) value.getClass();
            if (value.getClass().getSimpleName().equals("String")) {
                mValue = mType.cast("\'" + value + "\'");
            } else {
                mValue = value;
            }
        }

        public Column getColumn() {
            return mColumn;
        }

        public T getValue() {
            return mValue;
        }

        public Class<T> getType() {
            return mType;
        }
    }

    private final class Condition<T, V> {
        String condition = "";

        private Condition(T value1, Operator operator, V value2) {
            this(value1, operator, value2, PostCondition.NULL);
        }

        private Condition(T value1, Operator operator, V value2, PostCondition postCondition) {
            if (value1.getClass().getSimpleName().equals("String")) {
                if (operator == Operator.LIKE)
                    condition += "\'%" + value1.toString().replaceAll("(?i)[^a-z0-9@._]", "") + "%\'";
                else
                    condition += "\'" + value1.toString().replaceAll("(?i)[^a-z0-9@._]", "") + "\'";
            } else {
                condition += value1.toString().replaceAll("(?i)[^a-z0-9@._]", "");
            }
            switch (operator) {
                case EQUALS:
                    condition += " = ";
                    break;
                case MORETHAN:
                    condition += " > ";
                    break;
                case LESSTHAN:
                    condition += " < ";
                    break;
                case MORETHANEQUALS:
                    condition += " >= ";
                    break;
                case LESSTHANEQUALS:
                    condition += " <= ";
                    break;
                case NOTEQUALS:
                    condition += " != ";
                    break;
                case NOTMORETHAN:
                    condition += " !> ";
                    break;
                case NOTLESSTHAN:
                    condition += " !< ";
                    break;
                case LIKE:
                    condition += " LIKE ";
                    break;
            }
            if (value2.getClass().getSimpleName().equals("String")) {
                if (operator == Operator.LIKE)
                    condition += "\'%" + value2.toString().replaceAll("(?i)[^a-z0-9@._]", "") + "%\'";
                else
                    condition += "\'" + value2.toString().replaceAll("(?i)[^a-z0-9@._]", "") + "\'";
            } else {
                condition += value2.toString().replaceAll("(?i)[^a-z0-9@._]", "");
            }
            switch (postCondition) {
                case AND:
                    condition += " AND ";
                    break;
                case OR:
                    condition += " OR ";
                    break;
                default:
                    break;
            }
        }

        public String get() {
            return condition;
        }

        @Override
        public String toString() {
            return get();
        }
    }

    private final class DatabaseTask extends AsyncTask<KeyPair, Integer, String> {
        private Context mContext;
        private String mFile;

        private DatabaseTask(Context context, String file) {
            mContext = context;
            mFile = file;
        }

        @Override
        protected String doInBackground(KeyPair... keyPairs) {
            return DatabaseHelper.this.execute(mContext, mFile, keyPairs);
        }
    }

    private class KeyPair {
        String mKey;
        String mValue;

        private KeyPair(String key, String value) {
            mKey = key;
            mValue = value;
        }

        public String getKey() {
            return mKey;
        }

        public String getValue() {
            return mValue;
        }
    }
}