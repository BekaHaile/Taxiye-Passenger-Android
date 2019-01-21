package com.fugu.agent.Util;

/**
 * Created by ankit on 31/05/17.
 */

public enum AgentType {
    AGENT(11),
    ADMIN(13);

    private int ordinal;

    AgentType(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
