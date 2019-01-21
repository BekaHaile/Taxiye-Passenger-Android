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
    @SerializedName("is_new")
    @Expose
    private int isNew;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("icon_highlighted")
    @Expose
    private String iconHighlighted;
    @SerializedName("icon_normal")
    @Expose
    private String iconNormal;
    private boolean isUIEnabled=true;

    public boolean isUIEnabled() {
        return isUIEnabled;
    }

    public void setUIEnabled(boolean UIEnabled) {
        isUIEnabled = UIEnabled;
    }

    public boolean getShowInAccount() {
        return showInAccount==1;
    }

    public void setShowInAccount(int showInAccount) {
        this.showInAccount = showInAccount;
    }

    @SerializedName("show_in_account")
    private int showInAccount;

    /**
     * for matching with list
     * @param tag tag value
     */
    public MenuInfo(String tag){
        this.tag = tag;
    }
    public MenuInfo(String name, String tag){
        this.name = name;
        this.tag = tag;
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

    public String getIconHighlighted() {
        return iconHighlighted;
    }

    public void setIconHighlighted(String iconHighlighted) {
        this.iconHighlighted = iconHighlighted;
    }

    public String getIconNormal() {
        return iconNormal;
    }

    public void setIconNormal(String iconNormal) {
        this.iconNormal = iconNormal;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuInfo && ((MenuInfo)obj).tag.equalsIgnoreCase(tag);
    }
}
