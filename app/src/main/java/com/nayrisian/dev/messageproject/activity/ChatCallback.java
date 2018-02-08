package com.nayrisian.dev.messageproject.activity;

import com.nayrisian.dev.messageproject.database.type.Message;

import java.util.List;

/**
 *
 * Created by Nayrisian on 03/12/2016.
 */
interface ChatCallback {
    void updateChat(List<Message> messages);
}