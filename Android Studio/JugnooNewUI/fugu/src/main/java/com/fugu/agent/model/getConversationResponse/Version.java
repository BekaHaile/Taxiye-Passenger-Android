package com.fugu.agent.model.getConversationResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 20/06/18.
 *
 * @author gurmail
 */
public class Version {
    @SerializedName("text")
    private String text;
    @SerializedName("download_link")
    private String downloadLink;
    @SerializedName("is_force")
    private Integer isForce;
    @SerializedName("current_version")
    private Integer currentVersion;
    @SerializedName("latest_version")
    private Integer latestVersion;

    public String getText() {
        return text;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public Integer getIsForce() {
        return isForce;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public Integer getLatestVersion() {
        return latestVersion;
    }
}
