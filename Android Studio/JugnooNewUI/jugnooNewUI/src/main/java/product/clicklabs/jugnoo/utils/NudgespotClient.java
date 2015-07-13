package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.nudgespot.api.GcmClient;
import com.nudgespot.api.auth.NudgespotCredentials;
import com.nudgespot.resource.NudgespotActivity;
import com.nudgespot.resource.NudgespotSubscriber;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;

/**
 * Created by socomo20 on 7/13/15.
 */
public class NudgespotClient {

    private static NudgespotClient instance;

    public static NudgespotClient getInstance(Context context){
        if(instance == null){
            instance = new NudgespotClient(context);
        }
        return instance;
    }


    private NudgespotClient(Context context){
        getGcmClientInstance(context);
    }


    private static GcmClient mGcmClient;

    public static GcmClient getGcmClientInstance(Context context){
        if(mGcmClient == null){
            mGcmClient = GcmClient.getClient(new NudgespotCredentials(Config.getNudgespotApiKey(), Config.getNudgespotRestApiKey()), context);
        }
        return mGcmClient;
    }


    public void register(String userEmail){
        if(mGcmClient != null) {
            mGcmClient.initialize(userEmail);
        }
    }

    public void registerWithProperties(String userEmail, JSONObject props){
        if(mGcmClient != null) {
            NudgespotSubscriber subscriber = new NudgespotSubscriber(userEmail);
            try {
                props.put("app", "android");
            } catch (Exception e) {
                e.printStackTrace();
            }
            subscriber.setProperties(props);
            mGcmClient.initialize(subscriber);
        }
    }

    public void clearRegisteration(){
        if(mGcmClient != null) {
            mGcmClient.clearRegistration();
        }
    }



    public void track(String event) {
        if(mGcmClient != null) {
            NudgespotActivity activity = new NudgespotActivity(event);
            JSONObject properties = new JSONObject();
            try {
                properties.put("app", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            activity.setProperties(properties);
            mGcmClient.track(activity);
        }
    }

}
