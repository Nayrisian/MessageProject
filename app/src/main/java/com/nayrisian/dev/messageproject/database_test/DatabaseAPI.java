package com.nayrisian.dev.messageproject.database_test;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import com.nayrisian.dev.messageproject.database_test.structure.Database;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * The database instance used to connect to a listed database.
 * This class uses a strict input system to prevent SQL injection attempts.
 * Created by Nayrisian on 10/12/2016.
 */
class DatabaseAPI {
    /**
     * Query method to return values listed in a database.
     *
     * @param context  The application level context referencing to where the API is operating at.
     * @param keyPairs The key pair value to store within the POST request.
     * @return An output class with the state and value of the output.
     */
    public static Output doQuery(Context context, Database database, KeyPair... keyPairs) {
        // Performs a query instruction to the database.
        String output = doTask(context, "dbQuery.php", keyPairs);
        if (output.matches("Connection failed: .*")) {
            return new Output(OutputCode.FAILED_CONNECT, null);
        } else if (output.matches("0 results")) {
            return new Output(OutputCode.NO_RESULTS, null);
        }
        try {
            JSONArray jsonArray = new JSONArray(output);
            return new Output(OutputCode.SUCCESS, jsonArray);
        } catch (JSONException ex) {
            Error.log(ex, context);
            return new Output(OutputCode.ERROR, null);
        }
    }

    /**
     * Execute method to perform an operation on a database.
     *
     * @param context  The application level context referencing to where the API is operating at.
     * @param keyPairs The key pair value to store within the POST request.
     * @return An output class with the state and value of the output.
     */
    public static OutputExec doExec(Context context, Database database, KeyPair... keyPairs) {
        // Performs an execution instruction to the database.
        String output = doTask(context, "dbExec.php", keyPairs);
        if (output.matches("Success: [0-9]+")) {
            return new OutputExec(OutputCode.SUCCESS, Long.parseLong(output.substring(9)));
        } else if (output.matches("Connection failed: .*")) {
            return new OutputExec(OutputCode.FAILED_CONNECT, -1);
        } else {
            return new OutputExec(OutputCode.ERROR, -1);
        }
    }

