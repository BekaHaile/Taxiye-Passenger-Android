package product.clicklabs.jugnoo.stripe.model;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

/**
 * Created by Parminder Saini on 24/05/18.
 */
public class StripeCardResponse extends FeedCommonResponse {

    @SerializedName("card_data")
    private ArrayList<StripeCardData> stripeCardData;

    public ArrayList<StripeCardData> getStripeCardData() {
        return stripeCardData;
    }
}
