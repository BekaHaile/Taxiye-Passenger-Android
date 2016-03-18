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

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ActivityCloser;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideCancellationActivity extends BaseActivity implements ActivityCloser, FlurryEventNames {

	private final String TAG = RideCancellationActivity.class.getSimpleName();
	
	LinearLayout relative;
	
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
		setContentView(R.layout.activity_cancel_ride);

		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RideCancellationActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));

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
		((TextView)findViewById(R.id.textViewOtherError)).setTypeface(Fonts.latoRegular(this));
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
                if(Data.cancelOptionsList != null) {
                    String cancelReasonsStr = "";

                    if("".equalsIgnoreCase(Data.cancelOptionsList.additionalReason)){
                        otherChecked = false;
                    }

                    if (otherChecked) {
                        cancelReasonsStr = editTextOtherCancelOption.getText().toString().trim();
                        if ("".equalsIgnoreCase(cancelReasonsStr)) {
							relativeLayoutOtherError.setVisibility(View.VISIBLE);
                        } else {
                            cancelRideAPI(RideCancellationActivity.this, Data.cancelOptionsList.additionalReason, cancelReasonsStr);
                        }
                    } else {
                        for (int i = 0; i < Data.cancelOptionsList.cancelOptions.size(); i++) {
                            if (Data.cancelOptionsList.cancelOptions.get(i).checked) {
                                cancelReasonsStr = Data.cancelOptionsList.cancelOptions.get(i).name;
                                break;
                            }
                        }

                        if ("".equalsIgnoreCase(cancelReasonsStr)) {
                            DialogPopup.alertPopup(RideCancellationActivity.this, "", "Please select one reason");
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
                if(Data.cancelOptionsList != null && !"".equalsIgnoreCase(Data.cancelOptionsList.additionalReason)) {
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
            if(Data.cancelOptionsList != null){
                for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
                    Data.cancelOptionsList.cancelOptions.get(i).checked = false;
                }

                textViewCancelInfo.setText(Data.cancelOptionsList.message);

                if("".equalsIgnoreCase(Data.cancelOptionsList.additionalReason)){
                    relativeLayoutOtherCancelOptionInner.setVisibility(View.GONE);
                    editTextOtherCancelOption.setVisibility(View.GONE);
                }
                else{
                    relativeLayoutOtherCancelOptionInner.setVisibility(View.VISIBLE);
                    editTextOtherCancelOption.setVisibility(View.VISIBLE);
                    textViewOtherCancelOption.setText(Data.cancelOptionsList.additionalReason);
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
			return Data.cancelOptionsList.cancelOptions.size();
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
			
			CancelOption cancelOption = Data.cancelOptionsList.cancelOptions.get(position);

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
					for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
						if(holder.id == i){
							Data.cancelOptionsList.cancelOptions.get(i).checked = true;
						}
						else{
							Data.cancelOptionsList.cancelOptions.get(i).checked = false;
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
            for(int i=0; i<Data.cancelOptionsList.cancelOptions.size(); i++){
                Data.cancelOptionsList.cancelOptions.get(i).checked = false;
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
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				HashMap<String, String> params = new HashMap<>();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("reasons", reasons);
				params.put("addn_reason", addtionalReason);

				RestClient.getApiServices().cancelRideByCustomer(params, new Callback<SettleUserDebt>() {
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

									Data.userData.setJugnooBalance(jObj.optDouble(Constants.KEY_JUGNOO_BALANCE,
											Data.userData.getJugnooBalance()));
									Data.userData.setPaytmBalance(jObj.optDouble(Constants.KEY_PAYTM_BALANCE,
											Data.userData.getPaytmBalance()));

									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onCancelCompleted();
									}

									DialogPopup.alertPopupWithListener(activity, "", serverMessage, new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											performBackPressed();
										}
									});
									FlurryEventLogger.event(RIDE_CANCELLED_COMPLETE);
								} else {
									DialogPopup.alertPopup(activity, "", serverMessage);
								}
							} else {
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "cancelRideByCustomer error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			}
			else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}

	@Override
	public void close() {
		performBackPressed();
	}
	
}
