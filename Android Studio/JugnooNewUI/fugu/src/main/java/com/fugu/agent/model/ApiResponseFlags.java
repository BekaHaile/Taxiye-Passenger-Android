package com.fugu.agent.model;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public enum ApiResponseFlags {
    ACTION_ACCEPTED(202),
    ACTION_COMPLETE(200),
    SESSION_EXPIRED(403),
    LOGIN_ERROR(401);

    private int ordinal;

    ApiResponseFlags(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
