package com.fugu.agent.Util;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public enum ConversationMode {
    DEFAULT(0),
    MY_CHATS(1),
    UNASSIGNED(2),
    ALL(10),
    TAGGED(4);

    private int ordinal;

    ConversationMode(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
