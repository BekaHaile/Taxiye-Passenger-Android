package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/12/17.
 */

public class Format {

    @SerializedName("is_bold")
    private int isBold;

    @SerializedName("color")
    private String color;

    public int getIsBold() {
        return isBold;
    }

    public String getColor() {
        return color;
    }
}
