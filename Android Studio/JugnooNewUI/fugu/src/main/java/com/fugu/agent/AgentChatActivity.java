package com.fugu.agent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguNotificationConfig;
import com.fugu.FuguStringConfig;
import com.fugu.R;
import com.fugu.adapter.FuguMessageAdapter;
import com.fugu.agent.Util.AgentType;
import com.fugu.agent.Util.ConversationMode;
import com.fugu.agent.Util.CustomRelative;
import com.fugu.agent.Util.FragmentType;
import com.fugu.agent.Util.MessageMode;
import com.fugu.agent.Util.Overlay;
import com.fugu.agent.Util.TypingMode;
import com.fugu.agent.Util.UserType;
import com.fugu.agent.adapter.FuguAgentMessageAdapter;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.listeners.OnUserChannelListener;
import com.fugu.agent.listeners.UnreadListener;
import com.fugu.agent.model.AgentData;
import com.fugu.agent.model.ApiResponseFlags;
import com.fugu.agent.model.EventItem;
import com.fugu.agent.model.FuguAgentGetMessageParams;
import com.fugu.agent.model.FuguAgentGetMessageResponse;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.HeaderItem;
import com.fugu.agent.model.ListItem;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.Message;
import com.fugu.agent.model.TagData;
import com.fugu.agent.model.createConversation.CreateConversation;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.MultipartParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguImageUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.Utils;
import com.fugu.utils.beatAnimation.AVLoadingIndicatorView;
import com.fugu.utils.loadingBox.LoadingBox;
import com.fugu.utils.loadingBox.ProgressWheel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import faye.FayeAgentListener;
import faye.FayeClient;

public class AgentChatActivity extends AgentBaseActivity implements FayeAgentListener,
        View.OnClickListener, FuguAppConstant, OnUserChannelListener {

    private static final String TAG = AgentChatActivity.class.getSimpleName();

    public static Long pushChannelId = -1L;
    public static Long currentChannelId = -1L;

    // Initial FayeClient
    private static final FayeClient mClient = FuguConfig.getClient();

    private String LOG_TAG = getClass().getSimpleName();
    private CustomRelative llRoot;
    private TextView tvNoInternet, tvTitle, tvAssignNotify;
    private EditText etMsg;

    private RecyclerView rvMessages;
    private LinearLayoutManager layoutManager;
    private FuguAgentMessageAdapter fuguMessageAdapter;
    private RelativeLayout relativeLayout;
    private CardView cvTypeMessage;
    private int isTyping = 0;
    private Toolbar myToolbar;
    private AVLoadingIndicatorView aviTyping;
    private LinearLayout llTyping, llUserDetails;
    private int onSubscribe = 0;
    private Conversation conversation;
    private ProgressBar pbSendingImage;
    private ImageView ivSend, ivAudioView;
    private View vText;
    private DateUtils dateUtils;
    private String sentAtUTC = "", savedPrivateNote = "";
    private int messageType = TEXT_MESSAGE;
    private ProgressBar pbLoading;
    private boolean showLoading = true;
    private boolean allMessagesFetched = false;
    private boolean isNetworkStateChanged = false;
    //    private RelativeLayout rlScrollBottom;
    private ImageView ivViewInfo;
    private FuguImageUtils imageUtils;
    private FuguColorConfig fuguColorConfig;

    private int position;
    // Declaring objects and variables
    private ArrayList<TagData> tagData = new ArrayList<>();
    private Type listType = new TypeToken<List<TagData>>() {
    }.getType();

    private Type countlistType = new TypeToken<ArrayList<String>>() {
    }.getType();

    private int pageStart = 1;
    private int dateItemCount = 0;
    private int scrollBottomCount = 0;
    private int status;

    private UserData userData;
    private boolean isBlock = false;

    private boolean firstOpen = true;
    private boolean isFromGetConversation;
    private boolean isAgentChatActivity = false;
    private boolean isReadAcknowledgement = true;
    private boolean isMsgReceviedInBackground = false;
    private boolean isFromPushNotification = false;

    private String lineBeforeFeedback, lineAfterFeedback_1, lineAfterFeedback_2;
    private static int API_DELAY_TIME = 1000;
    private int fragmentType;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private LinearLayout llBottom, retryLayout;
    private TextView btnRetry;
    private ProgressWheel progressWheel;
    private boolean isFirstTimeOpened = true;

    // Declaring objects and variables
    @NonNull
    private ArrayList<ListItem> fuguMessageList = new ArrayList<>();
    private LinkedHashMap<String, ListItem> sentMessages = new LinkedHashMap<>();
    private LinkedHashMap<String, ListItem> unsentMessages = new LinkedHashMap<>();

    @NonNull
    private LinkedHashMap<String, JSONObject> unsentMessageMapNew = new LinkedHashMap<>();

    private String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private String outputFormat = "yyyy-MM-dd";

    private Handler handler = new Handler();
    private final static Integer RECONNECTION_TIME = 2000;
    private int index = -1;
    private int messageIndex = -1;
    private FuguStringConfig stringConfig;

    private FayeClient getmClient() {
        return mClient;
    }

    private ImageView ivVideoView;
    private boolean infoClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_agent_chat);

        initViews();
        fetchIntentData();
        setUpUI();
        stateChangeListeners();

        isFirstTimeOpened = true;

//        setSubscribeChannel();
        mClient.setAgentListener(this);
        if (!getmClient().isConnectedServer())
            getmClient().connectServer();
        else
            setSubscribeChannel();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, getIntentFilter());

    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        intent.addAction(NOTIFICATION_TAPPED);
        return intent;
    }


    private void initViews() {
        isAgentChatActivity = true;
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = myToolbar.findViewById(R.id.tv_toolbar_name);
        ivViewInfo = findViewById(R.id.ivViewInfo);

        ivViewInfo.setVisibility(View.GONE);
        fuguColorConfig = CommonData.getColorConfig();
        stringConfig = CommonData.getStringConfig();
//        ivViewInfo.setImageResource(FuguConfig.getInstance().getInfoIcon() == -1
//                ? R.drawable.hippo_ic_icon_info : FuguConfig.getInstance().getInfoIcon());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            ivViewInfo.setBackground(FuguColorConfig.makeRoundedSelector(fuguColorConfig.getFuguActionBarBg()));
//        } else {
//            ivViewInfo.setBackgroundDrawable(FuguColorConfig.makeRoundedSelector(fuguColorConfig.getFuguActionBarBg()));
//        }

        ivAudioView = (ImageView) findViewById(R.id.ivAudioView);
        ivAudioView.setImageResource(FuguConfig.getInstance().getVideoCallDrawableId() == -1
                ? R.drawable.hippo_ic_call_black : FuguConfig.getInstance().getVideoCallDrawableId());
        if(FuguConfig.getInstance().getVideoCallDrawableId() != -1) {
            ivAudioView.getDrawable().setColorFilter(fuguColorConfig.getFuguHomeColor(), PorterDuff.Mode.SRC_IN);
        }
        ivAudioView.setVisibility(View.GONE);
        /*ivAudioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoCallInit(FuguAppConstant.AUDIO_CALL_VIEW);
            }
        });*/


        ivVideoView = findViewById(R.id.ivVideoView);
        ivVideoView.setImageResource(FuguConfig.getInstance().getVideoCallDrawableId() == -1
                ? R.drawable.hippo_ic_videocam : FuguConfig.getInstance().getVideoCallDrawableId());
        if(FuguConfig.getInstance().getVideoCallDrawableId() != -1) {
            ivVideoView.getDrawable().setColorFilter(fuguColorConfig.getFuguHomeColor(), PorterDuff.Mode.SRC_IN);
        }
        ivVideoView.setVisibility(View.GONE);

        /*ivVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoCallInit(VIDEO_CALL_VIEW);
            }
        });*/

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conversation.getAgentId() == null || !infoClickable)
                    return;
                if(!isNetworkAvailable()) {
                    Toast.makeText(AgentChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Utils.preventMultipleClicks()) {
                    return;
                }
                Intent i = new Intent(AgentChatActivity.this, AgentChatOptions.class);
                i.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                i.putExtra(FuguAppConstant.TAGS_DATA, new Gson().toJson(tagData, listType));
                startActivityForResult(i, 100);
            }
        });

        relativeLayout = findViewById(R.id.activity_main);
        llRoot = findViewById(R.id.llRoot);
        cvTypeMessage = findViewById(R.id.cvTypeMessage);
        tvNoInternet = findViewById(R.id.tvNoInternet);
        tvAssignNotify = (TextView) findViewById(R.id.tvAssignNotify);
        aviTyping = findViewById(R.id.aviTyping);
        llTyping = findViewById(R.id.llTyping);
        llTyping.setFocusable(false);
        etMsg = findViewById(R.id.etMsg);
        rvMessages = findViewById(R.id.rvMessages);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);

        imageUtils = new FuguImageUtils(AgentChatActivity.this);

        etMsg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                }
                return false;
            }
        });

        rvMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvMessages.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (rvMessages.getAdapter() != null) {
                                    rvMessages.smoothScrollToPosition(
                                            rvMessages.getAdapter().getItemCount() - 1);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, 100);
                }
            }
        });


        pbSendingImage = findViewById(R.id.pbSendingImage);
        ivSend = findViewById(R.id.ivSend);

        pbLoading = findViewById(R.id.pbLoading);
        dateUtils = DateUtils.getInstance();
        configColors();
        if (!isNetworkAvailable()) {
            // TODO: 31/08/18
//            llInternet.setVisibility(View.VISIBLE);
//            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
//            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }
    }

    private void configColors() {
        relativeLayout.setBackgroundColor(fuguColorConfig.getFuguChatBg());
        GradientDrawable drawable = (GradientDrawable) llTyping.getBackground();
        drawable.setColor(fuguColorConfig.getFuguBgMessageFrom());
        drawable.setStroke((int) getResources().getDimension(R.dimen.fugu_border_width), fuguColorConfig.getFuguBorderColor()); // set stroke width and stroke color
        aviTyping.setIndicatorColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
//        tvClosed.setTextColor(fuguColorConfig.getFuguThemeColorPrimary());
//        tvClosed.getBackground().setColorFilter(fuguColorConfig.getFuguChannelItemBg(), PorterDuff.Mode.SRC_ATOP);
        cvTypeMessage.getBackground().setColorFilter(fuguColorConfig.getFuguTypeMessageBg(), PorterDuff.Mode.SRC_ATOP);
        etMsg.setHintTextColor(fuguColorConfig.getFuguTypeMessageHint());
        etMsg.setTextColor(fuguColorConfig.getFuguTypeMessageText());
        tvNoInternet.setTextColor(fuguColorConfig.getFuguThemeColorPrimary());
    }

    private void fetchIntentData() {
        userData = AgentCommonData.getUserData();
        if (!isFromGetConversation) {
            conversation = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.CONVERSATION), Conversation.class);
            fragmentType = getIntent().getIntExtra(FuguAppConstant.FRAGMENT_TYPE, FragmentType.ALL_CHAT.getOrdinal());
            isFromPushNotification = getIntent().getBooleanExtra("is_from_push", false);
            if (isFromPushNotification) {
                FuguConfig.getInstance().getTotalUnreadCount();
                try {
                    for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                        if (listener != null)
                            listener.updateCount(conversation.getChannelId());
                    }
                } catch (Exception e) {

                }
            }

            /*try {
                for (OnUserChannelListener listener : FuguConfig.getInstance().getUIListeners(OnUserChannelListener.class)) {
                    if (listener != null)
                        listener.onRefreshData();
                }
            } catch (Exception e) {

            }*/

            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        isFromPushNotification = false;
                        for (UnreadListener listener : FuguConfig.getInstance().getUIListeners(UnreadListener.class)) {
                            if (listener != null)
                                listener.getUpdatedUnreadCount(null);
                        }
                    }
                }, 1500);
            } catch (Exception e) {

            }
        }
        llBottom = findViewById(R.id.llMessageLayout);
        btnRetry = findViewById(R.id.btnRetry);
        progressWheel = findViewById(R.id.retry_loader);
        retryLayout = findViewById(R.id.retry_layout);
        if (conversation.isDisableReply()) {
            llBottom.setVisibility(View.GONE);
        }
        status = conversation.getStatus().equals(MessageMode.OPEN_CHAT.getOrdinal()) ? MessageMode.CLOSED_CHAT.getOrdinal() : MessageMode.OPEN_CHAT.getOrdinal();
