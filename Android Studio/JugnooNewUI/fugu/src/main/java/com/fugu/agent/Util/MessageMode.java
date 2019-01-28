package com.fugu.agent.Util;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public enum MessageMode {
    OPEN_CHAT(1),
    CLOSED_CHAT(2);

    private int ordinal;

    MessageMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
