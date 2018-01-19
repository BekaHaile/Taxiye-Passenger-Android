package product.clicklabs.jugnoo.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;


/**
 * Created by Parminder Saini on 19/01/18.
 */

public class FetchOfferingsVisibilityResponse extends FeedCommonResponse {


    @SerializedName("data")
    private FetchOfferingsVisibilityData data;

    public FetchOfferingsVisibilityData getData() {
        return data;
    }

    public static class FetchOfferingsVisibilityData {

        @SerializedName("fresh_enabled")
        @Expose
        private Integer freshEnabled = 0;
        @SerializedName("meals_enabled")
        @Expose
        private Integer mealsEnabled = 0;
        @SerializedName("delivery_enabled")
        @Expose
        private Integer deliveryEnabled = 0;
        @SerializedName("grocery_enabled")
        @Expose
        private Integer groceryEnabled = 0;
        @SerializedName("menus_enabled")
        @Expose
        private Integer menusEnabled = 0;
        @SerializedName("delivery_customer_enabled")
        @Expose
        private Integer deliveryCustomerEnabled = 0;
        @SerializedName("pay_enabled")
        @Expose
        private Integer payEnabled = 0;
        @SerializedName("feed_enabled")
        @Expose
        private Integer feedEnabled = 0;
        @SerializedName("autos_enabled")
        @Expose
        private Integer autosEnabled ;
        @SerializedName("pros_enabled")
        @Expose
        private Integer prosEnabled = 0;
        @SerializedName("integrated_jugnoo_enabled")
        @Expose
        private Integer integratedJugnooEnabled;


        public Integer getFreshEnabled() {
            return freshEnabled;
        }

        public Integer getMealsEnabled() {
            return mealsEnabled;
        }

        public Integer getDeliveryEnabled() {
            return deliveryEnabled;
        }

        public Integer getGroceryEnabled() {
            return groceryEnabled;
        }

        public Integer getMenusEnabled() {
            return menusEnabled;
        }

        public Integer getDeliveryCustomerEnabled() {
            return deliveryCustomerEnabled;
        }

        public Integer getPayEnabled() {
            return payEnabled;
        }

        public Integer getFeedEnabled() {
            return feedEnabled;
        }

        public Integer getAutosEnabled() {
            return autosEnabled;
        }

        public Integer getProsEnabled() {
            return prosEnabled;
        }

        public Integer getIntegratedJugnooEnabled() {
            return integratedJugnooEnabled;
        }
    }
}
