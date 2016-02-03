package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.NearbyDriversAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.NearbyDriver;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class JugnooLineActivity extends BaseActivity implements FlurryEventNames {

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

	RelativeLayout relativeLayoutDriverImage, relativeLayoutDriverAutoImage;
	ImageView imageViewDriverImage, imageViewDriverAutoImage;
	TextView textViewDriverName, textViewDriverCarNumber, textViewDriverCar;

	RelativeLayout relativeLayoutFixedFare;
	TextView textViewFixedFare, textViewFixedFareValue, textViewAutoId;
	EditText editTextAutoId;

	RecyclerView recyclerViewDrivers;
	ProgressWheel progressBarNearbyDrivers;

	Button buttonMakePayment;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;


	ArrayList<NearbyDriver> nearbyDrivers;
	NearbyDriversAdapter nearbyDriversAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_line);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


		relativeLayoutDriverImage = (RelativeLayout) findViewById(R.id.relativeLayoutDriverImage);
		relativeLayoutDriverAutoImage = (RelativeLayout) findViewById(R.id.relativeLayoutDriverAutoImage);
		imageViewDriverImage = (ImageView) findViewById(R.id.imageViewDriverImage);
		imageViewDriverAutoImage = (ImageView) findViewById(R.id.imageViewDriverAutoImage);
		textViewDriverName = (TextView) findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.latoRegular(this));
		textViewDriverCarNumber = (TextView) findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.latoRegular(this));
		textViewDriverCar = (TextView) findViewById(R.id.textViewDriverCar); textViewDriverCar.setTypeface(Fonts.latoRegular(this));

		relativeLayoutFixedFare = (RelativeLayout) findViewById(R.id.relativeLayoutFixedFare);
		textViewFixedFare = (TextView) findViewById(R.id.textViewFixedFare); textViewFixedFare.setTypeface(Fonts.latoRegular(this));
		textViewFixedFareValue = (TextView) findViewById(R.id.textViewFixedFareValue); textViewFixedFareValue.setTypeface(Fonts.latoRegular(this));
		textViewAutoId = (TextView) findViewById(R.id.textViewAutoId); textViewAutoId.setTypeface(Fonts.latoRegular(this));

		editTextAutoId = (EditText) findViewById(R.id.editTextAutoId); editTextAutoId.setTypeface(Fonts.latoRegular(this));
		buttonMakePayment = (Button) findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(this));

		recyclerViewDrivers = (RecyclerView) findViewById(R.id.recyclerViewDrivers);
		recyclerViewDrivers.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.HORIZONTAL);
		recyclerViewDrivers.setLayoutManager(llm);
		recyclerViewDrivers.setVisibility(View.GONE);

		progressBarNearbyDrivers = (ProgressWheel) findViewById(R.id.progressBarNearbyDrivers);
		progressBarNearbyDrivers.setVisibility(View.GONE);

		nearbyDrivers = new ArrayList<>();
		nearbyDriversAdapter = new NearbyDriversAdapter(this, nearbyDrivers, adapterHandler);
		recyclerViewDrivers.setAdapter(nearbyDriversAdapter);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);


        imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonMakePayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String autoId = editTextAutoId.getText().toString().trim();
				if ("".equalsIgnoreCase(autoId)) {
					editTextAutoId.requestFocus();
					editTextAutoId.setError("Please fill the Auto ID.");
				} else {
					DialogPopup.alertPopupTwoButtonsWithListeners(JugnooLineActivity.this, "", "Are you sure you want to pay "
									+ getResources().getString(R.string.rupee) + " "+Utils.getMoneyDecimalFormat().format(Data.userData.sharingFareFixed)+" to the Driver with auto id: " + autoId + "?", "OK", "Cancel",
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									editTextAutoId.setError(null);
									makeSharingPaymentAPI(JugnooLineActivity.this, autoId);
									if(Data.userData.getTotalWalletBalance() > 0){
										FlurryEventLogger.event(JUGNOO_LINE_PAYMENT);
									}
									else{
										FlurryEventLogger.event(LINE_PAYED_VIA_CASH);
									}
								}
							},
							new OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}, true, false);
				}
			}
		});


		editTextAutoId.setOnFocusChangeListener(onFocusChangeListener);
		editTextAutoId.setOnClickListener(onClickListener);


		try {
			fetchNearbyDriversAPI(JugnooLineActivity.this);
			textViewFixedFareValue.setText(getResources().getString(R.string.rupee)+" "+ Utils.getMoneyDecimalFormat().format(Data.userData.sharingFareFixed));

			if(Data.userData.getTotalWalletBalance() > 0){
				buttonMakePayment.setText("PAY VIA JUGNOO CASH");
			}
			else{
				buttonMakePayment.setText("PAY VIA CASH");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


	}

	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, buttonMakePayment.getTop());
					}
				}, 200);
			} else {
				try {
					((EditText)v).setError(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				((EditText)v).setError(null);
			}
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					scrollView.smoothScrollTo(0, buttonMakePayment.getTop());
				}
			}, 200);
			try {
				if(v.getId() == R.id.editTextEmail) {
					((AutoCompleteTextView) v).showDropDown();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			FlurryEventLogger.event(AUTO_ID_ENTERED_LINE);
		}
	};


	NearbyDriversAdapter.NearbyDriversAdapterHandler adapterHandler = new NearbyDriversAdapter.NearbyDriversAdapterHandler() {
		@Override
		public void itemClicked(NearbyDriver nearbyDriver) {
			try {
				editTextAutoId.setText(nearbyDriver.autoId);
				editTextAutoId.setSelection(editTextAutoId.getText().length());
				editTextAutoId.setError(null);

				if("".equalsIgnoreCase(nearbyDriver.userImage)){
					imageViewDriverImage.setVisibility(View.GONE);
				}
				else {
					imageViewDriverImage.setVisibility(View.VISIBLE);
					try{Picasso.with(JugnooLineActivity.this).load(nearbyDriver.userImage).transform(new CircleTransform()).into(imageViewDriverImage);} catch (Exception e) {}
				}

				if("".equalsIgnoreCase(nearbyDriver.driverCarImage)){
					imageViewDriverAutoImage.setVisibility(View.GONE);
				}
				else {
					imageViewDriverAutoImage.setVisibility(View.VISIBLE);
					try {Picasso.with(JugnooLineActivity.this).load(nearbyDriver.driverCarImage).transform(new CircleTransform()).into(imageViewDriverAutoImage);} catch (Exception e) {}
				}

				textViewDriverName.setText(nearbyDriver.userName);
				if("".equalsIgnoreCase(nearbyDriver.driverCarNo)){
					textViewDriverCarNumber.setVisibility(View.GONE);
				}
				else{
					textViewDriverCarNumber.setVisibility(View.VISIBLE);
					textViewDriverCarNumber.setText(nearbyDriver.driverCarNo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};




	/**
	 * ASync for fetching nearby drivers
	 */
	public void fetchNearbyDriversAPI(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				progressBarNearbyDrivers.setVisibility(View.VISIBLE);
				recyclerViewDrivers.setVisibility(View.GONE);

				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("latitude", ""+Data.lastRefreshLatLng.latitude);
				params.put("longitude", "" + Data.lastRefreshLatLng.longitude);

				RestClient.getApiServices().findSharingAutosNearby(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response faq ", "response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ALL_DRIVERS.getOrdinal() == flag) {
								nearbyDrivers.clear();
								nearbyDrivers.addAll(JSONParser.parseNearbySharingDrivers(jObj));
								nearbyDriversAdapter.notifyDataSetChanged();
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						progressBarNearbyDrivers.setVisibility(View.GONE);
						recyclerViewDrivers.setVisibility(View.VISIBLE);
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						progressBarNearbyDrivers.setVisibility(View.GONE);
						recyclerViewDrivers.setVisibility(View.GONE);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			progressBarNearbyDrivers.setVisibility(View.GONE);
			recyclerViewDrivers.setVisibility(View.GONE);
		}
	}


	/**
	 * ASync for sending sharing payment
	 */
	public void makeSharingPaymentAPI(final Activity activity, String autoId) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("auto_id", autoId);
			params.put("money_transacted", ""+Data.userData.sharingFareFixed);
			params.put("transaction_latitude", ""+Data.latitude);
			params.put("transaction_longitude", ""+Data.longitude);
			params.put("wallet_balance_before", ""+Data.userData.getTotalWalletBalance());

			RestClient.getApiServices().endSharingRide(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
					Log.i("Server response faq ", "response = " + responseStr);
					DialogPopup.dismissLoadingDialog();
					try {
						JSONObject jObj = new JSONObject(responseStr);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.getInt("flag");
						if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
							try {
								if(Data.userData != null){
									Data.userData.setJugnooBalance(jObj.optDouble("wallet_balance", Data.userData.getJugnooBalance()));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							DialogPopup.alertPopupWithListener(activity, "", message, new OnClickListener() {
								@Override
								public void onClick(View v) {
									performBackPressed();
								}
							});
						}
						else{
							DialogPopup.alertPopup(activity, "", message);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}



    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

}