//        if (status == MessageMode.OPEN_CHAT.getOrdinal()) {
//            ivCloseConv.setVisibility(View.GONE);
//        }
        tvTitle.setText(conversation.getLabel());
        pushChannelId = conversation.getChannelId();
        currentChannelId = conversation.getChannelId();
        FuguNotificationConfig.agentPushChannelId = pushChannelId;

        if (AgentCommonData.getUnsentMessageMapByChannel(conversation.getChannelId()) != null) {
            unsentMessageMapNew = AgentCommonData.getUnsentMessageMapByChannel(conversation.getChannelId());
        }

        if (currentChannelId > 0) {
            AgentCommonData.removeTotalUnreadCount(currentChannelId.intValue());
        }

        clearNotification(currentChannelId);

        rvMessages = findViewById(R.id.rvMessages);
        fuguMessageAdapter = new FuguAgentMessageAdapter(AgentChatActivity.this, fuguMessageList, conversation);
        layoutManager = new LinearLayoutManager(AgentChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(fuguMessageAdapter);
    }

    private String videoCustomerName = "Customer";
    private String videoAgentName = "Agent";
    private boolean isChatAssignToMe = false;
    private boolean isVideoCallEnable = false;

    private void setUpUI() {
        if (AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue()) != null
                && AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue()).getData().getMessages().size() > 0) {
            FuguAgentGetMessageResponse response = AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue());
            //customerName = response.getData().getCustomerName();

            videoCustomerName = response.getData().getCustomerName();
            videoAgentName = response.getData().getAgentName();
            isChatAssignToMe = (response.getData().getUserId() == AgentCommonData.getUserData().getUserId().intValue()) ? true : false;

            try {
                if(AgentCommonData.getUserData().isVideoCallEnabled()
                        && response != null && response.getData() != null && response.getData().isAllowVideoCall()) {
                    isVideoCallEnable = true;
                }
            } catch (Exception e) {

            }


            Log.e(TAG, "System.currentTimeMillis(): " + System.currentTimeMillis());
            showLoading = false;
            sentMessages = new LinkedHashMap<>();
            unsentMessages = new LinkedHashMap<>();
            fuguMessageList.clear();

            if (AgentCommonData.getSentMessageByChannel(conversation.getChannelId()) != null) {
                sentMessages = AgentCommonData.getSentMessageByChannel(conversation.getChannelId());
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

            if (AgentCommonData.getUnsentMessageByChannel(conversation.getChannelId()) != null) {
                unsentMessages = AgentCommonData.getUnsentMessageByChannel(conversation.getChannelId());
                if (unsentMessages == null)
                    unsentMessages = new LinkedHashMap<>();

                for (String key : unsentMessages.keySet()) {
                    ListItem listItem = unsentMessages.get(key);
                    String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                    int expireTimeCheck = ((EventItem) listItem).getEvent().getIsMessageExpired();
                    if (((EventItem) listItem).getEvent().getMessageType() != IMAGE_MESSAGE
                            && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
                        ((EventItem) listItem).getEvent().setIsMessageExpired(1);
                        try {
                            JSONObject messageJson = unsentMessageMapNew.get(key);
                            messageJson.put("is_message_expired", 1);
                            unsentMessageMapNew.put(key, messageJson);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else if(((EventItem) listItem).getEvent().getMessageType() == IMAGE_MESSAGE) {
                        JSONObject messageJson = unsentMessageMapNew.get(key);
                        if(messageJson == null) {
                            ((EventItem) listItem).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                        }
                    }
                }
                AgentCommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
                AgentCommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

                for (String key : unsentMessages.keySet()) {
                    ListItem listItem = unsentMessages.get(key);
                    String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                    String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                    //String date = dateUtils.getDate(loaclDate);
                    if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                        fuguMessageList.add(new HeaderItem(localDate));
                        sentAtUTC = localDate;
                        System.out.println("Date: " + localDate);
                        //dateItemCount = dateItemCount + 1;
                    }
                    fuguMessageList.add(unsentMessages.get(key));
                }

            }

            Log.e(TAG, "System.currentTimeMillis(): " + System.currentTimeMillis());

            try {
                if(userData.isVideoCallEnabled() && response.getData() != null) {
                    if(response.getData().isAllowVideoCall() && response.getData().getUserId() > 0
                            && userData.getUserId().intValue() == response.getData().getUserId()) {
                        ivVideoView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                if(FuguConfig.DEBUG)
                    e.printStackTrace();
            }

            try {
                if(AgentCommonData.getUserData().isAudioCallEnabled() && response.getData() != null) {
                    if(response.getData().isAllowAudioCall() && response.getData().getUserId() > 0
                            && AgentCommonData.getUserData().getUserId().intValue() == response.getData().getUserId()) {
                        ivAudioView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (java.lang.Exception e) {
                if(FuguConfig.DEBUG)
                    e.printStackTrace();
            }

            setRecyclerViewData();
            llRoot.setVisibility(View.VISIBLE);
        } else {
            showLoading = true;
        }
        if (isNetworkAvailable()) {
            getMessages();
        } else {
            setConnectionMessage(3);
        }
        /*else {
            if((sentMessages == null || sentMessages.size() == 0) && (unsentMessages == null || unsentMessages.size() == 0)) {
                llInternet.setVisibility(View.VISIBLE);
                llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
                tvStatus.setText(R.string.fugu_not_connected_to_internet);
                setConnectionMessage(0);
            }
        }*/

        if (conversation.isDisableReply()) {
            cvTypeMessage.setVisibility(View.GONE);
        }

    }

    private void stateChangeListeners() {
        etMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
            }
        });

        etMsg.addTextChangedListener(new TextWatcher() {
            private final long DELAY = 3000; // milliseconds
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && isNetworkAvailable()) {
                    ivSend.setClickable(true);
                    ivSend.setAlpha(1f);
                } else {
                    ivSend.setClickable(false);
                    ivSend.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isNetworkAvailable()) {
                    if (isTyping != 1) {
                        FuguLog.d(LOG_TAG, isTyping + "started typing");
                        // publish start typing event

                        if (!etMsg.getText().toString().isEmpty()) {
                            isTyping = 1;
                            publishOnFaye(getString(R.string.fugu_empty), messageType,
                                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), NOTIFICATION_DEFAULT, null);
                        }
                    }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    FuguLog.d(LOG_TAG, isTyping + "stopped typing");
                                    //publish stopped typing event
                                    stopTyping();
                                }
                            },
                            DELAY
                    );
                }
            }
        });
    }

    private void setSubscribeChannel() {
        try {
            if (conversation.getChannelId() != null && conversation.getChannelId() > -1) {
                getmClient().subscribeChannel("/" + String.valueOf(conversation.getChannelId()));
                // Include ext and id
                if (getmClient() != null) {
                    if (getmClient().isConnectedServer()) {
                        getmClient().publish("/" + String.valueOf(conversation.getChannelId()), prepareMessageJson(1));
                        fayeDisconnect = false;
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkUnsentMessageStatus(RefreshDone done) {
        //unsentMessages = CommonData.getUnsentMessageByChannel(conversation.getChannelId());
        index = -1;
        if (unsentMessages == null)
            unsentMessages = new LinkedHashMap<>();
        for (String key : unsentMessages.keySet()) {
            ListItem listItem = unsentMessages.get(key);
            String time = ((EventItem) listItem).getEvent().getSentAtUtc();
            int expireTimeCheck = ((EventItem) listItem).getEvent().getIsMessageExpired();
            if (((EventItem) listItem).getEvent().getMessageType() != IMAGE_MESSAGE
                    && expireTimeCheck == 0 && DateUtils.getTimeDiff(time)) {
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
        AgentCommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
        AgentCommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);

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
    }

    boolean messageSending = false;
    boolean messageSendingRecursion = false;
    boolean fayeDisconnect = true;
    boolean fayeConnecting = false;

    @Override
    public void onConnectedServer(FayeClient fc) {
        fayeDisconnect = false;
        if (conversation.getChannelId() > -1) {
            fc.subscribeChannel("/" + String.valueOf(conversation.getChannelId()));
            if (mClient != null && mClient.isConnectedServer()) {
                mClient.publish("/" + String.valueOf(conversation.getChannelId()), prepareMessageJson(1));
            }

            getMessages(false, true);
//            try {
//                setConnectionMessage(1);
//            } catch (Exception e) {
//
//            }
        }

        try {
            setConnectionMessage(0);
        } catch (Exception e) {

        }
        try {
            handlerDisable.removeCallbacks(runnableDisable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectedServer(FayeClient fc) {
        messageSending = false;
        messageSendingRecursion = false;
        fayeDisconnect = true;
        try {
            if(!isFirstTimeDisconnected) {
                setConnectionMessage(2);
            } else {
                handlerDisable.postDelayed(runnableDisable, 2000);
            }
            isFirstTimeDisconnected = false;

            if (isNetworkAvailable())
                handler.postDelayed(runnable, RECONNECTION_TIME);
        } catch (Exception e) {

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (pushChannelId > 0 && fayeDisconnect) {
                    //btnRetry.setText("Server connecting... ");
                    setConnectionMessage(4);
                    connectAgainToServer();
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
        if (!fayeConnecting) {
            fayeConnecting = true;
            handler.removeCallbacks(runnable);

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
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            fayeConnecting = false;
        }
    }

    private void updateFeedback(final int position, final boolean scrollDown) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (scrollDown) {
                        fuguMessageAdapter.notifyDataSetChanged();
                        scrollListToBottom();
                    } else {
                        fuguMessageAdapter.notifyItemChanged(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onWebSocketError() {
        messageSending = false;
        messageSendingRecursion = false;
        fayeDisconnect = true;
        try {
            if (isNetworkAvailable())
                handler.postDelayed(runnable, RECONNECTION_TIME);
            else
                setConnectionMessage(2);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPongReceived() {
        fayeDisconnect = false;
        checkUnsentMessageStatus(new RefreshDone() {
            @Override
            public void onRefreshComplete() {
                sendMessages();
            }
        });

        try {
            if (retryLayout.getVisibility() == View.VISIBLE) {
                setConnectionMessage(0);
            }
        } catch (Exception e) {
        }
        try {
            handlerDisable.removeCallbacks(runnableDisable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedMessage(FayeClient fc, String msg, String channel) {
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
            FuguLog.e(TAG, "TopActivity Name = " + taskList.get(0).topActivity.getClassName());
            if (!isAgentChatActivity && !taskList.get(0).topActivity.getClassName().equals("com.fugu.agent.AgentChatActivity")) {
                FuguLog.e(TAG, " AgentChatActivity not opened right now");
                return;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(msg))
            return;
        if (channel.replace("/", "").equalsIgnoreCase(String.valueOf(conversation.getChannelId()))) {
            FuguLog.e(TAG, "Message: " + msg);
            FuguLog.e(TAG, "channel" + channel);

            boolean isSelf = false;

            try {
                final JSONObject messageJson = new JSONObject(msg);

                if (!messageJson.getString("user_id").equals(String.valueOf(String.valueOf(userData.getUserId()))) && messageJson.has("on_subscribe")) {
                    onSubscribe = messageJson.getInt("on_subscribe");
                    FuguLog.e("onReceived onSubscribe", "==" + onSubscribe);
                }

                try {
                    if (messageJson.getInt("user_type") == UserType.AGENT.getOrdinal()) {
                        isSelf = true;
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }

                if (messageJson.optInt(NOTIFICATION_TYPE, 0) == READ_MESSAGE) {
                    // Read functionality
                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF && messageJson.optInt(USER_TYPE) == 1) {
                            if (((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus().intValue() == MESSAGE_SENT) {
                                ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rvMessages.getRecycledViewPool().clear();
                                if (fuguMessageAdapter != null) {
                                    fuguMessageAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if(messageJson.optInt(IS_TYPING, 0) == TYPING_SHOW_MESSAGE &&
                        messageJson.getInt(MESSAGE_TYPE) == VIDEO_CALL && messageJson.has("muid")) {
                    // for video call
                } else {
                    if (messageJson.optInt("is_typing") == 0 &&
                            (!messageJson.getString("message").isEmpty()
                                    || (messageJson.has("image_url")
                                    && !messageJson.getString("image_url").isEmpty()))
                            && (messageJson.getInt("message_type") == TEXT_MESSAGE
                            || messageJson.getInt("message_type") == PRIVATE_NOTE
                            || messageJson.getInt("message_type") == IMAGE_MESSAGE)) {


                        if (isSelf && messageJson.has("message_status") && messageJson.has("message_index")
                                && messageJson.getInt("message_status") == MESSAGE_UNSENT) {

                            messageIndex = messageJson.getInt("message_index");
                            try {
                                if (fuguMessageList.get(messageJson.getInt("message_index")).getType() == ListItem.TYPE_HEADER
                                        && (messageJson.getInt("message_index") + 1 < fuguMessageList.size())) {
                                    ((EventItem) fuguMessageList.get(messageJson.getInt("message_index") + 1))
                                            .getEvent().setMessageStatus(MESSAGE_SENT);
                                    messageIndex = messageIndex + 1;
                                } else if (messageJson.getInt("message_index") < fuguMessageList.size()) {
                                    ((EventItem) fuguMessageList.get(messageJson.getInt("message_index")))
                                            .getEvent().setMessageStatus(MESSAGE_SENT);
                                }
                            } catch (Exception e) {
                                messageIndex = -1;
                                try {
                                    for(int i=fuguMessageList.size()-1;i>=0;i--) {
                                        if(((EventItem) fuguMessageList.get(i)).getEvent().getMuid().equals(messageJson.getString("muid"))) {
                                            messageIndex = i;
                                            break;
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                if(messageIndex > -1) {
                                    ((EventItem) fuguMessageList.get(messageJson.getInt("message_index")))
                                            .getEvent().setMessageStatus(MESSAGE_SENT);
                                } else {
                                    return;
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

                            Log.e(TAG, "Remainning keys: " + unsentMessageMapNew.keySet());

                            if (unsentMessageMapNew.size() == 0 && isNetworkStateChanged) {
                                pageStart = 1;
                                getMessages();
                                isNetworkStateChanged = false;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        fuguMessageAdapter.notifyItemChanged(messageIndex);
                                    } catch (Exception e) {
                                        fuguMessageAdapter.notifyDataSetChanged();
                                        e.printStackTrace();
                                    }
                                }
                            });

                            messageSending = false;
                            sendMessages();


                        } else {
                            //String date = dateUtils.getDate(dateUtils.convertToLocal(messageJson.getString("date_time")));
                            String localDate = dateUtils.convertToLocal(messageJson.getString("date_time"), inputFormat, outputFormat);

                            if (!sentAtUTC.equalsIgnoreCase(localDate)) {
                                sentMessages.put(localDate, new HeaderItem(localDate));
                                fuguMessageList.add(new HeaderItem(localDate));
                                sentAtUTC = localDate;
                                dateItemCount = dateItemCount + 1;
                            }

                            Message message = new Message(messageJson.getString("full_name"),
                                    Integer.parseInt(messageJson.getString("user_id")),
                                    messageJson.getString("message"),
                                    messageJson.getString("date_time"),
                                    isSelf,
                                    onSubscribe == 1 ? MESSAGE_READ : MESSAGE_SENT,
                                    fuguMessageList.size(),
                                    messageJson.has("image_url") ? messageJson.getString("image_url") : "",
                                    messageJson.has("thumbnail_url") ? messageJson.getString("thumbnail_url") : "",
                                    messageJson.getInt("message_type"),
                                    messageJson.has("user_type") ? messageJson.getInt("user_type") : UserType.AGENT.getOrdinal());

                            message.setMuid(messageJson.optString("muid", ""));

                            fuguMessageList.add(new EventItem(message));
                            if (isReadAcknowledgement) {
                                sendReadAcknowledgement();
                            } else {
                                isMsgReceviedInBackground = true;
                            }
                        }

                    }
                }

                if (!messageJson.getString("user_id").equals(String.valueOf(String.valueOf(userData.getUserId()))) &&
                        onSubscribe == 1 && messageJson.optString("message").isEmpty() && messageJson.has("on_subscribe") && messageJson.optInt(USER_TYPE) == 1) {

                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF && messageJson.optInt(USER_TYPE) == 1 &&
                                ((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus() == MESSAGE_SENT) {
                            ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                        }
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rvMessages.getRecycledViewPool().clear();
                                fuguMessageAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (messageJson.optInt("message_type") == TEXT_MESSAGE
                                    || messageJson.optInt("message_type") == PRIVATE_NOTE
                                    || messageJson.optInt("message_type") == IMAGE_MESSAGE) {
                                if ((messageJson.optInt("is_typing") == 0) &&
                                        (!messageJson.getString("message").isEmpty() ||
                                                (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()))) {
                                    updateRecycler(Integer.parseInt(messageJson.getString("user_id")));

                                } else if ((messageJson.getInt("is_typing") == 1) && !messageJson.getString("user_id").equals(String.valueOf(userData.getUserId()))) {
                                    if (layoutManager != null) {
                                        if (layoutManager.findLastVisibleItemPosition() >= fuguMessageList.size() - 2) {
                                            startAnim();
                                        }
                                    }
                                } else {
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

    }

    private JSONObject prepareMessageJson(int onSubscribe) {
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put("user_id", String.valueOf(userData.getUserId()));
            messageJson.put("full_name", String.valueOf(userData.getFullName()));
            messageJson.put("is_typing", isTyping);
            messageJson.put("message", "");
            messageJson.put("message_type", TEXT_MESSAGE);
            messageJson.put(USER_TYPE, String.valueOf(UserType.AGENT.getOrdinal()));
            messageJson.put("on_subscribe", onSubscribe);
            messageJson.put("channel_id", conversation.getChannelId());

            String localDate = DateUtils.getInstance().getFormattedDate(
                    new Date());
            messageJson.put("date_time", DateUtils.getInstance().convertToUTC(localDate));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageJson;
    }

    private void addMessageToList(String message, int messageType, String imageUrl,
                                  String thumbnailUrl, String muid, String localPath) {

        String localDate = DateUtils.getInstance().getFormattedDate(new Date());
        String localDate1 = DateUtils.getInstance().getFormattedDate(new Date(), outputFormat);

        if (!sentAtUTC.equalsIgnoreCase(localDate1)) {
            fuguMessageList.add(new HeaderItem(localDate1));
            sentAtUTC = localDate1;
            dateItemCount = dateItemCount + 1;
        }

        Message messageObj = new Message(String.valueOf(userData.getFullName()),
                userData.getUserId(),
                message,
                DateUtils.getInstance().convertToUTC(localDate),
                true,
                MESSAGE_UNSENT,
                fuguMessageList.size(),
                imageUrl.isEmpty() ? "" : imageUrl,
                thumbnailUrl.isEmpty() ? "" : thumbnailUrl,
                messageType,
                UserType.AGENT.getOrdinal());

        messageObj.setMuid(muid);
        messageObj.setIsMessageExpired(0);
        messageObj.setLocalImagePath(localPath);

        /*if (bot != null) {
            if (messageType == FEEDBACK_MESSAGE) {
                messageObj.setComment("");
                messageObj.setRatingGiven(0);
                messageObj.setIsRatingGiven(0);
                messageObj.setTotalRating(5);
                messageObj.setLineBeforeFeedback(CommonData.getLineFeedback("lineBeforeFeedback"));
                messageObj.setLineAfterFeedback_1(CommonData.getLineFeedback("lineBeforeFeedback_1"));
                messageObj.setLineAfterFeedback_2(CommonData.getLineFeedback("lineAfterFeedback_2"));
            } else if (messageType == BOT_FORM_MESSAGE) {
                messageObj.setContentValue(bot.getContentValue());
                messageObj.setValues((ArrayList<String>) bot.getValues());
            }
        }*/

        /*if (mentions != null && mentions.getInsertedMentions() != null && mentions.getInsertedMentions().size() != 0) {

            taggedUsers = new ArrayList<>();

            for (Mentionable mention : mentions.getInsertedMentions()) {
                if (!taggedUsers.contains((int) mention.getUserId())) {
                    taggedUsers.add((int) mention.getUserId());
                }
            }
            messageObj.setTaggedUsers(taggedUsers);
        }*/

        fuguMessageList.add(new EventItem(messageObj));
        unsentMessages.put(muid, new EventItem(messageObj));
        etMsg.setText("");
        updateRecycler(userData.getUserId());

        scrollListToBottom();
    }

    private void sendMessage(String message, int messageType, String imageUrl, String thumbnailUrl,
                             ArrayList<Integer> ids, String localPath) {
        sendMessage(message, messageType, imageUrl, thumbnailUrl, ids, localPath, null, -1);
    }

    private void sendMessage(String message, int messageType, String imageUrl, String thumbnailUrl,
                             ArrayList<Integer> ids, String localPath, String localMuid, int localIndex) {
        String removsinglequote = message.trim();
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());

        int index = fuguMessageList.size();
        if(localIndex>0)
            index = localIndex;

        String muid;

        if (TextUtils.isEmpty(localMuid)) {
            muid = UUID.randomUUID().toString() + "." + new Date().getTime();
            addMessageToList(removsinglequote, messageType, localPath, localPath, muid, localPath);
        } else {
            muid = localMuid;
        }
        //addMessageToList(removsinglequote, messageType, localPath, localPath, muid, localPath);

        if(messageType == IMAGE_MESSAGE && !TextUtils.isEmpty(localPath)) {
            uploadFileServerCall(localPath, "image/*", fuguMessageList.size() - 1, muid);
        } else {
            try {
                JSONObject messageJson = new JSONObject();
                if (String.valueOf(userData.getFullName()) != null) {
                    messageJson.put("full_name", String.valueOf(userData.getFullName()));
                }
                messageJson.put("muid", muid);
                messageJson.put("is_message_expired", 0);
                messageJson.put("message", removsinglequote);
                messageJson.put("message_type", messageType);
                messageJson.put(USER_TYPE, UserType.AGENT.getOrdinal());
                messageJson.put("date_time", DateUtils.getInstance().convertToUTC(localDate));
                messageJson.put("message_index", fuguMessageList.size() - 1);

                if (messageType == IMAGE_MESSAGE) {
                    if (!imageUrl.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                        messageJson.put("image_url", imageUrl);
                        messageJson.put("thumbnail_url", thumbnailUrl);
                    } else if (!TextUtils.isEmpty(localPath)) {
                        messageJson.put("local_url", localPath);
                    }
                }

                messageJson.put("is_typing", TYPING_SHOW_MESSAGE);

                messageJson.put("message_status", MESSAGE_UNSENT);
                messageJson.put("user_id", userData.getUserId());
                messageJson.put("user_type", UserType.AGENT.getOrdinal());

                unsentMessageMapNew.put(muid, messageJson);
                if (messageType == IMAGE_MESSAGE) {
                    if (!TextUtils.isEmpty(localPath)) {
                        uploadFileServerCall(localPath, "image/*", fuguMessageList.size() - 1, muid);
                    } else {
                        if (isNetworkAvailable()) {
                            sendMessages();
                        }
                    }
                } else {
                    if (!messageSendingRecursion && isNetworkAvailable()) {
                        sendMessages();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    int sendingTry = 0;

    private synchronized void sendMessages() {
        if (getmClient() != null) {
            if (getmClient().isConnectedServer()) {
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
                        if (!messageSending && messageJson.optInt("is_message_expired", 0) == 0) {
                            Log.e(TAG, "Sending: " + new Gson().toJson(messageJson));
                            sendingTry = 0;
                            messageSending = true;
                            getmClient().publish("/" + String.valueOf(conversation.getChannelId()), messageJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*{
                    Log.e(TAG, "Sending keys: " + key);
                    sendingTry = sendingTry + 1;
                    if(sendingTry == 3) {
                        messageSendingRecursion = false;
                        messageSending = false;
                    }
                    JSONObject messageJson = unsentMessageMapNew.get(key);
                    if (!messageSending && messageJson.optInt("message_type") != IMAGE_MESSAGE && messageJson.optInt("is_message_expired", 0) == 0) {
                        Log.e(TAG, "Sending: " + new Gson().toJson(messageJson));
                        sendingTry = 0;
                        messageSending = true;
                        MyApplication.getmClient().publish("/" + String.valueOf(conversation.getChannelId()), messageJson);

                    }
                }*/
                if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
                    isTyping = TypingMode.TYPING_START.getOrdinal();
                }
            } else {
                try {
                    connectAgainToServer();
                } catch (Exception e) {

                }
            }
        }
    }

    private void publishOnFaye(String message, int messageType, String imageUrl, String thumbnailUrl, int notificationType, String muid) {
        publishOnFaye(message, messageType, imageUrl, thumbnailUrl, notificationType, false, null, muid);
    }

    private void publishOnFaye(String message, int messageType, String imageUrl, String thumbnailUrl,
                               int notificationType, boolean isPrivateNotes, ArrayList<Integer> ids, String muid) {

        String removsinglequote = message.trim();
        String localDate = DateUtils.getInstance().getFormattedDate(new Date());

        try {

            if (isNetworkAvailable()) {

                JSONObject messageJson = new JSONObject();

                if (notificationType == READ_MESSAGE) {
                    messageJson.put("notification_type", notificationType);
                    messageJson.put("channel_id", conversation.getChannelId());
                } else {
                    if (userData.getFullName() != null) {
                        messageJson.put("full_name", userData.getFullName());
                    }

                    messageJson.put("message", removsinglequote);
                    messageJson.put("message_type", messageType);
                    messageJson.put(USER_TYPE, UserType.AGENT.getOrdinal());
                    messageJson.put("date_time", DateUtils.getInstance().convertToUTC(localDate));
                    messageJson.put("message_index", fuguMessageList.size() - 1);
                    if (isPrivateNotes) {
                        messageJson.put("tagged_users", ids);
                    }

                    if (messageType == IMAGE_MESSAGE && !imageUrl.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                        messageJson.put("image_url", imageUrl);
                        messageJson.put("thumbnail_url", thumbnailUrl);
                    }

                    if (messageType == TEXT_MESSAGE) {
                        messageJson.put("is_typing", isTyping);
                    } else {
                        messageJson.put("is_typing", TYPING_SHOW_MESSAGE);
                    }

                    messageJson.put("message_status", MESSAGE_UNSENT);
                }
                if (!TextUtils.isEmpty(muid))
                    messageJson.put("muid", muid);

                messageJson.put("user_id", userData.getUserId());
                messageJson.put("user_type", UserType.AGENT.getOrdinal());

                FuguLog.v("messageJson", "messageJson = " + new Gson().toJson(messageJson));
                if (getmClient() != null) {
                    if (getmClient().isConnectedServer()) {
                        getmClient().publish("/" + String.valueOf(conversation.getChannelId()), messageJson);

                        if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
                            isTyping = TypingMode.TYPING_START.getOrdinal();
                        }

                    } else {
                        if (!getmClient().isConnectedServer())
                            getmClient().connectServer();
                    }
                }
            }
//            else if (!removsinglequote.isEmpty()) {
//                unsentMessageMap.put(fuguMessageList.size() - 1, fuguMessageList.get(fuguMessageList.size() - 1));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAnim() {
        aviTyping.show();
        llTyping.setVisibility(View.VISIBLE);
        // or avi.smoothToShow();
    }

    private void stopAnim() {
        llTyping.setVisibility(View.GONE);
        aviTyping.hide();
        // or avi.smoothToHide();
    }

    @Override
    protected void onDestroy() {
        isAgentChatActivity = false;
        FuguNotificationConfig.isChannelActivityOnPause = false;
        stopFayeClient();

        FuguLog.i(TAG, "getUIListeners = " + FuguConfig.getInstance().getUIListeners(UnreadListener.class).size());
        for (UnreadListener listener : FuguConfig.getInstance().getUIListeners(UnreadListener.class)) {
            FuguLog.i(TAG, "  = " + listener);
            if (listener != null) {
                listener.sendTotalUnreadCount();
                listener.getUnreadCount();
            }
        }

//        UnreadCountHelper.getInstance().sendTotalUnreadCount();
//        UnreadCountHelper.getInstance().getUnreadCount();
        pushChannelId = -1L;
        currentChannelId = -1L;
        FuguNotificationConfig.agentPushChannelId = -1L;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        isReadAcknowledgement = true;
        FuguNotificationConfig.isChannelActivityOnPause = false;
        isAgentChatActivity = true;
        super.onResume();
        pushChannelId = conversation.getChannelId();
        currentChannelId = conversation.getChannelId();
        if (!isFirstTimeOpened && isNetworkAvailable()) {
            setConnectionMessage(1);
            getMessages(false, true);
        }
        isFirstTimeOpened = false;

        if (isMsgReceviedInBackground && isNetworkAvailable()) {
            isMsgReceviedInBackground = false;
            sendReadAcknowledgement();
        }
    }

    @Override
    protected void onPause() {
        isReadAcknowledgement = false;
        FuguNotificationConfig.isChannelActivityOnPause = true;
        super.onPause();
        stopTyping();
        try {
            if (conversation.getChannelId() > 0) {
                AgentCommonData.removeTotalUnreadCount(conversation.getChannelId().intValue());
            }
        } catch (Exception e) {
            FuguLog.e(TAG, "Error on saving total unread count: " + e.toString());
        }
        pushChannelId = -1L;
        if (unsentMessages != null && unsentMessages.size() == 0) {
            AgentCommonData.removeUnsentMessageMapChannel(conversation.getChannelId());
            AgentCommonData.removeUnsentMessageChannel(conversation.getChannelId());
        } else {
            AgentCommonData.setUnsentMessageMapByChannel(conversation.getChannelId(), unsentMessageMapNew);
            AgentCommonData.setUnsentMessageByChannel(conversation.getChannelId(), unsentMessages);
        }
        AgentCommonData.setSentMessageByChannel(conversation.getChannelId(), sentMessages);

    }

    public void createConversation() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(FuguAppConstant.ACCESS_TOKEN, String.valueOf(userData.getAccessToken()));
        params.put("initiator_en_agent_id", String.valueOf(userData.getEnUserId()));
        if (fragmentType == FragmentType.USER_CHAT.getOrdinal()) {
            ArrayList<String> userUniqueKey = new ArrayList<>();
            userUniqueKey.add(conversation.getUserUniqueKeys());
            params.put(FuguAppConstant.OTHER_USER_UNIQUE_KEY, new Gson().toJson(userUniqueKey));

        } else {
            params.put("user_id", String.valueOf(conversation.getUserId()));
        }
        params.put("chat_type", "0");

        pbSendingImage.setVisibility(View.VISIBLE);
        ivSend.setVisibility(View.GONE);

        RestClient.getAgentApiInterface().createConversation(params)
                .enqueue(new ResponseResolver<CreateConversation>(AgentChatActivity.this, false, true) {
                    @Override
                    public void success(CreateConversation createConversation) {
                        conversation = new Conversation();
                        conversation.setUserId(Integer.parseInt(createConversation.getData().getUserId()));
                        conversation.setLabel(createConversation.getData().getLabel());
                        conversation.setChannelName(createConversation.getData().getChannelName());
                        conversation.setChannelId(createConversation.getData().getChannelId());

                        ivSend.setVisibility(View.VISIBLE);
                        pbSendingImage.setVisibility(View.GONE);

                        if (!etMsg.getText().toString().trim().isEmpty()) {
                            if (isBlock) {
                                Animation animation1 = AnimationUtils.loadAnimation(AgentChatActivity.this, R.anim.hippo_shake);
                                tvAssignNotify.startAnimation(animation1);
                                return;
                            }

                            isTyping = 0;
//                            publishOnFaye(etMsg.getText().toString().trim(), messageType,
//                                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), NOTIFICATION_DEFAULT, null);

                        }

                        if (fragmentType == FragmentType.USER_CHAT.getOrdinal()) {
                            getMessages();
                        } else {
                            apiGetConversation(true, createConversation.getData().getChannelId().intValue());
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        ivSend.setVisibility(View.VISIBLE);
                        pbSendingImage.setVisibility(View.GONE);
                    }

                });
    }

    public void apiGetConversation(boolean showLoading, final Integer channelId) {
        if (isNetworkAvailable()) {
            int[] statusArray = new int[]{MessageMode.OPEN_CHAT.getOrdinal()};
            int[] typeArray = new int[]{
                    ConversationMode.DEFAULT.getOrdinal()};
            int[] labelsArray = new int[]{};
            int pageStart = 1;
            FuguLog.v("statusArray......------", Arrays.toString(statusArray));
            FuguLog.v("typeArray......------", Arrays.toString(typeArray));
            HashMap<String, Object> params = new HashMap<>();
            params.put(FuguAppConstant.EN_USER_ID, String.valueOf(userData.getEnUserId()));
            params.put(FuguAppConstant.ACCESS_TOKEN, String.valueOf(userData.getAccessToken()));
            params.put(FuguAppConstant.STATUS, Arrays.toString(statusArray));
            params.put(FuguAppConstant.DEVICE_TYPE, 1);
//            params.put(FuguAppConstant.APP_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
            params.put(FuguAppConstant.TYPE, Arrays.toString(typeArray));
//            if (labelsArray.length > 0) {
//                params.put(FuguAppConstant.LABEL, Arrays.toString(labelsArray));
//            }
            if (pageStart != 0) {
                params.put(FuguAppConstant.PAGE_START, String.valueOf(pageStart));
            }

            RestClient.getAgentApiInterface().getConversation(params)
                    .enqueue(new ResponseResolver<GetConversationResponse>(this, showLoading, false) {
                        @Override
                        public void success(final GetConversationResponse getConversationResponse) {
                            LoadingBox.showOn(AgentChatActivity.this);
                            try {
                                FuguLog.e("Size of Array", getConversationResponse.getData().getConversation().size() + "");
                                if (getConversationResponse != null) {
                                    for (int i = 0; i < getConversationResponse.getData().getConversation().size(); i++) {
                                        if (String.valueOf(channelId).equalsIgnoreCase(String.valueOf(getConversationResponse.getData().getConversation().get(i).getChannelId()))) {
                                            conversation = new Conversation();
                                            conversation = getConversationResponse.getData().getConversation().get(i);
                                        }
                                    }
                                    pushChannelId = conversation.getChannelId();
                                    currentChannelId = conversation.getChannelId();
                                    isFromGetConversation = true;
                                    initViews();
                                    fetchIntentData();
                                    setUpUI();
                                    stateChangeListeners();

                                    setSubscribeChannel();

                                }
                                LoadingBox.hide();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            FuguLog.e("error", "error" + error.getMessage());
                            LoadingBox.hide();
                            Toast.makeText(AgentChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AgentChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            if (conversation.getChannelId() > 0 && fayeDisconnect) {
                if (retryLayout.getVisibility() != View.VISIBLE) {
                    setConnectionMessage(2);
                }
                return;
            }

            if (conversation.getChannelId() > 0) {
                if (!etMsg.getText().toString().trim().isEmpty()) {
                    if (isBlock) {
                        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.hippo_shake);
                        tvAssignNotify.startAnimation(animation1);
                        return;
                    }

                    isTyping = 0;
                    try {
                        sendMessage(etMsg.getText().toString().trim(), messageType, "", "", null, "");
                    } catch (Exception ignored) {

                    }
                }
            } else {
                if(!isNetworkAvailable()) {
                    Toast.makeText(AgentChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                    return;
                }
                createConversation();
            }
        } else if (v.getId() == R.id.tvNoInternet) {
            getMessages();
        } else if (v.getId() == R.id.ivAttachment) {
            if(!isNetworkAvailable()) {
                Toast.makeText(AgentChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            selectImage();
        }
    }

    private void updateRecycler(final int userId) {
        if (fuguMessageAdapter != null) {
            fuguMessageAdapter.updateList(fuguMessageList);
            //scrollListToBottom();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fuguMessageAdapter != null) {
                        try {
                            fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                            if (fuguMessageAdapter.getItemCount() > 0
                                    && layoutManager.findLastVisibleItemPosition() == fuguMessageList.size() - 2) {
                                scrollBottomCount = 0;
                                scrollListToBottom();
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

    boolean isApiRunning;
    private HashSet<String> messagesApi = new HashSet<>();

    private void getMessages() {
        getMessages(false, false);
    }

    private void getMessages(final boolean flag, final boolean isFromOnResume) {
        if (isApiRunning) {
            messagesApi.add("getMessages");
            return;
        }

        if (conversation.getChannelId() > 0) {
            if (isNetworkAvailable()) {
                if (!allMessagesFetched || isNetworkStateChanged || isFromOnResume) {
                    FuguAgentGetMessageParams commonParams = new FuguAgentGetMessageParams(userData.getAccessToken(),
                            conversation.getChannelId().intValue(),
                            userData.getEnUserId(),
                            isFromOnResume ? 1 : pageStart);

                    if (isFromOnResume && fuguMessageList.size() > 100)
                        commonParams.setPageEnd(fuguMessageList.size() - dateItemCount);

                    try {
                        if (!showLoading && (pageStart == 1 || isFromOnResume)) {
                            setConnectionMessage(1);
                        } else {
                            setConnectionMessage(0);
                        }
                    } catch (Exception e) {

                    }

                    isApiRunning = true;
                    RestClient.getAgentApiInterface().getMessages(commonParams)
                            .enqueue(new ResponseResolver<FuguAgentGetMessageResponse>(this, showLoading, false) {
                                @Override
                                public void success(FuguAgentGetMessageResponse fuguGetMessageResponse) {

                                    if (fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null) {
                                        if (fuguGetMessageResponse.getData().isDisableReply()) {
                                            llBottom.setVisibility(View.GONE);
                                        }
                                    }

                                    try {
                                        if(userData.isVideoCallEnabled()
                                                && fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null) {
                                            if(fuguGetMessageResponse.getData().isAllowVideoCall() && fuguGetMessageResponse.getData().getUserId() > 0
                                                    && userData.getUserId().intValue() == fuguGetMessageResponse.getData().getUserId()) {
                                                ivVideoView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    } catch (Exception e) {
                                        if(FuguConfig.DEBUG)
                                            e.printStackTrace();
                                    }

                                    try {
                                        if(AgentCommonData.getUserData().isAudioCallEnabled()
                                                && fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null) {
                                            if(fuguGetMessageResponse.getData().isAllowAudioCall() && fuguGetMessageResponse.getData().getUserId() > 0
                                                    && AgentCommonData.getUserData().getUserId().intValue() == fuguGetMessageResponse.getData().getUserId()) {
                                                ivAudioView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    } catch (Exception e) {
                                        if(FuguConfig.DEBUG)
                                            e.printStackTrace();
                                    }

                                    boolean flaghide = true;
                                    if (fuguGetMessageResponse.getData().getMessages() != null) {
                                        setAssignTaskLocaly(fuguGetMessageResponse);
                                        tagData = fuguGetMessageResponse.getData().getTags();
                                        userData.setUserName(fuguGetMessageResponse.getData().getFullName());
                                        onSubscribe = fuguGetMessageResponse.getData().getOnSubscribe();

                                        FuguLog.e("getMessages onSubscribe", "==" + onSubscribe);

                                        LinkedHashMap<String, ListItem> tempMessages = new LinkedHashMap<>();
                                        LinkedHashMap<String, ListItem> tempSentMessages = new LinkedHashMap<>();

                                        String tempSentAtUtc = "";

                                        AgentData messageResponseData = fuguGetMessageResponse.getData();

                                        if(userData == null)
                                            userData = AgentCommonData.getUserData();

                                        if(messageResponseData.getUserId() > 0 && userData.getUserId() == messageResponseData.getUserId())
                                            ivVideoView.setVisibility(View.VISIBLE);

                                        if (conversation != null && conversation.getAgentId() == null)
                                            conversation.setAgentId(messageResponseData.getUserId());

                                        if (conversation != null && conversation.getUserId() == null)
                                            conversation.setUserId(messageResponseData.getOwnerId());

                                        if (messageResponseData.getMessages().size() < messageResponseData.getPageSize()) {
                                            allMessagesFetched = true;
                                        }

                                        if (pageStart == 1 || isFromOnResume) {
                                            fuguMessageList.clear();
                                            tempSentMessages.putAll(sentMessages);
                                            sentMessages.clear();
                                            dateItemCount = 0;
                                        }

                                        if (pageStart == 1 && !flag && !isFromOnResume) {
                                            fuguMessageList.clear();
                                            dateItemCount = 0;
                                        }

                                        conversation.setStatus(messageResponseData.getChannelStatus());

                                        if (conversation.getUserId() > 0) {
                                            //ivViewInfo.setVisibility(View.VISIBLE);
                                            infoClickable = true;
                                        }


                                        int dateCount = 0;
                                        try {
                                            for (int i = 0; i < messageResponseData.getMessages().size(); i++) {
                                                Message messageObj = fuguGetMessageResponse.getData().getMessages().get(i);
                                                boolean isSelf = false;
                                                if (messageObj.getUserType() != null) {
                                                    if (messageObj.getUserType() == UserType.AGENT.getOrdinal()) {
                                                        isSelf = true;
                                                    }
                                                }

                                                String localDate = dateUtils.convertToLocal(messageObj.getSentAtUtc(), inputFormat, outputFormat);

                                                if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                                    tempMessages.put(localDate, new HeaderItem(localDate));
                                                    tempSentAtUtc = localDate;
                                                    dateItemCount = dateItemCount + 1;
                                                    dateCount = dateCount + 1;
                                                }

                                                int messageType;
                                                if (messageObj.getMessageType() == PRIVATE_NOTE) {
                                                    messageType = PRIVATE_NOTE;
                                                } else if (messageObj.getMessageType() == ASSIGNMENT_MESSAGE) {
                                                    messageType = ASSIGNMENT_MESSAGE;
                                                } else if(messageObj.getMessageType() == VIDEO_CALL) {
                                                    if(isSelf)
                                                        messageType = 18;
                                                    else
                                                        messageType = 19;
                                                } else {
                                                    messageType = messageObj.getImageUrl().isEmpty() ? TEXT_MESSAGE : IMAGE_MESSAGE;
                                                }

                                                String muid = messageObj.getMuid();
                                                if (TextUtils.isEmpty(muid))
                                                    muid = messageObj.getId();

                                                Message message = new Message(messageObj.getfromName(),
                                                        messageObj.getUserId(),
                                                        messageObj.getMessage(),
                                                        messageObj.getSentAtUtc(),
                                                        isSelf,
                                                        messageObj.getMessageStatus(),
                                                        i,
                                                        messageObj.getImageUrl(),
                                                        messageObj.getThumbnailUrl(),
                                                        messageType,
                                                        messageObj.getUserType());

                                                message.setMuid(muid);

                                                /*if (messageObj.getMessageType() == FEEDBACK_MESSAGE) {
                                                    message.setIsRatingGiven(messageObj.getIsRatingGiven());
                                                    message.setTotalRating(messageObj.getTotalRating());
                                                    message.setRatingGiven(messageObj.getRatingGiven());
                                                    message.setComment(messageObj.getComment());

                                                    message.setLineBeforeFeedback(messageObj.getLineBeforeFeedback());
                                                    message.setLineAfterFeedback_1(messageObj.getLineAfterFeedback_1());
                                                    message.setLineAfterFeedback_2(messageObj.getLineAfterFeedback_2());
                                                } else if (messageObj.getMessageType() == BOT_FORM_MESSAGE) {
                                                    message.setContentValue(messageObj.getContentValue());
                                                    message.setValues(messageObj.getValues());
                                                    message.setId(messageObj.getId());
                                                }*/

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
                                                /*if (!TextUtils.isEmpty(messageObj.getMuid()) && unsentMessageMapNew.size() > 0) {
                                                    unsentMessageMapNew.remove(messageObj.getMuid());
                                                    unsentMessages.remove(messageObj.getMuid());
                                                }*/


                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        /*for (int i = 0; i < fuguMessageList.size(); i++) {
                                            if (fuguMessageList.get(i) instanceof HeaderItem) {
                                                if (tempSentAtUtc.equalsIgnoreCase(((HeaderItem) fuguMessageList.get(i)).getDate())) {
                                                    fuguMessageList.remove(i);
                                                    dateItemCount = dateItemCount - 1;
                                                    break;
                                                }
                                            }
                                        }*/

                                        Log.e(TAG, "tempSentMessages 2: " + new Gson().toJson(tempSentMessages.values()));
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
                                            long lastMessageTime = dateUtils.getTimeInLong(messageResponseData.getMessages().get(messageResponseData.getMessages().size() - 1).getSentAtUtc());
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
                                            if (!sentMessages.containsKey(localDate)) {
                                                if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                                    fuguMessageList.add(new HeaderItem(localDate));
                                                    tempSentAtUtc = localDate;
                                                    System.out.println("Date 2: " + localDate);
                                                }
                                            }
                                            if(unsentMessageMapNew != null && unsentMessageMapNew.size() == 0) {
                                                CommonData.removeUnsentMessageMapChannel(currentChannelId);
                                            } else {
                                                CommonData.setUnsentMessageMapByChannel(currentChannelId, unsentMessageMapNew);
                                            }


                                            if (unsentMessages != null && unsentMessages.size() == 0) {
                                                AgentCommonData.removeUnsentMessageChannel(currentChannelId);
                                            } else {
                                                AgentCommonData.setUnsentMessageByChannel(currentChannelId, unsentMessages);
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

                                        /*for (String key : unsentMessages.keySet()) {
                                            ListItem listItem = unsentMessages.get(key);
                                            String time = ((EventItem) listItem).getEvent().getSentAtUtc();
                                            String localDate = dateUtils.convertToLocal(time, inputFormat, outputFormat);
                                            //String date = dateUtils.getDate();
                                            if (!tempSentAtUtc.equalsIgnoreCase(localDate)) {
                                                fuguMessageList.add(new HeaderItem(localDate));
                                                tempSentAtUtc = localDate;
                                                System.out.println("Date: " + localDate);
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
                                            //fuguMessageList.add(unsentMessages.get(key));
                                        }*/


                                        tvNoInternet.setVisibility(View.GONE);
                                        llRoot.setVisibility(View.VISIBLE);

                                        if (pageStart == 1 || isFromOnResume) {
                                            showLoading = false;
                                            sentAtUTC = tempSentAtUtc;
                                            AgentCommonData.setAgentMessageResponse(conversation.getChannelId().intValue(), fuguGetMessageResponse);
                                            //setRecyclerViewData();

                                            try {
                                                videoCustomerName = fuguGetMessageResponse.getData().getCustomerName();
                                                videoAgentName = fuguGetMessageResponse.getData().getAgentName();
                                                isChatAssignToMe = (fuguGetMessageResponse.getData().getUserId() == AgentCommonData.getUserData().getUserId().intValue()) ? true : false;
                                                try {
                                                    if(AgentCommonData.getUserData().isVideoCallEnabled()
                                                            && fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null && fuguGetMessageResponse.getData().isAllowVideoCall()) {
                                                        isVideoCallEnable = true;
                                                    }
                                                } catch (Exception e) {

                                                }
                                            } catch (Exception e) {

                                            }

                                            try {
                                                flaghide = false;
                                                setConnectionMessage(0);
                                                setRecyclerViewData();
                                                /*new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        setRecyclerViewData();
                                                        setConnectionMessage(0);
                                                    }
                                                }, 400);*/
                                            } catch (Exception e) {
                                                flaghide = true;
                                                if(FuguConfig.DEBUG) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            pbLoading.setVisibility(View.GONE);
                                            fuguMessageAdapter.updateList(fuguMessageList);
                                            fuguMessageAdapter.notifyItemRangeInserted(0, messageResponseData.getMessages().size() + dateCount);
                                        }

                                        pageStart = sentMessages.values().size() + 1 - dateItemCount;
                                    } else {
                                        allMessagesFetched = true;
                                    }
                                    if(flaghide)
                                        setConnectionMessage(0);
                                    pbLoading.setVisibility(View.GONE);
//
                                    if (conversation.getChannelId() > 0) {
                                        AgentCommonData.removeTotalUnreadCount(conversation.getChannelId().intValue());
                                    }

                                    isApiRunning = false;
                                    if(messagesApi != null && messagesApi.size()>0) {
                                        if(messagesApi.contains("getMessages")) {
                                            messagesApi.remove("getMessages");
                                            getMessages(flag, isFromOnResume);
                                        }
                                    }
                                }


                                @Override
                                public void failure(APIError error) {
                                    isApiRunning = false;
                                    if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                        try {
                                            Toast.makeText(AgentChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (error.getStatusCode() == ApiResponseFlags.SESSION_EXPIRED.getOrdinal()) {
                                                if (getmClient() != null) {
                                                    if (getmClient().isConnectedServer()) {
                                                        getmClient().unsubscribeChannel(String.valueOf(conversation.getChannelId()));
                                                    }
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (pageStart == 1 &&
                                                (AgentCommonData.getSentMessageByChannel(conversation.getChannelId()) == null ||
                                                        AgentCommonData.getSentMessageByChannel(conversation.getChannelId()).size() == 0)) {
                                            llRoot.setVisibility(View.GONE);
                                            tvNoInternet.setVisibility(View.VISIBLE);
                                        }
                                        pbLoading.setVisibility(View.GONE);
                                        Toast.makeText(AgentChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    if(messagesApi != null && messagesApi.size()>0) {
                                        if(messagesApi.contains("getMessages")) {
                                            messagesApi.remove("getMessages");
                                            getMessages(flag, isFromOnResume);
                                        }
                                    }
                                }
                            });
                }
            } else {
                if (pageStart == 1 &&
                        (AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue()) == null ||
                                AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue()).getData().getMessages().size() == 0)) {
                    llRoot.setVisibility(View.GONE);
                    tvNoInternet.setVisibility(View.VISIBLE);
                }
                pbLoading.setVisibility(View.GONE);
                setConnectionMessage(3);
            }
        }
    }

    private void setRecyclerViewData() {
//        rvMessages = findViewById(R.id.rvMessages);
//        fuguMessageAdapter = new FuguAgentMessageAdapter(AgentChatActivity.this, fuguMessageList, conversation);
//        layoutManager = new LinearLayoutManager(AgentChatActivity.this);
//        layoutManager.setStackFromEnd(true);

//        rvMessages.setLayoutManager(layoutManager);

        if(fuguMessageAdapter != null) {
            fuguMessageAdapter.setCustomeName(videoCustomerName);
            fuguMessageAdapter.setAgentName(videoAgentName);
            fuguMessageAdapter.setIsChatAssignToMe(isChatAssignToMe);
            fuguMessageAdapter.isVideoCallEnabled(isVideoCallEnable);
        }

        try {
            fuguMessageAdapter.updateList(fuguMessageList);
            fuguMessageAdapter.notifyDataSetChanged();
            rvMessages.setVisibility(View.VISIBLE);
            rvMessages.setItemAnimator(null);
            rvMessages.scrollToPosition(fuguMessageList.size()-1);
        } catch (Exception e) {
            if(FuguConfig.DEBUG)
                e.printStackTrace();
        }

        fuguMessageAdapter.setOnVideoCallListener(new FuguMessageAdapter.onVideoCall() {
            @Override
            public void onVideoCallClicked(int callType) {
                /*try {
                    FuguAgentGetMessageResponse fuguGetMessageResponse = AgentCommonData.getAgentMessageResponse(conversation.getChannelId().intValue());
                    if(userData.isVideoCallEnabled()
                            && fuguGetMessageResponse != null && fuguGetMessageResponse.getData() != null) {
                        if(fuguGetMessageResponse.getData().isAllowVideoCall() && fuguGetMessageResponse.getData().getUserId() > 0
                                && userData.getUserId().intValue() == fuguGetMessageResponse.getData().getUserId()) {
                            videoCallInit(callType);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

        fuguMessageAdapter.setOnRetryListener(new FuguAgentMessageAdapter.OnRetryListener() {
            @Override
            public void onRetry(String file, String fileType, int messageIndex, String muid) {
                uploadFileServerCall(file, fileType, messageIndex, muid);
            }

            @Override
            public void onMessageRetry(String muid, int position) {
                try {
                    //String updatedMuid = UUID.randomUUID().toString();

                    JSONObject jsonObject = unsentMessageMapNew.get(muid);
                    ListItem listItem = unsentMessages.get(muid);

                    //fuguMessageList.remove(position);

                    jsonObject.put("is_message_expired", 0);
                    //jsonObject.put("muid", updatedMuid);
                    jsonObject.put("message_index", position);
                    String localDate = DateUtils.getInstance().getFormattedDate(new Date());
                    jsonObject.put("date_time", DateUtils.getInstance().convertToUTC(localDate));

                    ((EventItem) listItem).getEvent().setIsMessageExpired(0);
                    ((EventItem) listItem).getEvent().setMessageIndex(position);
                    ((EventItem) listItem).getEvent().setSentAtUtc(DateUtils.getInstance().convertToUTC(localDate));
                    //((EventItem) listItem).getEvent().setMuid(updatedMuid);

                    //unsentMessageMapNew.remove(muid);
                    //unsentMessages.remove(muid);

                    unsentMessageMapNew.put(muid, jsonObject);
                    unsentMessages.put(muid, listItem);
                    fuguMessageList.remove(position);
                    fuguMessageList.add(position, listItem);

                    updateRecycler(userData.getUserId());
                    if (fuguMessageAdapter != null) {
                        fuguMessageAdapter.updateList(fuguMessageList);
                        //scrollListToBottom();
                    }
                    fuguMessageAdapter.notifyItemRangeChanged(position, fuguMessageList.size());
//                    scrollListToBottom();

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
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE && isNetworkAvailable()) {
                    pbLoading.setVisibility(View.VISIBLE);
                    if (unsentMessageMapNew.size() == 0) {
                        getMessages();
                    }
                }
            }
        });

        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                position = newState;
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NETWORK_STATE_INTENT:
                    FuguLog.d(TAG, "Network connectivity change " + intent.getBooleanExtra("isConnected", false));
                    if (intent.getBooleanExtra("isConnected", false)) {
                        if (getmClient() != null) {
                            getmClient().setAgentListener(AgentChatActivity.this);
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
                            getMessages();
                        } else {
                            isNetworkStateChanged = true;
                            btnRetry.setText("Connecting...");
                        }
                        enableButtons();
                    } else if (llTyping.getVisibility() == View.VISIBLE) {
                        stopAnim();
                    }
                    if (!intent.getBooleanExtra("isConnected", false)) {
                        setConnectionMessage(3);
                        enableButtons();
                    }
                    break;
                case NOTIFICATION_TAPPED:
                    conversation = new Gson().fromJson(intent.getStringExtra(FuguAppConstant.CONVERSATION), Conversation.class);
                    FuguNotificationConfig.agentPushChannelId = conversation.getChannelId();
                    currentChannelId = conversation.getChannelId();
                    clearNotification(currentChannelId);
                    allMessagesFetched = false;
                    pageStart = 1;
                    setUpUI();
                    if (mClient.isConnectedServer()) {
                        mClient.unsubscribeAll();
                        mClient.subscribeChannel("/" + String.valueOf(conversation.getChannelId()));
                    } else {
                        mClient.connectServer();
                    }
                    break;
            }

        }
    };

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (getmClient() != null) {
            if (getmClient().isConnectedServer()) {
                getmClient().publish("/" + String.valueOf(conversation.getChannelId()), prepareMessageJson(0));
            }
        }
        super.onBackPressed();
    }

    /**
     * Method to select an image for Position in
     * the List of AddImages
     */
    public void selectImage() {
        imageUtils.showImageChooser(OPEN_CAMERA_ADD_IMAGE, OPEN_GALLERY_ADD_IMAGE, SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        FuguLog.e(TAG, "onRequestPermissionsResult" + requestCode);

        if (grantResults.length > 0
                && FuguConfig.getInstance().isPermissionGranted(AgentChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            switch (requestCode) {
                case PERMISSION_CONSTANT_CAMERA:
                    imageUtils.startCamera();
                    break;

                case PERMISSION_READ_FILE:
                    compressAndSaveImageBitmap();
                    break;

                case PERMISSION_CONSTANT_GALLERY:
                    imageUtils.openGallery();
                    break;

                case PERMISSION_SAVE_BITMAP:
                    compressAndSaveImageBitmap();
                    break;
            }
        } else {
            Toast.makeText(AgentChatActivity.this, getString(R.string.fugu_permission_was_not_granted_text),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                Bundle conData = new Bundle();
                if (data != null && data.hasExtra(FuguAppConstant.CHANNEL_ID)) {
                    conData.putString(FuguAppConstant.CHANNEL_ID, data.getExtras().getString(FuguAppConstant.CHANNEL_ID));
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    setResult(resultCode, intent);
                    onBackPressed();
                }
                break;
            case OPEN_CAMERA_ADD_IMAGE:
                if (resultCode == RESULT_OK) {
                    compressAndSaveImageBitmap();
                }
                break;

            case OPEN_GALLERY_ADD_IMAGE:
                try {
                    imageUtils.copyFileFromUri(data.getData());
                    compressAndSaveImageBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Problem in fetching image...", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void compressAndSaveImageBitmap() {
        try {
            String image = imageUtils.compressAndSaveBitmap(AgentChatActivity.this);//(null, squareEdge);
            if (image == null) {
                Toast.makeText(AgentChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (conversation.getChannelId() < 0) {
                    pbSendingImage.setVisibility(View.VISIBLE);
                    ivSend.setVisibility(View.GONE);
                }
                sendMessage(getString(R.string.fugu_empty), IMAGE_MESSAGE, "", "", null, image);
                //addMessageToList(getString(R.string.empty), IMAGE_MESSAGE, image, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AgentChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFileServerCall(String file, String fileType, final int messageIndex, final String muid) {
        if (getmClient() != null) {
            if (isNetworkAvailable() && getmClient().isConnectedServer()) {

                MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
                MultipartParams multipartParams = multipartBuilder
                        .addFile("file", new File(file))
                        .add(FuguAppConstant.ACCESS_TOKEN, userData.getAccessToken())
                        .add("file_type", fileType).build();

                //Log.v("map = ", multipartParams.getMap().toString());
                RestClient.getApiInterface()
                        .uploadFile(multipartParams.getMap())
                        .enqueue(new ResponseResolver<FuguUploadImageResponse>(AgentChatActivity.this, false, false) {
                            @Override
                            public void success(FuguUploadImageResponse fuguUploadImageResponse) {

                                try {
                                    String image_url = fuguUploadImageResponse.getData().getUrl();
                                    String thumbnail_url = fuguUploadImageResponse.getData().getThumbnailUrl();

                                    sendMessage("", IMAGE_MESSAGE, image_url, thumbnail_url,
                                            null, null, muid, messageIndex);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void failure(APIError error) {
                                try {
                                    ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                                    fuguMessageAdapter.notifyItemChanged(messageIndex);
                                    if(ivSend.getVisibility() == View.GONE) {
                                        pbSendingImage.setVisibility(View.GONE);
                                        ivSend.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        });
            } else {
                ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                fuguMessageAdapter.notifyItemChanged(messageIndex);
            }
        }
    }


    private void sendReadAcknowledgement() {
        if (conversation.getChannelId() > -1) {
            publishOnFaye(getString(R.string.fugu_empty), 0,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), READ_MESSAGE, null);
        }
    }

    private void scrollListToBottom() {
        try {
            rvMessages.scrollToPosition(fuguMessageList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setAssignTaskLocaly(FuguAgentGetMessageResponse fuguGetMessageResponse) {
        try {
            if (conversation != null && conversation.getAgentId() != null) {
                if (!conversation.getAgentId().equals(fuguGetMessageResponse.getData().getUserId())) {
                    if (fuguGetMessageResponse.getData().getUserId() == userData.getUserId()) {
                        String txt = "This chat has been <b>Assigned</b> to you";
                        tvAssignNotify.setVisibility(View.VISIBLE);
                        tvAssignNotify.setText(Html.fromHtml(txt));
                        tvAssignNotify.setBackgroundColor(getResources().getColor(R.color.fugu_green));
                        tvAssignNotify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hippo_ic_self_assign, 0, 0, 0);
                        Bundle conData = new Bundle();
                        conData.putString(FuguAppConstant.CHANNEL_ID, String.valueOf(fuguGetMessageResponse.getData().getChannelId()));
                        Intent intent = new Intent();
                        intent.putExtras(conData);
                        setResult(Overlay.ASSIGNMENT.getOrdinal(), intent);
                        goneAssignLayout();
                    } else if (!fuguGetMessageResponse.getData().getAgentName().equals("") && fuguGetMessageResponse.getData().getUserId() != userData.getUserId()) {
                        String txt = "This chat has been <b>Assigned</b> to " + fuguGetMessageResponse.getData().getAgentName();
                        tvAssignNotify.setVisibility(View.VISIBLE);
                        tvAssignNotify.setText(Html.fromHtml(txt));
                        tvAssignNotify.setBackgroundColor(getResources().getColor(R.color.fugu_assign_color));
                        tvAssignNotify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hippo_ic_self_assign, 0, 0, 0);
                        Bundle conData = new Bundle();
                        conData.putString(FuguAppConstant.CHANNEL_ID, String.valueOf(fuguGetMessageResponse.getData().getChannelId()));
                        Intent intent = new Intent();
                        intent.putExtras(conData);
                        setResult(Overlay.ASSIGNMENT.getOrdinal(), intent);
                    }
                    try {
                        conversation.setAgentId(fuguGetMessageResponse.getData().getUserId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecyclerView getRvMessages() {
        return rvMessages;
    }

    private void goToGmail(String mail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void goToPhone(String phone) {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                "tel", phone, null));
        startActivity(phoneIntent);
    }

    private void handleChatTypeCase() {
        {
            FuguLog.v("fragmentType --->>>", String.valueOf(fragmentType));
            if (fragmentType == FragmentType.MY_CHAT.getOrdinal() &&
                    userData.getAgentType() != AgentType.ADMIN.getOrdinal()) {
                isBlock = true;
            } else {
                isBlock = false;
            }
        }

        if (fragmentType == FragmentType.ALL_CHAT.getOrdinal()) {
            goneAssignLayout();
        }
    }


    private void showKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etMsg, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void goneAssignLayout() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvAssignNotify.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void changeAssignTextBackground(String txt, int color, int drawable) {
        tvAssignNotify.setVisibility(View.VISIBLE);
        tvAssignNotify.setText(Html.fromHtml(txt));
        tvAssignNotify.setBackgroundColor(color);
        tvAssignNotify.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }


    @Override
    public void onAssignChat(JSONObject msgData) {

        String channelId = "";
        try {
            channelId = msgData.getString("channel_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (msgData != null && channelId.equals(String.valueOf(conversation.getChannelId().toString()))) {

            if (msgData.optInt("assigned_by") == msgData.optInt("assigned_to")
                    && userData.getUserId() == msgData.optInt("assigned_to")) {
                FuguLog.v("assign----->>>>", "self");
                String txt = "This chat has been <b>Assigned</b> to you";
                changeAssignTextBackground(txt, getResources().getColor(R.color.fugu_green), R.drawable.hippo_ic_self_assign);
                goneAssignLayout();


            } else if (msgData.optInt("assigned_by") == msgData.optInt("assigned_to")
                    && userData.getUserId() != msgData.optInt("assigned_to")) {
                try {
                    FuguLog.v("done with----->>>>", msgData.get("message").toString());
                    String txt = msgData.get("assigned_by_name").toString() + " assigned the chat to themselves";
                    changeAssignTextBackground(txt, getResources().getColor(R.color.fugu_assign_color), R.drawable.hippo_ic_other_assign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleChatTypeCase();
            } else {
                try {
                    FuguLog.v("done with----->>>>", msgData.get("message").toString());
                    String txt = msgData.get("message").toString();
                    changeAssignTextBackground(txt, getResources().getColor(R.color.fugu_assign_color), R.drawable.hippo_ic_other_assign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleChatTypeCase();
            }
            try {
                conversation.setAgentId(msgData.optInt("assigned_to"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onControlChannelData(JSONObject jsonObject) {

    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public void onReadAll(JSONObject jsonObject) {

    }

    @Override
    public void updateCount(Long channelId) {

    }

    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;

    public static void changeStatus(int status) {
        /*try {
            switch (status) {
                case CONNECTED_TO_INTERNET:
                case CONNECTED_TO_INTERNET_VIA_WIFI:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enableButtons();
                        }
                    }, 500);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {

        }*/
    }

    private void enableButtons() {
        try {
            if (etMsg.getText().toString().trim().length() > 0 && isNetworkAvailable()) {
                ivSend.setClickable(true);
                ivSend.setAlpha(1f);
            } else {
                ivSend.setClickable(false);
                ivSend.setAlpha(0.5f);
            }
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
                    if (getmClient().isConnectedServer()) {
                        getmClient().disconnectServer();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTyping() {
        if (isTyping == TYPING_STARTED) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), messageType,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), NOTIFICATION_DEFAULT, null);
        }
    }

    private void callUnreadHelper(int type) {
        for (UnreadListener listener : FuguConfig.getInstance().getUIListeners(UnreadListener.class))
            switch (type) {
                case 1:

                    break;
                default:

                    break;
            }
    }

    public interface RefreshDone {
        void onRefreshComplete();
    }

    private void setConnectionMessage(int status) {
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

    private boolean isFirstTimeDisconnected = true;
    Handler handlerDisable = new Handler();
    Runnable runnableDisable = new Runnable() {
        @Override
        public void run() {
            setConnectionMessage(2);
        }
    };

    private void clearNotification(Long channelId) {
        try {
            ArrayList<Integer> arrayList = AgentCommonData.getNotificationArray(channelId);
            FuguNotificationConfig.clearNotifications(AgentChatActivity.this, arrayList);
            AgentCommonData.removeNotificationChannel(channelId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
