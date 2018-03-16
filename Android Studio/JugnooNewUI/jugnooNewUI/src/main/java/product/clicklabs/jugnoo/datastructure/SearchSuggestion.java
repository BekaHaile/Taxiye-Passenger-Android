package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 3/16/18.
 */

public class SearchSuggestion {

    @SerializedName("line_1")
    private String text;

    @SerializedName("line_1_color")
    private String textColor;

    @SerializedName("restaurant_item_id")
    private int itemId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(final String textColor) {
        this.textColor = textColor;
    }
}
