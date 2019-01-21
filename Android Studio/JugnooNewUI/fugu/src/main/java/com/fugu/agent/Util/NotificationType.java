package com.fugu.agent.Util;

/**
 * Created by gurmail on 20/06/18.
 *
 * @author gurmail
 */

public enum NotificationType {
    ASSIGNMENT(3);

    private int ordinal;

    NotificationType(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
