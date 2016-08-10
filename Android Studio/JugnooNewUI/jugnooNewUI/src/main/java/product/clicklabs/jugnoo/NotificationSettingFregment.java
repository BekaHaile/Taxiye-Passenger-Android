package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.NotificationSettingAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
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
public class NotificationSettingFregment extends Fragment implements NotificationSettingAdapter.Callback {

    private NotificationCenterActivity activity;
    private LinearLayout root;
    private View rootView;
    private RecyclerView recyclerView;
    private NotificationSettingAdapter settingAdapter;
    private TextView textViewpref;
    public NotificationSettingFregment(){}
    private ArrayList<ShowPanelResponse.Item> arrayList = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_settings, container, false);

//        activity = (NotificationCenterActivity) getActivity();
//        activity.setTitle(MyApplication.getInstance().ACTIVITY_NAME_NOTIFICATION_SETTING);

        root = (LinearLayout) rootView.findViewById(R.id.linearRoot);
        try {
            if(root != null) {
                new ASSL(activity, root, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_noti_settings);
        recyclerView.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        textViewpref = (TextView) rootView.findViewById(R.id.textViewpref);
        textViewpref.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        settingAdapter = new NotificationSettingAdapter(arrayList, activity, R.layout.list_item_notification_setting, this);
        recyclerView.setAdapter(settingAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            activity.setTitle(MyApplication.getInstance().ACTIVITY_NAME_NOTIFICATION_SETTING);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
    }

    private void getNotificationStatus() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                RestClient.getApiServices().notificationInbox(params, new Callback<NotificationInboxResponse>() {
                    @Override
                    public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

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
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
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
    public void setNotificationSettingStatus() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                RestClient.getApiServices().notificationInbox(params, new Callback<NotificationInboxResponse>() {
                    @Override
                    public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

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
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
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


    @Override
    public void onClick(int position, ShowPanelResponse.Item supportFAq) {
        
    }
}
