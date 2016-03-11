package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PromotionsActivity extends BaseActivity implements FlurryEventNames {

    private final String TAG = PromotionsActivity.class.getSimpleName();
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	EditText editTextPromoCode;
	Button buttonApplyPromo;

    RelativeLayout relativeLayoutListTitle;
	TextView textViewCouponsAvailable;
	GridView listViewCoupons;
	CouponsListAdapter couponsListAdapter;
    LinearLayout linearLayoutNoCoupons;

	TextView textViewOngoingOffers, textViewPromoInfo;
    NonScrollListView listViewPromotions;
    PromotionListAdapter promotionListAdapter;

    RelativeLayout relativeLayoutInvite;
    TextView textViewInvite;

	

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
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(PromotionsActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));

		editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode); editTextPromoCode.setTypeface(Fonts.latoRegular(this));
        editTextPromoCode.setImeOptions(EditorInfo.IME_ACTION_DONE);
		buttonApplyPromo = (Button) findViewById(R.id.buttonApplyPromo); buttonApplyPromo.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutListTitle = (RelativeLayout) findViewById(R.id.relativeLayoutListTitle);
		textViewCouponsAvailable = (TextView) findViewById(R.id.textViewCouponsAvailable); textViewCouponsAvailable.setTypeface(Fonts.mavenLight(this));
        relativeLayoutListTitle.setVisibility(View.GONE);

		couponInfosList.clear();
		
		listViewCoupons = (GridView) findViewById(R.id.listViewCoupons);
		couponsListAdapter = new CouponsListAdapter(PromotionsActivity.this);
		listViewCoupons.setAdapter(couponsListAdapter);

        linearLayoutNoCoupons = (LinearLayout) findViewById(R.id.linearLayoutNoCoupons);
        ((TextView)findViewById(R.id.textViewNoCoupons)).setTypeface(Fonts.mavenLight(this));
        linearLayoutNoCoupons.setVisibility(View.GONE);


        textViewOngoingOffers = (TextView) findViewById(R.id.textViewOngoingOffers); textViewOngoingOffers.setTypeface(Fonts.latoRegular(this));
        textViewOngoingOffers.setVisibility(View.GONE);
        textViewPromoInfo = (TextView) findViewById(R.id.textViewPromoInfo); textViewPromoInfo.setTypeface(Fonts.latoRegular(this));
        textViewPromoInfo.setVisibility(View.GONE);

        listViewPromotions = (NonScrollListView) findViewById(R.id.listViewPromotions);
        promotionListAdapter = new PromotionListAdapter(this);
        listViewPromotions.setAdapter(promotionListAdapter);

        relativeLayoutInvite = (RelativeLayout) findViewById(R.id.relativeLayoutInvite);
        textViewInvite =(TextView)findViewById(R.id.textViewInvite); textViewInvite.setTypeface(Fonts.mavenLight(this));


		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		
		buttonApplyPromo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String promoCode = editTextPromoCode.getText().toString().trim();
				if(promoCode.length() > 0){
					applyPromoCodeAPI(PromotionsActivity.this, promoCode);
                    FlurryEventLogger.event(PROMO_CODE_ENTERED);
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
                FlurryEventLogger.event(INVITE_EARN_PROMOTIONS);
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
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
            linearLayoutNoCoupons.setVisibility(View.VISIBLE);
            relativeLayoutListTitle.setVisibility(View.GONE);
            listViewCoupons.setVisibility(View.GONE);

            textViewOngoingOffers.setVisibility(View.GONE);
            listViewPromotions.setVisibility(View.GONE);
            textViewPromoInfo.setVisibility(View.GONE);
			
			couponInfosList.clear();
			couponsListAdapter.notifyDataSetChanged();

            promotionInfoList.clear();
            promotionListAdapter.notifyDataSetChanged();
		}
		else{
			if(couponInfosList.size() == 0 && promotionInfoList.size() == 0){
                relativeLayoutListTitle.setVisibility(View.GONE);
				textViewCouponsAvailable.setGravity(Gravity.CENTER);
                textViewCouponsAvailable.setText(message);
                listViewCoupons.setVisibility(View.GONE);
                linearLayoutNoCoupons.setVisibility(View.VISIBLE);

                textViewOngoingOffers.setVisibility(View.GONE);
                listViewPromotions.setVisibility(View.GONE);
                textViewPromoInfo.setVisibility(View.GONE);
			}
            else if(couponInfosList.size() > 0 && promotionInfoList.size() == 0){
                relativeLayoutListTitle.setVisibility(View.VISIBLE);
				textViewCouponsAvailable.setGravity(Gravity.CENTER_VERTICAL);
                textViewCouponsAvailable.setText("COUPONS AVAILABLE");
                listViewCoupons.setVisibility(View.VISIBLE);
                linearLayoutNoCoupons.setVisibility(View.GONE);

                textViewOngoingOffers.setVisibility(View.GONE);
                listViewPromotions.setVisibility(View.GONE);
                textViewPromoInfo.setVisibility(View.GONE);
            }
            else if(couponInfosList.size() == 0 && promotionInfoList.size() > 0){
                relativeLayoutListTitle.setVisibility(View.GONE);
                listViewCoupons.setVisibility(View.GONE);
                linearLayoutNoCoupons.setVisibility(View.VISIBLE);

                textViewOngoingOffers.setVisibility(View.GONE);
                listViewPromotions.setVisibility(View.VISIBLE);
                textViewPromoInfo.setVisibility(View.GONE);
            }
			else{
                relativeLayoutListTitle.setVisibility(View.VISIBLE);
				textViewCouponsAvailable.setGravity(Gravity.CENTER_VERTICAL);
                textViewCouponsAvailable.setText("COUPONS AVAILABLE");
                listViewCoupons.setVisibility(View.VISIBLE);
                linearLayoutNoCoupons.setVisibility(View.GONE);

                textViewOngoingOffers.setVisibility(View.GONE);
                listViewPromotions.setVisibility(View.VISIBLE);
                textViewPromoInfo.setVisibility(View.GONE);
			}

            if(!"".equalsIgnoreCase(inviteMessage)) {
                textViewInvite.setText(inviteMessage);
            }

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
				
				holder.textViewCouponTitle = (TextView) convertView.findViewById(R.id.textViewCouponTitle); holder.textViewCouponTitle.setTypeface(Fonts.mavenLight(context));
				holder.textViewExpiryDate = (TextView) convertView.findViewById(R.id.textViewExpiryDate); holder.textViewExpiryDate.setTypeface(Fonts.mavenLight(context));
                ((TextView) convertView.findViewById(R.id.textViewTNC)).setTypeface(Fonts.mavenLight(context));

				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCoupon) convertView.getTag();
			}
			
			holder.id = position;
			
			CouponInfo couponInfo = couponInfosList.get(position);

			holder.textViewCouponTitle.setText(couponInfo.title);
			holder.textViewExpiryDate.setText("Valid until "+DateOperations.getDate(DateOperations.utcToLocal(couponInfo.expiryDate)));
