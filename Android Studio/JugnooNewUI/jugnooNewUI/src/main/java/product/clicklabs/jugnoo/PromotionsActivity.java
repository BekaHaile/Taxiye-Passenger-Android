package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import rmn.androidscreenlibrary.ASSL;

public class PromotionsActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	EditText editTextPromoCode;
	Button buttonApplyPromo;

	TextView textViewCouponsAvailable, textViewCouponInfo;
	GridView listViewCoupons;
	CouponsListAdapter couponsListAdapter;

	TextView textViewOngoingOffers, textViewPromoInfo;
    NonScrollListView listViewPromotions;
    PromotionListAdapter promotionListAdapter;

    RelativeLayout relativeLayoutInvite;
    TextView textViewInvite;

	
	
	AsyncHttpClient fetchAccountInfoClient;
	
	ArrayList<CouponInfo> couponInfosList = new ArrayList<CouponInfo>();
    ArrayList<PromotionInfo> promotionInfoList = new ArrayList<PromotionInfo>();
    String headMessage = "", inviteMessage = "";
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
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
		setContentView(R.layout.activity_promotions);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(PromotionsActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode); editTextPromoCode.setTypeface(Fonts.latoRegular(this));
		buttonApplyPromo = (Button) findViewById(R.id.buttonApplyPromo); buttonApplyPromo.setTypeface(Fonts.latoRegular(this));
		textViewCouponsAvailable = (TextView) findViewById(R.id.textViewCouponsAvailable); textViewCouponsAvailable.setTypeface(Fonts.latoRegular(this));
		textViewCouponsAvailable.setVisibility(View.GONE);
        textViewCouponInfo = (TextView) findViewById(R.id.textViewCouponInfo); textViewCouponInfo.setTypeface(Fonts.latoRegular(this));
        textViewCouponInfo.setVisibility(View.GONE);
		
		couponInfosList.clear();
		
		listViewCoupons = (GridView) findViewById(R.id.listViewCoupons);
		couponsListAdapter = new CouponsListAdapter(PromotionsActivity.this);
		listViewCoupons.setAdapter(couponsListAdapter);


        textViewOngoingOffers = (TextView) findViewById(R.id.textViewOngoingOffers); textViewOngoingOffers.setTypeface(Fonts.latoRegular(this));
        textViewOngoingOffers.setVisibility(View.GONE);
        textViewPromoInfo = (TextView) findViewById(R.id.textViewPromoInfo); textViewPromoInfo.setTypeface(Fonts.latoRegular(this));
        textViewPromoInfo.setVisibility(View.GONE);

        listViewPromotions = (NonScrollListView) findViewById(R.id.listViewPromotions);
        promotionListAdapter = new PromotionListAdapter(this);
        listViewPromotions.setAdapter(promotionListAdapter);

        relativeLayoutInvite = (RelativeLayout) findViewById(R.id.relativeLayoutInvite);
        textViewInvite =(TextView)findViewById(R.id.textViewInvite); textViewInvite.setTypeface(Fonts.latoRegular(this));


		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        textViewCouponInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccountInfoAsync(PromotionsActivity.this);
			}
		});
		
		
		buttonApplyPromo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String promoCode = editTextPromoCode.getText().toString().trim();
				if(promoCode.length() > 0){
					applyPromoCodeAPI(PromotionsActivity.this, promoCode);
					FlurryEventLogger.promoCodeTried(Data.userData.accessToken, promoCode);
				}
				else{
					editTextPromoCode.requestFocus();
					editTextPromoCode.setError("Code can't be empty");
				}
			}
		});
		
		editTextPromoCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextPromoCode.setError(null);
            }
        });
		
		editTextPromoCode.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonApplyPromo.performClick();
                        return false;

                    case EditorInfo.IME_ACTION_NEXT:
                        return false;

                    default:
                        return false;
                }
            }
        });


        relativeLayoutInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionsActivity.this, ShareActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.shareScreenOpened(Data.userData.accessToken);
            }
        });

		
		getAccountInfoAsync(PromotionsActivity.this);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
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
		if(fetchAccountInfoClient != null){
			fetchAccountInfoClient.cancelAllRequests(true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
            textViewCouponInfo.setText(message);
            textViewCouponInfo.setVisibility(View.VISIBLE);
            textViewCouponsAvailable.setVisibility(View.GONE);

            textViewPromoInfo.setVisibility(View.GONE);
            textViewOngoingOffers.setVisibility(View.GONE);
			
			couponInfosList.clear();
			couponsListAdapter.notifyDataSetChanged();

            promotionInfoList.clear();
            promotionListAdapter.notifyDataSetChanged();
		}
		else{
			if(couponInfosList.size() == 0){
                textViewCouponInfo.setText(message);
                textViewCouponInfo.setVisibility(View.VISIBLE);
				textViewCouponsAvailable.setVisibility(View.GONE);
			}
			else{
                textViewCouponInfo.setVisibility(View.GONE);
				textViewCouponsAvailable.setVisibility(View.VISIBLE);
			}

            if(promotionInfoList.size() == 0){
                textViewPromoInfo.setText(headMessage);
                textViewPromoInfo.setVisibility(View.VISIBLE);
                textViewOngoingOffers.setVisibility(View.GONE);
            }
            else{
                textViewPromoInfo.setVisibility(View.GONE);
                textViewOngoingOffers.setVisibility(View.VISIBLE);
            }

            textViewInvite.setText(inviteMessage);

			couponsListAdapter.notifyDataSetChanged();
            promotionListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderCoupon {
		TextView textViewCouponTitle, textViewExpiryDate;
		LinearLayout relative;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCoupon();
				convertView = mInflater.inflate(R.layout.list_item_coupon, null);
				
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Fonts.latoRegular(context));
				holder.textViewExpiryDate = (TextView) convertView.findViewById(R.id.textViewExpiryDate); holder.textViewExpiryDate.setTypeface(Fonts.latoLight(context), Typeface.BOLD);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCoupon) convertView.getTag();
			}
			
			holder.id = position;
			
			CouponInfo couponInfo = couponInfosList.get(position);

			holder.textViewCouponTitle.setText(couponInfo.title);
			holder.textViewExpiryDate.setText("Expiring on "+DateOperations.getDate(DateOperations.utcToLocal(couponInfo.expiryDate)));
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCoupon) v.getTag();
					CouponInfo couponInfo = couponInfosList.get(holder.id);
					DialogPopup.alertPopupLeftOriented(PromotionsActivity.this, "", couponInfo.description);
					FlurryEventLogger.couponInfoOpened(Data.userData.accessToken, couponInfo.type);
				}
			});
			
			return convertView;
		}

	}



    class ViewHolderPromotion {
        TextView textViewPromotionTitle, textViewExpiryDate;
        LinearLayout relative;
        int id;
    }

    class PromotionListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ViewHolderPromotion holder;
        Context context;
        public PromotionListAdapter(Context context) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return promotionInfoList.size();
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
                holder = new ViewHolderPromotion();
                convertView = mInflater.inflate(R.layout.list_item_promotion, null);

                holder.textViewPromotionTitle = (TextView) convertView.findViewById(R.id.textViewPromotionTitle); holder.textViewPromotionTitle.setTypeface(Fonts.latoRegular(context));
                holder.textViewExpiryDate = (TextView) convertView.findViewById(R.id.textViewExpiryDate); holder.textViewExpiryDate.setTypeface(Fonts.latoLight(context), Typeface.BOLD);

                holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

                holder.relative.setTag(holder);

                holder.relative.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ASSL.DoMagic(holder.relative);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderPromotion) convertView.getTag();
            }

            holder.id = position;

            PromotionInfo promotionInfo = promotionInfoList.get(position);

            holder.textViewPromotionTitle.setText(promotionInfo.title);
            holder.textViewExpiryDate.setText("Expiring on "+DateOperations.getDate(DateOperations.utcToLocal(promotionInfo.expiryDate)));

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder = (ViewHolderPromotion) v.getTag();
                    PromotionInfo promotionInfo = promotionInfoList.get(holder.id);
                    DialogPopup.alertPopupHtml(PromotionsActivity.this, "", promotionInfo.terms);
                }
            });

            return convertView;
        }

    }

	
	/**
	 * ASync for get Account info from server
	 */
	public void getAccountInfoAsync(final Activity activity) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (fetchAccountInfoClient == null) {
                if (AppStatus.getInstance(activity).isOnline(activity)) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");
                    couponInfosList.clear();
                    couponsListAdapter.notifyDataSetChanged();
                    textViewCouponInfo.setVisibility(View.GONE);

                    RequestParams params = new RequestParams();
                    params.put("access_token", Data.userData.accessToken);
                    params.put("latitude", ""+Data.latitude);
                    params.put("longitude", ""+Data.longitude);


                    fetchAccountInfoClient = Data.getClient();
                    fetchAccountInfoClient.post(Config.getServerUrl() + "/get_coupons_and_promotions", params,
                        new CustomAsyncHttpResponseHandler() {
                            private JSONObject jObj;

                            @Override
                            public void onFailure(Throwable arg3) {
                                Log.e("request fail", arg3.toString());
                                DialogPopup.dismissLoadingDialog();
                                updateListData("Some error occurred. Tap to retry", true);
                            }

                            @Override
                            public void onSuccess(String response) {
                                Log.e("Server response", "response = " + response);
                                try {
                                    jObj = new JSONObject(response);

                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                        int flag = jObj.getInt("flag");
                                        if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {


//                                            {
//                                                "flag": 140,
//                                                "coupons": [
//                                                {
//                                                    "title": "Free ride",
//                                                    "subtitle": "upto Rs. 100",
//                                                    "description": "Your next ride with Jugnoo upto Rs. 100 will be FREE.Terms of Use:1. The coupon will be applied automatically at the end of your next ride.2. Only one coupon will be applied in one ride.3. The maximum value of this coupon is Rs. 100 and you will have to pay the remaining amount at the end of the ride.4. Jugnoo reserves the right to discontinue the coupon at its discretion.",
//                                                    "discount": 100,
//                                                    "maximum": 100,
//                                                    "image": "",
//                                                    "type": 0,
//                                                    "account_id": 546,
//                                                    "redeemed_on": "0000-00-00 00:00:00",
//                                                    "status": 1,
//                                                    "expiry_date": "2015-10-30 18:29:59"
//                                                }
//                                                ],
//                                                "promotions": [
//                                                {
//                                                    "promo_id": 12,
//                                                    "title": "CDCL drop 20% cashback",
//                                                    "max_allowed": 0,
//                                                    "terms_n_conds": "Terms and conditions for drop to CDCL"
//                                                }
//                                                ],
//                                                "head": "Ongoing offers in chandigarh",
//                                                "invite_message": "Invite your friends and get more coupons"
//                                            }

                                            couponInfosList.clear();
                                            couponInfosList.addAll(JSONParser.parseCouponsArray(jObj));

                                            promotionInfoList.clear();
                                            promotionInfoList.addAll(JSONParser.parsePromotionsArray(jObj));

                                            headMessage = jObj.getString("head");
                                            inviteMessage = jObj.getString("invite_message");

                                            updateListData("No Coupons available", false);

                                            if (Data.userData != null) {
                                                Data.userData.numCouponsAvaliable = couponInfosList.size();
                                            }

                                        } else {
                                            updateListData("Some error occurred. Tap to retry", true);
                                        }
                                    } else {
                                        updateListData("Some error occurred. Tap to retry", true);
                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    updateListData("Some error occurred. Tap to retry", true);
                                }
                                DialogPopup.dismissLoadingDialog();
                            }

                            @Override
                            public void onFinish() {
                                fetchAccountInfoClient = null;
                                super.onFinish();
                            }

                        });
                } else {
                    updateListData("No Internet connection. Tap to retry", true);
                }
            }
        }
	}

	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
        if(!HomeActivity.checkIfUserDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                params.put("access_token", Data.userData.accessToken);
                params.put("code", promoCode);

                AsyncHttpClient asyncHttpClient = Data.getClient();
                asyncHttpClient.post(Config.getServerUrl() + "/enter_code", params,
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
                                if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                                    HomeActivity.logoutUser(activity);
                                } else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                                    String errorMessage = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                                    String message = jObj.getString("message");
                                    DialogPopup.dialogBanner(activity, message);
                                    getAccountInfoAsync(activity);
                                    FlurryEventLogger.promoCodeApplied(Data.userData.accessToken, promoCode, message);
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
                            } catch (Exception exception) {
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
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}
	
}
