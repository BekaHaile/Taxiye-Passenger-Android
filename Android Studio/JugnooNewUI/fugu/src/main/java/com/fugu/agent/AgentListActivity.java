package com.fugu.agent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguStringConfig;
import com.fugu.R;
import com.fugu.agent.Util.ConversationMode;
import com.fugu.agent.Util.FragmentType;
import com.fugu.agent.adapter.PagerAdapter;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.fragment.AllChatFragment;
import com.fugu.agent.fragment.MyChatFragment;
import com.fugu.agent.listeners.AgentConnectionListener;
import com.fugu.agent.listeners.UnreadListener;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;
import com.fugu.utils.loadingBox.LoadingBox;
import com.fugu.utils.loadingBox.ProgressWheel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.fugu.constant.FuguAppConstant.NETWORK_STATE_INTENT;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class AgentListActivity extends AgentBaseActivity implements AgentConnectionListener {

    private static final String TAG = AgentListActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    ArrayList<Fragment> pagerFragments = new ArrayList<>();
    String[] titles = new String[2];
    public UserData userData;
    /*@SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;*/
    private FuguColorConfig fuguColorConfig;
    private TextView tvPoweredBy;
    private LinearLayout fragmentView;
    private boolean skipFirstResume;
    private ImageView ivViewInfo, ivViewBroadcast;
    private Toolbar myToolbar;

    private LinearLayout retryLayout;
    private TextView btnRetry;
    private ProgressWheel progressWheel;
    private FuguStringConfig stringConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_chatlist);
        FuguConfig.getInstance().setChannelActivity(true);
        setConnectionManager();
        initView();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, getIntentFilter());
    }


    private void setConnectionManager() {
        userData = AgentCommonData.getUserData();
        AgentConnectionManager.getInstance().setUserData(userData);
        AgentConnectionManager.getInstance().setUpFayeConnection();
        AgentConnectionManager.getInstance().setConnectionListener(this);
    }

    private void initView() {

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        ivViewInfo = findViewById(R.id.ivViewInfo);
        ivViewBroadcast = findViewById(R.id.ivViewBroadcast);

        ivViewInfo.setVisibility(View.GONE);
        ivViewBroadcast.setVisibility(View.GONE);

        btnRetry = findViewById(R.id.btnRetry);
        progressWheel = findViewById(R.id.retry_loader);
        retryLayout = findViewById(R.id.retry_layout);

        tvPoweredBy = findViewById(R.id.tvPoweredBy);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.sliding_tabs);
