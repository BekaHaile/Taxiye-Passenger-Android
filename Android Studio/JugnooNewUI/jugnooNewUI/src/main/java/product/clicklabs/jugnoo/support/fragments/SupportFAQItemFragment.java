package product.clicklabs.jugnoo.support.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SupportFAQItemFragment extends Fragment implements FlurryEventNames, Constants {

	private ScrollView scrollViewRoot;
	private TextView textViewSubtitle, textViewDescription;
	private EditText editTextMessage;
	private Button buttonSubmit;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId;
	private String parentName;
	private ShowPanelResponse.Item item;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportFAQItemFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQItemFragment(int engagementId, String parentName, ShowPanelResponse.Item item){
		this.engagementId = engagementId;
		this.parentName = parentName;
		this.item = item;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_item, container, false);

        activity = getActivity();
		setActivityTitle();

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewSubtitle = (TextView)rootView.findViewById(R.id.textViewSubtitle);
		textViewSubtitle.setTypeface(Fonts.mavenRegular(activity));
		textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
		textViewDescription.setTypeface(Fonts.mavenLight(activity));
		editTextMessage = (EditText)rootView.findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(Fonts.mavenLight(activity));
		buttonSubmit = (Button)rootView.findViewById(R.id.buttonSubmit);
		buttonSubmit.setTypeface(Fonts.mavenRegular(activity));

		textViewSubtitle.setText(parentName);
		textViewDescription.setText(item.getText());

		if(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.VISIBLE);
			buttonSubmit.setVisibility(View.VISIBLE);
		} else if(ActionType.INAPP_CALL.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.VISIBLE);
			buttonSubmit.setText(activity.getResources().getString(R.string.call_driver).toUpperCase(Locale.ENGLISH));
		} else if(ActionType.TEXT_ONLY.getOrdinal() == item.getActionType()){
			editTextMessage.setVisibility(View.GONE);
			buttonSubmit.setVisibility(View.GONE);
		}

		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()){
					String feedbackText = editTextMessage.getText().toString().trim();
					if(feedbackText.length() <= 0){
						editTextMessage.requestFocus();
						editTextMessage.setError(activity.getResources().getString(R.string.support_feedback_empty_error));
					} else if(feedbackText.length() > 1000){
						editTextMessage.requestFocus();
						editTextMessage.setError(String.format(activity.getResources()
								.getString(R.string.support_feedback_lengthy_error_format), "1000"));
					} else{
						submitFeedback(activity, engagementId, feedbackText, item.getSupportId());
					}
				} else if(ActionType.INAPP_CALL.getOrdinal() == item.getActionType()){
					Utils.openCallIntent(activity, "+910000000000");
				}
			}
		});


		return rootView;
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(activity.getResources().getString(R.string.support_main_title));
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.support_main_title));
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

	public void submitFeedback(final Activity activity, final int engagementId, String feedbackText, int supportId) {
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_SUPPORT_FEEDBACK_TEXT, feedbackText);
			params.put(Constants.KEY_SUPPORT_ID, ""+supportId);

			if(engagementId != -1){
				params.put(Constants.KEY_ENGAGEMENT_ID, ""+engagementId);
			}

			RestClient.getApiServices().submitSupportFeedback(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());

					} catch (Exception exception) {
						exception.printStackTrace();
						retryDialog(DialogErrorType.NO_NET);
					}
				}

				@Override
				public void failure(RetrofitError error) {
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


}
