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
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.FreshDeliverySlotsDialog;
import product.clicklabs.jugnoo.fresh.adapters.FreshDeliverySlotsAdapter;
import product.clicklabs.jugnoo.fresh.models.DeliverySlot;
import product.clicklabs.jugnoo.fresh.models.Slot;
import product.clicklabs.jugnoo.fresh.models.UserCheckoutResponse;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
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
	private FreshDeliverySlotsDialog freshDeliverySlotsDialog;
	private ArrayList<Slot> slots = new ArrayList<>();

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


		textViewTotalAmountValue = (TextView) rootView.findViewById(R.id.textViewTotalAmountValue); textViewTotalAmountValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDeliveryChargesValue = (TextView) rootView.findViewById(R.id.textViewDeliveryChargesValue); textViewDeliveryChargesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewAmountPayableValue = (TextView) rootView.findViewById(R.id.textViewAmountPayableValue); textViewAmountPayableValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.mavenRegular(activity));
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
				activity.getTransactionUtils().openAddressFragment(activity, activity.getRelativeLayoutContainer());
			}
		});

		relativeLayoutSlot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(slots != null && slots.size() > 0) {
					activity.setSlotToSelect(activity.getSlotSelected());
					getFreshDeliverySlotsDialog().show();
				}
			}
		});

		buttonProceedToPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getSlotSelected() == null) {
					Toast.makeText(activity, activity.getResources().getString(R.string.please_select_a_delivery_slot),
							Toast.LENGTH_LONG).show();
				} else if("".equalsIgnoreCase(activity.getSelectedAddress())){
					Toast.makeText(activity, activity.getResources().getString(R.string.please_select_a_delivery_address),
							Toast.LENGTH_LONG).show();
				} else{
					activity.getTransactionUtils().openPaymentFragment(activity, activity.getRelativeLayoutContainer());
				}
			}
		});


		try{
			if(activity.getProductsResponse() != null
					&& activity.getProductsResponse().getDeliveryInfo() != null){
				double totalAmount = activity.updateCartValuesGetTotalPrice().first;
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

		getCheckoutData();


		return rootView;
	}

	private void setAddressAndTimeSlot(){
		try {
			generateSlots();
			if((activity.getUserCheckoutResponse() == null
					|| activity.getUserCheckoutResponse().getCheckoutData() == null
					|| activity.getUserCheckoutResponse().getCheckoutData().getLastAddress() == null
					|| activity.getUserCheckoutResponse().getCheckoutData().getLastAddress().equalsIgnoreCase(""))
					&& activity.getSelectedAddress().equalsIgnoreCase("")){
				textViewAddressValue.setVisibility(View.GONE);
				textViewAddAddress.setText(activity.getResources().getString(R.string.add_address));
				imageViewEditAddress.setVisibility(View.GONE);

			} else if(activity.getSelectedAddress().equalsIgnoreCase("")) {
				textViewAddressValue.setVisibility(View.VISIBLE);
				textViewAddressValue.setText(activity.getUserCheckoutResponse().getCheckoutData().getLastAddress());
				imageViewEditAddress.setVisibility(View.VISIBLE);
				activity.setSelectedAddress(activity.getUserCheckoutResponse().getCheckoutData().getLastAddress());
				checkForHomeWorkAddress(activity.getUserCheckoutResponse().getCheckoutData().getLastAddress());
			} else{
				textViewAddressValue.setVisibility(View.VISIBLE);
				textViewAddressValue.setText(activity.getSelectedAddress());
				imageViewEditAddress.setVisibility(View.VISIBLE);
				checkForHomeWorkAddress(activity.getSelectedAddress());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkForHomeWorkAddress(String address){
		if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
			String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
			AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(homeString);
			if(address.equalsIgnoreCase(searchResult.address)){
				textViewAddAddress.setText(activity.getResources().getString(R.string.home));
				return;
			} else{
				textViewAddAddress.setText(activity.getResources().getString(R.string.address));
			}
		} else{
			textViewAddAddress.setText(activity.getResources().getString(R.string.address));
		}

		if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
			String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
			AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(workString);
			if(address.equalsIgnoreCase(searchResult.address)){
				textViewAddAddress.setText(activity.getResources().getString(R.string.work));
			} else{
				textViewAddAddress.setText(activity.getResources().getString(R.string.address));
			}
		} else{
			textViewAddAddress.setText(activity.getResources().getString(R.string.address));
		}
	}

	private void setSelectedSlotToView(){
		if(activity.getSlotSelected() == null){
			textViewSlotValue.setVisibility(View.GONE);
			textViewDay.setText(activity.getResources().getString(R.string.please_select_a_delivery_slot));
			imageViewEditSlot.setVisibility(View.GONE);
		} else{
			textViewSlotValue.setVisibility(View.VISIBLE);
			textViewSlotValue.setText(DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getStartTime())
					+ " - " + DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getEndTime()));
			textViewDay.setText(activity.getSlotSelected().getDayName());
			imageViewEditSlot.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
			setAddressAndTimeSlot();
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

				RestClient.getFreshApiService().userCheckoutData(params, new Callback<UserCheckoutResponse>() {
					@Override
					public void success(UserCheckoutResponse userCheckoutResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
								activity.setUserCheckoutResponse(userCheckoutResponse);
								setAddressAndTimeSlot();
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

	private FreshDeliverySlotsDialog getFreshDeliverySlotsDialog() {
		if (freshDeliverySlotsDialog == null) {
			freshDeliverySlotsDialog = new FreshDeliverySlotsDialog(activity, slots,
					new FreshDeliverySlotsDialog.FreshDeliverySlotsDialogCallback() {
						@Override
						public void onOkClicked() {
							setSelectedSlotToView();
						}
					});
		}
		return freshDeliverySlotsDialog;
	}

	private void generateSlots(){
		if(activity.getUserCheckoutResponse() != null
				&& activity.getUserCheckoutResponse().getCheckoutData() != null
				&& activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots() != null) {
			slots.clear();
//			DeliverySlot ds = new DeliverySlot();
//			ds.setDayName("Today");
//			ds.setDayId(DateOperations.getCurrentDayInt());
//			ArrayList<Slot> tempSlots = new ArrayList<>();
//			Slot s = new Slot();
//			s.setDayId(DateOperations.getCurrentDayInt());
//			s.setDay("Sunday");
//			s.setDeliverySlotId(112);
//			s.setStartTime("10:00:00");
//			s.setEndTime("14:00:00");
//			s.setThresholdTime("12:00:00");
//			tempSlots.add(s);
//			s = new Slot();
//			s.setDayId(DateOperations.getCurrentDayInt());
//			s.setDay("Sunday");
//			s.setDeliverySlotId(112);
//			s.setStartTime("16:00:00");
//			s.setEndTime("20:00:00");
//			s.setThresholdTime("14:30:00");
//			tempSlots.add(s);
//			ds.setSlots(tempSlots);
//			activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots().add(0, ds);

			if(activity.getSlotSelected() != null) {
				verifySlotTiming(activity.getSlotSelected());
				if (!activity.getSlotSelected().isEnabled()) {
					activity.setSlotSelected(null);
				}
			}


			for (DeliverySlot deliverySlot : activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots()) {
				Slot slotDay = new Slot();
				slotDay.setDayName(deliverySlot.getDayName());
				slotDay.setSlotViewType(FreshDeliverySlotsAdapter.SlotViewType.SLOT_DAY);
				slots.add(slotDay);
				int slotsEnabled = 0;
				for (Slot slot : deliverySlot.getSlots()) {
					slot.setSlotViewType(FreshDeliverySlotsAdapter.SlotViewType.SLOT_TIME);
					slot.setDayName(deliverySlot.getDayName());
					verifySlotTiming(slot);
					slotsEnabled = slot.isEnabled() ? slotsEnabled + 1 : slotsEnabled;
					slots.add(slot);
					if(activity.getSlotSelected() == null
							&& slot.isEnabled()){
						activity.setSlotSelected(slot);
					}
				}
				slotDay.setEnabled(slotsEnabled > 0);
				Slot slotDiv = new Slot();
				slotDiv.setSlotViewType(FreshDeliverySlotsAdapter.SlotViewType.DIVIDER);
				slots.add(slotDiv);
			}

			setSelectedSlotToView();
		}
	}

	private void verifySlotTiming(Slot slot){
		if(slot != null) {
			slot.setEnabled(!(slot.getDayId() == DateOperations.getCurrentDayInt()
					&& slot.getThresholdTimeSeconds() < DateOperations.getCurrentDayTimeSeconds()));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		generateSlots();
		getFreshDeliverySlotsDialog().notifySlots();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
