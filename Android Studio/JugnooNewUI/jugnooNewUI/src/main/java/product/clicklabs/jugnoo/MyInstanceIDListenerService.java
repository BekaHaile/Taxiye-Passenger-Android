package product.clicklabs.jugnoo;

/**
 * Created by socomo20 on 8/18/15.
 */

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Pair;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.client.Response;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        try {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            Prefs.with(this).save(Constants.SP_DEVICE_TOKEN, refreshedToken);

            Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
            if (!"".equalsIgnoreCase(pair.first)) {
                // call api
                refreshDeviceToken(this, refreshedToken, pair.first);
            } else {
                Intent intent = new Intent(Constants.INTENT_ACTION_DEVICE_TOKEN_UPDATE);
                intent.putExtra(Constants.KEY_DEVICE_TOKEN, refreshedToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void refreshDeviceToken(final Context activity, String refreshedToken, String accessToken) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                final HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
                params.put(Constants.KEY_DEVICE_TOKEN, refreshedToken);
                new HomeUtil().putDefaultParams(params);
                Response response = RestClient.getApiService().refreshDeviceToken(params);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}