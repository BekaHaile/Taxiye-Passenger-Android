package product.clicklabs.jugnoo.stripe.model;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

/**
 * Created by Parminder Saini on 24/05/18.
 */
public class StripeCardResponse extends FeedCommonResponse {

    @SerializedName("stripe_data")
    private StripeCardData stripeCardData;

    public StripeCardData getStripeCardData() {
        return stripeCardData;
    }
}
