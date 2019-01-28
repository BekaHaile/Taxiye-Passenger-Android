package com.fugu.agent.Util;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public enum  Overlay {
    DEFAULT(0),
    OPEN_CHAT(1),
    CLOSED_CHAT(2),
    ASSIGNMENT(3);

    private int ordinal;

    Overlay(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}