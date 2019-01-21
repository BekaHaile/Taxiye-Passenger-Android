package com.fugu.agent.model.getConversationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gurmail on 20/06/18.
 *
 * @author gurmail
 */
public class Data {

    @SerializedName("conversation_list")
    @Expose
    private List<Conversation> conversation = null;
    @SerializedName("page_size")
    @Expose
    private Integer pageSize;
    @SerializedName("version")
    @Expose
    private Version version;

    public List<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(List<Conversation> conversation) {
        this.conversation = conversation;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
