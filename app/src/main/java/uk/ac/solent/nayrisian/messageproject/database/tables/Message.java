package uk.ac.solent.nayrisian.messageproject.database.tables;

import java.sql.Time;

import uk.ac.solent.nayrisian.messageproject.Receiver;
import uk.ac.solent.nayrisian.messageproject.Sender;

/**
 * Message table within the SQLite database. Stores message properties.
 * Created by Nayrisian on 15/10/2016.
 */

public class Message implements ITable {
    public static final String TABLE_MESSAGES = "messages",
            COLUMN_MESSAGEID = "_id",
            COLUMN_MESSAGE = "message",
            COLUMN_SENDER = "sender",
            COLUMN_RECEIVER = "receiver",
            COLUMN_TIME = "time";

    private int _id;
    private String _message;
    private Sender _sender;
    private Receiver _receiver;
    private Time _time;

    public int getID() {
        return _id;
    }

    public void setID(int id) {
        _id = id;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public Sender getSender() {
        return _sender;
    }

    public void setSender(Sender sender) {
        _sender = sender;
    }

    public Receiver getReceiver() {
        return _receiver;
    }

    public void setReceiver(Receiver receiver) {
        _receiver = receiver;
    }

    public Time getTime() {
        return _time;
    }

    public void setTime(Time time) {
        _time = time;
    }
}
