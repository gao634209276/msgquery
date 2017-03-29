package com.sinova.monitor.model;

import java.util.Comparator;

/**
 */
public class MessageAndDateComparator implements Comparator<MessageAndDate> {
    @Override
    public int compare(MessageAndDate mad1, MessageAndDate mad2) {
        return mad1.getCode() - mad2.getCode();
//        return mad2.getDate().compareTo(mad1.getDate());

    }
}
