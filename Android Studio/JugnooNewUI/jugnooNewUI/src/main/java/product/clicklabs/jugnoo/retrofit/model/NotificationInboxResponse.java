package product.clicklabs.jugnoo.retrofit.model;

/**
 * Created by socomo33 on 3/28/16.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.datastructure.NotificationData;

public class NotificationInboxResponse {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("pushes")
    @Expose
    private List<NotificationData> pushes = new ArrayList<NotificationData>();
    @SerializedName("total")
    @Expose
    private Integer total;

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return The pushes
     */
    public List<NotificationData> getPushes() {
        return pushes;
    }

    /**
     * @param pushes The pushes
     */
    public void setPushes(List<NotificationData> pushes) {
        this.pushes = pushes;
    }

    /**
     * @return The total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

}