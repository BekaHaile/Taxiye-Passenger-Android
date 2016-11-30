package product.clicklabs.jugnoo.retrofit.model;

import java.io.Serializable;

/**
 * Created by ankit on 11/11/16.
 */

public class Chat implements Serializable {
    private int chatHistoryId;
    private String message;
    private String createdAt;
    private int isSender;

    public Chat(int chatHistoryId, String createdAt, int isSender, String message) {
        this.chatHistoryId = chatHistoryId;
        this.createdAt = createdAt;
        this.isSender = isSender;
        this.message = message;
    }

    public int getChatHistoryId() {
        return chatHistoryId;
    }

    public void setChatHistoryId(int chatHistoryId) {
        this.chatHistoryId = chatHistoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsSender() {
        return isSender;
    }

    public void setIsSender(int isSender) {
        this.isSender = isSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