//        llInternet = findViewById(R.id.llInternet);
//        tvStatus = findViewById(R.id.tvStatus);
        pagerFragments = new ArrayList<>();
        fuguColorConfig = CommonData.getColorConfig();
        fragmentView = findViewById(R.id.fragment_view);
        skipFirstResume = true;
        if (getIntent().hasExtra("user_unique_key")) {
            String userUniqueKey = getIntent().getStringExtra("user_unique_key");
            openUserUniqueChat(userUniqueKey);
        } else {
            openPagerScreen();
        }
        stringConfig = CommonData.getStringConfig();
        setTabColor();
        setPoweredByText();

        ivViewBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentListActivity.this, AgentBroadcastActivity.class);
                intent.putExtra("title", "Broadcast Message");
                startActivity(intent);
            }
        });
    }

    private void openPagerScreen() {
        setToolbar(myToolbar, AgentCommonData.getMainTitle());//getIntent().getStringExtra("title"));
        fragmentView.setVisibility(View.GONE);
        ivViewInfo.setVisibility(View.GONE);
//        ivViewBroadcast.setVisibility(View.VISIBLE);
        ivViewBroadcast.setImageResource(FuguConfig.getInstance().getBroadcastDrawable() == -1
                ? R.drawable.hippo_ic_broadcast : FuguConfig.getInstance().getBroadcastDrawable());
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        pagerFragments = new ArrayList<>();

        //clear unsent data
        AgentCommonData.clearTotalUnreadCount();

        boolean isReverseOrder = AgentCommonData.getPagerOrder();

        if(isReverseOrder) {
            pagerFragments.add(new AllChatFragment());
            pagerFragments.add(new MyChatFragment());

            titles[0] = "All Chat";
            titles[1] = "My Chat";
        } else {
            pagerFragments.add(new MyChatFragment());
            pagerFragments.add(new AllChatFragment());

            titles[0] = "My Chat";
            titles[1] = "All Chat";
        }



        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), pagerFragments, titles);
        viewPager.setAdapter(pagerAdapter);
        // Give the TabLayout the ViewPager

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setTabColor() {
        tabLayout.setSelectedTabIndicatorColor(fuguColorConfig.getFuguSelectedTabIndicatorColor());
//        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(fuguColorConfig.getFuguTabTextColor(), fuguColorConfig.getFuguTabSelectedTextColor());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!skipFirstResume)
            AgentConnectionManager.getInstance().onRefreshData();
        skipFirstResume = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        skipFirstResume = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        for (UnreadListener listener : FuguConfig.getInstance().getUIListeners(UnreadListener.class)) {
            if (listener != null) {
                listener.sendTotalUnreadCount();
                listener.getUnreadCount();
            }
        }
//        UnreadCountHelper.getInstance().sendTotalUnreadCount();
//        UnreadCountHelper.getInstance().getUnreadCount();
        FuguConfig.getInstance().setChannelActivity(false);
        AgentConnectionManager.getInstance().onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private int[] typeIntArray = new int[]{ConversationMode.DEFAULT.getOrdinal()};

    private void openUserUniqueChat(final String userUniqueId) {
        setToolbar(myToolbar, "");

        ArrayList<String> strings = new ArrayList<>();
        strings.add(userUniqueId);

        if (userData == null) {
            userData = AgentCommonData.getUserData();
        }
        if(!isNetworkAvailable()) {
            setToolbar(myToolbar, getIntent().getStringExtra("title"));
            return;
        }

        LoadingBox.showOn(AgentListActivity.this);

        String userID = String.valueOf(userData.getUserId());
        String accessToken = userData.getAccessToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put(FuguAppConstant.USER_ID, userID);
        params.put(FuguAppConstant.ACCESS_TOKEN, accessToken);
        params.put(FuguAppConstant.STATUS, "[1]");
        params.put(FuguAppConstant.DEVICE_TYPE, 1);
        params.put(FuguAppConstant.TYPE, Arrays.toString(typeIntArray));

        params.put("search_user_unique_key", new Gson().toJson(strings));

        RestClient.getAgentApiInterface().getConversation(params).enqueue(new ResponseResolver<GetConversationResponse>() {
            @Override
            public void success(GetConversationResponse getConversationResponse) {

                fragmentView.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                LoadingBox.hide();
                ArrayList<Conversation> conversationChatList = (ArrayList<Conversation>) getConversationResponse.getData().getConversation();
                if(conversationChatList != null && conversationChatList.size() == 0) {
                    openp2pConversation(userUniqueId, "Test");
                } else if(conversationChatList != null && conversationChatList.size() == 1) {
                    Conversation conversation = (Conversation) conversationChatList.get(0);
                    Intent intent = new Intent(AgentListActivity.this, AgentChatActivity.class);
                    intent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                    intent.putExtra(FuguAppConstant.CREATE_NEW_CHAT, true);
                    intent.putExtra(FuguAppConstant.FRAGMENT_TYPE, FragmentType.USER_CHAT.getOrdinal());
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
                else {
                    setToolbar(myToolbar, getIntent().getStringExtra("title"));
                    String data = new Gson().toJson(getConversationResponse, GetConversationResponse.class);
                    MyChatFragment myChatFragment = new MyChatFragment();
                    Bundle bundle = new Bundle();
                    int fragmentType = FragmentType.USER_CHAT.getOrdinal();
                    bundle.putString(FuguAppConstant.USER_UNIQUE_KEY, userUniqueId);
                    bundle.putString(FuguAppConstant.CONVERSATION, data);
                    bundle.putInt(FuguAppConstant.FRAGMENT_TYPE, fragmentType);
                    myChatFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_view, myChatFragment, MyChatFragment.class.getName())
                            .commitAllowingStateLoss();
                    ivViewInfo.setVisibility(View.VISIBLE);
                    ivViewBroadcast.setVisibility(View.GONE);
                    ivViewInfo.setImageResource(FuguConfig.getInstance().getHomeIconDrawable() == -1
                            ? R.drawable.hippo_ic_fugu_home : FuguConfig.getInstance().getHomeIconDrawable());
                    if(FuguConfig.getInstance().getHomeIconDrawable() != -1) {
                        ivViewInfo.getDrawable().setColorFilter(fuguColorConfig.getFuguHomeColor(), PorterDuff.Mode.SRC_IN);
                    }
                    ivViewInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPagerScreen();
                            ivViewInfo.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void failure(APIError error) {
                LoadingBox.hide();
                Log.e(TAG, "Error: "+error.getMessage());
            }
        });

    }

    /**
     * Set powered by text
     */
    private void setPoweredByText() {
        UserData userData = AgentCommonData.getUserData();
        tvPoweredBy.setVisibility(View.GONE);
        /*if (!userData.getWhiteLabel()) {
            try {
                poweredByView(getString(R.string.fugu_powered_by), getString(R.string.fugu_text), fuguColorConfig);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    String text = "<font color="
                            + String.format("#%06X",
                            (0xFFFFFF & fuguColorConfig.getFuguTextColorPrimary())) + ">"
                            + getString(R.string.fugu_powered_by)
                            + "<font color=" + String.format("#%06X",
                            (0xFFFFFF & fuguColorConfig.getFuguRunsOnColor())) + "> "
                            + getString(R.string.fugu_text) + "</font>";
                    //noinspection deprecation
                    tvPoweredBy.setText(Html.fromHtml(text));
                } else {
                    String text = "<font color="
                            + String.format("#%06X",
                            (0xFFFFFF & fuguColorConfig.getFuguTextColorPrimary())) + ">"
                            + getString(R.string.fugu_powered_by)
                            + "<font color=" + String.format("#%06X",
                            (0xFFFFFF & fuguColorConfig.getFuguRunsOnColor())) + "> "
                            + getString(R.string.fugu_text) + "</font>";
                    tvPoweredBy.setText(Html.fromHtml(text));
                }

                tvPoweredBy.setBackgroundDrawable(FuguColorConfig.makeSelector(fuguColorConfig.getFuguChannelItemBg(), fuguColorConfig.getFuguChannelItemBgPressed()));
            }
        } else {
            tvPoweredBy.setVisibility(View.GONE);
        }*/
    }

    private void poweredByView(String firstString, String lastString, FuguColorConfig fuguColorConfig) throws Exception {
        String changeString = (lastString != null ? lastString : "Hippo");
        String totalString = firstString + " " + changeString;
        Log.v(TAG, "totalString = " + totalString);
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new StyleSpan(Typeface.BOLD), String.valueOf(firstString).length(), totalString.length(), 0);
        spanText.setSpan(new ForegroundColorSpan(fuguColorConfig.getFuguRunsOnColor()), String.valueOf(firstString).length(), totalString.length(), 0);
        spanText.setSpan(new RelativeSizeSpan(0.8f), 0, String.valueOf(firstString).length(), 0);


        tvPoweredBy.setText(spanText);
        tvPoweredBy.setBackgroundDrawable(FuguColorConfig.makeSelector(fuguColorConfig.getFuguChannelItemBg(), fuguColorConfig.getFuguChannelItemBgPressed()));
    }

    private void openp2pConversation(String userUniqueKey, String title) {
        Conversation conversation = new Conversation();
        conversation.setChannelId(-5L);
        conversation.setChannelName("dummyChannelName");
        conversation.setStatus(-2);
        conversation.setAgentId(-2);
        conversation.setUserUniqueKeys(userUniqueKey);

        conversation.setLabel(title);
        conversation.setUnreadCount(0);

        Intent intent = new Intent(AgentListActivity.this, AgentChatActivity.class);
        intent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
        intent.putExtra(FuguAppConstant.CREATE_NEW_CHAT, true);
        intent.putExtra(FuguAppConstant.FRAGMENT_TYPE, FragmentType.USER_CHAT.getOrdinal());
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
    public void setConnectionMessage(int status) {
        if (isNetworkAvailable()) {
            switch (status) {
                case 0:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 1:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.VISIBLE);
                            btnRetry.setText(stringConfig.getFuguFetchingMessages());
                            retryLayout.setBackgroundColor(fuguColorConfig.getFuguConnected());
                        }
                    });
                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            btnRetry.setText(stringConfig.getFuguServerDisconnect());
                            retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                        }
                    });
                    break;
                case 3:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            btnRetry.setText(stringConfig.getFuguNoNetworkConnected());
                            retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                        }
                    });
                    break;
                case 4:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            btnRetry.setText(stringConfig.getFuguServerConnecting());
                            retryLayout.setBackgroundColor(fuguColorConfig.getFuguConnected());
                        }
                    });
                    break;
                default:

                    break;
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    retryLayout.setVisibility(View.VISIBLE);
                    progressWheel.setVisibility(View.GONE);
                    btnRetry.setText(stringConfig.getFuguNoNetworkConnected());
                    retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                }
            });
        }
    }

    @Override
    public void onConnectionStatus(final String message, int status) {
        if(status ==0 && !myChatFlag && !allChatFlag)
            setConnectionMessage(status);
        else
            setConnectionMessage(status);
    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        return intent;
    }

    public boolean myChatFlag, allChatFlag;
    public void hideLoader(int type) {
        if(type == 1)
            myChatFlag = true;
        if(type == 2)
            allChatFlag = true;
        if(myChatFlag && allChatFlag)
            setConnectionMessage(0);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NETWORK_STATE_INTENT:
                    FuguLog.d(TAG, "Network connectivity change " + intent.getBooleanExtra("isConnected", false));
                    if (intent.getBooleanExtra("isConnected", false)) {
                        AgentConnectionManager.getInstance().setNetworkStatus(true);
                        setConnectionManager();
                    } else {

                    }
                    break;
            }

        }
    };
}
