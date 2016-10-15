package uk.ac.solent.nayrisian.messageproject.database.tables;

import uk.ac.solent.nayrisian.messageproject.Receiver;
import uk.ac.solent.nayrisian.messageproject.Sender;

/**
 * Created by Nayrisian on 15/10/2016.
 */

public class Messages implements ITable {
    public static final String TABLE_MESSAGES = "messages",
            COLUMN_MESSAGEID = "_id",
            COLUMN_MESSAGE = "message",
            COLUMN_SENDER = "sender",
            COLUMN_RECEIVER = "receiver",
            COLUMN_TIME = "time";

    protected int _id;
    protected String _message;
    protected Sender _sender;
    protected Receiver _reveiver;
}
