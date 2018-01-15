package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 1/15/18.
 */

public class FatafatUploadImageInfo {

    @SerializedName("show_image_box")
    private int showImageBox;

    @SerializedName("image_limit")
    private int imageLimit;

    public int getShowImageBox() {
        return showImageBox;
    }

    public void setShowImageBox(final int showImageBox) {
        this.showImageBox = showImageBox;
    }

    public int getImageLimit() {
        return imageLimit;
    }

    public void setImageLimit(final int imageLimit) {
        this.imageLimit = imageLimit;
    }
}
