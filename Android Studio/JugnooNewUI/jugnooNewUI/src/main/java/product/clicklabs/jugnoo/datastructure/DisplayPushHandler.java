package product.clicklabs.jugnoo.datastructure;

import org.json.JSONObject;

/**
 * Created by socomo20 on 10/29/15.
 */
public interface DisplayPushHandler {
	void onDisplayMessagePushReceived(JSONObject jsonObject);
}
