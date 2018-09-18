package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

/**
 * Created by Parminder Saini on 18/09/18.
 */
public class AddCardPayStackModel extends FeedCommonResponse {

    @SerializedName("authorization_url")
    private String url;

    public String getUrl() {
        return url;
    }
}
