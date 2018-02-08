package com.nayrisian.dev.messageproject.event;

import com.nayrisian.dev.messageproject.database.type.Message;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by Nayrisian on 25/12/2016.
 */
public class ChatReceiveEvent {
    private static List<ChatEventListener> mListeners = new LinkedList<>();

    public static void call(Message message) {
        for (ChatEventListener listener : mListeners)
            listener.run(message);
    }

    public static void register(ChatEventListener listener) {
        mListeners.add(listener);
    }

    public static void unregister(ChatEventListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }
}
