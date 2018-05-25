package product.clicklabs.jugnoo.stripe;

import java.util.ArrayList;

import product.clicklabs.jugnoo.stripe.model.StripeCardData;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public interface StripeCardsStateListener {

    void onCardsUpdated(ArrayList<StripeCardData> stripeCardData);



}
