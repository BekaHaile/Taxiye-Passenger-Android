package com.fugu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.BuildConfig;
import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguNotificationConfig;
import com.fugu.FuguStringConfig;
import com.fugu.R;
import com.fugu.adapter.EventItem;
import com.fugu.adapter.FuguMessageAdapter;
import com.fugu.adapter.HeaderItem;
import com.fugu.adapter.ListItem;
import com.fugu.adapter.QuickReplyAdapaterActivityCallback;
import com.fugu.apis.ApiPutUserDetails;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChannelStatus;
import com.fugu.datastructure.ChatType;
import com.fugu.model.ContentValue;
import com.fugu.model.CustomAction;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguCreateConversationParams;
import com.fugu.model.FuguCreateConversationResponse;
import com.fugu.model.FuguFileDetails;
import com.fugu.model.FuguGetByLabelIdParams;
import com.fugu.model.FuguGetMessageParams;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.model.Message;
import com.fugu.model.UnreadCountModel;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.MultipartParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.CustomAlertDialog;
import com.fugu.utils.CustomLinear;
import com.fugu.utils.CustomLinearLayoutManager;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguImageUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.StringUtil;
import com.fugu.utils.beatAnimation.AVLoadingIndicatorView;
import com.fugu.utils.loadingBox.ProgressWheel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import faye.FayeClient;
import faye.FayeClientListener;

import static com.fugu.adapter.ListItem.FUGU_FORUM_VIEW;
import static com.fugu.adapter.ListItem.FUGU_QUICK_REPLY_VIEW;
import static com.fugu.adapter.ListItem.FUGU_TEXT_VIEW;
import static com.fugu.adapter.ListItem.ITEM_TYPE_RATING;


