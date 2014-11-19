package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AccountActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewAccountInfo;
	ProgressBar progressBarAccount;
	
	ListView listViewCoupons;
	CouponsListAdapter couponsListAdapter;
	
	RelativeLayout relativeLayoutReferral;
	TextView textViewWantMoreRides;
	Button buttonReferUs;
	
	
	
	AsyncHttpClient fetchAccountInfoClient;
	
	ArrayList<CouponInfo> couponInfosList = new ArrayList<CouponInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(AccountActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		textViewAccountInfo = (TextView) findViewById(R.id.textViewAccountInfo); textViewAccountInfo.setTypeface(Data.regularFont(getApplicationContext()));
		progressBarAccount = (ProgressBar) findViewById(R.id.progressBarAccount);
		
		couponInfosList.clear();
		
		listViewCoupons = (ListView) findViewById(R.id.listViewCoupons);
		couponsListAdapter = new CouponsListAdapter(AccountActivity.this);
		listViewCoupons.setAdapter(couponsListAdapter);
		
		relativeLayoutReferral = (RelativeLayout) findViewById(R.id.relativeLayoutReferral);
		textViewWantMoreRides = (TextView) findViewById(R.id.textViewWantMoreRides); textViewWantMoreRides.setTypeface(Data.regularFont(getApplicationContext()));
		buttonReferUs = (Button) findViewById(R.id.buttonReferUs); buttonReferUs.setTypeface(Data.regularFont(getApplicationContext()));

		textViewAccountInfo.setVisibility(View.GONE);
		progressBarAccount.setVisibility(View.GONE);
		relativeLayoutReferral.setVisibility(View.GONE);
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		textViewAccountInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccountInfoAsync(AccountActivity.this);
			}
		});
		
		
		buttonReferUs.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AccountActivity.this, ShareActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		getAccountInfoAsync(AccountActivity.this);
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	
	@Override
	public void onDestroy() {
		if(fetchAccountInfoClient != null){
			fetchAccountInfoClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewAccountInfo.setText(message);
			textViewAccountInfo.setVisibility(View.VISIBLE);
			
			couponInfosList.clear();
			couponsListAdapter.notifyDataSetChanged();
			relativeLayoutReferral.setVisibility(View.GONE);
		}
		else{
			if(couponInfosList.size() == 0){
				textViewAccountInfo.setText(message);
				textViewAccountInfo.setVisibility(View.VISIBLE);
				relativeLayoutReferral.setVisibility(View.GONE);
			}
			else{
				textViewAccountInfo.setVisibility(View.GONE);
				relativeLayoutReferral.setVisibility(View.VISIBLE);
			}
			couponsListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderCoupon {
		TextView textViewCouponYouHave, textViewCouponTitle, textViewCouponSubTitle;
		RelativeLayout relative;
		int id;
	}

	class CouponsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderCoupon holder;
		Context context;
		public CouponsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return couponInfosList.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCoupon();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewCouponYouHave = (TextView) convertView.findViewById(R.id.textViewCouponYouHave); holder.textViewCouponYouHave.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Data.museoSlab(context), Typeface.BOLD);
				holder.textViewCouponSubTitle = (TextView) convertView.findViewById(R.id.textViewCouponSubTitle); holder.textViewCouponSubTitle.setTypeface(Data.museoSlab(context));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCoupon) convertView.getTag();
			}
			
			holder.id = position;
			
			CouponInfo couponInfo = couponInfosList.get(position);
			
			if(couponInfo.enabled){
				if(couponInfo.count > 1){
					holder.textViewCouponTitle.setText(couponInfo.count + " " + couponInfo.title + "s");
				}
				else{
					holder.textViewCouponTitle.setText(couponInfo.count + " " + couponInfo.title);
				}
				holder.textViewCouponSubTitle.setText(couponInfo.subtitle);
				holder.textViewCouponSubTitle.setVisibility(View.VISIBLE);
				holder.textViewCouponYouHave.setAlpha(1.0f);
				holder.textViewCouponTitle.setAlpha(1.0f);
			}
			else{
				holder.textViewCouponTitle.setText("0 Free rides");
				holder.textViewCouponSubTitle.setVisibility(View.GONE);
				holder.textViewCouponYouHave.setAlpha(0.5f);
				holder.textViewCouponTitle.setAlpha(0.5f);
				
			}
			
			
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCoupon) v.getTag();
					CouponInfo couponInfo = couponInfosList.get(holder.id);
					if(couponInfo.enabled){
						alertPopup(AccountActivity.this, "", couponInfo.description);
					}
				}
			});
			
			return convertView;
		}

	}
	
	Dialog dialog;
	
	void alertPopup(Activity activity, String title, String message) {
		try {
			try{
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}catch(Exception e){
				
			}
			if("".equalsIgnoreCase(title)){
				title = activity.getResources().getString(R.string.alert);
			}
			
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.coupon_description_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.regularFont(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.regularFont(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			textHead.setVisibility(View.GONE);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.regularFont(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ASync for get Account info from server
	 */
	public void getAccountInfoAsync(final Activity activity) {
		if(fetchAccountInfoClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarAccount.setVisibility(View.VISIBLE);
				textViewAccountInfo.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				fetchAccountInfoClient = Data.getClient();
				fetchAccountInfoClient.post(Data.SERVER_URL + "/get_coupons", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBarAccount.setVisibility(View.GONE);
								updateListData("Some error occurred. Tap to retry", true);
							}
	
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
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
										
//										{
//										    "coupons": [
//										        {
//										            "title": "Free ride",
//										            "description": "upte 100/-",
//										            "discount": 100,
//										            "maximum": 100,
//										            "image": null,
//										            "type": 0,
//										            "redeemed_on": null,
//										            "status": 1,
//										            "expiry_date": "2014-11-07T18:29:59.000Z"
//										        }
//										    ]
//										}
										
										couponInfosList.clear();
										
										if(jObj.has("coupons")){
											JSONArray couponsData = jObj.getJSONArray("coupons");
											if(couponsData.length() > 0){
												for(int i=0; i<couponsData.length(); i++){
													JSONObject coData = couponsData.getJSONObject(i);
													
													CouponInfo couponInfo = new CouponInfo(coData.getInt("type"), 
															coData.getInt("status"), 
															coData.getString("title"), 
															coData.getString("subtitle"), 
															coData.getString("description"), 
															coData.getString("image"), 
															coData.getString("redeemed_on"), 
															coData.getString("expiry_date"), 
															coData.getDouble("discount"), 
															coData.getDouble("maximum"));
													
													if(CouponStatus.ACTIVE.getOrdinal() == couponInfo.status){
														if(couponInfosList.contains(couponInfo)){
															couponInfosList.get(couponInfosList.indexOf(couponInfo)).count++;
														}
														else{
															couponInfosList.add(couponInfo);
														}
													}
												}
											}
										}
										else{
											CouponInfo couponInfo = new CouponInfo(1, 
													1, 
													"", 
													"", 
													"", 
													"", 
													"", 
													"", 
													0, 
													0);
											couponInfo.enabled = false;
											couponInfosList.add(couponInfo);
										}
										
										
										updateListData("Account info fetched", false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									updateListData("Some error occurred. Tap to retry", true);
								}
								progressBarAccount.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								fetchAccountInfoClient = null;
								super.onFinish();
							}
							
						});
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}

	}

}
