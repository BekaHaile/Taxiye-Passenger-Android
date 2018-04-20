package product.clicklabs.jugnoo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.NotificationSettingAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.NotificationSettingResponseModel;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gurmail on 10/08/16.
 */
public class NotificationSettingFragment extends Fragment implements NotificationSettingAdapter.Callback, GAAction, GACategory{

    private NotificationCenterActivity activity;
    private LinearLayout root;
    private View rootView;
    private RecyclerView recyclerView;
    private NotificationSettingAdapter settingAdapter;
    private TextView textViewpref, textViewNotiDesc;
    public NotificationSettingFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        root = (LinearLayout) rootView.findViewById(R.id.linearRoot);
        activity = (NotificationCenterActivity) getActivity();
        try {
            if(root != null) {
                new ASSL(activity, root, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        init(rootView);
        getNotificationStatus();
        return rootView;
    }

    private void init(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_noti_settings);
        recyclerView.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        textViewpref = (TextView) rootView.findViewById(R.id.textViewpref);
        textViewpref.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        textViewNotiDesc = (TextView) rootView.findViewById(R.id.textViewNotiDesc); textViewNotiDesc.setTypeface(Fonts.mavenMedium(activity));

        settingAdapter = new NotificationSettingAdapter(activity, R.layout.list_item_notification_setting, this);
        recyclerView.setAdapter(settingAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
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
    public void updateNotificationPreferenceStatus(final String name, final int status, final int position) {
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
