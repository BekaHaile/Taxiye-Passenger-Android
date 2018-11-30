package com.fugu.agent.model;

import com.fugu.constant.FuguAppConstant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class FuguAgentGetMessageParams {
    @SerializedName(FuguAppConstant.PAGE_START)
    @Expose
    private Integer pageStart;
    @SerializedName(FuguAppConstant.ACCESS_TOKEN)
    @Expose
    private String appSecretKey;
    @SerializedName(FuguAppConstant.CHANNEL_ID)
    @Expose
    private Integer channelId;
    @SerializedName(FuguAppConstant.USER_ID)
    @Expose
    private Integer userId;
    @SerializedName(FuguAppConstant.PAGE_END)
    @Expose
    private Integer pageEnd;

    public FuguAgentGetMessageParams(String appSecretKey, Integer channelId, Integer userId, Integer pageStart){
        this.appSecretKey = appSecretKey;
        this.channelId = channelId;
        this.userId = userId;
        this.pageStart = pageStart;
    }

    public void setPageEnd(Integer size) {
        pageEnd = size;
    }
}
