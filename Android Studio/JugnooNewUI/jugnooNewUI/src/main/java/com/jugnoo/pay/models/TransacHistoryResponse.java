package com.jugnoo.pay.models;

/**
 * Created by cl-macmini-38 on 9/23/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransacHistoryResponse {

    private Integer flag;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private List<TransactionHistory> transaction_history = new ArrayList<TransactionHistory>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return The transactionHistory
     */
    public List<TransactionHistory> getTransactionHistory() {
        return transaction_history;
    }

    /**
     * @param transactionHistory The transaction_history
     */
    public void setTransactionHistory(List<TransactionHistory> transactionHistory) {
        this.transaction_history = transactionHistory;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


    public class TransactionHistory {

        private Integer amount;
        private String date;
        private Integer status;
        private String name;
        private Integer txn_type;
        private Integer id;
        private String requester_phone_no;
        private String message;
        private Integer is_remind_active;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * @return The amount
         */
        public Integer getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        /**
         * @return The date
         */
        public String getDate() {
            return date;
        }

        /**
         * @param date The date
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * @return The status
         */
        public Integer getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(Integer status) {
            this.status = status;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The txnType
         */
        public Integer getTxnType() {
            return txn_type;
        }

        /**
         * @param txnType The txn_type
         */
        public void setTxnType(Integer txnType) {
            this.txn_type = txnType;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        public String getRequesterPhoneNo() {
            return requester_phone_no;
        }

        public void setRequesterPhoneNo(String requester_phone_no) {
            this.requester_phone_no = requester_phone_no;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getIs_remind_active() {
            if(is_remind_active == null){
                return 0;
            }
            return is_remind_active;
        }

        public void setIs_remind_active(Integer is_remind_active) {
            this.is_remind_active = is_remind_active;
        }
    }

    public enum Type{
        REQUEST_BY_SENT(1),
        REQUESTED_FROM_PENDING(2), // cancel, remind
        REQUEST_BY_PENDING(3), // decline, send money
        REQUESTED_FROM_RECEIVED(4)
        ;

        private int ordinal;
        Type(int ordinal){
           this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }
}