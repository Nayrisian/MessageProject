package com.nayrisian.dev.messageproject.event;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by Nayrisian on 04/12/2016.
 */
public class BaseEvent {
    private static List<EventListener> mListeners = new LinkedList<>();

    public static void call() {
        for (EventListener listener : mListeners)
            listener.run();
    }

    public static void register(EventListener listener) {
        mListeners.add(listener);
    }

    public static void unregister(EventListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }
}