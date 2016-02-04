package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class PushPendingCallsReceiver extends BroadcastReceiver {

    private final String TAG = PushPendingCallsReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (PushPendingCallsService.PUSH_PENDING_CALLS.equals(action)) {
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<PendingAPICall> pendingAPICalls = Database2.getInstance(context).getAllPendingAPICalls();
                            for(PendingAPICall pendingAPICall : pendingAPICalls){
                                Log.e("pendingAPICall", "=" + pendingAPICall);
                                startAPI(context, pendingAPICall);
                            }

                            int pendingApisCount = Database2.getInstance(context).getAllPendingAPICallsCount();
                            if(pendingApisCount > 0){
                                // continue next time
                                int lastCount = Prefs.with(context).getInt(SPLabels.PENDING_CALLS_RETRY_COUNT, 0);
                                if(lastCount < 5){
                                    Prefs.with(context).save(SPLabels.PENDING_CALLS_RETRY_COUNT, lastCount+1);
                                }
                                else{
                                    PushPendingCallsService.cancelUploadPathAlarm(context);
                                    context.stopService(new Intent(context, PushPendingCallsService.class));
                                }
                            }
                            else{
                                PushPendingCallsService.cancelUploadPathAlarm(context);
                                context.stopService(new Intent(context, PushPendingCallsService.class));
                                if(HomeActivity.appInterruptHandler != null){
                                    HomeActivity.appInterruptHandler.refreshOnPendingCallsDone();
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void startAPI(Context context, PendingAPICall pendingAPICall) {
        if (AppStatus.getInstance(context).isOnline(context)) {
            try {
                if (AppStatus.getInstance(context).isOnline(context)) {
                    Response response = null;
                    if(PendingCall.EMERGENCY_ALERT.getPath().equalsIgnoreCase(pendingAPICall.url)){
                        response = RestClient.getApiServices().emergencyAlertSync(pendingAPICall.nameValuePairs);
                    }
                    else if(PendingCall.SKIP_RATING_BY_CUSTOMER.getPath().equalsIgnoreCase(pendingAPICall.url)){
                        response = RestClient.getApiServices().skipRatingByCustomerSync(pendingAPICall.nameValuePairs);
                    }
                    Log.e(TAG, "response="+response);
                    if(response != null){
                        Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
                        Log.e(TAG, "response to string=" + new String(((TypedByteArray)response.getBody()).getBytes()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "e="+e);
            }
        }
    }

}