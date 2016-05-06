package product.clicklabs.jugnoo.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ankit on 4/29/16.
 */
public class MenuInfo {

    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isNew")
    @Expose
    private int isNew;
    @SerializedName("icon")
    @Expose
    private String icon;

    public MenuInfo(String tag, String name, int isNew, String icon){
        this.tag = tag;
        this.name = name;
        this.isNew = isNew;
        this.icon = icon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