//            holder.textViewValidTime.setText(DateOperations.getUTCTimeInLocalTime(couponInfo.startTime)+" - "+DateOperations.getUTCTimeInLocalTime(couponInfo.endTime));

			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCoupon) v.getTag();
					CouponInfo couponInfo = couponInfosList.get(holder.id);
					DialogPopup.alertPopupLeftOriented(PromotionsActivity.this, "", couponInfo.description);
					FlurryEventLogger.couponInfoOpened(Data.userData.accessToken, couponInfo.couponType);
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
            holder.textViewExpiryDate.setText(promotionInfo.expiryDate);

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder = (ViewHolderPromotion) v.getTag();
                    PromotionInfo promotionInfo = promotionInfoList.get(holder.id);
                    DialogPopup.alertPopupHtml(PromotionsActivity.this, "", promotionInfo.terms);
                    FlurryEventLogger.event(ONGOING_OFFERS_CHECKED);
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
                if (AppStatus.getInstance(activity).isOnline(activity)) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");
                    couponInfosList.clear();
                    couponsListAdapter.notifyDataSetChanged();
                    linearLayoutNoCoupons.setVisibility(View.GONE);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("access_token", Data.userData.accessToken);
                    params.put("latitude", "" + Data.latitude);
                    params.put("longitude", "" + Data.longitude);

                    RestClient.getApiServices().getCouponsAndPromotions(params, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            Log.i(TAG, "getCouponsAndPromotions response = " + responseStr);
                            try {
                                JSONObject jObj = new JSONObject(responseStr);

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {
                                        couponInfosList.clear();
                                        couponInfosList.addAll(JSONParser.parseCouponsArray(jObj));

                                        promotionInfoList.clear();
                                        promotionInfoList.addAll(JSONParser.parsePromotionsArray(jObj));

                                        headMessage = jObj.getString("head");
                                        if (jObj.has("invite_message")) {
                                            inviteMessage = jObj.getString("invite_message");
                                        }

                                        promotionInfoList.clear();
                                        updateListData(headMessage, false);

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
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "getCouponsAndPromotions error="+error.toString());
                            DialogPopup.dismissLoadingDialog();
                            updateListData("Some error occurred. Tap to retry", true);
                        }
                    });
                } else {
                    //updateListData("No Internet connection. Tap to retry", true);
                    DialogPopup.dialogNoInternet(PromotionsActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG
                            , new Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View v) {
                            getAccountInfoAsync(PromotionsActivity.this);
                        }

                        @Override
                        public void neutralClick(View v) {
                        }

                        @Override
                        public void negativeClick(View v) {
                        }
                    });
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

                HashMap<String, String> params = new HashMap<>();

                params.put("access_token", Data.userData.accessToken);
                params.put("code", promoCode);

                RestClient.getApiServices().enterCode(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "enterCode response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
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
                                FlurryEventLogger.event(PROMO_CODE_APPLIED);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                            double jugnooBalance = jObj.optDouble("jugnoo_balance", Data.userData.getJugnooBalance());
                            Data.userData.setJugnooBalance(jugnooBalance);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "enterCode error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            } else {
                DialogPopup.dialogNoInternet(PromotionsActivity.this,
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                        new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                applyPromoCodeAPI(activity, promoCode);
                            }

                            @Override
                            public void neutralClick(View v) {

                            }

                            @Override
                            public void negativeClick(View v) {

                            }
                        });
            }
        }
	}
	
}
