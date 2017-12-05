package com.sabkuchfresh.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/5/17.
 */

public class FatafatTutorialData {

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("heading")
    private String pointHeader;

    @SerializedName("info")
    private String pointContent;

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPointHeader(final String pointHeader) {
        this.pointHeader = pointHeader;
    }

    public void setPointContent(final String pointContent) {
        this.pointContent = pointContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPointHeader() {
        return pointHeader;
    }

    public String getPointContent() {
        return pointContent;
    }
}
