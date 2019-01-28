package com.fugu.agent.model.LoginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */
class Version {
    @SerializedName("latest_version")
    @Expose
    private Integer latestVersion;
    @SerializedName("current_version")
    @Expose
    private String currentVersion;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("download_link")
    @Expose
    private String downloadLink;
    @SerializedName("is_force")
    @Expose
    private Integer isForce;

    public Integer getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public Integer getIsForce() {
        return isForce;
    }

    public void setIsForce(Integer isForce) {
        this.isForce = isForce;
    }


}
