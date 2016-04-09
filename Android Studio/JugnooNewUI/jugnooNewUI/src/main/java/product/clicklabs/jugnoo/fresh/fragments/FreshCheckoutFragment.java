package product.clicklabs.jugnoo.fresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshCheckoutFragment extends Fragment {

	private final String TAG = FreshCheckoutFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private TextView textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue;
	private RelativeLayout relativeLayoutAddress;
	private TextView textViewAddAddress, textViewAddressValue;
	private ImageView imageViewEditAddress;
	private RelativeLayout relativeLayoutSlot;
	private TextView textViewDay, textViewSlotValue;
	private ImageView imageViewEditSlot;
	private Button buttonProceedToPayment;

	private View rootView;
    private FreshActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshCheckoutFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_checkout, container, false);


        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		textViewTotalAmountValue = (TextView) rootView.findViewById(R.id.textViewTotalAmountValue); textViewTotalAmountValue.setTypeface(Fonts.latoRegular(activity));
		textViewDeliveryChargesValue = (TextView) rootView.findViewById(R.id.textViewDeliveryChargesValue); textViewDeliveryChargesValue.setTypeface(Fonts.latoRegular(activity));
		textViewAmountPayableValue = (TextView) rootView.findViewById(R.id.textViewAmountPayableValue); textViewAmountPayableValue.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewDeliveryAddress)).setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddress);
		textViewAddAddress = (TextView) rootView.findViewById(R.id.textViewAddAddress); textViewAddAddress.setTypeface(Fonts.mavenRegular(activity));
		textViewAddressValue = (TextView) rootView.findViewById(R.id.textViewAddressValue); textViewAddressValue.setTypeface(Fonts.mavenLight(activity));
		imageViewEditAddress = (ImageView) rootView.findViewById(R.id.imageViewEditAddress);
		((TextView)rootView.findViewById(R.id.textViewDeliveryDateTime)).setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutSlot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSlot);
		textViewDay = (TextView) rootView.findViewById(R.id.textViewDay); textViewDay.setTypeface(Fonts.mavenRegular(activity));
		textViewSlotValue = (TextView) rootView.findViewById(R.id.textViewSlotValue); textViewSlotValue.setTypeface(Fonts.mavenLight(activity));
		imageViewEditSlot = (ImageView) rootView.findViewById(R.id.imageViewEditSlot);
		buttonProceedToPayment = (Button) rootView.findViewById(R.id.buttonProceedToPayment); buttonProceedToPayment.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		relativeLayoutSlot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		buttonProceedToPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		try{
			if(activity.getProductsResponse() != null){
				double totalAmount = activity.updateCartValuesGetTotalPrice();
				double amountPayable = totalAmount;
				if(activity.getProductsResponse().getDeliveryInfo().getMinAmount() > totalAmount){
					textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
							Utils.getMoneyDecimalFormat().format(activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges())));
					amountPayable = amountPayable + activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
				} else{
					textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), "0"));
				}
				textViewTotalAmountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(totalAmount)));
				textViewAmountPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(amountPayable)));



			}
		} catch(Exception e){
			e.printStackTrace();
		}


		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}

	public void getCheckoutData() {
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
				Log.i(TAG, "getAllProducts params=" + params.toString());

				RestClient.getFreshApiService().getCheckoutData(params, new Callback<ProductsResponse>() {
					@Override
					public void success(ProductsResponse productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						getCheckoutData();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
