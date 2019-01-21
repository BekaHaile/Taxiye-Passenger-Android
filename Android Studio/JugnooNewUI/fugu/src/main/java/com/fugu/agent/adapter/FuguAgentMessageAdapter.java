package com.fugu.agent.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fugu.FuguColorConfig;
import com.fugu.R;
import com.fugu.adapter.FuguMessageAdapter;
import com.fugu.agent.AgentChatActivity;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.model.EventItem;
import com.fugu.agent.model.HeaderItem;
import com.fugu.agent.model.ListItem;
import com.fugu.agent.model.Message;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.utils.DateUtils;
import com.fugu.utils.RoundedCornersTransformation;
import com.fugu.utils.zoomview.ZoomageView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.fugu.constant.FuguAppConstant.MESSAGE_DELIVERED;
import static com.fugu.constant.FuguAppConstant.MESSAGE_IMAGE_RETRY;
import static com.fugu.constant.FuguAppConstant.MESSAGE_READ;
import static com.fugu.constant.FuguAppConstant.MESSAGE_SENT;
import static com.fugu.constant.FuguAppConstant.MESSAGE_UNSENT;
import static com.fugu.constant.FuguAppConstant.PRIVATE_NOTE;

/**
 * Created by Bhavya Rattan on 02/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguAgentMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FuguAgentMessageAdapter.class.getSimpleName();
    //private ArrayList<Message> fuguMessageList = new ArrayList<>();
    private AgentChatActivity context;
    private final int TYPE_HEADER = 0;
    private final int ITEM_TYPE_OTHER = 1;
    private final int ITEM_TYPE_SELF = 2;
    private final int ITEM_ASSIGNMENT = 3;
    private final int FUGU_VIDEO_CALL_VIEW = 18;
    private final int FUGU_OTHER_VIDEO_CALL_VIEW = 19;
    private DateUtils dateUtil = DateUtils.getInstance();
    private OnRetryListener mOnRetry;
    private FuguColorConfig fuguColorConfig;
    private Conversation conversation;
    private FuguMessageAdapter.onVideoCall onVideoCall;

    private String customerName;
    private String agentName;
    private boolean isChatAssignToMe = false;
    private boolean isVideoCallEnabled = true;
    private int agentId;

    @NonNull
    private List<ListItem> items = Collections.emptyList();

    public FuguAgentMessageAdapter(AgentChatActivity context, @NonNull List<ListItem> items, Conversation conversation) {

        this.items = items;
        // this.fuguMessageList = fuguMessageList;
        this.context = context;
        this.fuguColorConfig = CommonData.getColorConfig();
        this.conversation = conversation;
        //removeDefaultMsgTime();
    }

    public void updateList(@NonNull List<ListItem> items) {
        this.items = items;
        //  removeDefaultMsgTime();
    }

    public void setCustomeName(String customeName) {
        this.customerName = customeName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setIsChatAssignToMe(boolean isChatAssignToMe) {
        this.isChatAssignToMe = isChatAssignToMe;
    }

    public void isVideoCallEnabled(boolean isVideoCallEnabled) {
        this.isVideoCallEnabled = isVideoCallEnabled;
    }

    public void setOnVideoCallListener(FuguMessageAdapter.onVideoCall onVideoCall) {
        this.onVideoCall = onVideoCall;
    }

    public void setOnRetryListener(OnRetryListener OnRetryListener) {
        mOnRetry = OnRetryListener;
    }

    public interface OnRetryListener {
        public void onRetry(String file, String fileType, final int messageIndex, String muid);
        public void onMessageRetry(String muid, int position);
        public void onMessageCancel(String muid, int position);
    }

//    public void setAgentName(String agentName) {
//        this.agentName = agentName;
//    }

    private void removeDefaultMsgTime() {
        for (int i = 0; i < 2; i++) {

            if (i > items.size()) {
                break;
            }

            if (items.get(i).getType() == ITEM_TYPE_OTHER) {
                ((EventItem) items.get(i)).getEvent().setSentAtUtc("");
                break;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View normalView = LayoutInflater.from(context).inflate(R.layout.fugu_item_message_date, parent, false);
            return new DateViewHolder(normalView);
        } else if (viewType == ITEM_TYPE_OTHER) {
            View normalView = LayoutInflater.from(context).inflate(R.layout.fugu_item_message_left, parent, false);
            return new OtherMessageViewHolder(normalView);
        } else if (viewType == ITEM_ASSIGNMENT) {
            View headerRow = LayoutInflater.from(context).inflate(R.layout.fugu_item_assignment, parent, false);
            return new AssignmentViewHolder(headerRow);
        } else if (viewType == ITEM_TYPE_SELF) {
            View headerRow = LayoutInflater.from(context).inflate(R.layout.fugu_item_message_right, parent, false);
            return new SelfMessageViewHolder(headerRow);
        } else if(viewType == FUGU_VIDEO_CALL_VIEW) {
            View videoCallView = LayoutInflater.from(context).inflate(R.layout.hippo_video_self_side, parent, false);
            return new SelfVideoViewHolder(videoCallView);
        } else if(viewType == FUGU_OTHER_VIDEO_CALL_VIEW) {
            View otherVideoCallView = LayoutInflater.from(context).inflate(R.layout.hippo_video_other_side, parent, false);
            return new VideoViewHolder(otherVideoCallView);
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);

        if(itemType == FUGU_VIDEO_CALL_VIEW) {
            final SelfVideoViewHolder videoViewHolder = (SelfVideoViewHolder) holder;
            Message videoMessage = ((EventItem) items.get(position)).getEvent();
            videoViewHolder.tvMsg.setText(getMessage(videoMessage, FUGU_VIDEO_CALL_VIEW));
            /*if(videoMessage.getMessageState() != null && videoMessage.getMessageState().intValue() == 2) {
                videoViewHolder.tvMsg.setText(agentName+" missed a video call with you");
            } else {
                videoViewHolder.tvMsg.setText("The video call ended");
            }*/
            if (videoMessage.getSentAtUtc().isEmpty()) {
                videoViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                videoViewHolder.tvTime.setText(DateUtils.getTime(dateUtil.convertToLocal(videoMessage.getSentAtUtc())));
                videoViewHolder.tvTime.setVisibility(View.VISIBLE);
            }
            if(videoMessage.getVideoCallDuration()>0) {
                videoViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                videoViewHolder.tvDuration.setVisibility(View.VISIBLE);
                videoViewHolder.tvDuration.setText(convertSeconds(videoMessage.getVideoCallDuration())+" at ");
//                    videoViewHolder.tvDuration.setText(videoMessage.getVideoCallDuration()+"sec at ");

            } else {
                videoViewHolder.ivCallIcon.setVisibility(View.GONE);
                videoViewHolder.tvDuration.setVisibility(View.GONE);
            }

            if(!isChatAssignToMe || !AgentCommonData.getUserData().isVideoCallEnabled()
                    || !isVideoCallEnabled) {
                videoViewHolder.callAgain.setVisibility(View.GONE);
            }

            videoViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if(onVideoCall != null) {
                        int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                        if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                            callType = FuguAppConstant.AUDIO_CALL_VIEW;
                        }
                        onVideoCall.onVideoCallClicked(callType);
                    }*/
                }
            });
        } else if(itemType == FUGU_OTHER_VIDEO_CALL_VIEW) {
            final VideoViewHolder videoOtherViewHolder = (VideoViewHolder) holder;
            Message videoOtherMessage = ((EventItem) items.get(position)).getEvent();
            videoOtherViewHolder.tvMsg.setText(getMessage(videoOtherMessage, FUGU_OTHER_VIDEO_CALL_VIEW));
            /*if(videoOtherMessage.getMessageState() != null && videoOtherMessage.getMessageState().intValue() == 2) {
                videoOtherViewHolder.tvMsg.setText("You missed a video call with "+videoOtherMessage.getfromName());
            } else {
                videoOtherViewHolder.tvMsg.setText("The video call ended");
            }*/
            if (videoOtherMessage.getSentAtUtc().isEmpty()) {
                videoOtherViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                videoOtherViewHolder.tvTime.setText(DateUtils.getTime(dateUtil.convertToLocal(videoOtherMessage.getSentAtUtc())));
                videoOtherViewHolder.tvTime.setVisibility(View.VISIBLE);
            }

            if(videoOtherMessage.getVideoCallDuration()>0) {
                videoOtherViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                videoOtherViewHolder.tvDuration.setVisibility(View.VISIBLE);
                videoOtherViewHolder.tvDuration.setText(convertSeconds(videoOtherMessage.getVideoCallDuration())+" at ");
//                    videoOtherViewHolder.tvDuration.setText(videoOtherMessage.getVideoCallDuration()+"sec at ");
            } else {
                videoOtherViewHolder.ivCallIcon.setVisibility(View.GONE);
                videoOtherViewHolder.tvDuration.setVisibility(View.GONE);
            }
            videoOtherViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if(onVideoCall != null) {
                        int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                        if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                            callType = FuguAppConstant.AUDIO_CALL_VIEW;
                        }
                        onVideoCall.onVideoCallClicked(callType);
                    }*/
                }
            });
        } else if (itemType == TYPE_HEADER) {
            final DateViewHolder dateViewHolder = (DateViewHolder) holder;
            HeaderItem headerItem = (HeaderItem) items.get(position);
            if (headerItem.getDate().isEmpty()) {
                dateViewHolder.tvDate.setVisibility(View.GONE);
            } else {
                String date = DateUtils.getInstance().getDate(headerItem.getDate());
                dateViewHolder.tvDate.setText(date);
                dateViewHolder.tvDate.setVisibility(View.VISIBLE);
            }
            GradientDrawable drawable = (GradientDrawable) dateViewHolder.tvDate.getBackground();
            drawable.setStroke((int) context.getResources().getDimension(R.dimen.fugu_border_width), fuguColorConfig.getFuguBorderColor()); // set stroke width and stroke color
            dateViewHolder.tvDate.setTextColor(fuguColorConfig.getFuguChatDateText());
        } else if (itemType == ITEM_TYPE_OTHER) {
            final OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
            final Message currentOrderItem = ((EventItem) items.get(position)).getEvent();
//            if(position==items.size()-1) {
//                final Message lastMessage = ((EventItem) items.get(position)).getEvent();
//                context.lastMessage = lastMessage.getMessage();
//            }
            otherMessageViewHolder.tvUserName.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgFromName());
            otherMessageViewHolder.tvMsg.setTextColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
            otherMessageViewHolder.tvMsg.setLinkTextColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
            otherMessageViewHolder.tvMsg.setAutoLinkMask(Linkify.ALL);
            otherMessageViewHolder.tvTime.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgFrom());


            if (currentOrderItem.getMessage().isEmpty()) {
                otherMessageViewHolder.tvMsg.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params2.weight = 1.0f;
                params2.gravity = Gravity.END;
                otherMessageViewHolder.rlMessages.setLayoutParams(params2);
                otherMessageViewHolder.tvMsg.setTextSize(pxToDp(3));
            } else {
                otherMessageViewHolder.tvMsg.setText(currentOrderItem.getMessage());
                otherMessageViewHolder.tvMsg.setTextSize(17);
                String msg = currentOrderItem.getMessage().replace(" ", "&nbsp;");
                otherMessageViewHolder.tvMsg.setText(Html.fromHtml(msg.replace("\n", "<br /> ")));
                otherMessageViewHolder.tvMsg.setVisibility(View.VISIBLE);
            }

