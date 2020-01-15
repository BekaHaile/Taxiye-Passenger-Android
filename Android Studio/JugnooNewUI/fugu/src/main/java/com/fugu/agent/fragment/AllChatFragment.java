package com.fugu.agent.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.fugu.R;
import com.fugu.agent.AgentChatActivity;
import com.fugu.agent.AgentListActivity;
import com.fugu.agent.Util.ConversationMode;
import com.fugu.agent.Util.FragmentType;
import com.fugu.agent.Util.MessageMode;
import com.fugu.agent.Util.NotificationType;
import com.fugu.agent.Util.Overlay;
import com.fugu.agent.Util.UserType;
import com.fugu.agent.Util.WrapContentLinearLayoutManager;
import com.fugu.agent.adapter.ChatListAdapter;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.helper.ConversationListHelper;
import com.fugu.agent.listeners.AgentServerListener;
import com.fugu.agent.listeners.ConversationListerner;
import com.fugu.agent.listeners.OnUserChannelListener;
import com.fugu.agent.model.ApiResponseFlags;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.agent.recylerviewAnimation.FadeInLeftAnimator;
import com.fugu.constant.FuguAppConstant;
import com.fugu.retrofit.APIError;
import com.fugu.utils.FuguLog;
import com.fugu.utils.Utils;
import com.fugu.utils.loadingBox.LoadingBox;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class AllChatFragment extends BaseFragment implements AgentServerListener, OnUserChannelListener,
        SwipeRefreshLayout.OnRefreshListener, ChatListAdapter.Callback {

    private static final String TAG = AllChatFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private ChatListAdapter chatListAdapter;
    private LinearLayout llNoConversation;
    private WrapContentLinearLayoutManager layoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ConversationListerner listerner;
    private ChatListAdapter.ProgressBarItem progressBarItem;
    private ArrayList<Object> conversationChatList = new ArrayList<>();
    private TextView title_error,detail_error;

    private boolean isPagingApiInProgress;
    private boolean isLastItemReached;
    private Snackbar snackbar;
    private int newMessageCount = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hippo_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.list_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.hippo_white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.fugu_theme_color_primary);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        llNoConversation = view.findViewById(R.id.llNoConversation);
        title_error = view.findViewById(R.id.title_error);
        detail_error = view.findViewById(R.id.detail_error);

        layoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setItemAnimator(new FadeInLeftAnimator());
        listView.getItemAnimator().setAddDuration(500);
        listView.getItemAnimator().setRemoveDuration(500);
        listView.setHasFixedSize(false);

        if (userData == null)
            userData = AgentCommonData.getUserData();

        if(userData != null && !TextUtils.isEmpty(userData.getAccessToken())) {
            chatListAdapter = new ChatListAdapter(userData.getUserId(), true,
                    FragmentType.ALL_CHAT.getOrdinal(), conversationChatList, this, listView);
            listView.setAdapter(chatListAdapter);
            listView.addOnScrollListener(onScrollListener);

            listerner = new ConversationListHelper();
            listerner.getOfflineData(FragmentType.ALL_CHAT.getOrdinal());
            //setConnectionManager(true);
        } else {
            llNoConversation.setVisibility(View.VISIBLE);
            title_error.setVisibility(View.GONE);
            detail_error.setText("Something went wrong. Please try again");
        }

    }

    private int[] statusIntArray = new int[]{MessageMode.OPEN_CHAT.getOrdinal()};
    private int[] typeIntArray = new int[]{ConversationMode.ALL.getOrdinal()};
    private int[] labelsIntArray = new int[]{};

    private UserData userData;

    private void setConnectionManager(boolean loader) {
        isLastItemReached = false;
        if (userData == null)
            userData = AgentCommonData.getUserData();
        getConversationData(0, loader);
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            FuguLog.v("scroll state ", "changed");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //To implement Pagination
            if (dy > 0) {
                if(!isNetworkAvailable())
                    return;
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (!isPagingApiInProgress && !isLastItemReached) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        int pageStart = conversationChatList.size()+1;
                        toggleProgressBarVisibility(true);
                        getConversationData(pageStart, false);
                    }
                }
            } else {
                if (getSnackbar() != null && layoutManager.findFirstVisibleItemPosition() == 1) {
                    getSnackbar().dismiss();
                }
            }
        }
    };

    private void getConversationData(int pageStart, boolean loader) {
        getConversationData(pageStart, loader, -1, false);
    }
    private void getConversationData(int pageStart, boolean loader, int endPage, boolean isRefreshing) {

        if(!isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (userData == null)
            userData = AgentCommonData.getUserData();
        String userID = String.valueOf(userData.getEnUserId());
        String accessToken = userData.getAccessToken();
        HashMap<String, Object> params = new HashMap<>();
        params.put(FuguAppConstant.EN_USER_ID, userID);
        params.put(FuguAppConstant.ACCESS_TOKEN, accessToken);
        params.put(FuguAppConstant.STATUS, "[1]");//Arrays.toString(statusArray).equals("[]")?"[1]":Arrays.toString(statusArray));
        params.put(FuguAppConstant.DEVICE_TYPE, 1);
//        params.put(FuguAppConstant.APP_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        params.put(FuguAppConstant.TYPE, Arrays.toString(typeIntArray));
//        if (labelsArray.length > 0) {
//            params.put(FuguAppConstant.LABEL, Arrays.toString(labelsArray));
//        }
        if (pageStart != 0) {
            params.put(FuguAppConstant.PAGE_START, String.valueOf(pageStart));
        }

        if(endPage > 0)
            params.put(FuguAppConstant.PAGE_END, String.valueOf(endPage));

        getConversationList(params, pageStart > 0, loader, isRefreshing, endPage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FuguConfig.getInstance().addUIListener(AgentServerListener.class, this);
        FuguConfig.getInstance().addUIListener(OnUserChannelListener.class, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FuguConfig.getInstance().removeUIListener(AgentServerListener.class, this);
        FuguConfig.getInstance().removeUIListener(OnUserChannelListener.class, this);
        AgentCommonData.setAgentConversationList(FragmentType.ALL_CHAT.getOrdinal(), conversationChatList);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private Snackbar getSnackbar() {
        return snackbar;
    }

    private void newConversationSnackBar() {
        String message;
        newMessageCount++;
        if (newMessageCount > 1) {
            message = newMessageCount + " new Chats";
        } else {
            message = newMessageCount + " new Chat";
        }
        snackbar = Snackbar
                .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.fugu_tap_to_view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do work
                        newMessageCount = 0;
                        listView.smoothScrollToPosition(0);
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        Button button = sbView.findViewById(com.google.android.material.R.id.snackbar_action);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void getConversationList(HashMap<String, Object> params, boolean isPagination, boolean loader,
                                     boolean onRefreshing, int endPage) {
        if (listerner != null && !isPagingApiInProgress) {
            if (loader)
                LoadingBox.showOn(getActivity());
            try {
                if(conversationChatList != null && conversationChatList.size()>0 && !isPagination && !loader && !onRefreshing) {
                    if (getActivity() != null) {
                        ((AgentListActivity) getActivity()).allChatFlag = true;
                        ((AgentListActivity) getActivity()).setConnectionMessage(1);
                    }
                }
            } catch (Exception e) {

            }
            isPagingApiInProgress = true;
            listerner.getAgentConversation(params, isPagination, FragmentType.ALL_CHAT.getOrdinal(), endPage);
        }
    }

    private void getConversationResponse(GetConversationResponse getConversationResponse, boolean isPagination,
                                         int fragmentType, int endPage) {
        if (fragmentType != FragmentType.ALL_CHAT.getOrdinal())
            return;
        try {
            isPagingApiInProgress = false;
            FuguLog.e("Size of Array", getConversationResponse.getData().getConversation().size() + "");
            if (getConversationResponse != null) { // Null handled in try catch
                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == getConversationResponse.getStatusCode()) {
                    setConversationChatData((ArrayList<Conversation>)
                            getConversationResponse.getData().getConversation(), isPagination, endPage);
                    toggleProgressBarVisibility(false);
                    if (getConversationResponse.getData().getConversation().size() == 0) {
                        isLastItemReached = true;
                    }
                }
            }
            if (getActivity() != null) {
                ((AgentListActivity) getActivity()).hideLoader(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoadingBox.hide();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setConversationChatData(ArrayList<Conversation> allChatData,
                                         final boolean isPagination) {
        setConversationChatData(allChatData, isPagination, -1);
    }
    private void setConversationChatData(ArrayList<Conversation> allChatData,
                                         final boolean isPagination, final int endPage) {
        if (!isPagination) {
            conversationChatList.clear();
        }

        /*try {
            if(((MainActivity) activity).notifiacrionClicked) {
                Integer channelId = ((MainActivity) activity).conversation.getChannelId();
                ((MainActivity) activity).notifiacrionClicked = false;
                for(int i = 0;i<allChatData.size();i++) {
                    if(channelId.intValue() == allChatData.get(i).getChannelId().intValue()) {
                        allChatData.get(i).setUnreadCount(0);
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }*/

        conversationChatList.addAll(allChatData);
        if (getView() != null && chatListAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatListAdapter.notifyDataSetChanged();
                    if (!isPagination && endPage < 1) {
                        listView.scrollToPosition(0);
                    }
                }
            });
        }
        setLlNoConversation(null);
        addUnreadCount(allChatData, isPagination);
    }

    private void setLlNoConversation(final String message) {
        if (llNoConversation != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message != null) {
                        if (conversationChatList == null || conversationChatList.size() == 0) {
                            llNoConversation.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            llNoConversation.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(message))
                            detail_error.setText(message);
                    } else {
                        if (conversationChatList == null || conversationChatList.size() == 0) {
                            llNoConversation.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            llNoConversation.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }


    private void toggleProgressBarVisibility(boolean isVisible) {
        if (isVisible) {
            if (progressBarItem == null) {
                progressBarItem = new ChatListAdapter.ProgressBarItem();
            }
            if (!conversationChatList.contains(progressBarItem)) {
                conversationChatList.add(progressBarItem);
                listView.post(new Runnable() {
                    public void run() {
                        chatListAdapter.notifyItemInserted(conversationChatList.size() - 1);
                    }
                });
            }
        } else {
            if (progressBarItem != null && conversationChatList.contains(progressBarItem)) {
                conversationChatList.remove(progressBarItem);
                chatListAdapter.notifyItemRemoved(conversationChatList.size() - 1);
            }
        }
    }

    @Override
    public void onRefresh() {
        if(!isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            getConversationData(0, false, -1, true);
        }

    }

    @Override
    public void onClick(int position, int fragmentType, Conversation conversation) {
        conversation.setUnreadCount(0);
        chatListAdapter.notifyDataSetChanged();
        if(!Utils.preventMultipleClicks()) {
            return;
        }
        //setConversationChatCount(conversation);
        Intent chatIntent = new Intent(getActivity(), AgentChatActivity.class);
        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
        chatIntent.putExtra(FuguAppConstant.FRAGMENT_TYPE, fragmentType);
        startActivityForResult(chatIntent, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FuguLog.v("onActivityResult", "enter");
        if (requestCode == 100) {
            if (resultCode == MessageMode.OPEN_CHAT.getOrdinal()) {
                Long channelId = Long.parseLong(data.getExtras().getString(FuguAppConstant.CHANNEL_ID));
                setOverlayTransition(channelId, MessageMode.OPEN_CHAT.getOrdinal(), resultCode);
            } else if (resultCode == MessageMode.CLOSED_CHAT.getOrdinal()) {
                Long channelId = Long.parseLong(data.getExtras().getString(FuguAppConstant.CHANNEL_ID));
                setOverlayTransition(channelId, MessageMode.CLOSED_CHAT.getOrdinal(), resultCode);
            } else if (resultCode == Overlay.ASSIGNMENT.getOrdinal()) {
                Long channelId = Long.parseLong(data.getExtras().getString(FuguAppConstant.CHANNEL_ID));
                setOverlayTransition(channelId, MessageMode.OPEN_CHAT.getOrdinal(), resultCode);
            }
        }
    }

    private void setOverlayTransition(final Long channelId, int messageMode, int resultCode) {
        boolean success = false;
        Conversation overlayConversation = null;
        for (int i = 0; i < conversationChatList.size(); i++) {
            if (conversationChatList.get(i) instanceof Conversation) {
                Conversation conversation1 = (Conversation) conversationChatList.get(i);
                //overlayConversation = conversation1;
                if (conversation1.getChannelId().compareTo(channelId) == 0) {
                    success = true;
                    if (resultCode != Overlay.ASSIGNMENT.getOrdinal()) {
                        conversation1.setStatus(messageMode);
                    }
                    conversation1.setOverlay(resultCode);

                    break;
                }
            }
        }
        if (success) {
            chatListAdapter.notifyDataSetChanged();
            removeConversation(channelId);
            if (resultCode != Overlay.ASSIGNMENT.getOrdinal()) {
                removeOverlay(overlayConversation);
            } else {
                removeConversation(channelId);
            }
        }
    }

    private Handler handler = new Handler();
    private final int OVERLAY_TIME = 1000;

    private void removeOverlay(final Conversation conversation) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (conversation != null) {
                            conversation.setOverlay(Overlay.DEFAULT.getOrdinal());
                            chatListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }, OVERLAY_TIME);
    }

    private void removeConversation(final Long channelId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < conversationChatList.size(); i++) {
                            if (conversationChatList.get(i) instanceof Conversation
                                    && channelId == ((Conversation) conversationChatList.get(i)).getChannelId()) {
                                conversationChatList.remove(i);
                                chatListAdapter.notifyItemRemoved(i);
                                HashMap<Integer, Integer> unreadCount = AgentCommonData.removeTotalUnreadCount(channelId.intValue());
                                sendUnreadCount(unreadCount);
                                break;

                            }
                        }
                    }
                });
            }
        }, OVERLAY_TIME);

    }


    private void setConversationChatCount(Conversation conversationCount) {
        try {
            for (Object conversation : conversationChatList) {
                if (conversation instanceof Conversation) {
                    if (((Conversation) conversation).getChannelId().equals(conversationCount.getChannelId())) {
                        ((Conversation) conversation).setUnreadCount(0);
                        HashMap<Integer, Integer> unreadCount = AgentCommonData.removeTotalUnreadCount(conversationCount.getChannelId().intValue());
                        sendUnreadCount(unreadCount);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void conversationList(GetConversationResponse getConversationResponse, boolean isPagination, int fragmentType, int endPage) {
        getConversationResponse(getConversationResponse, isPagination, fragmentType, endPage);
    }

    @Override
    public void onError(APIError error, int fragmentType) {
        if (fragmentType != FragmentType.ALL_CHAT.getOrdinal())
            return;
        setLlNoConversation(error.getMessage() == null ? "" : error.getMessage());
    }

    @Override
    public void offlineConversationList(int fragmentType, ArrayList<Object> arrayList) {
        boolean loader = true;
        if(fragmentType == FragmentType.ALL_CHAT.getOrdinal()) {
            if(fragmentType == FragmentType.ALL_CHAT.getOrdinal() && arrayList != null && arrayList.size()>0) {
                conversationChatList.addAll(arrayList);
                chatListAdapter.notifyDataSetChanged();
                loader = false;
            }
            setConnectionManager(loader);
        }
        /*if(fragmentType == FragmentType.ALL_CHAT.getOrdinal() && arrayList != null && arrayList.size()>0) {
            conversationChatList.addAll(arrayList);
            chatListAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void onAssignChat(JSONObject jsonObject) {
//        try {
//            setOverlayTransition(jsonObject.optLong("channel_id"), MessageMode.OPEN_CHAT.getOrdinal(), Overlay.ASSIGNMENT.getOrdinal());
//        } catch (Exception e) {
//
//        }

        FuguLog.e(TAG, "onAssignChat jsonObject: "+jsonObject);
        ArrayList<Conversation> allChatData = new ArrayList<>();
        for (Object obj : conversationChatList) {
            if (obj instanceof Conversation) {
                Conversation conversation = (Conversation) obj;
                allChatData.add(conversation);
            }
        }

        for(int i=0;i<allChatData.size();i++) {
            if(allChatData.get(i).getChannelId().compareTo(jsonObject.optLong("channel_id")) == 0) {
                //allChatData.get(i).setLast_sent_by_full_name("");
                Conversation conversation = allChatData.get(i);
                conversation.setMessage(jsonObject.optString("message", ""));
                conversation.setAgentId(jsonObject.optInt("assigned_to", 0));
                conversation.setAgentName(jsonObject.optString("assigned_to_name"));
                conversation.setLabel(jsonObject.optString("label", ""));
                if(jsonObject.optInt("notification_type", 0) == 3) {
                    conversation.setLast_sent_by_id(jsonObject.optInt("assigned_by"));
                    conversation.setLast_sent_by_full_name(jsonObject.optString("assigned_by_name"));
                    conversation.setLast_sent_by_user_type(2);
                } else {
                    conversation.setLast_sent_by_id(jsonObject.optInt("last_sent_by_id"));
                    conversation.setLast_sent_by_full_name(jsonObject.optString("last_sent_by_full_name"));
                    conversation.setLast_sent_by_user_type(jsonObject.optInt("last_sent_by_user_type"));
                }
                conversation.setLastUpdatedAt(jsonObject.optString("date_time", ""));
                conversation.setUnreadCount(0);
                break;
            }
        }

        Collections.sort(allChatData, new Comparator<Conversation>() {
            public int compare(Conversation o1, Conversation o2) {
                if (o1.getLastUpdatedAt() == null || o2.getLastUpdatedAt() == null)
                    return 0;
                return o2.getLastUpdatedAt().compareTo(o1.getLastUpdatedAt());
            }
        });
        setConversationChatData(allChatData, false);
    }

    @Override
    public void onControlChannelData(JSONObject jsonObject) {
        setControlChannelData(jsonObject);
    }

    @Override
    public void onRefreshData() {
        //setConnectionManager(false);
        if(!isNetworkAvailable()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            if(userData == null)
                userData = AgentCommonData.getUserData();

            getConversationData(0, false, conversationChatList.size(), false);
            //setConnectionManager(false);
        }
    }

    @Override
    public void onReadAll(JSONObject jsonObject) {
        if(jsonObject != null) {
            if (userData == null)
                userData = AgentCommonData.getUserData();
            if(jsonObject.optInt("user_id", 0) == userData.getUserId().intValue()) {
                for (Object obj : conversationChatList) {
                    if (obj instanceof Conversation) {
                        Conversation conversation = (Conversation) obj;
                        if (conversation.getChannelId().compareTo(jsonObject.optLong("channel_id")) == 0) {
                            conversation.setUnreadCount(0);
                            HashMap<Integer, Integer> unreadCount = AgentCommonData.removeTotalUnreadCount(conversation.getChannelId().intValue());
                            sendUnreadCount(unreadCount);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatListAdapter.notifyDataSetChanged();
                                }
                            });

                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateCount(Long channelId) {
        try {
            for (Object obj : conversationChatList) {
                if (obj instanceof Conversation) {
                    Conversation conversation = (Conversation) obj;
                    if (conversation.getChannelId().compareTo(channelId) == 0) {
                        conversation.setUnreadCount(0);
                        HashMap<Integer, Integer> unreadCount = AgentCommonData.addTotalUnreadCount(conversation.getChannelId().intValue(), conversation.getUnreadCount());
                        sendUnreadCount(unreadCount);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private void setControlChannelData(JSONObject jsonObject) {
        try {
            boolean newConversation = true;
            for (Object obj : conversationChatList) {
                if (obj instanceof Conversation) {
                    Conversation conversation = (Conversation) obj;
                    if (conversation.getChannelId().compareTo(jsonObject.optLong("channel_id")) == 0) {
                        newConversation = false;
                        conversation.setMessage(jsonObject.optString("message", ""));
                        conversation.setAgentId(jsonObject.optInt("agent_id", 0));
                        conversation.setLast_sent_by_id(jsonObject.optInt("last_sent_by_id"));
                        conversation.setLast_sent_by_full_name(jsonObject.optString("last_sent_by_full_name"));
                        conversation.setLast_sent_by_user_type(jsonObject.optInt("last_sent_by_user_type"));
                        conversation.setLastUpdatedAt(jsonObject.optString("date_time", ""));
                        if (jsonObject.optInt(FuguAppConstant.USER_TYPE, UserType.USER.getOrdinal()) != UserType.AGENT.getOrdinal()
                                && jsonObject.optInt("channel_id", -1) != AgentChatActivity.pushChannelId) {
                            conversation.setUnreadCount(conversation.getUnreadCount() + 1);
                            HashMap<Integer, Integer> unreadCount = AgentCommonData.addTotalUnreadCount(conversation.getChannelId().intValue(), conversation.getUnreadCount());
                            sendUnreadCount(unreadCount);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatListAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        if (userData == null)
                            userData = AgentCommonData.getUserData();
                        if(jsonObject.optInt("last_sent_by_id", -1) == userData.getUserId().intValue()) {
                            conversation.setUnreadCount(0);
                        }
                        break;
                    }
                }
            }

            if (newConversation) {
                FuguLog.v("New chat arrived", "send to unasssigned");
                Conversation conversation = new Conversation();
                conversation.setChannelId(jsonObject.optLong("channel_id"));
                conversation.setChannelName(jsonObject.optString("channel_name", ""));
                conversation.setUserId(jsonObject.optInt("user_id"));
                conversation.setLastUpdatedAt(jsonObject.optString("date_time", ""));
                conversation.setMessage(jsonObject.optString("message", ""));
                conversation.setLabel(jsonObject.optString("label", ""));
                conversation.setStatus(jsonObject.optInt("status", 1));
                conversation.setBotChannelName(jsonObject.optString("bot_channel_name", ""));
                conversation.setUnreadCount(jsonObject.optInt("unread_count", 1));
                conversation.setAgentId(jsonObject.optInt("agent_id", 0));
                conversation.setOverlay(Overlay.DEFAULT.getOrdinal());

                conversationChatList.add(0, conversation);
                setLlNoConversation(null);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatListAdapter.notifyItemInserted(0);
                        if (layoutManager.findFirstVisibleItemPosition() == 0) {
                            listView.scrollToPosition(0);
                        } else if (layoutManager.findFirstVisibleItemPosition() > 0) {
                            newConversationSnackBar();
                        }
                    }
                });
                HashMap<Integer, Integer> unreadCount = AgentCommonData.addTotalUnreadCount(conversation.getChannelId().intValue(), conversation.getUnreadCount());
                sendUnreadCount(unreadCount);
            } else {
                if (jsonObject.optInt("notification_type") == NotificationType.ASSIGNMENT.getOrdinal()) {
//                    int agentId = jsonObject.optInt("agent_id", -1);
//                    String agentName = jsonObject.optString("agent_name", "");
                    //setOverlayTransition(jsonObject.optLong("channel_id"), MessageMode.OPEN_CHAT.getOrdinal(), Overlay.ASSIGNMENT.getOrdinal());


                } else {
                    ArrayList<Conversation> allChatData = new ArrayList<>();
                    for (Object obj : conversationChatList) {
                        if (obj instanceof Conversation) {
                            Conversation conversation = (Conversation) obj;
                            allChatData.add(conversation);
                        }
                    }

                    Collections.sort(allChatData, new Comparator<Conversation>() {
                        public int compare(Conversation o1, Conversation o2) {
                            if (o1.getLastUpdatedAt() == null || o2.getLastUpdatedAt() == null)
                                return 0;
                            return o2.getLastUpdatedAt().compareTo(o1.getLastUpdatedAt());
                        }
                    });

                    setConversationChatData(allChatData, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remove(final int position) {
        Conversation conversation = (Conversation) conversationChatList.get(position);
        conversation.setOverlay(Overlay.ASSIGNMENT.getOrdinal());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatListAdapter.notifyItemChanged(position);
            }
        });
        removeConversation(conversation.getChannelId());
    }

    private void sendUnreadCount(HashMap<Integer, Integer> unreadCount) {
        if(unreadCount == null)
            unreadCount = AgentCommonData.getTotalUnreadCount();

        int count = 0;
        for(Integer value : unreadCount.values()) {
            count = count + value;
        }
        if (FuguConfig.getInstance().getCallbackListener() != null) {
            FuguConfig.getInstance().getCallbackListener().count(count);
        }
    }

    private void addUnreadCount(ArrayList<Conversation> allChatData, boolean isPagination) {
        HashMap<Integer, Integer> hashMap = AgentCommonData.getTotalUnreadCount();
        for(Conversation conversation : allChatData) {
            hashMap.put(conversation.getChannelId().intValue(), conversation.getUnreadCount());
        }
        AgentCommonData.addTotalUnreadCount(hashMap);
        sendUnreadCount(null);
    }

    /**
     * Check Network Connection
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

}