public class FuguChatActivity extends FuguBaseActivity implements Animation.AnimationListener, FayeClientListener,
        FuguMessageAdapter.OnRatingListener, QuickReplyAdapaterActivityCallback {

    private String TAG = getClass().getSimpleName();
    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    // Initial FayeClient
    private static final FayeClient mClient = FuguConfig.getClient();

    // Declaring Views
    private RelativeLayout rlRoot;
    private CustomLinear llRoot;
    private CardView cvTypeMessage;
    private LinearLayout llMessageLayout;
    private TextView tvClosed;
    private TextView tvNoInternet;
    private EditText etMsg;
    private RecyclerView rvMessages;
    private FuguMessageAdapter fuguMessageAdapter;
    private Toolbar myToolbar;
    private AVLoadingIndicatorView aviTyping;
    private LinearLayout llTyping;
    private ProgressBar pbLoading;
    private FuguConversation conversation;
    private ProgressBar pbSendingImage;
    private ImageView ivSend;
    private boolean isNetworkStateChanged = false;
    private boolean isFayeChannelActive = false;
    private boolean firstTime = true;
    private boolean isFirst = true;
    private Animation animSlideUp, animSlideDown;
    private ProgressBar pbPeerChat;
    private FuguGetMessageResponse mFuguGetMessageResponse;


    private String sentAtUTC = "";
    private Long channelId = -1L;
    public static Long currentChannelId = -1L;
    private Long userId = -1L;
    private String enUserId = "";
    private Long labelId = -1L;
    private String userName = "";
    private int isTyping = TYPING_SHOW_MESSAGE;
    private String label = "";
    private String defaultMessage = "";
    private String businessName = "";
    private int status;
    private boolean isConversationCreated;
    private FuguImageUtils fuguImageUtils;
    private int onSubscribe = CHANNEL_UNSUBSCRIBED;
    private boolean showLoading = true;
    private boolean allMessagesFetched = false;
    private DateUtils dateUtils;
    private int pageStart = 1, position;
    private int dateItemCount = 0;
    private FuguColorConfig fuguColorConfig;
    private CustomLinearLayoutManager layoutManager;

    private boolean isP2P = false;
    private FuguCreateConversationParams fuguCreateConversationParams;
    private int previousPos = 0;
    private boolean runAnim = true, runAnim2 = false;
    private Handler handler = null;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private TextView tvDateLabel;
    private HashMap<String, Long> transactionIdsMap;
    private String globalUuid;


    // new key for code cleanning
//    private HelperConnectionListener connectionListener;
    @NonNull
    private ArrayList<ListItem> fuguMessageList = new ArrayList<>();
    private LinkedHashMap<String, ListItem> sentMessages = new LinkedHashMap<>();
    private LinkedHashMap<String, ListItem> unsentMessages = new LinkedHashMap<>();
    @NonNull
    private LinkedHashMap<String, JSONObject> unsentMessageMapNew = new LinkedHashMap<>();
    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";
    private LinearLayout retryLayout;
    private TextView btnRetry;
    private ProgressWheel progressWheel;
    private int messageIndex = -1;
    private FuguStringConfig stringConfig;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_chat);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverChat, new IntentFilter(NOTIFICATION_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(nullListenerReceiver, new IntentFilter(FuguAppConstant.FUGU_LISTENER_NULL));

        isFirstTimeOpened = true;
        initViews();
        fetchIntentData();
        setUpFayeConnection();
        setUpUI();
        stateChangeListeners();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, getIntentFilter());
        CommonData.clearPushChannel();
    }

    private void updateUnreadCount(Long channelId, Long labelId) {
        ArrayList<UnreadCountModel> unreadCountModels = CommonData.getUnreadCountModel();
        if (unreadCountModels.size() == 0)
            return;
        int index = -1;
        if (channelId > 0) {
            index = unreadCountModels.indexOf(new UnreadCountModel(channelId));
        } else if (labelId > 0) {
            for (int i = 0; i < unreadCountModels.size(); i++) {
                if (unreadCountModels.get(i).getLabelId().compareTo(labelId) == 0) {
                    index = i;
                    break;
                }
            }
        }
        FuguLog.v(TAG, "index = " + index);
        FuguLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
        if (index > -1)
            unreadCountModels.remove(index);

        FuguLog.v(TAG, "unreadCountModels = " + new Gson().toJson(unreadCountModels));
        FuguLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
        CommonData.setUnreadCount(unreadCountModels);

        int count = 0;
        for (int i = 0; i < unreadCountModels.size(); i++) {
            count = count + unreadCountModels.get(i).getCount();
            FuguLog.v(TAG, i + " count = " + unreadCountModels.get(i).getCount());
        }

        FuguLog.v(TAG, "count = " + count);

        if (FuguConfig.getInstance().getCallbackListener() != null) {
            FuguConfig.getInstance().getCallbackListener().count(count);
        }
    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        intent.addAction(NOTIFICATION_TAPPED);
        return intent;
    }

    private void initViews() {
        transactionIdsMap = CommonData.getTransactionIdsMap();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        fuguColorConfig = CommonData.getColorConfig();
        stringConfig = CommonData.getStringConfig();
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        llRoot = (CustomLinear) findViewById(R.id.llRoot);
        tvDateLabel = (TextView) findViewById(R.id.tvDateLabel);
        cvTypeMessage = (CardView) findViewById(R.id.cvTypeMessage);
        tvClosed = (TextView) findViewById(R.id.tvClosed);
        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        aviTyping = (AVLoadingIndicatorView) findViewById(R.id.aviTyping);
        llTyping = (LinearLayout) findViewById(R.id.llTyping);
        etMsg = (EditText) findViewById(R.id.etMsg);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbSendingImage = (ProgressBar) findViewById(R.id.pbSendingImage);
        ivSend = (ImageView) findViewById(R.id.ivSend);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        llMessageLayout = (LinearLayout) findViewById(R.id.llMessageLayout);
        fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);
        dateUtils = DateUtils.getInstance();
        pbPeerChat = (ProgressBar) findViewById(R.id.pbPeerChat);
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        btnRetry = findViewById(R.id.btnRetry);
        retryLayout = findViewById(R.id.retry_layout);
        progressWheel = findViewById(R.id.retry_loader);
        configColors();
        /*if (!isNetworkAvailable()) {
            llInternet.setVisibility(View.VISIBLE);
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }*/
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_up_time);
        animSlideUp.setAnimationListener(this);

        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_down_time);
        animSlideDown.setAnimationListener(this);

        Typeface typeface = CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext());
        tvDateLabel.setTypeface(typeface); tvClosed.setTypeface(typeface); tvNoInternet.setTypeface(typeface);
        etMsg.setTypeface(typeface); tvStatus.setTypeface(typeface);


    }

    private void configColors() {
        rlRoot.setBackgroundColor(fuguColorConfig.getFuguChatBg());
        GradientDrawable drawable = (GradientDrawable) llTyping.getBackground();
        drawable.setColor(fuguColorConfig.getFuguBgMessageFrom());
        drawable.setStroke((int) getResources().getDimension(R.dimen.fugu_border_width), fuguColorConfig.getFuguBorderColor()); // set stroke width and stroke color
        aviTyping.setIndicatorColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
        tvClosed.setTextColor(fuguColorConfig.getFuguThemeColorPrimary());
        tvClosed.getBackground().setColorFilter(fuguColorConfig.getFuguChannelItemBg(), PorterDuff.Mode.SRC_ATOP);
        cvTypeMessage.getBackground().setColorFilter(fuguColorConfig.getFuguTypeMessageBg(), PorterDuff.Mode.SRC_ATOP);
        etMsg.setHintTextColor(fuguColorConfig.getFuguTypeMessageHint());
        etMsg.setTextColor(fuguColorConfig.getFuguTypeMessageText());
        tvNoInternet.setTextColor(fuguColorConfig.getFuguThemeColorPrimary());
    }

    private BroadcastReceiver mMessageReceiverChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));
                FuguLog.d("receiver", "Got message: " + messageJson.toString());
                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    CommonData.setIsNewchat(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver nullListenerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", 0);
            setListener(status);
        }
    };

    private void setListener(int status) {
        FuguLog.e(TAG, "Listener added");
        mClient.setListener(this);
        switch (status) {
            case 1:
                onPongReceived();
                break;
            case 2:
                onWebSocketError();
                break;
            default:

                break;
        }

    }

    private void fetchIntentData() {
        conversation = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);
        if (conversation.getUnreadCount() > 0) {
            rvMessages.setAlpha(0);
        }
        getUnreadCount();
        FuguLog.d("userName in SDK", "FuguChatActivity onCreate " + conversation.getUserName());
        int chatType = getIntent().getIntExtra(CHAT_TYPE, ChatType.P2P.getOrdinal());
        if (!TextUtils.isEmpty(conversation.getLabel())) {
            label = conversation.getLabel();
        } else {
            label = conversation.getBusinessName();
        }
        if (conversation.getLabelId() != null) {
            labelId = conversation.getLabelId();
        }
        if (conversation.isDisableReply()) {
            llMessageLayout.setVisibility(View.GONE);
        }
        channelId = conversation.getChannelId();
        updateUnreadCount(channelId, labelId);
        FuguNotificationConfig.pushLabelId = labelId;
        FuguNotificationConfig.pushChannelId = conversation.getChannelId();
        currentChannelId = conversation.getChannelId();
        FuguLog.e(TAG, "==" + FuguNotificationConfig.pushChannelId);

        userId = conversation.getUserId();
        enUserId = conversation.getEnUserId();
        userName = StringUtil.toCamelCase(conversation.getUserName());
        status = conversation.getStatus();
        defaultMessage = conversation.getDefaultMessage();
        businessName = conversation.getBusinessName();


        FuguLog.v("is p2p chat", "---> " + isP2P);

        if (chatType == ChatType.GROUP_CHAT.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
        } else if (chatType == ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
            label = "";
        } else {
            fuguCreateConversationParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey(), labelId,
                    enUserId);
        }
        setToolbar(myToolbar, label);

    }

    private void setRecyclerViewData1() {
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        fuguMessageAdapter = new FuguMessageAdapter(FuguChatActivity.this, fuguMessageList,
                labelId, conversation, this, this, getSupportFragmentManager());
        layoutManager = new CustomLinearLayoutManager(FuguChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setHasFixedSize(false);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(fuguMessageAdapter);
        rvMessages.setItemAnimator(null);

        setRecyclerViewData();
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        runAnim2 = false;
                        if (handler == null) {
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!runAnim2) {
                                        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                                R.anim.fugu_slide_up_time);
                                        tvDateLabel.startAnimation(animSlideUp);
                                        tvDateLabel.setVisibility(View.INVISIBLE);
                                        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                runAnim = true;
                                                handler = null;
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    } else {
                                        handler = null;
                                    }
                                }
                            }, 1200);
                        }


                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        FuguLog.d(TAG, "scroll down triggered");
                        tvDateLabel.clearAnimation();
                        runAnim2 = true;
                        if (runAnim) {
                            tvDateLabel.setVisibility(View.VISIBLE);
                            tvDateLabel.clearAnimation();
                            animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.fugu_slide_down_time);
                            tvDateLabel.startAnimation(animSlideDown);
                            animSlideDown.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    runAnim = false;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            break;
                        }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = layoutManager.findFirstVisibleItemPosition();

                if (previousPos > position && fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_SELF || fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_OTHER) {
                        if (!TextUtils.isEmpty(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())) {
                            tvDateLabel.setText(DateUtils.getDate(dateUtils.convertToLocal(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())));
                        }
                    }
                } else if (fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.TYPE_HEADER) {
                        String date = DateUtils.getInstance().getDate(((HeaderItem) fuguMessageList.get(position)).getDate());
                        tvDateLabel.setText(date);
                    }
                }
                if (position == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (isNetworkAvailable()) {
                        pbLoading.setVisibility(View.VISIBLE);
                        getMessages(null);
                    }
                }
                previousPos = position;
            }
        });
    }

    private void setUpUI() {
        allMessagesFetched = false;
        setRecyclerViewData1();
        /*fuguMessageAdapter = new FuguMessageAdapter(FuguChatActivity.this, fuguMessageList,
                labelId, conversation, this, this, getSupportFragmentManager());
        layoutManager = new CustomLinearLayoutManager(FuguChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setHasFixedSize(false);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(fuguMessageAdapter);
        rvMessages.setItemAnimator(null);
        //fuguMessageAdapter.notifyDataSetChanged();
        setRecyclerViewData();
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        runAnim2 = false;
                        if (handler == null) {
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!runAnim2) {
                                        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                                R.anim.fugu_slide_up_time);
                                        tvDateLabel.startAnimation(animSlideUp);
                                        tvDateLabel.setVisibility(View.INVISIBLE);
                                        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                runAnim = true;
                                                handler = null;
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    } else {
                                        handler = null;
                                    }
                                }
                            }, 1200);
                        }


                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        FuguLog.d(TAG, "scroll down triggered");
                        tvDateLabel.clearAnimation();
                        runAnim2 = true;
                        if (runAnim) {
                            tvDateLabel.setVisibility(View.VISIBLE);
                            tvDateLabel.clearAnimation();
                            animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.fugu_slide_down_time);
                            tvDateLabel.startAnimation(animSlideDown);
                            animSlideDown.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    runAnim = false;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            break;
                        }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = layoutManager.findFirstVisibleItemPosition();

                if (previousPos > position && fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_SELF || fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_OTHER) {
                        if (!TextUtils.isEmpty(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())) {
                            tvDateLabel.setText(DateUtils.getDate(dateUtils.convertToLocal(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())));
                        }
                    }
                } else if (fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.TYPE_HEADER) {
                        String date = DateUtils.getInstance().getDate(((HeaderItem) fuguMessageList.get(position)).getDate());
                        tvDateLabel.setText(date);
                    }
                }
                if (position == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (isNetworkAvailable()) {
                        pbLoading.setVisibility(View.VISIBLE);
                        getMessages(null);
                    }
                }
                previousPos = position;
            }
        });*/
        if (channelId.compareTo(-1L) == 0 && labelId.compareTo(-1L) != 0 && !conversation.isOpenChat()) {
            globalUuid = UUID.randomUUID().toString();
            fuguMessageList.add(new EventItem(
                    new Message(businessName,
                            -1L,
                            defaultMessage,
                            "",
                            false,
                            onSubscribe == CHANNEL_SUBSCRIBED ? MESSAGE_READ : MESSAGE_UNSENT,
                            0,
                            TEXT_MESSAGE, globalUuid)));
        }

        if (isP2P) {
            FuguLog.v("call createConversation", "setUpUI");
            transactionIdsMap = new HashMap<>();
            if (CommonData.getTransactionIdsMap() != null) {
                transactionIdsMap = CommonData.getTransactionIdsMap();
            }
            if (!transactionIdsMap.isEmpty() && transactionIdsMap.get(fuguCreateConversationParams.getTransactionId()) != null) {
                channelId = transactionIdsMap.get(fuguCreateConversationParams.getTransactionId());
                pbLoading.setVisibility(View.GONE);
                pbPeerChat.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.VISIBLE);
                cvTypeMessage.setVisibility(View.VISIBLE);
                //getMessagesFromCache();
                getSavedMessages();
            } else {
                pbPeerChat.setVisibility(View.VISIBLE);
                llMessageLayout.setVisibility(View.GONE);
                cvTypeMessage.setVisibility(View.GONE);
            }
            createConversation(TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, isP2P);
            pbSendingImage.setVisibility(View.GONE);
        } else if (conversation.getChannelId() != null && conversation.getChannelId() > 0) {
            getSavedMessages();
            llRoot.setVisibility(View.VISIBLE);
            try {
                if (CommonData.getMessageResponse(conversation.getChannelId()) != null) {
                    label = CommonData.getLabelIdResponse(conversation.getChannelId()).getData().getLabel();
                    setToolbar(myToolbar, label);
                }
            } catch (Exception e) {

            }
            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            } else {
                getMessages(null);
            }
        } else if (conversation.getLabelId() != null && conversation.getLabelId() > 0) {
            getSavedMessages();
            getByLabelId();
        } else {
            setRecyclerViewData();
            llRoot.setVisibility(View.VISIBLE);

            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getSavedMessages() {
        showLoading = false;
        //LoadingBox.showOn(this);
        sentMessages = new LinkedHashMap<>();
        unsentMessages = new LinkedHashMap<>();
        fuguMessageList.clear();
        dateItemCount = 0;
        sentAtUTC = "";

        if (channelId > 0 && CommonData.getSentMessageByChannel(channelId) != null) {
            sentMessages = CommonData.getSentMessageByChannel(channelId);
            fuguMessageList.addAll(sentMessages.values());
        }
        List<String> reverseOrderedKeys = new ArrayList<String>(sentMessages.keySet());
        Collections.reverse(reverseOrderedKeys);
        sentAtUTC = "";
        for (String key : reverseOrderedKeys) {
            if (sentMessages.get(key) instanceof HeaderItem) {
                sentAtUTC = key;
                break;
            }
        }

        if(channelId > 0 && CommonData.getUnsentMessageMapByChannel(channelId) != null) {
            unsentMessageMapNew = CommonData.getUnsentMessageMapByChannel(channelId);
        }

        if (channelId > 0 && CommonData.getUnsentMessageByChannel(channelId) != null) {
            unsentMessages = CommonData.getUnsentMessageByChannel(channelId);
            if (unsentMessages == null)
                unsentMessages = new LinkedHashMap<>();

            for (String key : unsentMessages.keySet()) {
                ListItem listItem = unsentMessages.get(key);
                Message message = ((EventItem) listItem).getEvent();
                String time = message.getSentAtUtc();
                int expireTimeCheck = message.getIsMessageExpired();

                if (message.getMessageType() != IMAGE_MESSAGE && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                    message.setIsMessageExpired(1);
                    try {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        if(messageJson != null) {
                            messageJson.put("is_message_expired", 1);
                            unsentMessageMapNew.put(key, messageJson);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                } else if(message.getMessageType() == IMAGE_MESSAGE) {
                    JSONObject messageJson = unsentMessageMapNew.get(key);
                    if(messageJson == null) {
                        message.setMessageStatus(MESSAGE_IMAGE_RETRY);
                    }
                }
            }
            CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
            CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);

            for (String key : unsentMessages.keySet()) {
                ListItem listItem = unsentMessages.get(key);
                String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                    fuguMessageList.add(new HeaderItem(localDate));
                    sentAtUTC = localDate;
                    System.out.println("Date 1: " + localDate);
                }
                fuguMessageList.add(unsentMessages.get(key));
            }
        }
    }

    private Timer timer = new Timer();
    private final long DELAY = 3000; // milliseconds

    private void stateChangeListeners() {
        etMsg.requestFocus();
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (isNetworkAvailable()) {
                        ivSend.setClickable(true);
                        ivSend.setAlpha(1f);
                    }
                } else {
                    ivSend.setClickable(false);
                    ivSend.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isNetworkAvailable()) {
                    if (isTyping != TYPING_STARTED) {
                        FuguLog.d(TAG, isTyping + "started typing");
                        // publish start typing event
                        if (channelId > -1 && !etMsg.getText().toString().isEmpty()) {
                            isTyping = TYPING_STARTED;
                            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
                        }
                    }

                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                        FuguLog.d(TAG, isTyping + "stopped typing");
                        stopTyping();
                      }
                    },DELAY);
                }
            }
        });

        llRoot.setOnKeyBoardStateChanged(new CustomLinear.OnKeyboardOpened() {
            @Override
            public boolean onKeyBoardStateChanged(boolean isVisible) {
                if (etMsg.hasFocus() && isVisible && fuguMessageAdapter != null && fuguMessageAdapter.getItemCount() > 0) {
                    rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
    }

    private void stopTyping() {
        if (isTyping == TYPING_STARTED) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
        }
    }

    private void setUpFayeConnection() {
        // Set FayeClient listener
        mClient.setListener(this);
        if (!mClient.isConnectedServer() && isNetworkAvailable()) {
            mClient.connectServer();
        } else {
            if (!isNetworkAvailable()) {
                setConnectionMessage(1);
            }
        }
    }

    private void sendReadAcknowledgement() {
        if (channelId > -1) {
            publishOnFaye(getString(R.string.fugu_empty), 0,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_READ_ALL, null);
        }
    }

    private void updateFeedback(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fuguMessageAdapter.notifyItemChanged(position);
            }
        });
    }

    private void updateFeedback(final int position, final boolean scrollDown) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (scrollDown) {
                    fuguMessageAdapter.notifyDataSetChanged();
                    scrollListToBottom();
                } else {
                    fuguMessageAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    private void scrollListToBottom() {
        try {
            rvMessages.scrollToPosition(fuguMessageList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readFunctionality(JSONObject messageJson) {
        if (FuguConfig.getInstance().getUserData().getUserId().compareTo(messageJson.optLong(USER_ID)) != 0) {
            for (int i = 0; i < fuguMessageList.size(); i++) {
                if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF) {
                    if (((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus() == MESSAGE_SENT) {
                        ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                    }
//                    else {
//                        int status = ((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus();
//                        ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(status);
//                    }
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.getRecycledViewPool().clear();
                fuguMessageAdapter.notifyDataSetChanged();
            }
        });
    }

    void removeItemAndUpdateUI() {
        if (CommonData.getQuickReplyData() != null) {
            try {
                String defaultActionId;
                if (!TextUtils.isEmpty(CommonData.getQuickReplyData().getDefaultActionId())) {
                    defaultActionId = CommonData.getQuickReplyData().getDefaultActionId();
                } else {
                    defaultActionId = CommonData.getQuickReplyData().getContentValue().get(0).getActionId();
                }
                sendQuickReply(CommonData.getQuickReplyData(), 0, defaultActionId);

                fuguMessageList.remove(new EventItem(CommonData.getQuickReplyData()));
                CommonData.clearQuickReplyData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fuguMessageAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            if(CommonData.getQuickReplyData() != null)
//                fuguMessageList.remove(new EventItem(CommonData.getQuickReplyData()));
        }
    }

    private JSONObject prepareMessageJson(int onSubscribe) {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(FULL_NAME, userName);
            messageJson.put(IS_TYPING, isTyping);
            messageJson.put(MESSAGE, "");
            messageJson.put(MESSAGE_TYPE, TEXT_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(ON_SUBSCRIBE, onSubscribe);
            messageJson.put(CHANNEL_ID, channelId);

            String localDate = DateUtils.getFormattedDate(new Date());
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageJson;
    }


    private void publishOnFaye(final String message, final int messageType, final String url, final String thumbnailUrl,
                               final FuguFileDetails fileDetails, final int notificationType, String uuid) {
        try {
            publishMessage(message, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String message, int messageType, String url, String thumbnailUrl, FuguFileDetails fileDetails,
                                int notificationType, String uuid, int position) throws JSONException {
        if (isNetworkAvailable()) {
            String localDate = DateUtils.getFormattedDate(new Date());
            FuguLog.d("userName in SDK", "publishOnFaye " + userName);
            //To be shifted
            JSONObject messageJson = new JSONObject();

            if (notificationType == NOTIFICATION_READ_ALL) {
                messageJson.put(NOTIFICATION_TYPE, notificationType);
                messageJson.put(CHANNEL_ID, channelId);
            } else {
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE, message);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                if (position == 0) {
                    messageJson.put(MESSAGE_INDEX, fuguMessageList.size() - 1);
                } else {
                    messageJson.put(MESSAGE_INDEX, position);
                }
                if (uuid != null) {
                    messageJson.put("UUID", uuid);
                }
                if (messageType == IMAGE_MESSAGE && !url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                }

                if (messageType == FILE_MESSAGE && !url.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                if (messageType == TEXT_MESSAGE) {
                    messageJson.put(IS_TYPING, isTyping);
                } else {
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                }

                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            }

            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);

            if (mClient.isConnectedServer())
                mClient.publish("/" + String.valueOf(channelId), messageJson);
            else {
                setListener(0);
                mClient.connectServer();
            }
            //end to be shifted
        } else if (!message.isEmpty() && messageType == TEXT_MESSAGE) {

        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //noinspection ConstantConditions
            switch (intent.getAction()) {
                case NETWORK_STATE_INTENT:
                    FuguLog.d(TAG, "Network connectivity change " + intent.getBooleanExtra("isConnected", false));
                    if (intent.getBooleanExtra("isConnected", false)) {
                        if (mClient != null) {
                            mClient.setListener(FuguChatActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        connectAgainToServer();
                                    } catch (Exception e) {

                                    }
                                }
                            }, 500);

                        }
                        if (unsentMessageMapNew.size() == 0) {
                            pageStart = 1;
                            if (isP2P)
                                getMessages(label);
                            else
                                getMessages(null);
                        } else {
                            isNetworkStateChanged = true;
                            btnRetry.setText("Connecting...");
                        }
                        enableButtons();
                    } else {
                        setConnectionMessage(4);
                    }
                    break;

                case NOTIFICATION_TAPPED:

                    conversation = new Gson().fromJson(intent.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);

                    channelId = conversation.getChannelId();
                    userId = conversation.getUserId();
                    labelId = conversation.getLabelId();
                    FuguNotificationConfig.pushChannelId = channelId;
                    FuguNotificationConfig.pushLabelId = labelId;
                    currentChannelId = channelId;

                    pageStart = 1;
                    setUpUI();
                    if (mClient.isConnectedServer()) {
                        mClient.unsubscribeAll();
                        mClient.subscribeChannel("/" + String.valueOf(channelId));
                    } else {
                        mClient.connectServer();
                    }
                    break;
            }

        }
    };

    private void enableButtons() {
        try {
            if (etMsg.getText().toString().trim().length() > 0 && isNetworkAvailable()) {
                ivSend.setClickable(true);
                ivSend.setAlpha(1f);
            } else {
                ivSend.setClickable(false);
                ivSend.setAlpha(0.5f);
            }
            if(!isNetworkAvailable() && fayeDisconnect) {
                ivSend.setAlpha(0.5f);
            }
        } catch (Exception e) {

        }
    }

    private void startAnim() {
        aviTyping.show();
        aviTyping.setVisibility(View.VISIBLE);
        llTyping.setVisibility(View.VISIBLE);

    }

    private void stopAnim() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llTyping.setVisibility(View.GONE);
                aviTyping.setVisibility(View.GONE);
                aviTyping.hide();
            }
        });
    }

    private boolean isFirstTimeOpened = true;

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        FuguNotificationConfig.pushChannelId = channelId;
        FuguNotificationConfig.pushLabelId = labelId;
        currentChannelId = channelId;

        if (isNetworkAvailable())
            setUpFayeConnection();

        if (CommonData.getPushBoolean() && CommonData.getPushChannel().compareTo(channelId) == 0) {
            allMessagesFetched = false;
            pageStart = 1;
            //setConnectionMessage(1);
            //apiGetMessages(null, true);
            getMessages(null);
        } else if (!isFirstTimeOpened && isNetworkAvailable()) {
            allMessagesFetched = false;
            pageStart = 1;
            //setConnectionMessage(1);
//            progressWheel.setVisibility(View.VISIBLE);
            apiGetMessages(null, true);
            //getMessages(null);
        }
        isFirstTimeOpened = false;
        CommonData.setPushBoolean(false);
        CommonData.clearPushChannel();

        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fuguMessageAdapter.notifyDataSetChanged();
            }
        });*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        FuguNotificationConfig.pushChannelId = -1L;
        FuguNotificationConfig.pushLabelId = -1L;
        CommonData.setPushChannel(channelId);

        if (unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
            CommonData.removeUnsentMessageMapChannel(channelId);
        } else {
            CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
        }

        if(unsentMessages != null && unsentMessages.size() == 0) {
            CommonData.removeUnsentMessageChannel(channelId);
        } else {
            CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
        }

        if (channelId > 0)
            CommonData.setSentMessageByChannel(channelId, sentMessages);

        // Fire stop typing event on Faye before close the chat
        stopTyping();
        stopAnim();
    }


    @Override
    protected void onDestroy() {
        CommonData.clearQuickReplyData();
        FuguLog.e(TAG, "onDestroy");
        try {
            if (unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                CommonData.removeUnsentMessageMapChannel(channelId);
            } else {
                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
            }

            if(unsentMessages != null && unsentMessages.size() == 0) {
                CommonData.removeUnsentMessageChannel(channelId);
            } else {
                CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
            }
        } catch (Exception e) {

        }
        if (channelId > 0) {
            FuguLog.e(TAG, "count in onDestroy: " + channelId);
            CommonData.setSentMessageByChannel(channelId, sentMessages);
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverChat);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(nullListenerReceiver);

            updateUnreadCount(channelId, labelId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messagesApi.clear();
        super.onDestroy();
        try {
            // Fire stop typing event on Faye before closing the chat
            stopTyping();
            stopFayeClient();

            FuguNotificationConfig.pushChannelId = -1L;
            FuguNotificationConfig.pushLabelId = -1L;
            currentChannelId = -1L;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopFayeClient() {
        try {
            HandlerThread thread = new HandlerThread("TerminateThread");
            thread.start();
            new Handler(thread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mClient.isConnectedServer()) {
                        mClient.disconnectServer();
                        mClient.setListener(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            if (!isNetworkAvailable() || fayeDisconnect) {
                try {
                    connectAgainToServer();
                } catch (Exception e) {

                }
                return;
            }
            if (!etMsg.getText().toString().trim().isEmpty()) {
                if (channelId.compareTo(-1L) > 0) {
                    removeItemAndUpdateUI();
                    sendMessage(etMsg.getText().toString().trim(), TEXT_MESSAGE, "", "", null, null);
                } else {
                    if (mClient.isConnectedServer()) {
                        if (!isConversationCreated) {
                            conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                            FuguLog.v("call createConversation", "onClick");
                            createConversation(TEXT_MESSAGE, "", "", null, isP2P);
                        }
                    } else {
                        mClient.connectServer();
                        Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (v.getId() == R.id.ivAttachment) {
            if (isNetworkAvailable()) {
                selectImage();
            } else {
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.tvNoInternet) {
            if (isP2P) {
                pbPeerChat.setVisibility(View.VISIBLE);
                tvNoInternet.setVisibility(View.GONE);
                FuguLog.v("call createConversation", "onClick no internet");
                createConversation(TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, isP2P);
            } else if (conversation.isOpenChat() && conversation.getLabelId().compareTo(-1L) != 0) {
                FuguLog.e(TAG, "onClick: " + CommonData.getConversationList().get(0).getLabelId());
                tvNoInternet.setVisibility(View.GONE);
                getByLabelId();
            } else {
                pageStart = 1;
                tvNoInternet.setVisibility(View.GONE);
                getMessages(null);
            }
        }
    }

    private void createConversation(JSONObject jsonObject) {
        createConversation(IMAGE_MESSAGE, "", "", null, isP2P, jsonObject);
    }

    private void createConversation(final int messageType, final String url, final String thumbnailUrl,
                                    final FuguFileDetails fileDetails, final boolean isP2P) {
        createConversation(messageType, url, thumbnailUrl, fileDetails, isP2P, null);
    }

    private void createConversation(final int messageType, final String url, final String thumbnailUrl,
                                    final FuguFileDetails fileDetails, final boolean isP2P, final JSONObject jsonObject) {
        if (isNetworkAvailable()) {
            FuguLog.d("userName in SDK", "createConversation " + isP2P);
            FuguLog.d("Chat type in SDK", "chat type value " + fuguCreateConversationParams.toString());

            ArrayList<String> groupingTags = new ArrayList<>();
            if (fuguCreateConversationParams.getGroupingTags() != null && fuguCreateConversationParams.getGroupingTags().size() > 0)
                groupingTags.addAll(fuguCreateConversationParams.getGroupingTags());

            try {
                if (CommonData.getUserDetails() != null && CommonData.getUserDetails().getData() != null
                        && CommonData.getUserDetails().getData().getGroupingTags() != null
                        && CommonData.getUserDetails().getData().getGroupingTags().size() > 0) {
                    for (com.fugu.model.GroupingTag groupingTag : CommonData.getUserDetails().getData().getGroupingTags()) {
                        groupingTags.add(groupingTag.getTagName());
                    }
                }
            } catch (Exception e) {

            }
            fuguCreateConversationParams.setGroupingTags(groupingTags);

            pbSendingImage.setVisibility(View.VISIBLE);
            ivSend.setVisibility(View.VISIBLE);

            RestClient.getApiInterface().createConversation(fuguCreateConversationParams)
                    .enqueue(new ResponseResolver<FuguCreateConversationResponse>(FuguChatActivity.this, false, false) {
                        @Override
                        public void success(FuguCreateConversationResponse fuguCreateConversationResponse) {
                            try {
                                globalUuid = UUID.randomUUID().toString();
                                isConversationCreated = true;
                                isFayeChannelActive = true;
                                channelId = fuguCreateConversationResponse.getData().getChannelId();
                                try {
                                    transactionIdsMap.put(fuguCreateConversationParams.getTransactionId(),
                                            fuguCreateConversationResponse.getData().getChannelId());
                                } catch (Exception e) {

                                }
                                if (transactionIdsMap != null)
                                    CommonData.setTransactionIdsMap(transactionIdsMap);
                                FuguNotificationConfig.pushChannelId = channelId;
                                currentChannelId = channelId;

                                FuguLog.v("channelId in createConversation is", "--> " + fuguCreateConversationResponse.getData().getChannelId());
                                mClient.subscribeChannel("/" + String.valueOf(channelId));
                                mClient.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

                                isTyping = TYPING_SHOW_MESSAGE;

                                pbSendingImage.setVisibility(View.GONE);
                                ivSend.setVisibility(View.VISIBLE);

                                if (messageType == TEXT_MESSAGE && !TextUtils.isEmpty(etMsg.getText().toString().trim())) {
                                    sendMessage(etMsg.getText().toString().trim(), TEXT_MESSAGE, "", "", null, null);

//                                    publishOnFaye(etMsg.getText().toString().trim(), TEXT_MESSAGE,
//                                            getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, globalUuid);
                                } else if (messageType == IMAGE_MESSAGE && jsonObject != null) {
                                    sendMessages();
                                } else if (messageType == IMAGE_MESSAGE) {
                                    publishOnFaye(getString(R.string.fugu_empty), messageType, url,
                                            thumbnailUrl, null, NOTIFICATION_DEFAULT, globalUuid);
                                } else if (messageType == FILE_MESSAGE) {
                                    publishOnFaye(getString(R.string.fugu_empty), messageType, url,
                                            thumbnailUrl, fileDetails, NOTIFICATION_DEFAULT, globalUuid);
                                }

                                FuguGetMessageResponse fuguGetMessageResponse = new FuguGetMessageResponse();

                                fuguGetMessageResponse.setStatusCode(fuguCreateConversationResponse.getStatusCode());
                                fuguGetMessageResponse.setMessage(fuguCreateConversationResponse.getMessage());

                                FuguGetMessageResponse.Data data = fuguGetMessageResponse.getData();


                                data.getMessages().add((new Message(businessName, -1L, defaultMessage, "", false,
                                        MESSAGE_SENT, 0, TEXT_MESSAGE, globalUuid)));

                                if (isP2P)
                                    label = fuguCreateConversationResponse.getData().getlabel();
                                data.setLabel(label);
                                data.setFullName(userName);
                                data.setOnSubscribe(onSubscribe);
                                data.setPageSize(25);
                                data.setChannelId(channelId);
                                data.setStatus(STATUS_CHANNEL_OPEN);
                                data.setBusinessName(businessName);

                                fuguGetMessageResponse.setData(data);

                                FuguLog.v("set data is", "--> " + fuguGetMessageResponse.getData().getChannelId());

                                /*if (conversation.isOpenChat()) {
                                    FuguLog.v("createConversation isOpenChat", "--> " + conversation.isOpenChat());
                                    CommonData.setLabelIdResponse(channelId, fuguGetMessageResponse);
                                } else {
                                    FuguLog.v("createConversation isOpenChat not", "--> " + conversation.isOpenChat());
                                    CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                                }*/

                                CommonData.setMessageResponse(channelId, fuguGetMessageResponse);

                                if (isP2P) {
                                    setRecyclerViewData();
                                    llRoot.setVisibility(View.VISIBLE);
                                    getMessages(fuguCreateConversationResponse.getData().getlabel());
                                } else {
                                    getUnreadCount();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (isP2P) {
                                    tvNoInternet.setVisibility(View.VISIBLE);
                                    tvNoInternet.setText(error.getMessage());
                                } else {
                                    Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                                }
                            }

                            pbSendingImage.setVisibility(View.GONE);
                            ivSend.setVisibility(View.VISIBLE);
                        }
                    });
        } else {

            if (isP2P) {
                tvNoInternet.setVisibility(View.VISIBLE);
                pbPeerChat.setVisibility(View.GONE);
            } else {
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
            }
            pbSendingImage.setVisibility(View.GONE);
            ivSend.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecycler() {
        if (fuguMessageAdapter != null) {

            fuguMessageAdapter.updateList(fuguMessageList);
            //fuguMessageAdapter.notifyDataSetChanged();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fuguMessageAdapter != null) {
                        try {
//                            fuguMessageAdapter.notifyDataSetChanged();
                            fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                            if (fuguMessageAdapter.getItemCount() > 0) {
                                rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMessages(final String channelName) {
        FuguLog.d("userName in SDK", "getMessages before call" + userName);
        if (isNetworkAvailable()) {
            if ((FuguConfig.getInstance().getUserData() == null)
                    || (TextUtils.isEmpty(FuguConfig.getInstance().getAppKey()))) {
                new ApiPutUserDetails(FuguChatActivity.this, new ApiPutUserDetails.Callback() {
                    @Override
                    public void onSuccess() {
                        apiGetMessages(channelName);
                    }

                    @Override
                    public void onFailure() {

                    }
                }).sendUserDetails(FuguConfig.getmResellerToken(), FuguConfig.getmReferenceId());
            } else {
                apiGetMessages(channelName);
            }
        } else {
            if (pageStart == 1 &&
                    (CommonData.getMessageResponse(channelId) == null ||
                            CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            if (isP2P) {
                tvNoInternet.setVisibility(View.VISIBLE);
                pbPeerChat.setVisibility(View.GONE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    boolean isApiRunning;
    private HashSet<String> messagesApi = new HashSet<>();

    private void apiGetMessages(String channelName) {
        apiGetMessages(channelName, false);
    }

    private void apiGetMessages(final String channelName, final boolean isFromOnResume) {
        if (channelId < 0 || isApiRunning) {
            messagesApi.add("getMessages");
            return;
        }
        showLoading = false;
        if (!allMessagesFetched || isNetworkStateChanged) {
            FuguGetMessageParams commonParams = new FuguGetMessageParams(FuguConfig.getInstance().getAppKey(),
                    channelId,
                    enUserId,
                    pageStart,
                    channelName);

            if (isFromOnResume && fuguMessageList.size() > 100)
                commonParams.setPageEnd(fuguMessageList.size() - dateItemCount);

            if(sentMessages == null || sentMessages.size()==0) {
                showLoading = true;
                setConnectionMessage(0);
            } else if (pageStart == 1) {
                //if(!isFromOnResume) {
                    setConnectionMessage(1);
                //}
            }

            isApiRunning = true;
            final int localPageSize = pageStart;

            RestClient.getApiInterface().getMessages(commonParams).enqueue(new ResponseResolver<FuguGetMessageResponse>(FuguChatActivity.this, showLoading, false) {
                @Override
                public void success(FuguGetMessageResponse fuguGetMessageResponse) {
                    isApiRunning = false;
                    if(messagesApi != null && messagesApi.size()>0) {
                        messagesApi.remove("getMessages");
                        isNetworkStateChanged = true;
                        apiGetMessages(channelName, isFromOnResume);
                    }

                    mFuguGetMessageResponse = fuguGetMessageResponse;
//                    if(!isFromOnResume)
//                        LoadingBox.showOn(FuguChatActivity.this);
                    FuguLog.e("size of list--->", fuguGetMessageResponse.getData().getMessages().size() + "");
                    if (isP2P) {
                        pbPeerChat.setVisibility(View.GONE);
                        cvTypeMessage.setVisibility(View.VISIBLE);
                        llMessageLayout.setVisibility(View.VISIBLE);
                    }
                    label = fuguGetMessageResponse.getData().getLabel();
                    setToolbar(myToolbar, label);
                    if (fuguGetMessageResponse.getData().isDisableReply()) {
                        llMessageLayout.setVisibility(View.GONE);
                    }
                    if (fuguGetMessageResponse.getData().getMessages() != null) {

                        LinkedHashMap<String, ListItem> tempMessages = new LinkedHashMap<>();
                        LinkedHashMap<String, ListItem> tempSentMessages = new LinkedHashMap<>();

                        String tempSentAtUtc = "";

                        if (localPageSize == 1) {
                            tempSentMessages.putAll(sentMessages);
                            fuguMessageList.clear();
                            sentMessages.clear();
                            dateItemCount = 0;
                            FuguLog.e(TAG, "This is a first page");
                        } else {
                            FuguLog.e(TAG, "No first page");
                        }

                        FuguGetMessageResponse.Data messageResponseData = fuguGetMessageResponse.getData();
                        if (messageResponseData.getMessages().size() < messageResponseData.getPageSize()) {
                            allMessagesFetched = true;
                        } else {
                            allMessagesFetched = false;
                        }
                        onSubscribe = messageResponseData.getOnSubscribe();
                        FuguLog.e("getMessages onSubscribe", "==" + onSubscribe);

                        int dateCount = 0;
                        for (int i = 0; i < messageResponseData.getMessages().size(); i++) {
                            Message messageObj = messageResponseData.getMessages().get(i);
                            boolean isSelf = false;
                            if (messageObj.getUserId().compareTo(userId) == 0)
                                isSelf = true;

                            String localDate = dateUtils.convertToLocal(messageObj.getSentAtUtc(), inputFormat, outputFormat);

                            if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                tempMessages.put(localDate, new HeaderItem(localDate));
                                tempSentAtUtc = localDate;
                                dateItemCount = dateItemCount + 1;
                                dateCount = dateCount + 1;
                            }

                            String muid = messageObj.getMuid();
                            if (TextUtils.isEmpty(muid))
                                muid = TextUtils.isEmpty(String.valueOf(messageObj.getId()))
                                        ? UUID.randomUUID().toString() : String.valueOf(messageObj.getId());

                            Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                    messageObj.getUserId(),
                                    messageObj.getMessage(),
                                    messageObj.getSentAtUtc(),
                                    isSelf,
                                    messageObj.getMessageStatus(),
                                    i,
                                    messageObj.getUrl(),
                                    messageObj.getThumbnailUrl(),
                                    messageObj.getMessageType(),
                                    messageObj.getMuid());

                            if (messageObj.getCustomAction() != null) {
                                message.setCustomAction(messageObj.getCustomAction());
                            }

                            if (messageObj.getMessageType() == FILE_MESSAGE) {
                                message.setFileExtension(messageObj.getFileExtension());
                                message.setFileName(messageObj.getFileName());
                                message.setFileSize(messageObj.getFileSize());
                                message.setFilePath(messageObj.getFilePath());
                            } else if (messageObj.getMessageType() == FEEDBACK_MESSAGE) {
                                message.setIsRatingGiven(messageObj.getIsRatingGiven());
                                message.setTotalRating(messageObj.getTotalRating());
                                message.setRatingGiven(messageObj.getRatingGiven());
                                message.setComment(messageObj.getComment());
                                message.setLineBeforeFeedback(messageObj.getLineBeforeFeedback());
                                message.setLineAfterFeedback_1(messageObj.getLineAfterFeedback_1());
                                message.setLineAfterFeedback_2(messageObj.getLineAfterFeedback_2());
                            } else if (messageObj.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                                message.setContentValue(messageObj.getContentValue());
                                message.setDefaultActionId(messageObj.getDefaultActionId());
                                message.setValues(messageObj.getValues());
                                if (messageObj.getValues() != null && messageObj.getValues().size() > 0)
                                    continue;
                            } else if (messageObj.getMessageType() == FUGU_FORUM_VIEW) {
                                message.setContentValue(messageObj.getContentValue());
                                message.setValues(messageObj.getValues());
                                message.setId(messageObj.getId());
                            }

                            tempMessages.put(muid, new EventItem(message));
                            tempSentMessages.remove(muid);

                            if (!TextUtils.isEmpty(messageObj.getMuid())) {
                                if(unsentMessageMapNew.size() > 0) {
                                    unsentMessageMapNew.remove(messageObj.getMuid());
                                }
                                if(unsentMessages.size() > 0) {
                                    unsentMessages.remove(messageObj.getMuid());
                                }
                            }
                        }

                        if (pageStart == 1) {
                            fuguMessageList.clear();
                        }

                        if (sentMessages.containsKey(tempSentAtUtc)) {
                            sentMessages.remove(tempSentAtUtc);
                            dateItemCount = dateItemCount - 1;
                            dateCount = dateCount - 1;
                        }

                        tempMessages.putAll(sentMessages);
                        sentMessages.clear();
                        sentMessages.putAll(tempMessages);

                        // put local sent messages into updated sent list
                        if (tempSentMessages.values().size() > 0) {
                            long lastMessageTime = dateUtils.getTimeInLong(messageResponseData.getMessages()
                                    .get(messageResponseData.getMessages().size() - 1).getSentAtUtc());
                            if (lastMessageTime > 0) {
                                for (String key : tempSentMessages.keySet()) {
                                    try {
                                        if (tempSentMessages.get(key) instanceof EventItem) {
                                            ListItem listItem = tempSentMessages.get(key);
                                            long localMessageTime = dateUtils.getTimeInLong(((EventItem) listItem).getEvent().getSentAtUtc());
                                            FuguLog.i(TAG, "localMessageTime: " + localMessageTime+" > "+lastMessageTime);
                                            if (localMessageTime > lastMessageTime) {
                                                FuguLog.e(TAG, "localMessageTime: " + localMessageTime+" > "+lastMessageTime);
                                                sentMessages.put(((EventItem) listItem).getEvent().getMuid(), listItem);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        tempSentMessages.clear();

                        fuguMessageList = new ArrayList<>();
                        fuguMessageList.addAll(sentMessages.values());

                        for (String key : unsentMessages.keySet()) {
                            ListItem listItem = unsentMessages.get(key);
                            String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                            String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                            if (!sentMessages.containsKey(localDate)) {
                                if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                    fuguMessageList.add(new HeaderItem(localDate));
                                    tempSentAtUtc = localDate;
                                    System.out.println("Date 2: " + localDate);
                                }
                            }
                            if(unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                                CommonData.removeUnsentMessageMapChannel(channelId);
                            } else {
                                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMapNew);
                            }


                            if (unsentMessages != null && unsentMessages.size() == 0) {
                                CommonData.removeUnsentMessageChannel(channelId);
                            } else {
                                CommonData.setUnsentMessageByChannel(channelId, unsentMessages);
                            }

                            // update
                            try {
                                JSONObject messageJson = unsentMessageMapNew.get(key);
                                if (messageJson != null) {
                                    messageJson.put("message_index", fuguMessageList.size());
                                    unsentMessageMapNew.put(key, messageJson);
                                }
                                fuguMessageList.add(unsentMessages.get(key));
                            } catch (Exception e) {

                            }
                        }

                        tvNoInternet.setVisibility(View.GONE);
                        llRoot.setVisibility(View.VISIBLE);
                        if (conversation.getUnreadCount() > 0) {
                            rvMessages.setAlpha(0);
                        }
                        if (pageStart == 1) {
                            showLoading = false;
                            sentAtUTC = tempSentAtUtc;
                            CommonData.setMessageResponse(channelId, fuguGetMessageResponse);

//                            setRecyclerViewData1();

                            updateRecycler();
                            scrollListToBottom();
//                            fuguMessageAdapter.updateList(fuguMessageList);
//                            fuguMessageAdapter.notifyDataSetChanged();
//                            rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fuguMessageAdapter.notifyDataSetChanged();
                                    rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                }
                            });*/
                        } else {
                            pbLoading.setVisibility(View.GONE);
                            fuguMessageAdapter.updateList(fuguMessageList, false);
                            fuguMessageAdapter.notifyItemRangeInserted(0, messageResponseData.getMessages().size() + dateCount);
                        }
                        pageStart = sentMessages.values().size() + 1 - dateItemCount;
                    } else {
                        allMessagesFetched = true;
                    }
                    pbLoading.setVisibility(View.GONE);
                    isP2P = false;
//                    if(!isFromOnResume)
//                        LoadingBox.hide();
                    //if(!isFromOnResume)
                        setConnectionMessage(0);
                    getUnreadCount();
                }

                @Override
                public void failure(APIError error) {
                    setConnectionMessage(0);
                    isApiRunning = false;
                    if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                        Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        pbLoading.setVisibility(View.GONE);
                        if(messagesApi != null && messagesApi.size()>0) {
                            messagesApi.remove("getMessages");
                            isNetworkStateChanged = true;
                            apiGetMessages(channelName, isFromOnResume);
                        } else if(isP2P) {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            pbPeerChat.setVisibility(View.GONE);
                        } else if (pageStart == 1 &&
                                (CommonData.getMessageResponse(channelId) == null ||
                                        CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                            llRoot.setVisibility(View.GONE);
                            tvNoInternet.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    private void getByLabelId() {
        if (isNetworkAvailable()) {
            CommonData.clearQuickReplyData();
            if (!allMessagesFetched) {
                FuguGetByLabelIdParams commonParams = new FuguGetByLabelIdParams(FuguConfig.getInstance().getAppKey(),
                        labelId,
                        enUserId,
                        pageStart);

                if(sentMessages == null || sentMessages.size()==0) {
                    showLoading = true;
                    setConnectionMessage(0);
                } else if (pageStart == 1) {
                    setConnectionMessage(1);
                }

                RestClient.getApiInterface().getByLabelId(commonParams)
                        .enqueue(new ResponseResolver<FuguGetMessageResponse>(FuguChatActivity.this, showLoading, false) {
                            @Override
                            public void success(FuguGetMessageResponse fuguGetMessageResponse) {
                                if (pageStart == 1) {
                                    fuguMessageList.clear();
                                    dateItemCount = 0;
                                }
                                if (!TextUtils.isEmpty(fuguGetMessageResponse.getData().getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                } else if (!TextUtils.isEmpty(conversation.getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                }

                                setToolbar(myToolbar, label);
                                if (fuguGetMessageResponse.getData().isDisableReply()) {
                                    llMessageLayout.setVisibility(View.GONE);
                                }

                                if (fuguGetMessageResponse.getData() != null && fuguGetMessageResponse.getData().getMessages() != null) {
                                    showLoading = false;

                                    LinkedHashMap<String, ListItem> tempMessages = new LinkedHashMap<>();
                                    LinkedHashMap<String, ListItem> tempSentMessages = new LinkedHashMap<>();

                                    String tempSentAtUtc = sentAtUTC;

                                    FuguGetMessageResponse.Data data = fuguGetMessageResponse.getData();
                                    if (data.getMessages().size() < data.getPageSize()) {
                                        allMessagesFetched = true;
                                    } else {
                                        allMessagesFetched = false;
                                    }


                                    FuguLog.d("userName in SDK", "getByLabelId " + userName);
                                    onSubscribe = data.getOnSubscribe();
                                    channelId = data.getChannelId();
                                    FuguNotificationConfig.pushChannelId = data.getChannelId();
                                    conversation.setChannelId(data.getChannelId());
                                    currentChannelId = data.getChannelId();
                                    status = data.getStatus();
                                    businessName = data.getBusinessName();

                                    FuguLog.e("getByLabelId onSubscribe", "==" + onSubscribe);

                                    if (status == STATUS_CHANNEL_CLOSED) {
                                        cvTypeMessage.setVisibility(View.GONE);
                                        llMessageLayout.setVisibility(View.GONE);
                                        tvClosed.setVisibility(View.VISIBLE);
                                    }
                                    for (int i = 0; i < data.getMessages().size(); i++) {
                                        Message messageObj = data.getMessages().get(i);
                                        boolean isSelf = false;
                                        if (messageObj.getUserId().compareTo(userId) == 0)
                                            isSelf = true;

                                        String localDate = dateUtils.convertToLocal(messageObj.getSentAtUtc(), inputFormat, outputFormat);

                                        if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                            tempMessages.put(localDate, new HeaderItem(localDate));
                                            tempSentAtUtc = localDate;
                                            dateItemCount = dateItemCount + 1;
                                        }

                                        String muid = messageObj.getMuid();
                                        if (TextUtils.isEmpty(muid))
                                            muid = UUID.randomUUID().toString();

                                        Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                                messageObj.getUserId(),
                                                messageObj.getMessage(),
                                                messageObj.getSentAtUtc(),
                                                isSelf,
                                                messageObj.getMessageStatus(),
                                                i,
                                                messageObj.getUrl(),
                                                messageObj.getThumbnailUrl(),
                                                messageObj.getMessageType(),
                                                messageObj.getMuid());

                                        if (messageObj.getCustomAction() != null) {
                                            message.setCustomAction(messageObj.getCustomAction());
                                        }

                                        if (messageObj.getMessageType() == FILE_MESSAGE) {
                                            message.setFileExtension(messageObj.getFileExtension());
                                            message.setFileName(messageObj.getFileName());
                                            message.setFileSize(messageObj.getFileSize());
                                            message.setFilePath(messageObj.getFilePath());
                                        } else if (messageObj.getMessageType() == FEEDBACK_MESSAGE) {
                                            message.setIsRatingGiven(messageObj.getIsRatingGiven());
                                            message.setTotalRating(messageObj.getTotalRating());
                                            message.setRatingGiven(messageObj.getRatingGiven());
                                            message.setComment(messageObj.getComment());
                                            message.setLineBeforeFeedback(messageObj.getLineBeforeFeedback());
                                            message.setLineAfterFeedback_1(messageObj.getLineAfterFeedback_1());
                                            message.setLineAfterFeedback_2(messageObj.getLineAfterFeedback_2());
                                        } else if (messageObj.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                                            message.setContentValue(messageObj.getContentValue());
                                            message.setDefaultActionId(messageObj.getDefaultActionId());
                                            message.setValues(messageObj.getValues());
                                            if (messageObj.getValues() != null && messageObj.getValues().size() > 0)
                                                continue;
                                        } else if (messageObj.getMessageType() == FUGU_FORUM_VIEW) {
                                            message.setContentValue(messageObj.getContentValue());
                                            message.setValues(messageObj.getValues());
                                            message.setId(messageObj.getId());
                                        }

                                        tempMessages.put(muid, new EventItem(message));
                                        tempSentMessages.remove(muid);

                                        if (!TextUtils.isEmpty(messageObj.getMuid()) && unsentMessageMapNew.size() > 0) {
                                            unsentMessageMapNew.remove(messageObj.getMuid());
                                            unsentMessages.remove(messageObj.getMuid());
                                        }
                                    }

                                    if (sentMessages.containsKey(tempSentAtUtc)) {
                                        sentMessages.remove(tempSentAtUtc);
                                        dateItemCount = dateItemCount - 1;
                                    }

                                    tempMessages.putAll(sentMessages);
                                    sentMessages.clear();
                                    sentMessages.putAll(tempMessages);

                                    // put local sent messages into updated sent list
                                    if (tempSentMessages.values().size() > 0) {
                                        long lastMessageTime = dateUtils.getTimeInLong(data.getMessages().get(data.getMessages().size() - 1).getSentAtUtc());
                                        if (lastMessageTime > 0) {
                                            for (String key : tempSentMessages.keySet()) {
                                                try {
                                                    if (tempSentMessages.get(key) instanceof EventItem) {
                                                        ListItem listItem = tempSentMessages.get(key);
                                                        long localMessageTime = dateUtils.getTimeInLong(((EventItem) listItem).getEvent().getSentAtUtc());
                                                        Log.i(TAG, "localMessageTime: " + localMessageTime);
                                                        if (localMessageTime > lastMessageTime) {
                                                            sentMessages.put(((EventItem) listItem).getEvent().getMuid(), listItem);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    tempSentMessages.clear();

                                    fuguMessageList = new ArrayList<>();
                                    fuguMessageList.addAll(sentMessages.values());

                                    for (String key : unsentMessages.keySet()) {
                                        ListItem listItem = unsentMessages.get(key);
                                        String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                                        String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                                        //String date = dateUtils.getDate();
                                        if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                            fuguMessageList.add(new HeaderItem(localDate));
                                            tempSentAtUtc = localDate;
                                            System.out.println("Date 3: " + localDate);
                                        }

                                        // update
                                        try {
                                            JSONObject messageJson = unsentMessageMapNew.get(key);
                                            if (messageJson != null) {
                                                messageJson.put("message_index", fuguMessageList.size());
                                                unsentMessageMapNew.put(key, messageJson);
                                                fuguMessageList.add(unsentMessages.get(key));
                                            } else {
                                                fuguMessageList.remove(key);
                                            }
                                        } catch (JSONException e) {

                                        }
                                    }

                                    tvNoInternet.setVisibility(View.GONE);
                                    llRoot.setVisibility(View.VISIBLE);
                                    if (conversation.getUnreadCount() > 0) {
                                        rvMessages.setAlpha(0);
                                    }
                                    if (pageStart == 1) {
                                        showLoading = false;
                                        sentAtUTC = tempSentAtUtc;
                                        CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                                        fuguMessageAdapter.updateList(fuguMessageList);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fuguMessageAdapter.notifyDataSetChanged();
                                                rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                            }
                                        });
//                                        fuguMessageAdapter.notifyDataSetChanged();
//                                        rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                    } else {
                                        pbLoading.setVisibility(View.GONE);
                                        fuguMessageAdapter.notifyItemRangeInserted(0, data.getMessages().size());
                                    }
                                    pageStart = fuguMessageList.size() + 1 - dateItemCount;
                                } else {
                                    allMessagesFetched = true;
                                    fuguMessageList.clear();
                                }
                                pbLoading.setVisibility(View.GONE);
                                getUnreadCount();
                            }

                            @Override
                            public void failure(APIError error) {
                                FuguLog.e("error type", error.getType() + "");
                                if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                    Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if ((error.getStatusCode() == DATA_UNAVAILABLE && error.getType() == 1)) {
                                    FuguConfig.getInstance().openChatByTransactionId("7865",
                                            FuguConfig.getInstance().getUserData().getUserUniqueKey(), "Fugu Default", null);
                                    //Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    //finish();
                                } else {
                                    if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                                            || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                                        llRoot.setVisibility(View.GONE);
                                        tvNoInternet.setVisibility(View.VISIBLE);
                                    }

                                    pbLoading.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else {
            if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                    || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void dateParse(ArrayList<Message> messages, boolean isByLabelId) {

    }

    private void setRecyclerViewData() {
        fuguMessageAdapter.notifyDataSetChanged();
        fuguMessageAdapter.setOnRetryListener(new FuguMessageAdapter.OnRetryListener() {
            @Override
            public void onRetry(String file, int messageIndex, int messageType, FuguFileDetails fileDetails, String muid) {
                uploadFileServerCall(file, "image/*", messageIndex, muid);
            }

            @Override
            public void onMessageRetry(String muid, int position) {
                try {
                    JSONObject jsonObject = unsentMessageMapNew.get(muid);

                    if(jsonObject == null) {
                        return;
                    }
                    ListItem listItem = unsentMessages.get(muid);
//                    Message currentOrderItem = ((EventItem) listItem).getEvent();
//                    JSONObject messageJson = new JSONObject(new Gson().toJson(currentOrderItem));

                    jsonObject.put("is_message_expired", 0);
                    jsonObject.put("message_index", position);
                    String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                    jsonObject.put("date_time", DateUtils.getInstance().convertToUTC(localDate));

                    ((EventItem) listItem).getEvent().setIsMessageExpired(0);
                    ((EventItem) listItem).getEvent().setMessageIndex(position);
                    ((EventItem) listItem).getEvent().setSentAtUtc(DateUtils.getInstance().convertToUTC(localDate));

                    unsentMessageMapNew.put(muid, jsonObject);
                    unsentMessages.put(muid, listItem);
                    fuguMessageList.remove(position);
                    fuguMessageList.add(position, listItem);

                    updateRecycler();
                    if (fuguMessageAdapter != null) {
                        fuguMessageAdapter.updateList(fuguMessageList);
                    }
                    fuguMessageAdapter.notifyItemRangeChanged(position, fuguMessageList.size());

                    sendMessages();
                } catch (JSONException e) {

                }
            }

            @Override
            public void onMessageCancel(String muid, int position) {
                fuguMessageList.remove(position);
                fuguMessageAdapter.notifyItemRemoved(position);
                boolean isItemFound = false;


                for (String key : unsentMessageMapNew.keySet()) {
                    if (key.equalsIgnoreCase(muid)) {
//                        unsentMessageMapNew.remove(muid);
//                        unsentMessages.remove(muid);
                        isItemFound = true;
                        continue;
                    }
                    if (isItemFound) {
                        try {
                            ListItem listItem = unsentMessages.get(key);
                            int index = ((EventItem) listItem).getEvent().getMessageIndex();
                            ((EventItem) listItem).getEvent().setMessageIndex(index - 1);
                            JSONObject jsonObject = unsentMessageMapNew.get(key);
                            jsonObject.put("message_index", index);
                            unsentMessageMapNew.put(key, jsonObject);
                            unsentMessages.put(key, listItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                unsentMessageMapNew.remove(muid);
                unsentMessages.remove(muid);
            }
        });

        // Add the scroll listener
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (mClient.isConnectedServer()) {
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                    if (unsentMessages.size() == 0) {
                        getMessages(null);
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard(FuguChatActivity.this);
            onBackPressed(); // close this context and return to preview context (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        CommonData.clearQuickReplyData();
        if (mClient.isConnectedServer()) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
            mClient.publish("/" + String.valueOf(channelId), prepareMessageJson(CHANNEL_UNSUBSCRIBED));
        }

        FuguChannelsActivity.isRefresh = true;
        FuguChannelsActivity.readChannelId = channelId;
        FuguChannelsActivity.readLabelId = labelId;

        Intent intent = new Intent();
        if (fuguMessageList.size() > 0) {
            conversation.setChannelId(channelId);
            conversation.setLabelId(labelId);
            conversation.setDefaultMessage(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessage());
            conversation.setDateTime(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getSentAtUtc());
            conversation.setLast_sent_by_id(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getUserId());
            conversation.setLast_message_status(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessageStatus());
            conversation.setMessage_type(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessageType());
            intent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }

        super.onBackPressed();
    }


    /**
     * Method to select an image for Position in
     * the List of AddImages
     */
    public void selectImage() {
        fuguImageUtils.showImageChooser(OPEN_CAMERA_ADD_IMAGE, OPEN_GALLERY_ADD_IMAGE, SELECT_FILE, new FuguImageUtils.ViewOpened() {
            @Override
            public void open() {
                isFirstTimeOpened = true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        FuguLog.e(TAG, "onRequestPermissionsResult" + requestCode);

        try {
            if (FuguConfig.getInstance().getTargetSDKVersion() > 22 &&
                    grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {

                    case PERMISSION_CONSTANT_CAMERA:
                        fuguImageUtils.startCamera();
                        break;

                    case PERMISSION_READ_IMAGE_FILE:
                        compressAndSaveImageBitmap();
                        break;

                    case PERMISSION_CONSTANT_GALLERY:
                        fuguImageUtils.openGallery();
                        break;

                    case PERMISSION_SAVE_BITMAP:
                        compressAndSaveImageBitmap();
                        break;
                    case PERMISSION_READ_FILE:
                        break;
                }
            } else if (FuguConfig.getInstance().getTargetSDKVersion() <= 22 &&
                    grantResults.length > 0
                    && FuguConfig.getInstance().isPermissionGranted(FuguChatActivity.this, Manifest.permission.CAMERA)
                    && FuguConfig.getInstance().isPermissionGranted(FuguChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                switch (requestCode) {

                    case PERMISSION_CONSTANT_CAMERA:
                        fuguImageUtils.startCamera();
                        break;

                    case PERMISSION_READ_IMAGE_FILE:
                        compressAndSaveImageBitmap();
                        break;

                    case PERMISSION_CONSTANT_GALLERY:
                        fuguImageUtils.openGallery();
                        break;

                    case PERMISSION_SAVE_BITMAP:
                        compressAndSaveImageBitmap();
                        break;
                    case PERMISSION_READ_FILE:

                        break;
                }
            } else {
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_permission_was_not_granted_text), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case OPEN_CAMERA_ADD_IMAGE:
                    compressAndSaveImageBitmap();
                    break;

                case OPEN_GALLERY_ADD_IMAGE:
                    try {
                        fuguImageUtils.copyFileFromUri(data.getData());
                        compressAndSaveImageBitmap();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Problem in fetching image...", Toast.LENGTH_LONG).show();
                    }
                    break;

                case SELECT_FILE:
                    FuguFileDetails fileDetails = fuguImageUtils.saveFile(data.getData(), FileType.PDF_FILE);
                    if (fileDetails.getFilePath().isEmpty()) {
                        Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show();
                    } else {
                        FuguLog.e(TAG, "file path" + fileDetails.getFilePath());
                        globalUuid = UUID.randomUUID().toString();
//                        addMessageToList(getString(R.string.fugu_empty), FILE_MESSAGE,
//                                fileDetails.getFilePath(), "", fileDetails, globalUuid);
                    }

                    break;
            }
    }

    private void compressAndSaveImageBitmap() {
        try {
            String image = fuguImageUtils.compressAndSaveBitmap(FuguChatActivity.this);//(null, squareEdge);
            if (image == null) {
                Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (conversation.getChannelId() < 0) {
                    ivSend.setVisibility(View.GONE);
                }
                sendMessage(getString(R.string.fugu_empty), IMAGE_MESSAGE, "", "", image, null);
                //addMessageToList(getString(R.string.empty), IMAGE_MESSAGE, image, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
        }
    }
    /*private void compressAndSaveImageBitmap() {
        try {
            String image = fuguImageUtils.compressAndSaveBitmap(FuguChatActivity.this);//(null, squareEdge);
            if (image == null) {
                Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
            } else {
                globalUuid = UUID.randomUUID().toString();
//                addMessageToList(getString(R.string.fugu_empty), IMAGE_MESSAGE, image, image, null, globalUuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
        }
    }*/


    @Override
    public void onAnimationStart(Animation animation) {
        if (!(tvDateLabel.getVisibility() == View.VISIBLE)) {
            tvDateLabel.clearAnimation();
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * Called when a custom action button is clicked
     *
     * @param buttonAction the action button object associated with this button
     */
    public void onCustomActionClicked(final Object buttonAction) {
        //send a broadcast to listening parent app
        String payload = new Gson().toJson(buttonAction);
        Intent intent = new Intent();
        intent.putExtra(FUGU_CUSTOM_ACTION_PAYLOAD, payload);
        intent.setAction(FUGU_CUSTOM_ACTION_SELECTED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    private void getUnreadCount() {
//        try {
//            if(!FuguConfig.getInstance().isChannelActivity()) {
//                FuguConfig.getInstance().getUnreadCount();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onSubmitRating(String text, Message currentOrderItem, int position) {
        try {
            sendFeedbackData(currentOrderItem, position);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        new CustomAlertDialog.Builder(FuguChatActivity.this)
                .setMessage("Error in publishing message to faye")
                .setPositiveButton("Ok", null)
                .show();
    }

    @Override
    public void onRatingSelected(int rating, Message currentOrderItem) {
        currentOrderItem.setRatingGiven(rating);
    }

    @Override
    public void onFormDataCallback(Message currentOrderItem) {
        try {
            sendFormData(currentOrderItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFeedbackData(Message currentOrderItem, int position) throws Exception {
        if (isNetworkAvailable()) {
            currentOrderItem.setIsRatingGiven(1);
            currentOrderItem.setTotalRating(5);
            currentOrderItem.setUserId(CommonData.getUserDetails().getData().getUserId());
            JSONObject messageJson = new JSONObject(new Gson().toJson(currentOrderItem));
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            FuguLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            mClient.setListener(this);
            if (mClient.isConnectedServer()) {
                mClient.publish("/" + String.valueOf(channelId), messageJson);
            } else {
                mClient.connectServer();
            }
        }
    }

    private void sendFormData(Message message) throws Exception {
        if (isNetworkAvailable()) {

            List<String> arrayList = new ArrayList<>();
            if (message.getValues() != null)
                arrayList.addAll(message.getValues());
            arrayList.add(message.getComment());

            message.setValues((ArrayList<String>) arrayList);
            message.setComment("");

            message.setUserId(CommonData.getUserDetails().getData().getUserId());
            message.setMessageId(message.getId());

            JSONObject messageJson = new JSONObject(new Gson().toJson(message));
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            FuguLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            mClient.setListener(this);
            if (mClient.isConnectedServer()) {
                mClient.publish("/" + String.valueOf(channelId), messageJson);
            } else {
                mClient.connectServer();
            }
        }
    }

    private void sendQuickReply(Message message, int position, String defaultActionId) throws Exception {

        if (isNetworkAvailable()) {
            List<String> arrayList = new ArrayList<>();
            arrayList.add(defaultActionId);
            message.setValues((ArrayList<String>) arrayList);

            message.setUserId(CommonData.getUserDetails().getData().getUserId());
            if (message.getId() > 0) {
                message.setMessageId(message.getId());
            }

            JSONObject messageJson = new JSONObject(new Gson().toJson(message));
            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put(MESSAGE_TYPE, FUGU_QUICK_REPLY_VIEW);
            messageJson.put(USER_TYPE, ANDROID_USER);
            FuguLog.d("userName in SDK", "currentOrderItem " + new Gson().toJson(messageJson));
            mClient.setListener(this);
            if (mClient.isConnectedServer()) {
                mClient.publish("/" + String.valueOf(channelId), messageJson);
            } else {
                mClient.connectServer();
            }
        }
    }


    @Override
    public void QuickReplyListener(Message message, int pos) {
        try {
            if (channelId.compareTo(-1L) > 0) {
                fuguMessageList.remove(new EventItem(message));
                conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                isTyping = TYPING_SHOW_MESSAGE;
                globalUuid = UUID.randomUUID().toString();
                publishOnFaye(message.getContentValue().get(pos).getButtonTitle(), TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty),
                        null, NOTIFICATION_DEFAULT, globalUuid);
                //unsentMessageMap.put(globalUuid, fuguMessageList.get(fuguMessageList.size() - 1));

            } else {
                if (mClient.isConnectedServer()) {
                    if (!isConversationCreated) {
                        conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                        FuguLog.v("call createConversation", "onClick");
                        createConversation(TEXT_MESSAGE, "", "", null, isP2P);
                    }
                } else {
                    mClient.setListener(this);
                    mClient.connectServer();
                    Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                }
            }

            CommonData.clearQuickReplyData();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            sendQuickReply(message, pos, message.getContentValue().get(pos).getActionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendActionId(Message event) {
        try {
            sendQuickReply(event, position, event.getDefaultActionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedMessage(FayeClient fc, String msg, String channel) {

        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            if (!taskList.get(0).topActivity.getClassName().equals("com.fugu.activity.FuguChatActivity")) {
                FuguLog.e(TAG, "FuguChatActivity false");
                return;
            }
        } catch (Exception e) {
            if (FuguConfig.DEBUG)
                e.printStackTrace();
        }

        FuguLog.e(TAG, "FuguMessage: " + msg);
        FuguLog.e(TAG, "channel" + channel);

        boolean isSelf = false;
        try {
            final JSONObject messageJson = new JSONObject(msg);

            try {
                if (messageJson.optInt(MESSAGE_TYPE, 0) == 1 && TextUtils.isEmpty(messageJson.optString("message"))
                        && ! messageJson.has("is_typing")) {
                    return;
                }
                if(retryLayout.getVisibility() == View.VISIBLE) {
                    setConnectionMessage(0);
                }
            } catch (Exception e) {

            }
            if ((messageJson.optInt(IS_TYPING) == TYPING_STOPPED) && !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                FuguLog.v("onReceivedMessage", "in elseIf stopAnim");
                stopAnim();
            }
            if (!String.valueOf(messageJson.optString("user_id")).equals(String.valueOf(userId)) && messageJson.has("on_subscribe")) {
                onSubscribe = messageJson.getInt("on_subscribe");
                FuguLog.e("onReceivedMessage onSubscribe", "==" + onSubscribe);
            }
            try {
                if (messageJson.optInt(MESSAGE_TYPE, 0) == 14) {
                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == ITEM_TYPE_RATING) {
                            Message currentMessage = ((EventItem) fuguMessageList.get(i)).getEvent();
                            if (!TextUtils.isEmpty(currentMessage.getMuid()) && currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                currentMessage.setRatingGiven(messageJson.getInt("rating_given"));
                                currentMessage.setComment(messageJson.getString("comment"));
                                currentMessage.setIsRatingGiven(messageJson.getInt("is_rating_given"));
                                currentMessage.setTotalRating(messageJson.getInt("total_rating"));

                                currentMessage.setLineBeforeFeedback(messageJson.getString("line_before_feedback"));
                                currentMessage.setLineAfterFeedback_1(messageJson.getString("line_after_feedback_1"));
                                currentMessage.setLineAfterFeedback_2(messageJson.getString("line_after_feedback_2"));

                                updateFeedback(i);
                                removeItemAndUpdateUI();
                                return;
                            }
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_VIEW && !messageJson.has("id")) {
                    return;
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_FORUM_VIEW) {
                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == FUGU_FORUM_VIEW) {
                            Message currentMessage = ((EventItem) fuguMessageList.get(i)).getEvent();
                            if (currentMessage.getId() == messageJson.getLong("id")) {
                                //currentMessage.getValues()

                                ArrayList<String> values = new ArrayList<>();
                                JSONArray valuesArray = messageJson.getJSONArray("values");

                                if (valuesArray != null) {
                                    for (int b = 0; b < valuesArray.length(); b++) {
                                        values.add(valuesArray.getString(b));
                                    }
                                }
                                currentMessage.setValues(values);
                                //updateFeedback(i);
                                if (values != null) {
                                    updateFeedback(i, false);
                                    return;
                                }
                                removeItemAndUpdateUI();
                                return;
                            }
                        }
                    }
                } else if (messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_QUICK_REPLY_VIEW) {
                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i) instanceof EventItem) {
                            Message currentMessage = ((EventItem) fuguMessageList.get(i)).getEvent();

                            if (currentMessage.getId() == messageJson.getLong("id")) {
                                ArrayList<String> values = new ArrayList<>();
                                JSONArray valuesArray = messageJson.getJSONArray("values");

                                if (valuesArray != null) {
                                    for (int b = 0; b < valuesArray.length(); b++) {
                                        values.add(valuesArray.getString(b));
                                    }
                                }
                                currentMessage.setValues(values);

//                                    if (values != null) {
//                                        updateFeedback(i, false);
//                                        return;
//                                    }
                            }
                        }
                    }
                }
                        /*else  if(messageJson.optInt(MESSAGE_TYPE, 0) == FUGU_TEXT_VIEW){
//
                            for (int i = 0; i < fuguMessageList.size(); i++) {
                                if (fuguMessageList.get(i).getType() == FUGU_FORUM_VIEW) {
                                    Message currentMessage = ((EventItem) fuguMessageList.get(i)).getEvent();
                                    String message=messageJson.getString("message");
                                        currentMessage.setMessage(message);
                                        //updateFeedback(i);
                                        if (message != null && !message.isEmpty()) {
                                            updateFeedback(i, false);
                                            return;
                                        }
                                        return;
                                    }
                                }
                            }*/


//                        else if(messageJson.optInt(MESSAGE_TYPE, 0) == 16 && messageJson.getString(USER_ID).equals(String.valueOf(userId)) ) {
//                            return;
//                        }
            } catch (JSONException e) {
                //e.printStackTrace();
            }

            if (String.valueOf(messageJson.get("user_id")).equals(String.valueOf(userId))) {
                FuguLog.v("onReceivedMessage", "isSelf");
                isSelf = true;
            }
            if (messageJson.optInt(NOTIFICATION_TYPE, 0) == READ_MESSAGE) {
                readFunctionality(messageJson);
            } else {
                if (messageJson.optInt(IS_TYPING, TYPING_STOPPED) == TYPING_STARTED) {
                    isFayeChannelActive = true;
                    readFunctionality(messageJson);
                }
                if (messageJson.optInt(IS_TYPING, 0) == TYPING_SHOW_MESSAGE &&
                        (!messageJson.getString(MESSAGE).isEmpty() ||
                                (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                (messageJson.has("url") && !messageJson.getString("url").isEmpty()) || messageJson.has(CUSTOM_ACTION))
                        && (messageJson.getInt(MESSAGE_TYPE) == TEXT_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == ACTION_MESSAGE)
                        || messageJson.getInt(MESSAGE_TYPE) == FEEDBACK_MESSAGE
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_QUICK_REPLY_VIEW
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_TEXT_VIEW
                        || messageJson.getInt(MESSAGE_TYPE) == FUGU_FORUM_VIEW) {
                    FuguLog.v("onReceivedMessage", "in if 1");
                    if (isSelf && messageJson.has(MESSAGE_STATUS) && messageJson.has("muid")
                            && messageJson.getInt(MESSAGE_STATUS) == MESSAGE_UNSENT) {
                        try {
                            FuguLog.v("onReceivedMessage", "in if 2");

                            messageIndex = messageJson.getInt("message_index");
                            try {
                                if (fuguMessageList.get(messageJson.getInt("message_index")).getType() == ListItem.TYPE_HEADER
                                        && (messageJson.getInt(MESSAGE_INDEX) + 1 < fuguMessageList.size())) {
                                    FuguLog.v("onReceivedMessage", "in if 3");
                                    messageIndex = messageIndex + 1;
                                    ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX) + 1))
                                            .getEvent().setMessageStatus(MESSAGE_SENT);
                                } else if (messageJson.getInt(MESSAGE_INDEX) < fuguMessageList.size()) {
                                    FuguLog.v("onReceivedMessage", "in elseIf 1");
                                    ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)))
                                            .getEvent().setMessageStatus(MESSAGE_SENT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                try {
                                    for(int i = fuguMessageList.size()-1; i>=0; i--) {
                                        if (fuguMessageList.get(i) instanceof EventItem) {
                                            Message currentMessage = ((EventItem) fuguMessageList.get(i)).getEvent();
                                            if (currentMessage.getMuid().equals(messageJson.getString("muid"))) {
                                                messageIndex = i;
                                                try {
                                                    if (fuguMessageList.get(messageJson.getInt("message_index")).getType() == ListItem.TYPE_HEADER
                                                            && (messageJson.getInt(MESSAGE_INDEX) + 1 < fuguMessageList.size())) {
                                                        FuguLog.v("onReceivedMessage", "in if 3");
                                                        messageIndex = messageIndex + 1;
                                                        ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX) + 1))
                                                                .getEvent().setMessageStatus(MESSAGE_SENT);
                                                    } else if (messageJson.getInt(MESSAGE_INDEX) < fuguMessageList.size()) {
                                                        FuguLog.v("onReceivedMessage", "in elseIf 1");
                                                        ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)))
                                                                .getEvent().setMessageStatus(MESSAGE_SENT);
                                                    }
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }

                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }


                            }


                            ListItem listItem = unsentMessages.get(messageJson.getString("muid"));
                            if (listItem == null)
                                return;
                            ((EventItem) listItem).getEvent().setMessageStatus(MESSAGE_SENT);


                            List<String> reverseOrderedKeys = new ArrayList<>(sentMessages.keySet());
                            Collections.reverse(reverseOrderedKeys);
                            String tempSentAtUTC = "";
                            for (String key : reverseOrderedKeys) {
                                if (sentMessages.get(key) instanceof HeaderItem) {
                                    tempSentAtUTC = key;
                                    break;
                                }
                            }
                            String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                            String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                            if (!tempSentAtUTC.equalsIgnoreCase(localDate)) {
                                sentMessages.put(localDate, new HeaderItem(localDate));
                            }

                            sentMessages.put(messageJson.getString("muid"), listItem);
                            unsentMessageMapNew.remove(messageJson.getString("muid"));
                            unsentMessages.remove(messageJson.getString("muid"));
                            pageStart = pageStart + 1;
                            if (unsentMessageMapNew.size() == 0 && isNetworkStateChanged) {
                                pageStart = 1;
                                //getMessages(null);
                                isNetworkStateChanged = false;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FuguLog.e(TAG, "notifyItemChanged at: " + messageIndex);
                                        fuguMessageAdapter.updateList(fuguMessageList, false);
                                        fuguMessageAdapter.notifyItemChanged(messageIndex);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            messageSending = false;
                            sendMessages();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        FuguLog.v("onReceivedMessage", "in else 1");
                        String localDate = dateUtils.convertToLocal(messageJson.getString("date_time"), inputFormat, outputFormat);
                        if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                            sentMessages.put(localDate, new HeaderItem(localDate));
                            fuguMessageList.add(new HeaderItem(localDate));
                            sentAtUTC = localDate;
                            dateItemCount = dateItemCount + 1;
                        }
                        /*String date = DateUtils.getDate(dateUtils.convertToLocal(messageJson.getString(DATE_TIME)));
                        if (!sentAtUTC.equalsIgnoreCase(date)) {
                            fuguMessageList.add(new HeaderItem(date));
                            sentAtUTC = date;
                            dateItemCount = dateItemCount + 1;
                        }*/

                        String url = "";
                        if (messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE) {
                            url = messageJson.optString("image_url", "");
                        } else if (messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE) {
                            url = messageJson.optString("url", "");
                        }
                        String sentUuid;
                        try {
                            sentUuid = messageJson.getString("UUID");
                        } catch (Exception e) {
                            sentUuid = UUID.randomUUID().toString();
                        }
                        Message message = new Message(0, messageJson.getString(FULL_NAME),
                                Long.parseLong(messageJson.getString(USER_ID)),
                                messageJson.getString(MESSAGE),
                                messageJson.getString(DATE_TIME),
                                isSelf,
                                onSubscribe == 1 ? MESSAGE_READ : MESSAGE_SENT,
                                fuguMessageList.size(),
                                url,
                                messageJson.has("thumbnail_url") ? messageJson.getString("thumbnail_url") : "",
                                messageJson.getInt(MESSAGE_TYPE),
                                sentUuid);
                        if (messageJson.has(CUSTOM_ACTION)) {
                            message.setCustomAction(new Gson().fromJson(messageJson.getJSONObject(CUSTOM_ACTION).toString(), CustomAction.class));
                        }

                        message.setMuid(messageJson.optString("muid", ""));

                        if (message.getMessageType() == FUGU_QUICK_REPLY_VIEW) {
                            JSONArray valuesArray = messageJson.getJSONArray("values");
                            ArrayList<String> values = new ArrayList<>();
                            if (valuesArray != null) {
                                for (int b = 0; b < valuesArray.length(); b++) {
                                    values.add(valuesArray.getString(b));
                                }
                            }
                            if (values.size() > 0)
                                return;
                            if (messageJson.has("default_action_id")) {
                                message.setDefaultActionId(messageJson.getString("default_action_id"));
                            }

                            if (messageJson.has("id")) {
                                message.setId(messageJson.optLong("id"));
                                List<ContentValue> contentValue = new ArrayList<>();

                                JSONArray contentvaluesArray = messageJson.getJSONArray("content_value");
                                if (contentvaluesArray != null) {

                                    for (int a = 0; a < contentvaluesArray.length(); a++) {
                                        JSONObject object = contentvaluesArray.getJSONObject(a);
                                        ContentValue contentValue1 = new ContentValue();
                                        contentValue1.setBotId(object.getString("bot_id"));
                                        //contentValue1.setId(object.getString("_id"));
                                        contentValue1.setButtonId(object.getString("button_id"));
                                        contentValue1.setButtonType(object.getString("button_type"));
                                        contentValue1.setButtonTitle(object.getString("button_title"));
//                                            contentValue1.isDeleted(object.getBoolean("isDeleted"));
                                        contentValue1.setActionId(object.getString("action_id"));

                                        contentValue.add(contentValue1);
                                    }
                                }
                                message.setContentValue(contentValue);
                                CommonData.setQuickReplyData(message);
                            } else {
                                return;
                            }


                        } else if (message.getMessageType() == FUGU_FORUM_VIEW && message.getId() != null) {
                            message.setMuid(messageJson.optString("muid", ""));
                            message.setId(messageJson.optLong("id"));
                            List<ContentValue> contentValue = new ArrayList<>();
                            ArrayList<String> values = new ArrayList<>();


                            JSONArray contentvaluesArray = messageJson.getJSONArray("content_value");
                            JSONArray valuesArray = messageJson.getJSONArray("values");

                            if (contentvaluesArray != null) {

                                for (int a = 0; a < contentvaluesArray.length(); a++) {
                                    JSONObject object = contentvaluesArray.getJSONObject(a);
                                    ContentValue contentValue1 = new ContentValue();
                                    contentValue1.setBotId(object.getString("bot_id"));
                                    //contentValue1.setId(object.getString("_id"));
                                    JSONArray array = object.getJSONArray("questions");
                                    JSONArray dataTypeArray = object.getJSONArray("data_type");
                                    ArrayList<String> questions = new ArrayList<>();
                                    ArrayList<String> dataType = new ArrayList<>();

                                    for (int x = 0; x < array.length(); x++) {
                                        questions.add(array.getString(x));
                                    }

                                    for (int y = 0; y < dataTypeArray.length(); y++) {
                                        dataType.add(dataTypeArray.getString(y));
                                    }
                                    contentValue1.setQuestions(questions);
                                    contentValue1.setData_type(dataType);

                                    contentValue.add(contentValue1);
                                }
                            }

                            if (valuesArray != null) {
                                for (int b = 0; b < valuesArray.length(); b++) {
                                    values.add(valuesArray.getString(b));
                                }
                            }


                            message.setValues(values);
                            message.setContentValue(contentValue);
                            removeItemAndUpdateUI();
                        } else if (message.getMessageType() == FEEDBACK_MESSAGE) {

                            message.setRatingGiven(messageJson.getInt("rating_given"));
                            message.setComment(messageJson.getString("comment"));
                            message.setIsRatingGiven(messageJson.getInt("is_rating_given"));
                            message.setTotalRating(messageJson.getInt("total_rating"));

                            message.setLineBeforeFeedback(messageJson.getString("line_before_feedback"));
                            message.setLineAfterFeedback_1(messageJson.getString("line_after_feedback_1"));
                            message.setLineAfterFeedback_2(messageJson.getString("line_after_feedback_2"));
                        }


                        //fuguMessageList.add(new EventItem(message));
                        if(!sentMessages.containsValue(messageJson.optString("muid", ""))) {
                            fuguMessageList.add(new EventItem(message));
                            sentMessages.put(messageJson.optString("muid", ""), new EventItem(message));
                        }
                        pageStart = pageStart + 1;
                        stopAnim();

                        if (!isSelf) {
                            sendReadAcknowledgement();
                        }
                    }
                }
            }

            if (!messageJson.getString(USER_ID).equals(String.valueOf(userId)) &&
                    onSubscribe == 1 && messageJson.has("on_subscribe")) {
                FuguLog.v("onReceivedMessage", "in If 4");
                for (int i = 0; i < fuguMessageList.size(); i++) {
                    if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF &&
                            ((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus() == MESSAGE_SENT) {
                        ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                    }
                }
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                    }
                });*/
            }

            if (messageJson.optInt(MESSAGE_TYPE) == FUGU_QUICK_REPLY_VIEW || messageJson.optInt(MESSAGE_TYPE) == FUGU_FORUM_VIEW
                    || messageJson.optInt(MESSAGE_TYPE) == FUGU_TEXT_VIEW) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecycler();

                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (messageJson.optInt(MESSAGE_TYPE) == TEXT_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == IMAGE_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == FILE_MESSAGE ||
                                messageJson.optInt(MESSAGE_TYPE) == ACTION_MESSAGE) {
                            FuguLog.v("onReceivedMessage", "in If 5");
                            if ((messageJson.getInt(IS_TYPING) == 0) &&
                                    (!messageJson.getString(MESSAGE).isEmpty() ||
                                            (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                            (messageJson.has("url") && !messageJson.getString("url").isEmpty()) ||
                                            messageJson.has(CUSTOM_ACTION)) &&
                                    !String.valueOf(messageJson.get(USER_ID)).equals(String.valueOf(userId))) {
                                FuguLog.v("onReceivedMessage", "in If 6");
                                updateRecycler();
                                if (CommonData.getQuickReplyData() != null
                                        && CommonData.getQuickReplyData().getDefaultActionId() != null
                                        && !CommonData.getQuickReplyData().getDefaultActionId().isEmpty()) {
                                    try {
                                        //qwe
                                        sendQuickReply(CommonData.getQuickReplyData(), 0,
                                                CommonData.getQuickReplyData().getDefaultActionId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    fuguMessageList.remove(new EventItem(CommonData.getQuickReplyData()));
                                    CommonData.clearQuickReplyData();

                                    fuguMessageAdapter.notifyDataSetChanged();


                                } else {
                                    fuguMessageList.remove(new EventItem(CommonData.getQuickReplyData()));
                                }


                            } else if ((messageJson.getInt(IS_TYPING) == TYPING_STARTED) &&
                                    !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                FuguLog.v("onReceivedMessage", "in elseIf startAnim");
                                startAnim();
                            } else if ((messageJson.getInt(IS_TYPING) == TYPING_STOPPED) &&
                                    !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                FuguLog.v("onReceivedMessage", "in elseIf stopAnim");
                                stopAnim();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean messageSending = false;
    boolean messageSendingRecursion = false;
    boolean fayeDisconnect = true;
    boolean fayeConnecting = false;
    int sendingTry = 0;

    private void sendMessage(String message, int messageType, String imageUrl, String thumbnailUrl,
                             String localPath, FuguFileDetails fileDetails) {
        sendMessage(message, messageType, imageUrl, thumbnailUrl, localPath, fileDetails, null, -1);
    }

    private void sendMessage(String message, int messageType, String imageUrl, String thumbnailUrl,
                             String localPath, FuguFileDetails fileDetails, String localMuid, int localIndex) {
        String removsinglequote = message.trim();
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        int index = fuguMessageList.size();
        if(localIndex>0)
            index = localIndex;
        String muid;
        if(TextUtils.isEmpty(localMuid)) {
            muid = UUID.randomUUID().toString() + "." + new Date().getTime();
            addMessageToList(removsinglequote, messageType, imageUrl, thumbnailUrl, localPath, fileDetails, muid, index);
        } else {
            muid = localMuid;
        }

        if(messageType == IMAGE_MESSAGE && !TextUtils.isEmpty(localPath)) {
            uploadFileServerCall(localPath, "image/*", fuguMessageList.size() - 1, muid);
        } else {
            try {
                JSONObject messageJson = new JSONObject();
                messageJson.put("muid", muid);
                messageJson.put("is_message_expired", 0);
                messageJson.put(MESSAGE, removsinglequote);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                messageJson.put(MESSAGE_INDEX, index);

                if (messageType == IMAGE_MESSAGE) {
                    if (!imageUrl.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                        messageJson.put(IMAGE_URL, imageUrl);
                        messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                    } else if (!TextUtils.isEmpty(localPath)) {
                        messageJson.put("local_url", localPath);
                    }
                } else if (messageType == FILE_MESSAGE && !imageUrl.trim().isEmpty()) {
                    messageJson.put("url", imageUrl);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                messageJson.put("message_status", MESSAGE_UNSENT);
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
                messageJson.put(USER_ID, String.valueOf(userId));
                messageJson.put(USER_TYPE, ANDROID_USER);

                unsentMessageMapNew.put(muid, messageJson);
                if(conversation != null && conversation.getChannelId() != null)
                    CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

                if (messageType == IMAGE_MESSAGE) {
                    if (!TextUtils.isEmpty(localPath)) {
                        uploadFileServerCall(localPath, "image/*", fuguMessageList.size() - 1, muid);
                    } else {
                        if (isNetworkAvailable()) {
                            sendMessages();
                            //sendMessages();
                        }
                    }
                } else {
                    if (!messageSendingRecursion && isNetworkAvailable()) {
                        sendMessages();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*try {
            JSONObject messageJson = new JSONObject();
            messageJson.put("muid", muid);
            messageJson.put("is_message_expired", 0);
            messageJson.put(MESSAGE, removsinglequote);
            messageJson.put(MESSAGE_TYPE, messageType);
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
            messageJson.put(MESSAGE_INDEX, index);

            if (messageType == IMAGE_MESSAGE) {
                if (!imageUrl.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, imageUrl);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                } else if (!TextUtils.isEmpty(localPath)) {
                    messageJson.put("local_url", localPath);
                }
            } else if (messageType == FILE_MESSAGE && !imageUrl.trim().isEmpty()) {
                messageJson.put("url", imageUrl);
                messageJson.put("file_name", fileDetails.getFileName());
                messageJson.put("file_size", fileDetails.getFileSize());
            }

            messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
            messageJson.put("message_status", MESSAGE_UNSENT);
            messageJson.put(FULL_NAME, userName);
            messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);

            unsentMessageMapNew.put(muid, messageJson);

            if (messageType == IMAGE_MESSAGE) {
                if (!TextUtils.isEmpty(localPath)) {
                    uploadFileServerCall(localPath, "image*//*", fuguMessageList.size() - 1, muid);
                } else {
                    if (isNetworkAvailable()) {
                        sendMessages();
                        //sendMessages();
                    }
                }
            } else {
                if (!messageSendingRecursion && isNetworkAvailable()) {
                    sendMessages();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    private LinkedHashMap<String, JSONObject> unsentMessageMap = new LinkedHashMap<>();

    private synchronized void sendMessages() {
        if (mClient != null && mClient.isConnectedServer()) {
            if (unsentMessageMapNew == null || unsentMessageMapNew.size() == 0)
                messageSendingRecursion = false;

            sendingTry = sendingTry + 1;
            if (sendingTry == 3) {
                messageSendingRecursion = false;
                messageSending = false;
            }

            try {
                if (unsentMessageMapNew.keySet().iterator().hasNext()) {
                    String key = unsentMessageMapNew.keySet().iterator().next();
                    Log.e(TAG, "key: " + key);
                    JSONObject messageJson = unsentMessageMapNew.get(key);
                    if (messageJson.optInt("is_message_expired", 0) == 1) {
                        unsentMessageMap.put(key, messageJson);
                        unsentMessageMapNew.remove(key);
                        sendMessages();
                    } else {
                        int messageType = messageJson.optInt(MESSAGE_TYPE);
                        if (!messageSending && messageJson.optInt("is_message_expired", 0) == 0 && messageType != IMAGE_MESSAGE) {
                            Log.e(TAG, "Sending: " + new Gson().toJson(messageJson));
                            sendingTry = 0;
                            messageSending = true;
                            mClient.publish("/" + String.valueOf(channelId), messageJson);
                        }
                        if (!messageSending && messageJson.optInt("is_message_expired", 0) == 0 && messageType == IMAGE_MESSAGE) {
                            String localPath = messageJson.optString("local_url", "");
                            String imageUrl = messageJson.optString("image_url");
                            String muid = messageJson.optString("muid");
                            int index = messageJson.optInt(MESSAGE_INDEX);
                            if (!TextUtils.isEmpty(localPath)) {
                                uploadFileServerCall(localPath, "image/*", index, muid);
                            } else {
                                sendingTry = 0;
                                messageSending = true;
                                mClient.publish("/" + String.valueOf(channelId), messageJson);
                            }
                        }
                    }
                } else if (unsentMessageMap.size() > 0) {
                    unsentMessageMapNew.putAll(unsentMessageMap);
                    unsentMessageMap.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//                    if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
//                        isTyping = TypingMode.TYPING_START.getOrdinal();
//                    }
        } else {
            try {
                connectAgainToServer();
            } catch (Exception e) {

            }
        }
        /*connectionListener.connectionCheck(new HelperConnectionListener.CheckListener() {
            @Override
            public void status(boolean flag) {
                if(flag)  else {
                    try {
                        connectionListener.connect();
                    } catch (Exception e) {

                    }
                }
            }
        });*/
    }

    /**
     * @param message
     * @param messageType
     * @param imageUrl
     * @param thumbnailUrl
     * @param localPath
     * @param fileDetails
     * @param muid
     */
    private void addMessageToList(String message, int messageType, String imageUrl, String thumbnailUrl,
                                  String localPath, FuguFileDetails fileDetails, String muid, int index) {
        try {
            String localDate = DateUtils.getInstance().getFormattedDate(new Date());
            String localDate1 = DateUtils.getInstance().getFormattedDate(new Date(), outputFormat);

            if (!sentAtUTC.equalsIgnoreCase(localDate1)) {
                fuguMessageList.add(new HeaderItem(localDate1));
                sentAtUTC = localDate1;
                dateItemCount = dateItemCount + 1;
            }

            FuguLog.d("userName in SDK", "addMessageToList " + userName);

            Message messageObj = new Message(0,
                    userName,
                    userId,
                    message,
                    DateUtils.getInstance().convertToUTC(localDate),
                    true,
                    MESSAGE_UNSENT,
                    index,
                    imageUrl.isEmpty() ? localPath : imageUrl,
                    thumbnailUrl.isEmpty() ? localPath : thumbnailUrl,
                    messageType,
                    muid);

            messageObj.setMuid(muid);
            messageObj.setIsMessageExpired(0);
            messageObj.setLocalImagePath(localPath);

            if (fileDetails != null) {
                messageObj.setFileName(fileDetails.getFileName());
                messageObj.setFileSize(fileDetails.getFileSize());
                messageObj.setFileExtension(fileDetails.getFileExtension());
                messageObj.setFilePath(fileDetails.getFilePath());
            }

            fuguMessageList.add(new EventItem(messageObj));
            unsentMessages.put(muid, new EventItem(messageObj));
            etMsg.setText("");
            updateRecycler();

            scrollListToBottom();
            /*if (messageType == IMAGE_MESSAGE || messageType == FILE_MESSAGE) {
                FuguLog.v("upload pic", "ready for upload");
                FuguLog.v("upload pic", "ready for upload" + imageUrl + " " + messageType);
                unsentMessageMap.put(muid, fuguMessageList.get(fuguMessageList.size() - 1));
                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
                uploadFileServerCall(imageUrl, fuguMessageList.size() - 1, messageType, fileDetails);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*public void fayeStatus(int status) {
        switch (status) {
            case 0:
                messageSending = false;
                messageSendingRecursion = false;
                fayeDisconnect = true;
                enableButtons();
                break;
            default:

                break;
        }
    }*/

    @Override
    public void onConnectedServer(FayeClient fc) {
        fayeDisconnect = false;
        if (channelId > -1) {
            mClient.subscribeChannel("/" + String.valueOf(channelId));
            mClient.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

            pageStart = 1;
            isApiRunning = false;
            allMessagesFetched = false;
            if (isP2P && !TextUtils.isEmpty(label))
                getMessages(label);
            else
                getMessages(null);
            /*try {
                retryLayout.setVisibility(View.VISIBLE);
//                progressWheel.setVisibility(View.VISIBLE);
//                btnRetry.setText(stringConfig.getFuguFetchingMessages());
//                retryLayout.setBackgroundColor(fuguColorConfig.getFuguConnected());
            } catch (Exception e) {

            }*/

//            try {
//                setConnectionMessage(0);
//            } catch (Exception e) {
//
//            }
        }
    }

    private boolean isFirstTimeDisconnected = true;
    Handler handlerDisable = new Handler();
    Runnable runnableDisable = new Runnable() {
        @Override
        public void run() {
            setConnectionMessage(2);
        }
    };


    private Handler newhandler = new Handler();
    private final static Integer RECONNECTION_TIME = 2000;
    private int index = -1;

    @Override
    public void onDisconnectedServer(FayeClient fc) {
//        fayeStatus(0);
        messageSending = false;
        messageSendingRecursion = false;
        fayeDisconnect = true;
        enableButtons();
        try {
            if (isNetworkAvailable())
                newhandler.postDelayed(runnable, RECONNECTION_TIME);

//            setConnectionMessage(2);
            handlerDisable.postDelayed(runnableDisable, 2000);
            isFirstTimeDisconnected = false;

        } catch (Exception e) {

        }
    }

    @Override
    public void onPongReceived() {
        //sendMessages();
        fayeDisconnect = false;
        enableButtons();
        checkUnsentMessageStatus(new RefreshDone() {
            @Override
            public void onRefreshComplete() {
                sendMessages();
            }
        });

        if (retryLayout.getVisibility() == View.VISIBLE) {
            setConnectionMessage(0);
        }
    }

    @Override
    public void onWebSocketError() {
        //fayeStatus(0);

        messageSending = false;
        messageSendingRecursion = false;
        fayeDisconnect = true;
        enableButtons();
        try {
            if (isNetworkAvailable())
                newhandler.postDelayed(runnable, RECONNECTION_TIME);

            setConnectionMessage(2);

        } catch (Exception e) {

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (FuguNotificationConfig.pushLabelId > 0 && fayeDisconnect) {
                    connectAgainToServer();
                    setConnectionMessage(3);
                }
                try {
                    handlerDisable.removeCallbacks(runnableDisable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }
        }
    };

    private void connectAgainToServer() throws Exception {
        if (!isNetworkAvailable()) {
            return;
        }
        if (!fayeConnecting) {
            fayeConnecting = true;
            newhandler.removeCallbacks(runnable);
            mClient.setListener(this);
            mClient.connectServer();
        }

        HandlerThread thread = new HandlerThread("FayeReconnect");
        thread.start();

        try {
            new Handler(thread.getLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    fayeConnecting = false;
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            fayeConnecting = false;
        }
    }

    private void uploadFileServerCall(String file, String fileType, final int messageIndex, final String muid) {
        try {
            if (isNetworkAvailable()) {
                MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
                MultipartParams multipartParams = multipartBuilder
                        .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                        .add(APP_VERSION, BuildConfig.VERSION_NAME)
                        .add(DEVICE_TYPE, 1)
                        .addFile("file", new File(file)).build();

                FuguLog.v("map = ", multipartParams.getMap().toString());
                FuguLog.v("app_secret_key", "---> " + FuguConfig.getInstance().getAppKey());
                RestClient.getApiInterface()
                        .uploadFile(multipartParams.getMap())
                        .enqueue(new ResponseResolver<FuguUploadImageResponse>(FuguChatActivity.this, false, false) {
                            @Override
                            public void success(FuguUploadImageResponse fuguUploadImageResponse) {

                                String image_url = fuguUploadImageResponse.getData().getUrl();
                                String thumbnail_url = fuguUploadImageResponse.getData().getThumbnailUrl();

                                sendMessage("", IMAGE_MESSAGE, image_url, thumbnail_url, null, null, muid, messageIndex);
                                getUnreadCount();
                            }

                            @Override
                            public void failure(APIError error) {
                                ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                                fuguMessageAdapter.notifyItemChanged(messageIndex);
                            }
                        });
            } else {
                ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                fuguMessageAdapter.notifyItemChanged(messageIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkUnsentMessageStatus(RefreshDone done) {
        try {
            index = -1;
            if (unsentMessages == null)
                unsentMessages = new LinkedHashMap<>();
            for (String key : unsentMessages.keySet()) {
                ListItem listItem = unsentMessages.get(key);
                String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                int expireTimeCheck = ((EventItem) listItem).getEvent().getIsMessageExpired();
                if (expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                    ((EventItem) listItem).getEvent().setIsMessageExpired(1);
                    try {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        messageJson.put("is_message_expired", 1);
                        unsentMessageMapNew.put(key, messageJson);
                        if (index == -1)
                            index = messageJson.optInt("message_index", -1);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            CommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
            CommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

            if (index > -1 && fuguMessageAdapter != null) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fuguMessageAdapter.notifyItemRangeChanged(index, fuguMessageList.size());
                        }
                    });
                } catch (Exception e) {

                }
            }

            if (done != null) {
                done.onRefreshComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RefreshDone {
        void onRefreshComplete();
    }

    private void setConnectionMessage(final int status) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isNetworkAvailable()) {
                            switch (status) {
                                case 0:
                                    retryLayout.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    retryLayout.setVisibility(View.VISIBLE);
                                    progressWheel.setVisibility(View.VISIBLE);
                                    btnRetry.setText(stringConfig.getFuguFetchingMessages());
                                    retryLayout.setBackgroundColor(fuguColorConfig.getFuguConnected());
                                    break;
                                case 2:
                                    retryLayout.setVisibility(View.VISIBLE);
                                    progressWheel.setVisibility(View.GONE);
                                    btnRetry.setText(stringConfig.getFuguServerDisconnect());
                                    retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                                    enableButtons();
                                    break;
                                case 3:
                                    retryLayout.setVisibility(View.VISIBLE);
                                    progressWheel.setVisibility(View.GONE);
                                    btnRetry.setText(stringConfig.getFuguNoNetworkConnected());
                                    retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                                    enableButtons();
                                    break;
                                case 4:
                                    retryLayout.setVisibility(View.VISIBLE);
                                    progressWheel.setVisibility(View.GONE);
                                    btnRetry.setText(stringConfig.getFuguServerConnecting());
                                    retryLayout.setBackgroundColor(fuguColorConfig.getFuguConnected());
                                    enableButtons();
                                    break;
                                default:

                                    break;
                            }
                        } else {
                            retryLayout.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
                            btnRetry.setText(stringConfig.getFuguNoNetworkConnected());
                            retryLayout.setBackgroundColor(fuguColorConfig.getFuguNotConnected());
                            enableButtons();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}