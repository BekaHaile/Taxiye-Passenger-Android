package product.clicklabs.jugnoo.retrofit.model;

/**
 * Created by ankit on 08/11/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class NearbyPickupRegions {

    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("hover_info")
    @Expose
    private List<HoverInfo> hoverInfo = new ArrayList<HoverInfo>();
    @SerializedName("default_location")
    @Expose
    private HoverInfo defaultLocation;

    /**
     *
     * @return
     * The cityId
     */
    public Integer getCityId() {
        return cityId;
    }

    /**
     *
     * @param cityId
     * The city_id
     */
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    /**
     *
     * @return
     * The hoverInfo
     */
    public List<HoverInfo> getHoverInfo() {
        return hoverInfo;
    }

    /**
     *
     * @param hoverInfo
     * The hover_info
     */
    public void setHoverInfo(List<HoverInfo> hoverInfo) {
        this.hoverInfo = hoverInfo;
    }

    public HoverInfo getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(HoverInfo defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public class HoverInfo {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        /**
         *
         * @return
         * The text
         */
        public String getText() {
            return text;
        }

        /**
         *
         * @param text
         * The text
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         *
         * @return
         * The latitude
         */
        public String getLatitude() {
            return latitude;
        }

        /**
         *
         * @param latitude
         * The latitude
         */
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        /**
         *
         * @return
         * The longitude
         */
        public String getLongitude() {
            return longitude;
        }

        /**
         *
         * @param longitude
         * The longitude
         */
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

    }

}
