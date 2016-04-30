package product.clicklabs.jugnoo.home.models;

/**
 * Created by Ankit on 4/29/16.
 */
public class MenuInfo {
    public String tag;
    public String name;
    public int isNew;

    public MenuInfo(String tag, String name, int isNew){
        this.tag = tag;
        this.name = name;
        this.isNew = isNew;
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
}
