package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.ViewType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class SupportFAQItemFragment extends Fragment implements Constants, GAAction {

	private final String TAG = SupportFAQItemFragment.class.getSimpleName();

	private ScrollView scrollViewRoot;
	private LinearLayout linearLayoutMain;
	private TextView textViewSubtitle, textViewDescription, textViewRSOtherError, textViewScroll;
	private EditText editTextMessage;
	private Button buttonSubmit;
	private RelativeLayout relativeLayoutCallDriver, relativeLayoutCallJugnoo;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId, orderId, productType;
	private String parentName, phoneNumber, rideDate, orderDate, supportNumber;
	private ShowPanelResponse.Item item;

	private static final String PARENT_NAME = "parentName", ITEM = "item";

	public static SupportFAQItemFragment newInstance(int engagementId, String rideDate, String parentName,
													 ShowPanelResponse.Item item, String phoneNumber,
													 int orderId, String orderDate, String supportNumber, int productType){
		SupportFAQItemFragment fragment = new SupportFAQItemFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
		bundle.putString(Constants.KEY_RIDE_DATE, rideDate);
		bundle.putString(PARENT_NAME, parentName);
		bundle.putString(ITEM, new Gson().toJson(item, ShowPanelResponse.Item.class));
		bundle.putString(Constants.KEY_PHONE_NO, phoneNumber);
		bundle.putInt(Constants.KEY_ORDER_ID, orderId);
		bundle.putString(Constants.KEY_ORDER_DATE, orderDate);
		bundle.putString(Constants.KEY_SUPPORT_NUMBER, supportNumber);
		bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void parseArguments(){
		this.engagementId = getArguments().getInt(Constants.KEY_ENGAGEMENT_ID);
		this.rideDate = getArguments().getString(Constants.KEY_RIDE_DATE);
		this.parentName = getArguments().getString(PARENT_NAME);
		this.item = new Gson().fromJson(getArguments().getString(ITEM), ShowPanelResponse.Item.class);
		this.phoneNumber = getArguments().getString(Constants.KEY_PHONE_NO);
		this.orderId = getArguments().getInt(Constants.KEY_ORDER_ID);
		this.orderDate = getArguments().getString(Constants.KEY_ORDER_DATE);
		this.supportNumber = getArguments().getString(Constants.KEY_SUPPORT_NUMBER);
		this.productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_item, container, false);

        activity = getActivity();
		setActivityTitle();

		parseArguments();

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}


		linearLayoutMain = (LinearLayout)rootView.findViewById(R.id.linearLayoutMain);

		textViewSubtitle = (TextView)rootView.findViewById(R.id.textViewSubtitle);
		textViewSubtitle.setTypeface(Fonts.mavenMedium(activity));
		textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
		textViewDescription.setTypeface(Fonts.mavenRegular(activity));
		textViewRSOtherError = (TextView) rootView.findViewById(R.id.textViewRSOtherError);
		textViewRSOtherError.setTypeface(Fonts.mavenRegular(activity));
		textViewRSOtherError.setVisibility(View.GONE);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
		editTextMessage = (EditText)rootView.findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(Fonts.mavenRegular(activity));
		buttonSubmit = (Button)rootView.findViewById(R.id.buttonSubmit);
		buttonSubmit.setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutCallDriver = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutCallDriver);
		((TextView)rootView.findViewById(R.id.textViewCallDriver)).setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutCallJugnoo = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutCallJugnoo);
		((TextView)rootView.findViewById(R.id.textViewCallJugnoo)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewCallJugnoo)).setText(getString(R.string.call_jugnoo, getString(R.string.app_name)));

		textViewSubtitle.setText(parentName);
		textViewDescription.setText(item.getText());

		showItem();



		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()) {
					String feedbackText = editTextMessage.getText().toString().trim();
					if (feedbackText.length() <= 0) {
						textViewRSOtherError.setVisibility(View.VISIBLE);
						textViewRSOtherError.setText(activity.getResources().getString(R.string.star_please_fill_required_information));
					} else if (feedbackText.length() > 1000) {
						textViewRSOtherError.setVisibility(View.VISIBLE);
						textViewRSOtherError.setText(String.format(activity.getResources()
								.getString(R.string.support_feedback_lengthy_error_format), "1000"));
					} else {
						textViewRSOtherError.setVisibility(View.GONE);
						submitFeedback(activity, engagementId, feedbackText, parentName, item.getSupportId(), rideDate, orderId, orderDate);
					}
				}
			}
		});

		relativeLayoutCallDriver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ActionType.INAPP_CALL.getOrdinal() == item.getActionType()) {
					Utils.openCallIntent(activity, phoneNumber);
				}
			}
		});

		relativeLayoutCallJugnoo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ActionType.INAPP_CALL.getOrdinal() == item.getActionType()) {
					Utils.openCallIntent(activity, supportNumber);
				}
			}
		});

		editTextMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					textViewRSOtherError.setVisibility(View.GONE);
				}
			}
		});

		linearLayoutMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editTextMessage.getText().length() > 0
						&& editTextMessage.getText().length() < 1000) {
					textViewRSOtherError.setVisibility(View.GONE);
				}
			}
		});

		KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
				new KeyboardLayoutListener.KeyBoardStateHandler() {
					@Override
					public void keyboardOpened() {
						scrollViewRoot.smoothScrollTo(0, buttonSubmit.getBottom());
					}

					@Override
					public void keyBoardClosed() {

					}
				});
		keyboardLayoutListener.setResizeTextView(false);
		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

			if(activity instanceof FreshActivity){
				((FreshActivity)activity).fragmentUISetup(this);
				rootView.findViewById(R.id.vShadow).setVisibility(View.VISIBLE);
				setActivityTitle();
			} else {
				rootView.findViewById(R.id.vShadow).setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootView;
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(activity.getResources().getString(R.string.support_main_title));
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.support_main_title));
		} else if (activity instanceof FreshActivity) {
			((FreshActivity)activity).getTopBar().title.setText(activity.getResources().getString(R.string.support_main_title));
		}
	}

	private void performBackPressed(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).performBackPressed();
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).performBackPressed();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			setActivityTitle();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(scrollViewRoot);
        System.gc();
	}

	public void submitFeedback(final Activity activity, final int engagementId, String feedbackText,
							   String parentName, int supportId, final String rideDate, final int orderId, final String orderDate) {
		if (!HomeActivity.checkIfUserDataNull(activity) && MyApplication.getInstance().isOnline()) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_SUPPORT_FEEDBACK_TEXT, feedbackText);
			params.put(Constants.KEY_SUPPORT_ISSUE_TITLE, parentName);
			params.put(Constants.KEY_SUPPORT_ID, ""+supportId);

			if(engagementId != -1){
				params.put(Constants.KEY_ENGAGEMENT_ID, ""+engagementId);
				params.put(Constants.KEY_RIDE_DATE, rideDate);
			} else if(orderId != -1){
				params.put(Constants.KEY_ORDER_ID, ""+orderId);
				params.put(Constants.KEY_ORDER_DATE, orderDate);
				params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(productType));
				String category = GACategory.FRESH;
				if(productType == ProductType.MEALS.getOrdinal()) {
					category = GACategory.MEALS;
				} else if(productType == ProductType.MENUS.getOrdinal()) {
					category = GACategory.MENUS;
				} else if(productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
					category = GACategory.DELIVERY_CUSTOMER;
				}
				GAUtils.event(category, SUPPORT, TICKET_RAISED);
			}


			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().generateSupportTicket(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "generateSupportTicket jsonString=>"+jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.getInt(Constants.KEY_FLAG);
						String message = JSONParser.getServerMessage(jObj);
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									try {
										if(activity instanceof SupportActivity && ((SupportActivity)activity).fromBadFeedback == 1){
											activity.finish();
											activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							});
							if(!(activity instanceof SupportActivity) || ((SupportActivity)activity).fromBadFeedback != 1){
								if(getActivity().getSupportFragmentManager().findFragmentByTag(SupportRideIssuesFragment.class.getName()) != null){
									getActivity().getSupportFragmentManager().popBackStack(SupportRideIssuesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
								} else {
									getActivity().onBackPressed();
								}
							}
						} else {
							DialogPopup.alertPopup(activity, "", message);
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						retryDialog(DialogErrorType.NO_NET);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "generateSupportTicket error=>"+error);
					DialogPopup.dismissLoadingDialog();
					retryDialog(DialogErrorType.NO_NET);
				}
			});
		} else {
			retryDialog(DialogErrorType.NO_NET);
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {

					}
				});
	}

	private void showItem(){
		if(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.VISIBLE);
			buttonSubmit.setVisibility(View.VISIBLE);
			relativeLayoutCallDriver.setVisibility(View.GONE);
			relativeLayoutCallJugnoo.setVisibility(View.GONE);
		}
		else if(ActionType.INAPP_CALL.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.GONE);
			if(ViewType.CALL_DRIVER.getOrdinal() == item.getViewType()){
				relativeLayoutCallDriver.setVisibility(View.VISIBLE);
				relativeLayoutCallJugnoo.setVisibility(View.GONE);
			} else if(ViewType.CALL_JUGNOO.getOrdinal() == item.getViewType()){
				relativeLayoutCallDriver.setVisibility(View.GONE);
				relativeLayoutCallJugnoo.setVisibility(View.VISIBLE);
			} else if(ViewType.CALL_DRIVER_AND_JUGNOO.getOrdinal() == item.getViewType()){
				relativeLayoutCallDriver.setVisibility(View.VISIBLE);
				relativeLayoutCallJugnoo.setVisibility(View.VISIBLE);
			} else{
				relativeLayoutCallDriver.setVisibility(View.GONE);
				relativeLayoutCallJugnoo.setVisibility(View.GONE);
			}
		}
		else if(ActionType.TEXT_ONLY.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.GONE);
			relativeLayoutCallDriver.setVisibility(View.GONE);
			relativeLayoutCallJugnoo.setVisibility(View.GONE);
		}
		else if(ActionType.NEXT_LEVEL.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.GONE);
			relativeLayoutCallDriver.setVisibility(View.GONE);
			relativeLayoutCallJugnoo.setVisibility(View.GONE);
		}
	}

}
