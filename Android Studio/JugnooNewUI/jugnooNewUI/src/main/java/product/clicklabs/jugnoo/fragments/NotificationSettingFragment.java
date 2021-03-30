package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.NotificationSettingAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.NotificationSettingResponseModel;
import product.clicklabs.jugnoo.home.DeleteMyAccountActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.Activity.RESULT_OK;
import static product.clicklabs.jugnoo.Constants.KEY_DEACTIVATE_ACCOUNT_ENABLED;
import static product.clicklabs.jugnoo.Constants.KEY_REQUESTED_FOR_ACCOUNT_DELETION;

/**
 * Created by gurmail on 10/08/16.
 */
public class NotificationSettingFragment extends Fragment implements NotificationSettingAdapter.Callback, GAAction, GACategory{

    private static final String IS_FROM_PRIVACY_PREFERENCES = "isFromPrivacyPreferences";
    private static final int REQ_CODE_DELETE_ACCOUNT = 15001;
    private Activity activity;
    private NotificationSettingAdapter settingAdapter;
    private TextView tvDeleteAccount;
    private Button btnCancelDeleteAccountReq;

    public static NotificationSettingFragment newInstance(boolean isFromPrivacyPref) {
        NotificationSettingFragment notificationSettingFragment = new NotificationSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_FROM_PRIVACY_PREFERENCES, isFromPrivacyPref);
        notificationSettingFragment.setArguments(bundle);
        return notificationSettingFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        activity =  getActivity();
        init(rootView);
        getNotificationStatus();
        return rootView;
    }

    private void init(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycle_noti_settings);
        recyclerView.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        TextView textViewpref = rootView.findViewById(R.id.textViewpref);
        textViewpref.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        TextView textViewNotiDesc = rootView.findViewById(R.id.textViewNotiDesc);
        textViewNotiDesc.setTypeface(Fonts.mavenMedium(activity));
        tvDeleteAccount = rootView.findViewById(R.id.tvDeleteAccount); tvDeleteAccount.setTypeface(Fonts.mavenMedium(activity));
        btnCancelDeleteAccountReq = rootView.findViewById(R.id.btnCancelDeleteAccountReq);
        btnCancelDeleteAccountReq.setTypeface(Fonts.mavenMedium(activity));

        settingAdapter = new NotificationSettingAdapter(activity, R.layout.list_item_notification_setting, this);
        recyclerView.setAdapter(settingAdapter);

        if(getArguments() != null && getArguments().getBoolean(IS_FROM_PRIVACY_PREFERENCES, false)) {
            textViewpref.setVisibility(View.GONE);
        }
        setListener();

        if(Prefs.with(activity).getInt(KEY_DEACTIVATE_ACCOUNT_ENABLED, 0) == 1) {
            if(Prefs.with(activity).getInt(KEY_REQUESTED_FOR_ACCOUNT_DELETION, 0) == 1) {
                tvDeleteAccount.setVisibility(View.GONE);
                btnCancelDeleteAccountReq.setVisibility(View.VISIBLE);
            } else {
                btnCancelDeleteAccountReq.setVisibility(View.GONE);
                tvDeleteAccount.setVisibility(View.VISIBLE);
            }
        } else {
            tvDeleteAccount.setVisibility(View.GONE);
            btnCancelDeleteAccountReq.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        tvDeleteAccount.setOnClickListener(view -> {
            this.startActivityForResult(DeleteMyAccountActivity.callingIntent(activity), REQ_CODE_DELETE_ACCOUNT);
            requireActivity().overridePendingTransition(0,0);
        });

        btnCancelDeleteAccountReq.setOnClickListener(view -> DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getString(R.string.do_you_want_to_cancel_delete_account_request),
                getString(R.string.yes), getString(R.string.no), view12 -> {
                    cancelDeleteAccountRequest();
                }, view1 -> {

                }, false, false));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_DELETE_ACCOUNT && resultCode == RESULT_OK) {
            deleteAccountRequested();
        }
    }

    private void deleteAccountRequested() {
        btnCancelDeleteAccountReq.setVisibility(View.VISIBLE);
        tvDeleteAccount.setVisibility(View.GONE);
    }

    private void cancelDeleteAccountRequest() {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

        new ApiCommon<>(activity).showLoader(true).execute(params, ApiName.CANCEL_DELETE_ACCOUNT_REQUEST,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {

                        DialogPopup.alertPopupWithListener(activity,"",
                                message, getString(R.string.ok), v -> {
                                    btnCancelDeleteAccountReq.setVisibility(View.GONE);
                                    tvDeleteAccount.setVisibility(View.VISIBLE);
                                    Prefs.with(activity).save(KEY_REQUESTED_FOR_ACCOUNT_DELETION, 0);
                                }, false, false, false);
                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method used to get notification status from server
     */
    private void getNotificationStatus() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_SHOW_PROMOTIONAL_MESSAGE_PREFERENCES, "1");

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().getNotificationPreference(params, new Callback<NotificationSettingResponseModel>() {
                    @Override
                    public void success(final NotificationSettingResponseModel notificationPrefResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationPrefResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                settingAdapter.setResults((ArrayList<NotificationSettingResponseModel.NotificationPrefData>) notificationPrefResponse.getData());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                new Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View view) {
                                        getNotificationStatus();
                                    }

                                    @Override
                                    public void neutralClick(View view) {

                                    }

                                    @Override
                                    public void negativeClick(View view) {

                                    }
                                });
                    }
                });
            } else {
                DialogPopup.dialogNoInternet(activity,
                        activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                        new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                getNotificationStatus();
                            }

                            @Override
                            public void neutralClick(View v) {

                            }

                            @Override
                            public void negativeClick(View v) {

                            }
                        });
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }
    
    /**
     * ASync for confirming otp from server
     */
    private void updateNotificationPreferenceStatus(final String name, final int status, final int position) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_PUSH_TYPE, name);
                params.put(Constants.KEY_PUSH_STATUS, ""+status);

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().updateNotificationPreference(params, new Callback<NotificationInboxResponse>() {
                    @Override
                    public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                settingAdapter.getNotificationPrefDatas().get(position).setStatus(status);
                                settingAdapter.notifyDataSetChanged();
                                GAUtils.event(SIDE_MENU, INBOX+SET_PREFERENCES, name+" "+TOGGLED);
                            } else if(!TextUtils.isEmpty(notificationInboxResponse.getMessage())) {
                                DialogPopup.alertPopup(activity, "", notificationInboxResponse.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialogUpdate(DialogErrorType.SERVER_ERROR, name, status, position);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialogUpdate(DialogErrorType.CONNECTION_LOST, name, status, position);
                    }
                });
            } else {
                retryDialogUpdate(DialogErrorType.NO_NET, name, status, position);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }
    private void retryDialogUpdate(DialogErrorType dialogErrorType, final String name, final int status, final int position){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        updateNotificationPreferenceStatus(name, status, position);
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
    public void onClick(int position, NotificationSettingResponseModel.NotificationPrefData datum) {
        int status = 0;
        if(datum.getStatus() == 0) {
            status = 1;
        }
        updateNotificationPreferenceStatus(datum.getName(), status, position);
    }


}
