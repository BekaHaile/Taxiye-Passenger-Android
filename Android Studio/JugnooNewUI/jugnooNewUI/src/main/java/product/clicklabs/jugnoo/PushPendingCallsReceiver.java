package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

public class PushPendingCallsReceiver extends BroadcastReceiver {

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
            HttpRequester.setTimeouts(10000);
            HttpRequester simpleJSONParser = new HttpRequester();
            String result = simpleJSONParser.getJSONFromUrlParams(pendingAPICall.url, pendingAPICall.nameValuePairs);
            HttpRequester.setTimeouts(30000);
            Log.e("result in pendingAPICall ", "=" + pendingAPICall + " and result = " + result);
            if(result.contains(HttpRequester.SERVER_TIMEOUT)){
            }
            else{
                Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
            }
        }
    }

}