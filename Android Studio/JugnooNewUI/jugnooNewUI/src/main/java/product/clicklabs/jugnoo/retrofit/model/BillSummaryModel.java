package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/12/17.
 */

public class BillSummaryModel {

    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    @SerializedName("format")
    private Format format;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Format getFormat() {
        return format;
    }

}
