package com.fugu.agent.Util;

/**
 * Created by ankit on 31/05/17.
 */

public enum UserType {
    USER(1),
    AGENT(2);

    private int ordinal;

    UserType(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
