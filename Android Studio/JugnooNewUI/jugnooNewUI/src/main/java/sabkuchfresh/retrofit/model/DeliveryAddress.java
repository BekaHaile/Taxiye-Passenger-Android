package sabkuchfresh.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Gurmail S. Kang on 5/13/16.
 */
public class DeliveryAddress {

    @SerializedName("last_address")
    @Expose
    private String lastAddress;
    @SerializedName("delivery_latitude")
    @Expose
    private String deliveryLatitude;
    @SerializedName("delivery_longitude")
    @Expose
    private String deliveryLongitude;

    /**
     *
     * @return
     * The lastAddress
     */
    public String getLastAddress() {
        return lastAddress;
    }

    /**
     *
     * @param lastAddress
     * The last_address
     */
    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    /**
     *
     * @return
     * The deliveryLatitude
     */
    public String getDeliveryLatitude() {
        return deliveryLatitude;
    }

    /**
     *
     * @param deliveryLatitude
     * The delivery_latitude
     */
    public void setDeliveryLatitude(String deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    /**
     *
     * @return
     * The deliveryLongitude
     */
    public String getDeliveryLongitude() {
        return deliveryLongitude;
    }

    /**
     *
     * @param deliveryLongitude
     * The delivery_longitude
     */
    public void setDeliveryLongitude(String deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

}
