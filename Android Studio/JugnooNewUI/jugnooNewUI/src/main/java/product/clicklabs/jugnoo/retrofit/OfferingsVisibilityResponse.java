package product.clicklabs.jugnoo.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;


/**
 * Created by Parminder Saini on 19/01/18.
 */

public class OfferingsVisibilityResponse extends FeedCommonResponse {


    @SerializedName("data")
    private OfferingsVisibilityData data;

    public OfferingsVisibilityData getData() {
        return data;
    }

    public static class OfferingsVisibilityData {

        @SerializedName("fresh_enabled")
        @Expose
        private int freshEnabled  ;
        @SerializedName("meals_enabled")
        @Expose
        private int mealsEnabled ;
        @SerializedName("delivery_enabled")
        @Expose
        private int deliveryEnabled ;
        @SerializedName("grocery_enabled")
        @Expose
        private int groceryEnabled ;
        @SerializedName("menus_enabled")
        @Expose
        private int menusEnabled ;
        @SerializedName("delivery_customer_enabled")
        @Expose
        private int deliveryCustomerEnabled ;
        @SerializedName("pay_enabled")
        @Expose
        private int payEnabled;
        @SerializedName("feed_enabled")
        @Expose
        private int feedEnabled ;
        @SerializedName("autos_enabled")
        @Expose
        private int autosEnabled ;
        @SerializedName("pros_enabled")
        @Expose
        private int prosEnabled ;
        @SerializedName("integrated_jugnoo_enabled")
        @Expose
        private int integratedJugnooEnabled;


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
