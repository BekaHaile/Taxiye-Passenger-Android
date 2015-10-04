package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.NearbyDriversAdapter;
import product.clicklabs.jugnoo.adapters.NearbyDriversAdapterHandler;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.NearbyDriver;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class JugnooShareActivity extends BaseActivity implements FlurryEventNames {

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

	Button buttonMakePayment;

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
        setContentView(R.layout.activity_jugnoo_share);

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


		nearbyDrivers = new ArrayList<>();


		nearbyDriversAdapter = new NearbyDriversAdapter(this, nearbyDrivers, adapterHandler);
		recyclerViewDrivers.setAdapter(nearbyDriversAdapter);


		nearbyDrivers.add(new NearbyDriver("1234"));
		nearbyDrivers.add(new NearbyDriver("2345"));
		nearbyDrivers.add(new NearbyDriver("3456"));
		nearbyDrivers.add(new NearbyDriver("4567"));

		nearbyDriversAdapter.notifyDataSetChanged();



        imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonMakePayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String autoId = editTextAutoId.getText().toString().trim();
				if("".equalsIgnoreCase(autoId)){
					editTextAutoId.requestFocus();
					editTextAutoId.setError("Please fill the Auto ID.");
				}
				else{
					editTextAutoId.setError(null);
					makeSharingPaymentAPI(JugnooShareActivity.this, autoId);
				}
			}
		});

		editTextAutoId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextAutoId.setError(null);
			}
		});


    }

	NearbyDriversAdapterHandler adapterHandler = new NearbyDriversAdapterHandler() {
		@Override
		public void itemClicked(NearbyDriver nearbyDriver) {
			editTextAutoId.setText(nearbyDriver.driverId);
			editTextAutoId.setSelection(editTextAutoId.getText().length());
		}
	};


	/**
	 * ASync for sending sharing payment information for supplied section
	 */
	public void makeSharingPaymentAPI(final Activity activity, String autoId) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "Loading...");

			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("auto_id", autoId);
			params.put("amount", "10");
			params.put("transaction_latitude", ""+Data.latitude);
			params.put("transaction_longitude", ""+Data.longitude);


			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/make_sharing_payment", params,
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
							Log.i("Server response faq ", "response = " + response);
							try {
								jObj = new JSONObject(response);
								DialogPopup.alertPopup(activity, "", jObj.toString());
							} catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
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