//            otherMessageViewHolder.setIsRecyclable(true);
            otherMessageViewHolder.tvUserName.setText(currentOrderItem.getfromName());


            if (currentOrderItem.getSentAtUtc().isEmpty()) {
                otherMessageViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                otherMessageViewHolder.tvTime.setText(dateUtil.getTime(dateUtil.convertToLocal(currentOrderItem.getSentAtUtc())));
                otherMessageViewHolder.tvTime.setVisibility(View.VISIBLE);
            }

            String userNameText = "";

            int userNameStringCount = 1;
            if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                if (currentOrderItem.getfromName().split(" ").length > 0) {
                    userNameStringCount = currentOrderItem.getfromName().trim().split(" ").length;
                }

                String[] userNameSplitArray = currentOrderItem.getfromName().trim().split(" ");

                for (int i = 0; i < userNameStringCount; i++) {
                    try {
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(0, 1).toUpperCase());
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(1).toLowerCase());
                        userNameText = userNameText.concat(" ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            if (position != 0 && getItemViewType(position - 1) == ITEM_TYPE_OTHER) {
                Message lastOrderItem = ((EventItem) items.get(position - 1)).getEvent();
                if (currentOrderItem.getUserId().compareTo(lastOrderItem.getUserId()) != 0) {
                    otherMessageViewHolder.llChatLeft.setBackgroundResource(R.drawable.hippo_chat_bg_left);
                    otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvUserName.setText(userNameText);
                    otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                            pxToDp(8),
                            0,
                            pxToDp(1));
                    otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                            pxToDp(7),
                            pxToDp(10),
                            pxToDp(0));
                    otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                            pxToDp(7),
                            pxToDp(2),
                            pxToDp(7));
                } else if (position != 0 && (position + 1 != items.size()) && (getItemViewType(position + 1) != ITEM_TYPE_OTHER)) {
                    otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                    otherMessageViewHolder.llChatLeft.setBackgroundResource(R.drawable.hippo_chat_bg_left_normal);
                    otherMessageViewHolder.llRoot.setPadding(pxToDp(17),
                            pxToDp(1),
                            pxToDp(0),
                            pxToDp(2));
                    otherMessageViewHolder.tvUserName.setPadding(pxToDp(8),
                            pxToDp(7),
                            pxToDp(10),
                            pxToDp(0));
                    otherMessageViewHolder.tvMsg.setPadding(pxToDp(8),
                            pxToDp(7),
                            pxToDp(2),
                            pxToDp(7));
                } else {
                    otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                    otherMessageViewHolder.llChatLeft.setBackgroundResource(R.drawable.hippo_chat_bg_left_normal);
                    otherMessageViewHolder.llRoot.setPadding(pxToDp(17),
                            pxToDp(1),
                            0,
                            pxToDp(1));
                    otherMessageViewHolder.tvUserName.setPadding(pxToDp(8),
                            pxToDp(7),
                            pxToDp(10),
                            pxToDp(0));
                    otherMessageViewHolder.tvMsg.setPadding(pxToDp(8),
                            pxToDp(7),
                            pxToDp(2),
                            pxToDp(7));
                }
            } else if (position != 0 && (position + 1 != items.size()) && (getItemViewType(position + 1) == ITEM_TYPE_OTHER)) {
                otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                otherMessageViewHolder.tvUserName.setText(userNameText);
                otherMessageViewHolder.llChatLeft.setBackgroundResource(R.drawable.hippo_chat_bg_left);
//                    otherMessageViewHolder.llMessageBg.setPadding(pxToDp(0),
//                            pxToDp(7),
//                            pxToDp(0),
//                            pxToDp(0));
                otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                        pxToDp(4),
                        pxToDp(0),
                        pxToDp(1));
                otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                        pxToDp(7),
                        pxToDp(10),
                        pxToDp(0));
                otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                        pxToDp(0),
                        pxToDp(2),
                        pxToDp(7));
            } else {
                otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                otherMessageViewHolder.tvUserName.setText(userNameText);
                otherMessageViewHolder.llChatLeft.setBackgroundResource(R.drawable.hippo_chat_bg_left);
//                    otherMessageViewHolder.llMessageBg.setPadding(pxToDp(0),
//                            pxToDp(0),
//                            pxToDp(0),
//                            pxToDp(0));
                otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                        pxToDp(4),
                        0,
                        pxToDp(2));
                otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                        pxToDp(7),
                        pxToDp(10),
                        pxToDp(0));
                otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                        pxToDp(0),
                        pxToDp(2),
                        pxToDp(7));
            }


            NinePatchDrawable drawable2 = (NinePatchDrawable) otherMessageViewHolder.llChatLeft.getBackground();
            drawable2.setColorFilter(fuguColorConfig.getFuguBgMessageFrom(), PorterDuff.Mode.MULTIPLY);

            if (!currentOrderItem.getThumbnailUrl().isEmpty()) {
                new RequestOptions();
                RequestOptions myOptions = RequestOptions
                        .bitmapTransform(new RoundedCornersTransformation(context, 7, 2))
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder));
                Glide.with(context).load(currentOrderItem.getThumbnailUrl())
                        .apply(myOptions)
                        .into(otherMessageViewHolder.ivMsgImage);

                otherMessageViewHolder.rlImageMessage.setVisibility(View.VISIBLE);
            } else {
                otherMessageViewHolder.rlImageMessage.setVisibility(View.GONE);
            }

            otherMessageViewHolder.rlImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageDialog(context, currentOrderItem.getImageUrl());
                }
            });

        } else if (itemType == ITEM_TYPE_SELF) {
            final SelfMessageViewHolder selfMessageViewHolder = (SelfMessageViewHolder) holder;
            final Message currentOrderItem = ((EventItem) items.get(position)).getEvent();

            /*if (currentOrderItem.getMessageType() == PRIVATE_NOTE) {
                selfMessageViewHolder.llChat.setBackgroundResource(R.drawable.fugu_bg_msg_private);
             } else {
                selfMessageViewHolder.llChat.setBackgroundResource(R.drawable.fugu_bg_msg_you);
            }*/

//            FuguLog.e(TAG, position+ " currentOrderItem.getfromName() ====== "+currentOrderItem.getfromName());
            selfMessageViewHolder.tvName.setVisibility(View.VISIBLE);
            selfMessageViewHolder.tvName.setText(currentOrderItem.getfromName());
            if (currentOrderItem.getMessage().isEmpty()) {
                selfMessageViewHolder.tvMsg.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params2.weight = 1.0f;
                params2.gravity = Gravity.END;
                selfMessageViewHolder.rlMessages.setLayoutParams(params2);
                selfMessageViewHolder.tvMsg.setTextSize(pxToDp(3));
            } else {
                selfMessageViewHolder.tvMsg.setText(currentOrderItem.getMessage());
                selfMessageViewHolder.tvMsg.setTextSize(17);
                String msg = currentOrderItem.getMessage().replace("  ", "&nbsp;");
                selfMessageViewHolder.tvMsg.setText(Html.fromHtml(msg.replace("\n", "<br />")));
                selfMessageViewHolder.tvMsg.setVisibility(View.VISIBLE);
            }

//            selfMessageViewHolder.setIsRecyclable(true);
            if (currentOrderItem.getSentAtUtc().isEmpty()) {
                selfMessageViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                selfMessageViewHolder.tvTime.setText(dateUtil.getTime(dateUtil.convertToLocal(currentOrderItem.getSentAtUtc())));
                selfMessageViewHolder.tvTime.setVisibility(View.VISIBLE);
            }

//            FuguLog.e("currentOrderItem.getThumbnailUrl()", "==" + currentOrderItem.getThumbnailUrl());


            if (position != 0
                    && (getItemViewType(position - 1) == ITEM_TYPE_SELF)
                    && (position + 1 != items.size())
                    && (getItemViewType(position + 1) == ITEM_TYPE_SELF)) {
                selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                        pxToDp(1),
                        pxToDp(17),
                        pxToDp(1));
                selfMessageViewHolder.tvName.setVisibility(View.GONE);
            } else if (position != 0 && ((getItemViewType(position - 1) == ITEM_TYPE_SELF) && (position + 1 != items.size())
                    && (getItemViewType(position + 1) != ITEM_TYPE_SELF))
                    || (!(position - 1 < 0) && (getItemViewType(position - 1) == ITEM_TYPE_SELF))) {
                if (position == items.size() - 1) {
                    selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                            pxToDp(1),
                            pxToDp(17),
                            pxToDp(1));
                } else {
                    selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                            pxToDp(1),
                            pxToDp(17),
                            pxToDp(2));
                }
                selfMessageViewHolder.tvName.setVisibility(View.GONE);
            } else if (position != 0 && (position + 1 != items.size()) && (getItemViewType(position + 1) == ITEM_TYPE_SELF)) {
                selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                        pxToDp(4),
                        pxToDp(10),
                        pxToDp(1));
            } else {
                if (position == items.size() - 1) {
                    selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(10),
                            pxToDp(1));
                } else {
                    selfMessageViewHolder.llRoot.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(10),
                            pxToDp(2));
                }
            }

            //add new//
            if (position != 0 && (getItemViewType(position - 1) != ITEM_TYPE_SELF)) {
                selfMessageViewHolder.llChat.setBackgroundResource(R.drawable.hippo_chat_bg_right);
                selfMessageViewHolder.rlMessages.setPadding(pxToDp(0),
                        pxToDp(0),
                        pxToDp(12),
                        pxToDp(0));
            } else {
                selfMessageViewHolder.llChat.setBackgroundResource(R.drawable.hippo_chat_bg_right_normal);
                selfMessageViewHolder.rlMessages.setPadding(pxToDp(0),
                        pxToDp(0),
                        pxToDp(6),
                        pxToDp(0));
            }


