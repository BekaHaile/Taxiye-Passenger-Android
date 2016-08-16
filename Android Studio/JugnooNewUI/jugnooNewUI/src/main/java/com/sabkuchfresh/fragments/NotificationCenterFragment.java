package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.NotificationAdapter;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.DialogErrorType;
import com.sabkuchfresh.datastructure.SPLabels;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.NotificationData;
import com.sabkuchfresh.retrofit.model.NotificationInboxResponse;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.DisplayPushHandler;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Prefs;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterFragment extends Fragment implements DisplayPushHandler {

    private LinearLayout root;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private LinearLayout linearLayoutNoNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private SupportActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_notification_center, container, false);

        activity = (SupportActivity) getActivity();

        root = (LinearLayout) rootView.findViewById(R.id.root);
        new ASSL(activity, root, 1134, 720, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.theme_color);
        linearLayoutNoNotifications = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoNotifications);
        linearLayoutNoNotifications.setVisibility(View.GONE);
        ((TextView) rootView.findViewById(R.id.textViewNoNotifications)).setTypeface(Fonts.mavenLight(activity));

        recyclerViewNotification = (RecyclerView) rootView.findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewNotification.setHasFixedSize(false);

        myNotificationAdapter = new NotificationAdapter(activity,
                R.layout.list_item_notification, 0, new NotificationAdapter.Callback() {
            @Override
            public void onShowMoreClick() {
                getNotificationInboxApi(false);
            }
        });
        recyclerViewNotification.setAdapter(myNotificationAdapter);

//        textViewTitle.measure(0, 0);
//        int mWidth = textViewTitle.getMeasuredWidth();
//        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, mWidth));

        //loadListFromDB();

        getNotificationInboxApi(true);

//        FlurryEventLogger.event(this, FlurryEventNames.WHO_VISITED_THE_NOTIFICATION_SCREEN);
//        NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_NOTIFICATION_CHECKED, null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationInboxApi(true);
            }
        });

        return rootView;
    }






//    private void loadListFromDB() {
//        ArrayList<NotificationData> notificationDatas = Database2.getInstance(NotificationCenterFragment.this).getAllNotification();
//        Prefs.with(NotificationCenterFragment.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
//        if (notificationDatas.size() > 0) {
//            linearLayoutNoNotifications.setVisibility(View.GONE);
//        } else {
//            linearLayoutNoNotifications.setVisibility(View.VISIBLE);
//        }
//        myNotificationAdapter.notifyList(notificationDatas.size(), notificationDatas, true);
//    }


    public void performBackPressed() {
        activity.finish();
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onResume() {
        super.onResume();
        //MyApplication.displayPushHandler = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        //MyApplication.displayPushHandler = null;
    }


    @Override
    public void onDestroy() {
        ASSL.closeActivity(root);
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onDisplayMessagePushReceived() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getNotificationInboxApi(true);
            }
        });
    }

    private void getNotificationInboxApi(final boolean refresh) {
        try {
            if (AppStatus.getInstance(MyApplication.getInstance()).isOnline(MyApplication.getInstance())) {
                if(!swipeRefreshLayout.isRefreshing()) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");
                }
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                if(refresh){
                    params.put("offset", "0");
                } else{
                    params.put("offset", String.valueOf(myNotificationAdapter.getListSize()));
                }


                RestClient.getFreshApiService().notificationInbox(params, new Callback<NotificationInboxResponse>() {
                    @Override
                    public void success(final NotificationInboxResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {
                            swipeRefreshLayout.setRefreshing(false);
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if(notificationInboxResponse.getPushes().size() > 0) {
                                    myNotificationAdapter.notifyList(notificationInboxResponse.getTotal(),
                                            (ArrayList<NotificationData>) notificationInboxResponse.getPushes(), refresh);
                                }
                                Prefs.with(activity).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
                                if (myNotificationAdapter.getListSize() > 0) {
                                    linearLayoutNoNotifications.setVisibility(View.GONE);
                                } else {
                                    linearLayoutNoNotifications.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        swipeRefreshLayout.setRefreshing(false);
                        DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                new Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View view) {
                                        getNotificationInboxApi(refresh);
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
                                getNotificationInboxApi(refresh);
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

}
