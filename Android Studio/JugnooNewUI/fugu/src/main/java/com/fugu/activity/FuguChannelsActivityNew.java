package com.fugu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.BuildConfig;
import com.fugu.CaptureUserData;
import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguNotificationConfig;
import com.fugu.R;
import com.fugu.adapter.FuguChannelsPagerAdapter;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.fragments.FuguChannelsFragment;
import com.fugu.model.FuguChannelInfoObject;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguDeviceDetails;
import com.fugu.model.FuguGetConversationsResponse;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;
import com.fugu.utils.UniqueIMEIID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by cl-macmini-01 on 1/31/18.
 */

public class FuguChannelsActivityNew extends FuguBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    public static boolean isRefresh = false;
    public static Long readChannelId = -1L;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private final int READ_PHONE_PERMISSION = 101;
    private final String TAG = FuguChannelsActivityNew.class.getSimpleName();
    private final int IS_HIT_REQUIRED = 200;
    private TextView tvNoInternet, tvNewConversation;
    private TextView tvPoweredBy;
    private FuguColorConfig fuguColorConfig;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();
    private String label = "";
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private int appVersion = 0;
    private TabLayout tabLayout;
    private ViewPager viewPagerChannels;
    private FuguChannelsPagerAdapter mFuguChannelsPagerAdapter;
    private CaptureUserData userData;
    /**
     * Broadcast receiver to handle push messages on channels screen
     */
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));

                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    //fetch conversation again and do complete refresh
                    getConversations();
                } else {
                    //propagate push json to all tabs
                    for (Fragment fragment : mFuguChannelsPagerAdapter.getFragments()) {
                        ((FuguChannelsFragment) fragment).handleMessagePush(messageJson);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Change status of internet connection
     *
     * @param status status of internet conection
     */
    public static void changeStatus(int status) {
        switch (status) {
            case NOT_CONNECTED:
                llInternet.setVisibility(View.VISIBLE);
                llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
                tvStatus.setText(R.string.fugu_not_connected_to_internet);
                break;
            case CONNECTED_TO_INTERNET:
            case CONNECTED_TO_INTERNET_VIA_WIFI:
                llInternet.setBackgroundColor(Color.parseColor("#FFA500"));
                tvStatus.setText(R.string.fugu_connecting);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llInternet.setBackgroundColor(Color.parseColor("#00FF00"));
                        tvStatus.setText(R.string.fugu_connected);
                        llInternet.setVisibility(View.GONE);
                    }
                }, 1500);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_channel_new);

        userData = getIntent().getParcelableExtra("userData");
        if (userData == null) {
            userData = FuguConfig.getInstance().getUserData();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(NOTIFICATION_INTENT));
        initViews();
        decideAppFlow();

    }

    @Override
    public void onResume() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            //refresh the unread uncount for each tab for the particular readChannelId
            for (Fragment fragment : mFuguChannelsPagerAdapter.getFragments()) {
                ((FuguChannelsFragment) fragment).resetUnreadCount(readChannelId);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        readChannelId = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        FuguLog.d(TAG, "Permission callback called-------" + requestCode);
        switch (requestCode) {
            case READ_PHONE_PERMISSION: {
                if (FuguConfig.getInstance().getTargetSDKVersion() > 22 &&
                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendUserDetails();
                } else if (FuguConfig.getInstance().getTargetSDKVersion() <= 22 &&
                        grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    sendUserDetails();
                } else {
                    //ActivityCompat.shouldShowRequestPermissionRationale(FuguFuguChannelsActivity.this, Manifest.permission.READ_PHONE_STATE);
                    Toast.makeText(FuguChannelsActivityNew.this, "Go to Settings and grant permission to access phone state", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    /**
     * Init views
     */
    private void initViews() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, getIntent().getStringExtra("title"));

        appVersion = getIntent().getIntExtra("appVersion", 0);
        fuguColorConfig = CommonData.getColorConfig();
        findViewById(R.id.llRoot).setBackgroundColor(fuguColorConfig.getFuguChannelBg());

        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        tvNoInternet.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext()));
        tvNewConversation = (TextView) findViewById(R.id.tvNewConversation);
        tvPoweredBy = (TextView) findViewById(R.id.tvPoweredBy);
        tvPoweredBy.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext()));
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        configColors();

        if (!isNetworkAvailable()) {
            llInternet.setVisibility(View.VISIBLE);
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setBackgroundDrawable(new ColorDrawable(fuguColorConfig.getFuguActionBarBg()));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.fugu_jugnoo_orange));
        tabLayout.setTabTextColors(fuguColorConfig.getFuguActionBarText(),ContextCompat.getColor(this,android.R.color.black));
        viewPagerChannels = (ViewPager) findViewById(R.id.vwPagerChannels);
    }

    /**
     * Config Colors of App
     */
    private void configColors() {
        tvNewConversation.setTextColor(fuguColorConfig.getFuguActionBarText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvNewConversation.setBackground(FuguColorConfig.makeRoundedSelector(fuguColorConfig.getFuguActionBarBg()));
        } else {
            tvNewConversation.setBackgroundDrawable(FuguColorConfig.makeRoundedSelector(fuguColorConfig.getFuguActionBarBg()));
        }
        swipeRefresh.setColorSchemeColors(fuguColorConfig.getFuguThemeColorPrimary());
        tvNewConversation.setTextColor(fuguColorConfig.getFuguActionBarText());
        tvNoInternet.setTextColor(fuguColorConfig.getFuguThemeColorSecondary());

    }

    /**
     * Decide app flow on basis of user data and permisions
     */
    private void decideAppFlow() {
        if (CommonData.getUserDetails() != null && CommonData.getConversationList().size() > 0) {
            setUpUI();
            getConversations();
        } else if (FuguConfig.getInstance().isPermissionGranted(FuguChannelsActivityNew.this, Manifest.permission.READ_PHONE_STATE)) {
            sendUserDetails();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
            }
        }
    }

    /**
     * Set up application UI
     */
    private void setUpUI() {
        tvNoInternet.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
        label = userData.getBusinessName();
        businessName = userData.getBusinessName();
        userId = userData.getUserId();
        enUserId = userData.getEn_user_id();
        if (userData.getFullName() != null && !userData.getFullName().isEmpty())
            userName = userData.getFullName();
        fuguConversationList.clear();
        if (userData.getFuguConversations().size() > 0) {
            fuguConversationList.addAll(CommonData.getConversationList());
        }
        setRecyclerViewData();
        setPoweredByText(userData);
    }

    /**
     * Sets the channels data
     */
    private void setRecyclerViewData() {
        // set up viewpager and load content
        ArrayList<ArrayList<FuguConversation>> conversations = new ArrayList<>();
        ArrayList<Fragment> channelFragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        // first tab shall default to all and will have all conversations
        ArrayList<FuguConversation> allConversations = new ArrayList<>();
        for (FuguConversation conversation : fuguConversationList) {
            try {
                allConversations.add((FuguConversation) conversation.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        conversations.add(allConversations);
        channelFragments.add(FuguChannelsFragment.newInstance(allConversations));
        titles.add(getResources().getString(R.string.fugu_text_all));

        ArrayList<FuguChannelInfoObject> channels = new ArrayList<>();
        if (userData.getChannelInfoJson() != null) {
            channels = new Gson().fromJson(userData.getChannelInfoJson(),
                    new TypeToken<ArrayList<FuguChannelInfoObject>>() {
                    }.getType());

            // only keep visible channels
            ListIterator<FuguChannelInfoObject> iterator = channels.listIterator();
            while (iterator.hasNext()) {
                if (!iterator.next().getIsVisible()) {
                    iterator.remove();
                }
            }
        }

        for (FuguChannelInfoObject fuguChannelInfoObject : channels) {

            // add title
            if (fuguChannelInfoObject.getValue() != null) {
                titles.add(fuguChannelInfoObject.getValue());
            } else {
                titles.add("");
            }

            ArrayList<FuguConversation> filteredConversations = new ArrayList<>();
            // decide whether to filter by chat_type or transaction id
            if (fuguChannelInfoObject.isFilterByChatType()) {
                int chatTypeFilterValue = fuguChannelInfoObject.getChatTypeFilterValue();
                for (FuguConversation conversation : fuguConversationList) {
                    if (conversation.getChatType() == chatTypeFilterValue) {
                        try {
                            filteredConversations.add((FuguConversation) conversation.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // filter by transaction id ( key would be present after last underscore )
                String filterKey = fuguChannelInfoObject.getKey();
                for (FuguConversation conversation : fuguConversationList) {
                    if (conversation.getTransactionId() != null) {
                        String[] split = conversation.getTransactionId().split("_");
                        if (split != null && split.length > 0) {
                            // last index shall contain the key
                            String key = split[split.length - 1];
                            if (filterKey.equals(key)) {
                                try {
                                    filteredConversations.add((FuguConversation) conversation.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            channelFragments.add(FuguChannelsFragment.newInstance(filteredConversations));

            // add the conversation object
            conversations.add(filteredConversations);
        }

        // set up viewpager and tab
        if (mFuguChannelsPagerAdapter == null) {
            mFuguChannelsPagerAdapter = new FuguChannelsPagerAdapter(getSupportFragmentManager(),
                    channelFragments, titles);
            viewPagerChannels.setAdapter(mFuguChannelsPagerAdapter);
            viewPagerChannels.setOffscreenPageLimit(titles.size() - 1);
            tabLayout.setupWithViewPager(viewPagerChannels);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            try {
               changeFontInViewGroup(tabLayout);
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            // update list
            int i = 0;
            for (Fragment fragment : mFuguChannelsPagerAdapter.getFragments()) {
                ((FuguChannelsFragment) fragment).setConversationList(conversations.get(i));
                i++;
            }
        }
    }

    /**
     * Change font for a viewgroup
     * @param viewGroup the viewGroup
     */
    void changeFontInViewGroup(ViewGroup viewGroup) {
        Typeface typeface = CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext());
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                 ((TextView) child).setTypeface(typeface,Typeface.BOLD);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // pass the activity result to visible fragments
        for (Fragment fragment : mFuguChannelsPagerAdapter.getFragments()) {
            ((FuguChannelsFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Set powered by text
     *
     * @param userData user data
     */
    private void setPoweredByText(FuguPutUserDetailsResponse.Data userData) {
        if (!userData.getWhiteLabel()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                String text = "<font color="
                        + String.format("#%06X",
                        (0xFFFFFF & fuguColorConfig.getFuguTextColorPrimary())) + ">"
                        + getString(R.string.fugu_powered_by)
                        + "<font color=" + String.format("#%06X",
                        (0xFFFFFF & fuguColorConfig.getFuguThemeColorPrimary())) + "> "
                        + getString(R.string.fugu_text) + "</font>";
                //noinspection deprecation
                tvPoweredBy.setText(Html.fromHtml(text));
            } else {
                String text = "<font color="
                        + String.format("#%06X",
                        (0xFFFFFF & fuguColorConfig.getFuguTextColorPrimary())) + ">"
                        + getString(R.string.fugu_powered_by)
                        + "<font color=" + String.format("#%06X",
                        (0xFFFFFF & fuguColorConfig.getFuguThemeColorPrimary())) + "> "
                        + getString(R.string.fugu_text) + "</font>";
                tvPoweredBy.setText(Html.fromHtml(text));
            }

            tvPoweredBy.setBackgroundDrawable(FuguColorConfig.makeSelector(fuguColorConfig.getFuguChannelItemBg(), fuguColorConfig.getFuguChannelItemBgPressed()));
        } else {
            tvPoweredBy.setVisibility(View.GONE);
        }
    }

    /**
     * Get conversations api hit
     */
    private void getConversations() {
        if (isNetworkAvailable()) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, enUserId)
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().getConversations(commonParams.getMap())
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(FuguChannelsActivityNew.this,
                            false, false) {
                        @Override
                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                            try {

                                CommonData.setConversationList(fuguGetConversationsResponse.getData().getFuguConversationList());

                                fuguConversationList.clear();
                                fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
                                // set data
                                setRecyclerViewData();
                                swipeRefresh.setRefreshing(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            swipeRefresh.setRefreshing(false);
                        }
                    });
        } else {
            swipeRefresh.setRefreshing(false);
            // Toast.makeText(FuguFuguChannelsActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * onClick functionality
     *
     * @param v view clicked
     */
    public void onClick(View v) {

        if (v.getId() == R.id.tvNoInternet) {
            if (CommonData.getUserDetails() != null) {
                getConversations();
            } else {
                sendUserDetails();
            }
        } else if (v.getId() == R.id.tvNewConversation) {

            Intent chatIntent = new Intent(this, FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setUserId(userId);
            conversation.setLabel(label);
            conversation.setUserName(userName);
            conversation.setStatus(STATUS_CHANNEL_OPEN);
            conversation.setEnUserId(enUserId);
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            startActivityForResult(chatIntent, IS_HIT_REQUIRED);
        } else if (v.getId() == R.id.tvPoweredBy) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(FUGU_WEBSITE_URL));
            startActivity(i);
        }
    }


    /**
     * Send user details to server
     */
    private void sendUserDetails() {
        if (isNetworkAvailable()) {
            Gson gson = new GsonBuilder().create();
            JsonObject deviceDetailsJson = null;
            try {
                deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(appVersion).getDeviceDetails()).getAsJsonObject();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            HashMap<String, Object> commonParams = new HashMap<>();
            FuguConfig.getInstance();
            if (FuguConfig.getmResellerToken() != null) {
                commonParams.put(RESELLER_TOKEN, FuguConfig.getmResellerToken());
                commonParams.put(REFERENCE_ID, String.valueOf(FuguConfig.getmReferenceId()));
            } else {
                commonParams.put(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey());
            }
            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(FuguChannelsActivityNew.this));
            commonParams.put(APP_TYPE, FuguConfig.getInstance().getAppType());
            commonParams.put(DEVICE_TYPE, ANDROID_USER);
            commonParams.put(APP_VERSION, BuildConfig.VERSION_NAME);
            commonParams.put(DEVICE_DETAILS, deviceDetailsJson);

            if (userData != null) {
                if (!userData.getUserUniqueKey().trim().isEmpty())
                    commonParams.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());

                if (!userData.getFullName().trim().isEmpty())
                    commonParams.put(FULL_NAME, userData.getFullName());

                if (!userData.getEmail().trim().isEmpty())
                    commonParams.put(EMAIL, userData.getEmail());

                if (!userData.getPhoneNumber().trim().isEmpty())
                    commonParams.put(PHONE_NUMBER, userData.getPhoneNumber());
            }

            if (!FuguNotificationConfig.fuguDeviceToken.isEmpty())
                commonParams.put(DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken);
            if (userData != null && !userData.getCustom_attributes().isEmpty()) {
                commonParams.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
            }

            FuguLog.e(TAG + "sendUserDetails map", "==" + commonParams.toString());
            if (FuguConfig.getmResellerToken() != null) {
                apiPutUserDetailReseller(commonParams);
            } else {
                apiPutUserDetail(commonParams);
            }
        } else {
            tvNoInternet.setVisibility(View.VISIBLE);
            swipeRefresh.setVisibility(View.GONE);
            tvNewConversation.setVisibility(View.GONE);
        }
    }

    /**
     * APi to send user details
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        RestClient.getApiInterface().putUserDetails(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivityNew.this,
                        true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivityNew.this,
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            swipeRefresh.setVisibility(View.GONE);
                            tvNewConversation.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * APi to send user details for reseller
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        RestClient.getApiInterface().putUserDetailsReseller(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivityNew.this,
                        true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivityNew.this,
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            swipeRefresh.setVisibility(View.GONE);
                            tvNewConversation.setVisibility(View.GONE);

                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        getConversations();
    }
}