//            NinePatchDrawable drawable3 = (NinePatchDrawable) selfMessageViewHolder.llChat.getBackground();
//            drawable3.setColorFilter(fuguColorConfig.getFuguAgentBgMessageYou(), PorterDuff.Mode.MULTIPLY);

            NinePatchDrawable drawable3 = (NinePatchDrawable) selfMessageViewHolder.llChat.getBackground();
            if (currentOrderItem.getMessageType() == PRIVATE_NOTE) {
                drawable3.setColorFilter(fuguColorConfig.getFuguPrivateMsg(), PorterDuff.Mode.MULTIPLY);
            } else {
                drawable3.setColorFilter(fuguColorConfig.getFuguBgMessageYou(), PorterDuff.Mode.MULTIPLY);
            }

            selfMessageViewHolder.tvName.setTextColor(fuguColorConfig.getFuguPrimaryTextMsgFromName());
            selfMessageViewHolder.tvMsg.setTextColor(fuguColorConfig.getFuguPrimaryTextMsgYou());
            selfMessageViewHolder.tvMsg.setLinkTextColor(fuguColorConfig.getFuguPrimaryTextMsgYou());
            selfMessageViewHolder.tvMsg.setAutoLinkMask(Linkify.ALL);
            selfMessageViewHolder.tvTime.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgYou());

            if (!currentOrderItem.getThumbnailUrl().isEmpty()) {

                if (currentOrderItem.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                    RequestOptions myOptions = RequestOptions
                            .bitmapTransform(new RoundedCornersTransformation(context, 7, 2))
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder));
                    Glide.with(context).load(new File(currentOrderItem.getThumbnailUrl()))
                            .apply(myOptions)
                            .into(selfMessageViewHolder.ivMsgImage);

                    if (currentOrderItem.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                        selfMessageViewHolder.pbLoading.setVisibility(View.GONE);
                        selfMessageViewHolder.btnRetry.setVisibility(View.VISIBLE);
                    } else {
                        selfMessageViewHolder.pbLoading.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.btnRetry.setVisibility(View.GONE);
                    }

                } else {
                    RequestOptions myOptions = RequestOptions
                            .bitmapTransform(new RoundedCornersTransformation(context, 7, 2))
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder));
                    Glide.with(context).load(currentOrderItem.getThumbnailUrl())
                            .apply(myOptions)
                            .into(selfMessageViewHolder.ivMsgImage);

                    selfMessageViewHolder.pbLoading.setVisibility(View.GONE);
                    selfMessageViewHolder.btnRetry.setVisibility(View.GONE);

                }
                selfMessageViewHolder.rlImageMessage.setVisibility(View.VISIBLE);
            } else {
                selfMessageViewHolder.rlImageMessage.setVisibility(View.GONE);
            }

            selfMessageViewHolder.rlImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentOrderItem.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                        return;
                    }
                    showImageDialog(context, currentOrderItem.getImageUrl());
                }
            });

            if(currentOrderItem.getIsMessageExpired() == 1 && TextUtils.isEmpty(currentOrderItem.getThumbnailUrl())) {
                selfMessageViewHolder.llRetry.setVisibility(View.VISIBLE);
                selfMessageViewHolder.tvTryAgain.setTag(position);
                selfMessageViewHolder.tvCancel.setTag(position);
                selfMessageViewHolder.tvTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mOnRetry != null) {
                            String muid = currentOrderItem.getMuid();
                            int pos = selfMessageViewHolder.getAdapterPosition();
                            //int pos = (int) view.getTag();
                            mOnRetry.onMessageRetry(muid, pos);
                            //selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        }
                    }
                });

                selfMessageViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //int pos = (int) view.getTag();
                        int pos = selfMessageViewHolder.getAdapterPosition();
                        if(mOnRetry != null) {
                            String muid = currentOrderItem.getMuid();
                            mOnRetry.onMessageCancel(muid, pos);
                        }
                    }
                });
            } else {
                selfMessageViewHolder.llRetry.setVisibility(View.GONE);
            }

            try {
                switch (currentOrderItem.getMessageStatus()) {
                    case MESSAGE_UNSENT:
                    case MESSAGE_IMAGE_RETRY:
                        selfMessageViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fugu_ic_waiting));
                        selfMessageViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.ivMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        break;
                    case MESSAGE_READ:
                        selfMessageViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fugu_tick_double));
                        selfMessageViewHolder.ivMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguMessageRead(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                        break;
                    case MESSAGE_SENT:
                        selfMessageViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.ivMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        break;
                    case MESSAGE_DELIVERED:
                        selfMessageViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fugu_tick_double));
