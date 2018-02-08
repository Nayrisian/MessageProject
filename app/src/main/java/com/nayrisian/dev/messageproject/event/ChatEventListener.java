package com.nayrisian.dev.messageproject.event;

import com.nayrisian.dev.messageproject.database.type.Message;

/**
 * Created by Nayrisian on 25/12/2016.
 */
public interface ChatEventListener {
    void run(Message message);
}