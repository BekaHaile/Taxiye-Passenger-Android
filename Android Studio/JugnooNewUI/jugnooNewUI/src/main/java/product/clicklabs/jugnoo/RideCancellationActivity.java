package product.clicklabs.jugnoo;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ActivityCloser;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import rmn.androidscreenlibrary.ASSL;

public class RideCancellationActivity extends Activity implements ActivityCloser{
	
	
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
    boolean otherChecked = false;

	
	Button buttonCancelRide;
	
	TextView textViewCancelInfo;
	
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
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		textViewWantToCancel = (TextView) findViewById(R.id.textViewWantToCancel); textViewWantToCancel.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		
		listViewCancelOptions = (NonScrollListView) findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(RideCancellationActivity.this);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);

        relativeLayoutOtherCancelOptionInner = (RelativeLayout) findViewById(R.id.relativeLayoutOtherCancelOptionInner);
        textViewOtherCancelOption = (TextView) findViewById(R.id.textViewOtherCancelOption); textViewOtherCancelOption.setTypeface(Fonts.latoRegular(this));
        imageViewOtherCancelOptionCheck = (ImageView) findViewById(R.id.imageViewOtherCancelOptionCheck);
        editTextOtherCancelOption = (EditText) findViewById(R.id.editTextOtherCancelOption); editTextOtherCancelOption.setTypeface(Fonts.latoRegular(this));
        editTextOtherCancelOption.setMinHeight((int) (ASSL.Yscale() * 200));



		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide); buttonCancelRide.setTypeface(Fonts.latoRegular(this));
		
		textViewCancelInfo = (TextView) findViewById(R.id.textViewCancelInfo); textViewCancelInfo.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		
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
                            DialogPopup.alertPopup(RideCancellationActivity.this, "", "Please give some reason");
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
				
				holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption); holder.textViewCancelOption.setTypeface(Fonts.latoRegular(context));
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
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_checked_new);
			}
			else{
				holder.relative.setBackgroundColor(Color.TRANSPARENT);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked_new);
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
            imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_checked_new);
            if(View.VISIBLE != editTextOtherCancelOption.getVisibility()) {
                editTextOtherCancelOption.setVisibility(View.VISIBLE);
            }
        }
        else{
            relativeLayoutOtherCancelOptionInner.setBackgroundColor(Color.TRANSPARENT);
            imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked_new);
            if(View.GONE != editTextOtherCancelOption.getVisibility()) {
                editTextOtherCancelOption.setVisibility(View.GONE);
            }
        }
        cancelOptionsListAdapter.notifyDataSetChanged();
    }

	

	public void cancelRideAPI(final Activity activity, final String reasons, final String addtionalReason) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("reasons", reasons);
                params.put("addn_reason", addtionalReason);

				AsyncHttpClient asyncHttpClient = Data.getClient();
				asyncHttpClient.post(Config.getServerUrl() + "/cancel_ride_by_customer", params,
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

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    String serverMessage = JSONParser.getServerMessage(jObj);
                                    if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", serverMessage);
                                    } else if (ApiResponseFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {

                                        if (jObj.has("jugnoo_balance")) {
                                            Data.userData.jugnooBalance = jObj.getDouble("jugnoo_balance");
                                        }

                                        if (HomeActivity.appInterruptHandler != null) {
                                            HomeActivity.appInterruptHandler.onCancelCompleted();
                                        }

                                        DialogPopup.alertPopupWithListener(activity, "", serverMessage, new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                performBackPressed();
                                            }
                                        });
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
                        public void onFinish() {
                            super.onFinish();
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
