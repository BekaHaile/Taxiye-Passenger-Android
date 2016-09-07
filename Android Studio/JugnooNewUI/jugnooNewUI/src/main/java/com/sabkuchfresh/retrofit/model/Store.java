package com.sabkuchfresh.retrofit.model;

/**
 * Created by gurmail on 14/07/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Store {

    @SerializedName("store_id")
    @Expose
    private Integer storeId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("text_color")
    @Expose
    private String textColor;
    /**
     *
     * @return
     * The storeId
     */
    public Integer getStoreId() {
        return storeId;
    }

    /**
     *
     * @param storeId
     * The store_id
     */
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The textColor
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     *
     * @param textColor
     * The image
     */
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

}