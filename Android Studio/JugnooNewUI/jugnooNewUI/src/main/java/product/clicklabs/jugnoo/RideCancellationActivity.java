package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ActivityCloser;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideCancellationActivity extends BaseActivity implements ActivityCloser, GAAction, GACategory {

	private final String TAG = RideCancellationActivity.class.getSimpleName();
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewWantToCancel;
	
	NonScrollListView listViewCancelOptions;
	CancelOptionsListAdapter cancelOptionsListAdapter;

    RelativeLayout relativeLayoutOtherCancelOptionInner;
    TextView textViewOtherCancelOption;
    ImageView imageViewOtherCancelOptionCheck;
    EditText editTextOtherCancelOption;
	RelativeLayout relativeLayoutOtherError;
    boolean otherChecked = false;

	
	Button buttonCancelRide;
	
	TextView textViewCancelInfo;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;
	
	public static ActivityCloser activityCloser = null;
	
	
	@Override
	protected void onStart() {
		super.onStart();
//		FlurryAgent.init(this, Config.getFlurryKey());
//		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
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
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.avenirNext(this));
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		textViewWantToCancel = (TextView) findViewById(R.id.textViewWantToCancel); textViewWantToCancel.setTypeface(Fonts.mavenRegular(this));
		
		listViewCancelOptions = (NonScrollListView) findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(RideCancellationActivity.this);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);

        relativeLayoutOtherCancelOptionInner = (RelativeLayout) findViewById(R.id.relativeLayoutOtherCancelOptionInner);
        textViewOtherCancelOption = (TextView) findViewById(R.id.textViewOtherCancelOption); textViewOtherCancelOption.setTypeface(Fonts.mavenRegular(this));
        imageViewOtherCancelOptionCheck = (ImageView) findViewById(R.id.imageViewOtherCancelOptionCheck);
        editTextOtherCancelOption = (EditText) findViewById(R.id.editTextOtherCancelOption); editTextOtherCancelOption.setTypeface(Fonts.mavenRegular(this));
        editTextOtherCancelOption.setMinHeight((int) (ASSL.Yscale() * 160));
		relativeLayoutOtherError = (RelativeLayout) findViewById(R.id.relativeLayoutOtherError);
		((TextView)findViewById(R.id.textViewOtherError)).setTypeface(Fonts.mavenMedium(this));
		relativeLayoutOtherError.setVisibility(View.GONE);



		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide); buttonCancelRide.setTypeface(Fonts.mavenRegular(this));
		
		textViewCancelInfo = (TextView) findViewById(R.id.textViewCancelInfo); textViewCancelInfo.setTypeface(Fonts.mavenRegular(this));

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);

        editTextOtherCancelOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, editTextOtherCancelOption.getTop() - ((int) (ASSL.Yscale() * 15)));
                    }
                }, 200);
            }
        });

        editTextOtherCancelOption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, editTextOtherCancelOption.getTop() - ((int) (ASSL.Yscale() * 15)));
					}
				}, 200);
			}
		});

		editTextOtherCancelOption.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					if (relativeLayoutOtherError.getVisibility() == View.VISIBLE) {
						relativeLayoutOtherError.setVisibility(View.GONE);
					}
				}
			}
		});



		
		imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
		
		
		buttonCancelRide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                if(Data.autoData.getCancelOptionsList() != null) {
                    String cancelReasonsStr = "";

                    if("".equalsIgnoreCase(Data.autoData.getCancelOptionsList().additionalReason)){
                        otherChecked = false;
                    }

                    if (otherChecked) {
                        cancelReasonsStr = editTextOtherCancelOption.getText().toString().trim();
                        if ("".equalsIgnoreCase(cancelReasonsStr)) {
							relativeLayoutOtherError.setVisibility(View.VISIBLE);
                        } else {
                            cancelRideAPI(RideCancellationActivity.this, Data.autoData.getCancelOptionsList().additionalReason, cancelReasonsStr);
                        }
                    } else {
                        for (int i = 0; i < Data.autoData.getCancelOptionsList().cancelOptions.size(); i++) {
                            if (Data.autoData.getCancelOptionsList().cancelOptions.get(i).checked) {
                                cancelReasonsStr = Data.autoData.getCancelOptionsList().cancelOptions.get(i).name;
                                break;
                            }
                        }

                        if ("".equalsIgnoreCase(cancelReasonsStr)) {
                            DialogPopup.alertPopup(RideCancellationActivity.this, "", getString(R.string.please_select_one_reason));
                        } else {
                            cancelRideAPI(RideCancellationActivity.this, cancelReasonsStr, "");
                        }
                    }
                }
				
			}
		});

        relativeLayoutOtherCancelOptionInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Data.autoData.getCancelOptionsList() != null && !"".equalsIgnoreCase(Data.autoData.getCancelOptionsList().additionalReason)) {
                    otherChecked = true;
                    updateCheckBoxes();
                }
            }
        });




		
		
		RideCancellationActivity.activityCloser = this;

        setCancellationOptions();


	}


    private void setCancellationOptions() {
        try {
            if(Data.autoData.getCancelOptionsList() != null){
                for(int i=0; i<Data.autoData.getCancelOptionsList().cancelOptions.size(); i++){
                    Data.autoData.getCancelOptionsList().cancelOptions.get(i).checked = false;
                }

                textViewCancelInfo.setText(Data.autoData.getCancelOptionsList().message);

                if("".equalsIgnoreCase(Data.autoData.getCancelOptionsList().additionalReason)){
                    relativeLayoutOtherCancelOptionInner.setVisibility(View.GONE);
                    editTextOtherCancelOption.setVisibility(View.GONE);
                }
                else{
                    relativeLayoutOtherCancelOptionInner.setVisibility(View.VISIBLE);
                    editTextOtherCancelOption.setVisibility(View.VISIBLE);
                    textViewOtherCancelOption.setText(Data.autoData.getCancelOptionsList().additionalReason);
                }

                otherChecked = false;
                updateCheckBoxes();
            }
            else{
                performBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            performBackPressed();
        }
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
		RideCancellationActivity.activityCloser = null;
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
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
			try {
				return Data.autoData.getCancelOptionsList().cancelOptions.size();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
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
				convertView = mInflater.inflate(R.layout.list_item_cancel_option, null);
				
				holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption); holder.textViewCancelOption.setTypeface(Fonts.mavenRegular(context));
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
			
			CancelOption cancelOption = Data.autoData.getCancelOptionsList().cancelOptions.get(position);

			holder.textViewCancelOption.setText(cancelOption.name);
			
			if(cancelOption.checked){
				holder.relative.setBackgroundColor(Color.WHITE);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_checked);
			}
			else{
				holder.relative.setBackgroundColor(Color.TRANSPARENT);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked);
			}
			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderCancelOption) v.getTag();
					for(int i=0; i<Data.autoData.getCancelOptionsList().cancelOptions.size(); i++){
						if(holder.id == i){
							Data.autoData.getCancelOptionsList().cancelOptions.get(i).checked = true;
						}
						else{
							Data.autoData.getCancelOptionsList().cancelOptions.get(i).checked = false;
						}
					}
                    otherChecked = false;

					if(relativeLayoutOtherError.getVisibility() == View.VISIBLE){
						relativeLayoutOtherError.setVisibility(View.GONE);
					}

                    updateCheckBoxes();
				}
			});
			
			return convertView;
		}
	}

    private void updateCheckBoxes(){
        if(otherChecked){
            for(int i=0; i<Data.autoData.getCancelOptionsList().cancelOptions.size(); i++){
                Data.autoData.getCancelOptionsList().cancelOptions.get(i).checked = false;
            }
            relativeLayoutOtherCancelOptionInner.setBackgroundColor(Color.WHITE);
            imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_checked);
            if(View.VISIBLE != editTextOtherCancelOption.getVisibility()) {
                editTextOtherCancelOption.setVisibility(View.VISIBLE);
            }
        }
        else{
            relativeLayoutOtherCancelOptionInner.setBackgroundColor(Color.TRANSPARENT);
            imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked);
            if(View.GONE != editTextOtherCancelOption.getVisibility()) {
                editTextOtherCancelOption.setVisibility(View.GONE);
            }
        }
        cancelOptionsListAdapter.notifyDataSetChanged();
    }

	

	public void cancelRideAPI(final Activity activity, final String reasons, final String addtionalReason) {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
				
				HashMap<String, String> params = new HashMap<>();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("reasons", reasons);
				params.put("addn_reason", addtionalReason);
				if(Data.userData.getSubscriptionData().getUserSubscriptions() != null && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0) {
					params.put(Constants.KEY_AUTOS_BENEFIT_ID, String.valueOf(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getBenefitIdAutos()));
				}

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().cancelRideByCustomer(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "cancelRideByCustomer response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt("flag");
								String serverMessage = JSONParser.getServerMessage(jObj);
								if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", serverMessage);
								} else if (ApiResponseFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
									Data.userData.updateWalletBalances(jObj, false);
									MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(jObj);

									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onCancelCompleted();
									}
									if(!activity.isFinishing()) {
										DialogPopup.alertPopupWithListener(activity, "", serverMessage, new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												performBackPressed();
											}
										});
									}
									GAUtils.event(RIDES, WAITING_FOR_DRIVER, RIDE+CANCELLED);
								} else {
									DialogPopup.alertPopup(activity, "", serverMessage);
								}
							} else {
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "cancelRideByCustomer error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
				});
			}
			else {
                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
			}
	}

	@Override
	public void close() {
		performBackPressed();
	}
	
}