    /**
     * HTTP POST method that interfaces with the server PHP files -> Database.
     *
     * @param context  The application level context referencing to where the API is operating at.
     * @param file     The file name of the PHP API on the server.
     * @param keyPairs The key pair value to store within the POST request.
     * @return A string that is JSON encoded.
     */
    private static String doTask(Context context, String file, KeyPair... keyPairs) {
        // Sends a HTTP / POST to the central database.
        String output;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            DatabaseTask task;
            try {
                task = new DatabaseTask(context, file);
                output = task.execute(keyPairs).get();
            } catch (InterruptedException | ExecutionException ex) {
                // Caught when the task fails.
                Error.log(ex, context);
                output = "null";
            }
        } else {
            try {
                URL url = file.endsWith(".php") ? new URL("http://www.tau-network.co.uk/php/" + file)
                        : new URL("http://www.tau-network.co.uk/php/" + file + ".php");
                JSONObject postDataParams = new JSONObject();
                for (KeyPair keyPair : keyPairs)
                    postDataParams.put(keyPair.getKey(), keyPair.getValue());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                try (
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))
                ) {
                    StringBuilder result = new StringBuilder();
                    boolean first = true;
                    Iterator<String> itr = postDataParams.keys();
                    while (itr.hasNext()) {
                        String key = itr.next();
                        Object value = postDataParams.get(key);
                        if (first)
                            first = false;
                        else
                            result.append("&");
                        result.append(URLEncoder.encode(key, "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(value.toString(), "UTF-8"));
                    }
                    writer.write(result.toString());
                }
                // Get response code then proceed to setup input stream if HTTP:OK
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (
                            InputStreamReader temp = new InputStreamReader(connection.getInputStream());
                            BufferedReader in = new BufferedReader(temp)
                    ) {
                        StringBuilder sb = new StringBuilder("");
                        String line;
                        while ((line = in.readLine()) != null)
                            sb.append(line);
                        in.close();
                        return sb.toString();
                    }
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException | JSONException ex) {
                System.out.println(ex.getMessage());
                return "Error: " + ex.getClass().getName();
            }
        }
        return output;
    }

    /**
     * Conditional symbol that performs boolean logic between two conditional statements in a
     * WHERE clause.
     */
    public enum ConditionJoin {
        AND, OR
    }

    /**
     * Conditional symbol that performs boolean logic between two conditional objects in a WHERE
     * clause.
     */
    public enum ConditionCompare {
        EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, MORE_THAN, MORE_THAN_OR_EQUAL
    }

    /**
     * The state of the output after a HTTP POST response.
     */
    public enum OutputCode {
        SUCCESS, NO_RESULTS, FAILED_CONNECT, ERROR
    }

    /**
     * Generic value class with a class reference within to cast when getting the value, providing
     * additional type safety.
     * @param <T> The type of the value stored.
     */
    public static class Value<T> {
        private Class<T> mClazz;
        private T mValue;

        public Value(Class<T> clazz, T value) {
            mClazz = clazz;
            mValue = value;
        }

        public T getValue() {
            return mClazz.cast(mValue);
        }

        public Class<T> getClazz() {
            return mClazz;
        }
    }

    /**
     * Condition statement used in WHERE clauses for SQL.
     */
    public static class Condition {
        private String mOperator;
        private Value mObject1;
        private Value mObject2;

        public Condition(Value object1, ConditionCompare operator, Value object2) {
            switch (operator) {
                case EQUAL:
                    mOperator = " = ";
                    break;
                case NOT_EQUAL:
                    mOperator = " != ";
                    break;
                case LESS_THAN:
                    mOperator = " < ";
                    break;
                case LESS_THAN_OR_EQUAL:
                    mOperator = " <= ";
                    break;
                case MORE_THAN:
                    mOperator = " > ";
                    break;
                case MORE_THAN_OR_EQUAL:
                    mOperator = " >= ";
                    break;
            }
            mObject1 = object1;
            mObject2 = object2;
        }

        public String getCondition() {
            return mObject1.getValue().toString() + mOperator + mObject2.getValue().toString();
        }
    }

    /**
     * A group of condition statements with Conditions and ConditionJoins.
     */
    public static class Where {
        private Condition[] mConditions;
        private ConditionJoin[] mConditionJoins;

        public Where(Condition[] conditions, ConditionJoin[] conditionJoins) {
            int length = conditions.length;
            mConditions = new Condition[length];
            mConditionJoins = new ConditionJoin[length - 1];
            if (conditions.length > 0) {
                mConditions[0] = conditions[0];
            }
            for (int i = 1; i < length; i++) {
                switch (conditionJoins[i]) {
                    case AND:
                        mConditionJoins[i] = ConditionJoin.AND;
                        break;
                    case OR:
                        mConditionJoins[i] = ConditionJoin.OR;
                        break;
                    default:
                        mConditionJoins[i] = ConditionJoin.AND;
                        break;
                }
                mConditions[i] = conditions[i];
            }
        }

        public String get() {
            String output = "WHERE ";
            if (mConditions.length > 0) {
                output += mConditions[0].getCondition();
            }
            for (int i = 1; i < mConditions.length; i++) {
                switch (mConditionJoins[i]) {
                    case AND:
                        output += " AND ";
                        break;
                    case OR:
                        output += " OR ";
                        break;
                    default:
                        output += " AND ";
                        break;
                }
                output += mConditions[i].getCondition();
            }
            return output;
        }
    }

    /**
     * Output of the functions for SQL.
     */
    public static class Output {
        private OutputCode mCode;
        private JSONArray mOutput;

        private Output(OutputCode code, JSONArray output) {
            mCode = code;
            mOutput = output;
        }

        public OutputCode getCode() {
            return mCode;
        }

        public JSONArray getOutput() {
            return mOutput;
        }
    }

    /**
     * Output of the functions for SQL.
     */
    public static class OutputExec {
        private OutputCode mCode;
        private long mOutput;

        private OutputExec(OutputCode code, long output) {
            mCode = code;
            mOutput = output;
        }

        public OutputCode getCode() {
            return mCode;
        }

        public long getOutput() {
            return mOutput;
        }
    }

    /**
     * KeyPair class which holds a single map-like value, Key to Value.
     */
    public static class KeyPair {
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

    /**
     * DatabaseTask class which derives from an AsyncTask - Handles an asynchronous HTTP POST task.
     */
    public static class DatabaseTask extends AsyncTask<KeyPair, Integer, String> {
        private Context mContext;
        private String mFile;

        private DatabaseTask(Context context, String file) {
            mContext = context;
            mFile = file;
        }

        @Override
        protected String doInBackground(KeyPair ... keyPairs) {
            return DatabaseAPI.doTask(mContext, mFile, keyPairs);
        }
    }
}