//                        if (Build.VERSION.SDK_INT >= 21) {
//                            selfMessageViewHolder.ivMessageState.getDrawable().setTint(ContextCompat.getColor(context, R.color.fugu_drawable_color));
//                        }
                        selfMessageViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.ivMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        break;
                    default:
                        selfMessageViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.ivMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            selfMessageViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnRetry != null) {
                        selfMessageViewHolder.pbLoading.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.btnRetry.setVisibility(View.GONE);
                        String muid = currentOrderItem.getMuid();
                        mOnRetry.onRetry(currentOrderItem.getImageUrl(), "image/*", currentOrderItem.getMessageIndex(), muid);
                    }
                }
            });

        } else if (itemType == ITEM_ASSIGNMENT) {
            final AssignmentViewHolder assignmentViewHolder = (AssignmentViewHolder) holder;
            final Message currentOrderItem = ((EventItem) items.get(position)).getEvent();
            assignmentViewHolder.tvAssignment.setText(Html.fromHtml(currentOrderItem.getMessage()));
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private int pxToDp(int dpParam) {
        int dpValue = dpParam; // margin in dips
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * d); // margin in pixels
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
//        FuguLog.e("fuguMessageList count", "************==  " + items.size());
        return items.size();
    }

    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot, llChatLeft;
        private TextView tvUserName, tvMsg, tvTime;
        private RelativeLayout rlImageMessage, rlMessages;
        private ImageView ivMsgImage;


        public OtherMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llChatLeft = itemView.findViewById(R.id.llMessageBg);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            rlMessages = itemView.findViewById(R.id.rlMessages);
            ivMsgImage = itemView.findViewById(R.id.ivMsgImage);

        }
    }

    class SelfMessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot, llTime;
        private TextView tvMsg, tvTime, tvName;
        private RelativeLayout rlImageMessage, rlMessages;
        private LinearLayout llChat;
        private ImageView ivMessageState, ivMsgImage;
        private ProgressBar pbLoading;
        private Button btnRetry;
        private TextView tvTryAgain;
        private TextView tvCancel;
        private LinearLayout llRetry;

        public SelfMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llTime = itemView.findViewById(R.id.llTime);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            llChat = itemView.findViewById(R.id.llMessageBg);
            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            rlMessages = itemView.findViewById(R.id.rlMessages);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);
            ivMsgImage = itemView.findViewById(R.id.ivMsgImage);
            pbLoading = itemView.findViewById(R.id.pbLoading);
            btnRetry = itemView.findViewById(R.id.btnRetry);
            tvTryAgain = itemView.findViewById(R.id.tvTryAgain);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            llRetry = itemView.findViewById(R.id.llRetry);
        }
    }

    class SelfVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        public SelfVideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        public VideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
        }
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAssignment;

        public AssignmentViewHolder(View itemView) {
            super(itemView);
            tvAssignment = itemView.findViewById(R.id.tvAssignment);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;

        public DateViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }


    }

    private void showImageDialog(Context context, String imgUrl) {

        try {
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 1.0f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            //adding text dynamically
            ZoomageView ivImage = dialog.findViewById(R.id.ivImage);
            RequestOptions myOptions = RequestOptions
                    .bitmapTransform(new RoundedCornersTransformation(context, 7, 2))
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder));
            Glide.with(context).load(imgUrl)
                    .apply(myOptions)
                    .into(ivImage);
            //adding button click event
            TextView tvCross = (TextView) dialog.findViewById(R.id.tvCross);
            tvCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTimeView(TextView tvMsg, RelativeLayout rlMessages, View time, int itemType, Message message, int position) {
        View vTime;
        if (itemType == ITEM_TYPE_OTHER) {
            vTime = (TextView) time;
        } else {
            vTime = (LinearLayout) time;
        }
        int lineCount = 0;
        int difference = 0;
        lineCount = tvMsg.getLineCount();
        String stringInLastLine;
        if (lineCount != 0) {
            difference = tvMsg.getLayout().getLineStart(lineCount) - tvMsg.getLayout().getLineStart(lineCount - 1);
        }
        String completeString = tvMsg.getText().toString();
        boolean lineCountGreater = lineCount > 1;

        if (lineCountGreater) {
            stringInLastLine = completeString.substring(tvMsg.getText().toString().length() - difference, tvMsg.getText().toString().length());
        } else {
            stringInLastLine = tvMsg.getText().toString();
        }

        float emptySpaceLeft = rlMessages.getMeasuredWidth() - tvMsg.getPaint().measureText(stringInLastLine);
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) vTime.getLayoutParams();
        if (emptySpaceLeft > context.getResources().getDimensionPixelSize(R.dimen.height_ll_time)) {
            if (lineCountGreater) {
                layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layout.addRule(RelativeLayout.RIGHT_OF, -1);
                message.setTimeIndex(2);
            } else {
                layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                layout.addRule(RelativeLayout.RIGHT_OF, tvMsg.getId());
                message.setTimeIndex(3);
            }
            layout.addRule(RelativeLayout.BELOW, -1);
            layout.addRule(RelativeLayout.ALIGN_BOTTOM, tvMsg.getId());
        } else {
            if (!tvMsg.getText().toString().contains("/n")) {
                layout.addRule(RelativeLayout.ALIGN_BOTTOM, -1);
                layout.addRule(RelativeLayout.RIGHT_OF, -1);
                layout.addRule(RelativeLayout.BELOW, tvMsg.getId());
                layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                message.setTimeIndex(1);
            }
        }
        vTime.setLayoutParams(layout);
    }

    private String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

    private String getMessage(Message videoMessage, int userType) {
        String message = "The video call ended";
        String agentType = "you";
        if(userType == FUGU_VIDEO_CALL_VIEW) {
            if(videoMessage.getUserId().intValue() != AgentCommonData.getUserData().getUserId().intValue())
                agentType = videoMessage.getfromName();
            if (videoMessage.getMessageState() != null && videoMessage.getMessageState().intValue() == 2) {
                if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                    message = customerName + " missed a voice call with "+agentType;
                else
                    message = customerName + " missed a video call with "+agentType;
            } else {
                if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                    message = "The voice call ended";
                else
                    message = "The video call ended";
            }
        } else {
            agentType = "You";
            if(!isChatAssignToMe)
                agentType = agentName;

            if(videoMessage.getMessageState() != null && videoMessage.getMessageState().intValue() == 2) {
                if(!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                    message = agentType+" missed a voice call with "+videoMessage.getfromName();
                else
                    message = agentType+" missed a video call with "+videoMessage.getfromName();
            } else {
                if(!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString()))
                    message = "The voice call ended";
                else
                    message = "The video call ended";
            }
        }
        return message;
    }

}
