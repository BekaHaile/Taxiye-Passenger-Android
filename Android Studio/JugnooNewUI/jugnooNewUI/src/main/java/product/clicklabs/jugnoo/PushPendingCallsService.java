package product.clicklabs.jugnoo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PushPendingCallsService extends IntentService {
	private final String TAG = PushPendingCallsService.class.getSimpleName();

	public PushPendingCallsService() {
		this("RazorpayCallbackService");
	}

	public PushPendingCallsService(String name){
		super(name);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		onReceive(this);
	}
	public void onReceive(final Context context) {
		try {
			ArrayList<PendingAPICall> pendingAPICalls = MyApplication.getInstance().getDatabase2().getAllPendingAPICalls();
			for(PendingAPICall pendingAPICall : pendingAPICalls){
				Log.e(TAG, "pendingAPICall=" + pendingAPICall);
				startAPI(pendingAPICall);
			}

			int pendingApisCount = MyApplication.getInstance().getDatabase2().getAllPendingAPICallsCount();
			if(pendingApisCount > 0){
				// continue next time
				int lastCount = Prefs.with(context).getInt(SPLabels.PENDING_CALLS_RETRY_COUNT, 0);
				if(lastCount < 5){
					Prefs.with(context).save(SPLabels.PENDING_CALLS_RETRY_COUNT, lastCount+1);
				}
			}
			else{
				if(HomeActivity.appInterruptHandler != null){
					HomeActivity.appInterruptHandler.refreshOnPendingCallsDone();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void startAPI(PendingAPICall pendingAPICall) {
		if (MyApplication.getInstance().isOnline()) {
			try {
				if (MyApplication.getInstance().isOnline()) {
					Response response = null;
					if(PendingCall.EMERGENCY_ALERT.getPath().equalsIgnoreCase(pendingAPICall.url)){
						response = RestClient.getApiService().emergencyAlertSync(pendingAPICall.nameValuePairs);
					}
					else if(PendingCall.SKIP_RATING_BY_CUSTOMER.getPath().equalsIgnoreCase(pendingAPICall.url)){
						response = RestClient.getApiService().skipRatingByCustomerSync(pendingAPICall.nameValuePairs);
					}
					Log.e(TAG, "response="+response);
					if(response != null){
						MyApplication.getInstance().getDatabase2().deletePendingAPICall(pendingAPICall.id);
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
