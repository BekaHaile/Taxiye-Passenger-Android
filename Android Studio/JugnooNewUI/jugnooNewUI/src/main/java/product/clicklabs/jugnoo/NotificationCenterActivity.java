package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.EventsHolder;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private LinearLayout linearLayoutNoNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

        root = (LinearLayout) findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.theme_color);
        linearLayoutNoNotifications = (LinearLayout) findViewById(R.id.linearLayoutNoNotifications);
        linearLayoutNoNotifications.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textViewNoNotifications)).setTypeface(Fonts.mavenLight(this));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setHasFixedSize(false);

        myNotificationAdapter = new NotificationAdapter(NotificationCenterActivity.this,
                R.layout.list_item_notification, 0, new NotificationAdapter.Callback() {
            @Override
            public void onShowMoreClick() {
                getNotificationInboxApi(false);
            }
        });
        recyclerViewNotification.setAdapter(myNotificationAdapter);

        textViewTitle.measure(0, 0);
        int mWidth = textViewTitle.getMeasuredWidth();
        textViewTitle.getPaint().setShader(Utils.textColorGradient(mWidth));

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        //loadListFromDB();

        getNotificationInboxApi(true);

        FlurryEventLogger.event(this, FlurryEventNames.WHO_VISITED_THE_NOTIFICATION_SCREEN);
        NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_NOTIFICATION_CHECKED, null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationInboxApi(true);
            }
        });

    }






    private void loadListFromDB() {
        ArrayList<NotificationData> notificationDatas = Database2.getInstance(NotificationCenterActivity.this).getAllNotification();
        Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
        if (notificationDatas.size() > 0) {
            linearLayoutNoNotifications.setVisibility(View.GONE);
        } else {
            linearLayoutNoNotifications.setVisibility(View.VISIBLE);
        }
        myNotificationAdapter.notifyList(notificationDatas.size(), notificationDatas, true);
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventsHolder.displayPushHandler = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventsHolder.displayPushHandler = null;
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    @Override
    protected void onDestroy() {
        ASSL.closeActivity(root);
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onDisplayMessagePushReceived() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getNotificationInboxApi(true);
            }
        });
    }

    private void getNotificationInboxApi(final boolean refresh) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                if(!swipeRefreshLayout.isRefreshing()) {
                    DialogPopup.showLoadingDialog(NotificationCenterActivity.this, "Loading...");
                }
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                if(refresh){
                    params.put("offset", "0");
                } else{
                    params.put("offset", String.valueOf(myNotificationAdapter.getListSize()));
                }


                RestClient.getApiServices().notificationInbox(params, new Callback<NotificationInboxResponse>() {
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
                                Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
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
                        DialogPopup.dialogNoInternet(NotificationCenterActivity.this, DialogErrorType.CONNECTION_LOST,
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
                DialogPopup.dialogNoInternet(NotificationCenterActivity.this,
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
