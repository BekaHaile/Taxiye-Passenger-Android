package com.fugu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguFontConfig;
import com.fugu.R;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChannelStatus;
import com.fugu.model.FuguConversation;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.RoundedCornersTransformation;

import java.util.ArrayList;

import static com.fugu.constant.FuguAppConstant.IMAGE_MESSAGE;
import static com.fugu.constant.FuguAppConstant.MESSAGE_IMAGE_RETRY;
import static com.fugu.constant.FuguAppConstant.MESSAGE_READ;
import static com.fugu.constant.FuguAppConstant.MESSAGE_UNSENT;
import static com.fugu.constant.FuguAppConstant.VIDEO_CALL;

/**
 * Created by Bhavya Rattan on 08/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();
    private Activity activity;
    private String userName;
    private String businessName;
    private Long userId = -1L;
    private String enUserId = "";
    private FuguColorConfig fuguColorConfig;
    private Callback callback;

    public FuguChannelsAdapter(Activity activity, ArrayList<FuguConversation> fuguConversationList, String userName, Long userId, String businessName, Callback callback, String enUserId) {
        inflater = LayoutInflater.from(activity.getApplicationContext());

        fuguColorConfig = CommonData.getColorConfig();
        this.fuguConversationList = fuguConversationList;
        this.activity = activity;
        this.userName = userName;
        this.businessName = businessName;
        this.userId = userId;
        this.callback = callback;
        this.enUserId = enUserId;
    }

    public void updateList(ArrayList<FuguConversation> fuguConversationList) {
        this.fuguConversationList = fuguConversationList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_channels, parent, false);
        return new ChannelViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final ChannelViewHolder channelViewHolder = (ChannelViewHolder) holder;
            final FuguConversation currentChannelItem = fuguConversationList.get(position);

            channelViewHolder.tvChannelName.setText(currentChannelItem.getLabel());
            channelViewHolder.tvChannelName.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            channelViewHolder.tvMessage.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            channelViewHolder.viewDivider.setBackgroundColor(fuguColorConfig.getFuguBorderColor());
            if(currentChannelItem.getMessage_type() == VIDEO_CALL) {
                channelViewHolder.ivMessageState.setVisibility(View.GONE);
                channelViewHolder.tvMessage.setText(getMessageData(currentChannelItem));
            } else if (TextUtils.isEmpty(currentChannelItem.getMessage())) {
                if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                    if (currentChannelItem.getUserId().equals(FuguConfig.getInstance().getUserData().getUserId())) {
                        if (FuguConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                channelViewHolder.tvMessage.setText("You: " + activity.getString(R.string.fugu_attachment));
                            } else {
                                channelViewHolder.tvMessage.setText("You sent a message");
                            }
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + activity.getString(R.string.fugu_attachment));
                            } else {
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + " " + "sent a message");
                            }
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                        channelViewHolder.ivMessageState.setVisibility(View.GONE);
                    }
                }
            } else {
                if (currentChannelItem.getMessage().contains("\n")) {
                    channelViewHolder.tvMessage.setText(currentChannelItem.getMessage().replaceAll("\n", " "));
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        FuguLog.e("error", FuguConfig.getInstance().getUserData().getUserId() + "");
                        FuguLog.e("error", currentChannelItem.getUserId() + "");
                        if (FuguConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(FuguConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        FuguLog.e("error", FuguConfig.getInstance().getUserData().getUserId() + "");
                        FuguLog.e("error", currentChannelItem.getUserId() + "");
                        if (FuguConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(FuguConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    }
                }
            }
//            }

            if (currentChannelItem.getUnreadCount() > 0) {
                channelViewHolder.tvChannelName.setTypeface(channelViewHolder.tvChannelName.getTypeface(),Typeface.BOLD);
                channelViewHolder.tvMessage.setTypeface(channelViewHolder.tvMessage.getTypeface(),Typeface.BOLD);
                channelViewHolder.circularTvMessageCount.setVisibility(View.VISIBLE);
				channelViewHolder.rlRoot.setSelected(true);
                //            channelViewHolder.circularTvMessageCount.setSolidColor(FuguConfig.getInstance().getThemeColor());
                //            channelViewHolder.circularTvMessageCount.setStrokeWidth(0);
                channelViewHolder.circularTvMessageCount.setText(String.valueOf(currentChannelItem.getUnreadCount()));

                channelViewHolder.tvDate.setTextColor(fuguColorConfig.getFuguTextColorPrimary());

            } else {
                channelViewHolder.tvChannelName.setTypeface(channelViewHolder.tvChannelName.getTypeface(),Typeface.NORMAL);
                channelViewHolder.tvMessage.setTypeface(channelViewHolder.tvMessage.getTypeface(),Typeface.NORMAL);
                channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                channelViewHolder.tvDate.setTextColor(fuguColorConfig.getFuguChannelDateText());
				channelViewHolder.rlRoot.setSelected(false);
            }

            if (currentChannelItem.getChannelImage() == null || currentChannelItem.getChannelImage().trim().isEmpty()) {
                channelViewHolder.ivChannelIcon.setVisibility(View.GONE);

                channelViewHolder.tvChannelIcon.setText(currentChannelItem.getLabel().substring(0, 1).toUpperCase());
                channelViewHolder.tvChannelIcon.setVisibility(View.VISIBLE);

                Glide.with(activity).clear(channelViewHolder.ivChannelIcon);

                channelViewHolder.ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.hippo_ring_grey));

                channelViewHolder.ivChannelIcon.getDrawable()
                        .setColorFilter(fuguColorConfig.getFuguChannelDateText(), PorterDuff.Mode.SRC_ATOP);
                channelViewHolder.tvChannelIcon.setTextColor(fuguColorConfig.getFuguChannelItemBg());

                GradientDrawable tvBackground = (GradientDrawable) channelViewHolder.tvChannelIcon.getBackground();
                tvBackground.setColor(fuguColorConfig.getFuguChannelDateText());

            } else {
                RequestOptions myOptions = RequestOptions
                        .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                        .placeholder(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon));
                Glide.with(activity).load(currentChannelItem.getChannelImage())
                        .apply(myOptions)
                        .into(channelViewHolder.ivChannelIcon);

                channelViewHolder.tvChannelIcon.setVisibility(View.GONE);
            }

            if (currentChannelItem.getChannelId().compareTo(-1L) == 0) {
                channelViewHolder.tvDate.setVisibility(View.GONE);
            } else {
                channelViewHolder.tvDate.setText(DateUtils.getRelativeDate(DateUtils.getInstance().convertToLocal(currentChannelItem.getDateTime()), true));
                channelViewHolder.tvDate.setVisibility(View.VISIBLE);
            }

            if (currentChannelItem.getChannelStatus() == ChannelStatus.CLOSED.getOrdinal()
                    && currentChannelItem.getLabelId() < 0) {
                channelViewHolder.vClosed.setVisibility(View.VISIBLE);
            } else {
                channelViewHolder.vClosed.setVisibility(View.GONE);
            }

            channelViewHolder.rlRoot.setBackgroundDrawable(FuguColorConfig
                    .makeSelector(fuguColorConfig.getFuguChannelItemBg(), fuguColorConfig.getFuguChannelItemBgPressed()));

            channelViewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                    currentChannelItem.setUnreadCount(0);

                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabel(currentChannelItem.getLabel());
                    conversation.setChannelId(currentChannelItem.getChannelId());
                    conversation.setLabelId(currentChannelItem.getLabelId());
                    conversation.setDefaultMessage(currentChannelItem.getMessage());
                    conversation.setChannelStatus(currentChannelItem.getChannelStatus());
                    conversation.setBusinessName(businessName);
                    conversation.setUserId(userId);
                    conversation.setEnUserId(enUserId);
                    conversation.setOpenChat(true);
                    conversation.setUserName(userName);
                    conversation.setIsTimeSet(1);
                    conversation.setStatus(currentChannelItem.getStatus());
                    conversation.setLast_sent_by_id(currentChannelItem.getLast_sent_by_id());
                    callback.onClick(conversation);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return fuguConversationList.size();
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlRoot;
        private TextView tvChannelName, tvMessage, tvDate, tvChannelIcon;
        private ImageView ivChannelIcon, ivMessageState;
        private TextView circularTvMessageCount;
        private View viewDivider, vClosed;

        ChannelViewHolder(View itemView) {
            super(itemView);
            rlRoot = itemView.findViewById(R.id.rlRoot);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvChannelIcon = itemView.findViewById(R.id.tvChannelIcon);
            ivChannelIcon = itemView.findViewById(R.id.ivChannelIcon);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);
            circularTvMessageCount = itemView.findViewById(R.id.circularTvMessageCount);
            viewDivider = itemView.findViewById(R.id.viewDivider);
            vClosed = itemView.findViewById(R.id.vClosed);

            FuguFontConfig fuguFontConfig = CommonData.getFontConfig();
            tvChannelName.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            tvMessage.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            tvDate.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            circularTvMessageCount.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));

        }
    }

    public interface Callback {
        void onClick(FuguConversation conversation);
    }

    private String getMessageData(FuguConversation currentChannelItem) {
        String message = "The video call ended";
        String callType = "video";
        if(!TextUtils.isEmpty(currentChannelItem.getCallType()) && currentChannelItem.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
            callType = "voice";
        }
        if(currentChannelItem.getMessageState() != null && currentChannelItem.getMessageState().intValue() == 2) {
            if (currentChannelItem.getLast_sent_by_id().equals(FuguConfig.getInstance().getUserData().getUserId())) {
                message = "Customer missed a " + callType + " call with you";
            } else {
                message = "You missed a " + callType + " call with "+currentChannelItem.getLast_sent_by_full_name();
            }
        } else {
            message = "The " + callType + " call ended";
        }

        return message;
    }


}

