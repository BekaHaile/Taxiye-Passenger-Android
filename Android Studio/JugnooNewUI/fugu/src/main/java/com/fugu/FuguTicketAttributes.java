package com.fugu;

/**
 * Created by gurmail on 04/04/18.
 */

public class FuguTicketAttributes {

    private String mFaqName;
    private String mTransactionId;

    public String getmFaqName() {
        return mFaqName;
    }
    public String getmTransactionId() {
        return mTransactionId;
    }


    public static class Builder {

        private String mFaqName;
        private String mTransactionId;

        public Builder setFaqName(String mFaqName) {
            this.mFaqName = mFaqName;
            return this;
        }

        public Builder setTransactionId(String mTransactionId) {
            this.mTransactionId = mTransactionId;
            return this;
        }

        public FuguTicketAttributes build() {
            return new FuguTicketAttributes(this);
        }
    }

    private FuguTicketAttributes(Builder builder) {
        this.mFaqName = builder.mFaqName;
        this.mTransactionId = builder.mTransactionId;
    }
}
