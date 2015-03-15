package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.RideCancellationMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class RideCancellationActivity extends Activity{
	
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewWantToCancel;
	
	ListView listViewCancelOptions;
	CancelOptionsListAdapter cancelOptionsListAdapter;
	
	Button buttonCancelRide;

	TextView textViewInfo;
	ProgressBar progressBar;
	
	TextView textViewCancelInfo;
	
	
	ArrayList<CancelOption> cancelOptions = new ArrayList<CancelOption>();
	
	public static RideCancellationMode rideCancellationMode = RideCancellationMode.CURRENT_RIDE;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_ride);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(RideCancellationActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);

		textViewWantToCancel = (TextView) findViewById(R.id.textViewWantToCancel); textViewWantToCancel.setTypeface(Data.latoLight(this), Typeface.BOLD);
		
		listViewCancelOptions = (ListView) findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(RideCancellationActivity.this);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);
		
		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide); buttonCancelRide.setTypeface(Data.latoRegular(this));
		
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.latoRegular(this));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		textViewCancelInfo = (TextView) findViewById(R.id.textViewCancelInfo); textViewCancelInfo.setTypeface(Data.latoLight(this), Typeface.BOLD);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getCancelOptionsAPI(RideCancellationActivity.this);
			}
		});
		
		
		buttonCancelRide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		getCancelOptionsAPI(RideCancellationActivity.this);
		
	}
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			cancelOptions.clear();
			cancelOptionsListAdapter.notifyDataSetChanged();
		}
		else{
			if(cancelOptions.size() == 0){
				textViewInfo.setText(message);
				textViewInfo.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
			}
			cancelOptionsListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderCancelOption {
		TextView textViewCancelOption;
		ImageView imageViewCancelOptionCheck;
		LinearLayout relative;
		int id;
	}

	class CancelOptionsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderCancelOption holder;
		Context context;
		
		public CancelOptionsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return cancelOptions.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCancelOption();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption); holder.textViewCancelOption.setTypeface(Data.latoRegular(context));
				holder.imageViewCancelOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewCancelOptionCheck);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCancelOption) convertView.getTag();
			}
			
			holder.id = position;
			
			CancelOption cancelOption = cancelOptions.get(position);

			holder.textViewCancelOption.setText(cancelOption.name);
			
			if(cancelOptions.get(position).checked){
				holder.relative.setBackgroundColor(Color.WHITE);
				holder.imageViewCancelOptionCheck.setVisibility(View.VISIBLE);
			}
			else{
				holder.relative.setBackgroundColor(Color.TRANSPARENT);
				holder.imageViewCancelOptionCheck.setVisibility(View.GONE);
			}
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCancelOption) v.getTag();
					if(cancelOptions.get(holder.id).checked){
						cancelOptions.get(holder.id).checked = false;
					}
					else{
						cancelOptions.get(holder.id).checked = true;
					}
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}

	}
	
	
	/**
	 * ASync for get Account info from server
	 */
	public void getCancelOptionsAPI(final Activity activity) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBar.setVisibility(View.VISIBLE);
				cancelOptions.clear();
				cancelOptionsListAdapter.notifyDataSetChanged();
				textViewInfo.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/get_cancel_options", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBar.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
								
								cancelOptions.add(new CancelOption(1, "Driver is late"));
								cancelOptions.add(new CancelOption(2, "Driver denied duty"));
								cancelOptions.add(new CancelOption(3, "Changed my mind"));
								cancelOptions.add(new CancelOption(4, "Booked another cab"));
								updateListData("No options available", false);
								
							}
	
							@Override
							public void onSuccess(String response) {
								Log.e("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
										
										cancelOptions.clear();
										
										if(jObj.has("cancel_options")){
											JSONArray cancelOptionsData = jObj.getJSONArray("cancel_options");
											if(cancelOptionsData.length() > 0){
												for(int i=0; i<cancelOptionsData.length(); i++){
													JSONObject coData = cancelOptionsData.getJSONObject(i);
													cancelOptions.add(new CancelOption(coData.getInt("id"), coData.getString("name")));
												}
											}
										}
										updateListData("No options available", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}

	}

	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("code", promoCode);
			
				AsyncHttpClient asyncHttpClient = Data.getClient();
				asyncHttpClient.post(Data.SERVER_URL + "/enter_code", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
							
	
							@Override
							public void onSuccess(String response) {
								Log.i("Server response", "response = " + response);
								try {
									jObj = new JSONObject(response);
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										String errorMessage = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", errorMessage);	
									}
									else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.dialogBanner(activity, message);
										getCancelOptionsAPI(activity);
										FlurryEventLogger.promoCodeApplied(Data.userData.accessToken, promoCode, message);
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									
								}
								DialogPopup.dismissLoadingDialog();
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
							}
							
						});
			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}
	
}
