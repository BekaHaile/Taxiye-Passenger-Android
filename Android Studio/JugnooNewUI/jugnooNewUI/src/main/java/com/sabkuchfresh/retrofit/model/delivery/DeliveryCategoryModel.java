package com.sabkuchfresh.retrofit.model.delivery;

/**
 * Created by Parminder Saini on 02/09/17.
 */

public class DeliveryCategoryModel {

    private String categoryName;
    private String iconUrl;

    public DeliveryCategoryModel(String categoryName, String iconUrl) {
        this.categoryName = categoryName;
        this.iconUrl = iconUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
