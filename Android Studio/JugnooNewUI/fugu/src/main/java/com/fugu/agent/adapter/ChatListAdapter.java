package com.fugu.agent.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.R;
import com.fugu.agent.Util.MessageMode;
import com.fugu.agent.Util.Overlay;
import com.fugu.agent.listeners.ItemClickListener;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemClickListener {

    private static final String TAG = ChatListAdapter.class.getSimpleName();
    private Context context;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    public static final int ITEM_PROGRESS_BAR = 3;
    private RecyclerView recyclerView;
    private Integer userId;
    private Callback callback;
    private ArrayList<Object> conversationList;
    private boolean isAllChatClicked;
    private int fragmentType;
    

    public ChatListAdapter(Integer userId, boolean isAllChatClicked, int fragmentType,
                           ArrayList<Object> conversationList, Callback callback, RecyclerView recyclerView) {
        this.userId = userId;
        this.isAllChatClicked = isAllChatClicked;
        this.fragmentType = fragmentType;
        this.conversationList = conversationList;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hippo_list_item_header, parent, false);
            return new ChatListAdapter.ViewHolderHeader(v);
        } else if(viewType == ITEM_PROGRESS_BAR){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hippo_custom_loading_list_item, parent, false);
            return new ChatListAdapter.ProgressBarViewHolder(v);
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hippo_list_item_chat, parent, false);
            return new ChatListAdapter.ViewHolder(v, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        if (viewholder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewholder;
            Conversation conversation = (Conversation) conversationList.get(position);
            if (!TextUtils.isEmpty(conversation.getMessage())) {
                if (conversation.getLast_sent_by_user_type().equals(2) && conversation.getLast_sent_by_id().equals(userId)) {
                    holder.tvMessage.setText("You: " + Html.fromHtml(conversation.getMessage()));
                } else if (conversation.getLast_sent_by_user_type().equals(2) && !conversation.getLast_sent_by_id().equals(userId)) {
                    if (conversation.getLast_sent_by_full_name().contains(" ")) {
                        String[] nameArray = conversation.getLast_sent_by_full_name().split("\\ ");
                        holder.tvMessage.setText(nameArray[0] + ": " + Html.fromHtml(conversation.getMessage()));
                    } else {
                        holder.tvMessage.setText(conversation.getLast_sent_by_full_name() + ": " + Html.fromHtml(conversation.getMessage()));
                    }
                } else {
                    holder.tvMessage.setText(Html.fromHtml(conversation.getMessage()));
                }
            } else {

                if (conversation.getLast_sent_by_user_type().equals(2) && conversation.getLast_sent_by_id().equals(userId)) {
                    holder.tvMessage.setText("You sent a photo");
                } else if (conversation.getLast_sent_by_user_type().equals(2) && !conversation.getLast_sent_by_id().equals(userId)) {
                    if (conversation.getLast_sent_by_full_name().contains(" ")) {
                        String[] nameArray = conversation.getLast_sent_by_full_name().split("\\ ");
                        holder.tvMessage.setText(nameArray[0] + " sent a photo");
                    } else {
                        holder.tvMessage.setText(conversation.getLast_sent_by_full_name() + " sent a Photo");
                    }
                } else {
                    holder.tvMessage.setText("You received a photo");
                }
            }

            if (conversation.getAgentName() != null && !conversation.getAgentName().isEmpty() && isAllChatClicked) {
                holder.tvAgentName.setVisibility(View.VISIBLE);
                holder.tvAgentName.setText(conversation.getAgentName());
            } else {
                holder.tvAgentName.setVisibility(View.GONE);
            }

            holder.tvName.setText(conversation.getLabel());
            if (!TextUtils.isEmpty(conversation.getBotChannelName())) {
                holder.tvChannelName.setVisibility(View.VISIBLE);
                holder.tvChannelName.setText(conversation.getBotChannelName());
            } else {
                holder.tvChannelName.setVisibility(View.GONE);
            }

            if (!conversation.getUnreadCount().equals(0)) {
                holder.tvUnread.setVisibility(View.VISIBLE);
                holder.tvUnread.setText(String.valueOf(conversation.getUnreadCount()));
            } else {
                holder.tvUnread.setVisibility(View.GONE);
            }

            float d = context.getResources().getDisplayMetrics().density;

            if (conversation.getStatus() == MessageMode.CLOSED_CHAT.getOrdinal()) {
//                Typeface face = Typeface.createFromAsset(context.getAssets(),
//                        "fonts/Montserrat-Medium.ttf");
                holder.tvClosed.setVisibility(View.VISIBLE);
                float width = context.getResources().getDimension(R.dimen.fugu_name_width);
                holder.tvName.setMaxWidth((int) width);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.fugu_read_chat_text_color));
//                holder.tvName.setTypeface(face);
                holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.fugu_read_chat_text_color));
                holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.fugu_read_chat_text_color));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.tvMessage.getLayoutParams();
                layoutParams.setMargins(0, (int) (3 * d), (int) (50 * d), 0);
                holder.tvMessage.setLayoutParams(layoutParams);
                holder.tvUnassigned.setBackgroundResource(R.drawable.hippo_bg_tag_color_rounded);
                holder.tvUnassigned.setTextColor(ContextCompat.getColor(context, R.color.fugu_read_chat_text_color));
            } else {
//                Typeface face = Typeface.createFromAsset(context.getAssets(),
//                        "fonts/Montserrat-SemiBold.ttf");
                holder.tvClosed.setVisibility(View.GONE);
                holder.tvClosed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(R.dimen.fugu_name_width_unClose)});
                float width = context.getResources().getDimension(R.dimen.fugu_name_width_unClose);
                holder.tvName.setMaxWidth((int) width);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.fugu_text_color_primary));
//                holder.tvName.setTypeface(face);
                holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.fugu_text_color_primary));
                holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.fugu_text_color_primary));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.tvMessage.getLayoutParams();
                layoutParams.setMargins(0, (int) (-2 * d), (int) (50 * d), 0);
                holder.tvMessage.setLayoutParams(layoutParams);
                holder.tvUnassigned.setBackgroundResource(R.drawable.hippo_bg_unassigned_color_rounded);
                holder.tvUnassigned.setTextColor(ContextCompat.getColor(context, R.color.hippo_white));
            }

            if (conversation.getAgentId() <= 0) {
                holder.tvUnassigned.setVisibility(View.VISIBLE);
            } else {
                holder.tvUnassigned.setVisibility(View.GONE);
            }

            if (conversation.getOverlay() != null && conversation.getOverlay() != Overlay.DEFAULT.getOrdinal()) {
                holder.tvOverlay.setVisibility(View.VISIBLE);
                if (conversation.getOverlay() == Overlay.OPEN_CHAT.getOrdinal()) {
                    holder.tvOverlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hippo_ic_chat_re_opened, 0, 0, 0);
                    holder.tvOverlay.setBackgroundColor(ContextCompat.getColor(context, R.color.fugu_overlay_green_95));
                    holder.tvOverlay.setText(context.getResources().getString(R.string.fugu_conversation_re_opend));
                } else if (conversation.getOverlay() == Overlay.CLOSED_CHAT.getOrdinal()) {
                    holder.tvOverlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hippo_ic_fugu_chat_close, 0, 0, 0);
                    holder.tvOverlay.setBackgroundColor(ContextCompat.getColor(context, R.color.fugu_overlay_black_95));
                    holder.tvOverlay.setText(context.getResources().getString(R.string.fugu_conversation_closed));
                } else if (conversation.getOverlay() == Overlay.ASSIGNMENT.getOrdinal()) {
                    holder.tvOverlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hippo_ic_fugu_chat_assigned, 0, 0, 0);
                    holder.tvOverlay.setBackgroundColor(ContextCompat.getColor(context, R.color.fugu_overlay_yellow_95));
                    holder.tvOverlay.setText(context.getResources().getString(R.string.fugu_conversation_assigned));
                }
                //removeOverlay(holder.tvOverlay, conversation);
            } else {
                holder.tvOverlay.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(conversation.getCreatedAt())) {
                holder.tvTime.setText(DateUtils.getInstance().getDateTimeToShow(conversation.getCreatedAt()));
            }

        } else if (viewholder instanceof ViewHolderHeader) {
            ViewHolderHeader holder = (ViewHolderHeader) viewholder;
            holder.tvHeader.setText("2 " + context.getResources().getString(R.string.fugu_open_conversations));
        }
    }

    @Override
    public int getItemCount() {
        if (conversationList == null || conversationList.size() == 0) {
            return 0;
        } else {
            return conversationList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (conversationList.get(position) instanceof ChatListAdapter.ProgressBarItem) {
            return ITEM_PROGRESS_BAR;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onItemClick(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if (pos != RecyclerView.NO_POSITION) {
            if(viewClicked.getId() == R.id.rlChat) {
                Conversation conversation = (Conversation) conversationList.get(pos);
                callback.onClick(pos, fragmentType, conversation);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlChat;
        public TextView tvMessage, tvName, tvChannelName, tvTime, tvUnread, tvClosed, tvTyping, tvOverlay,
                tvTagged, tvUnassigned, tvAgentName;

        public ViewHolder(final View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            rlChat = itemView.findViewById(R.id.rlChat);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvName = itemView.findViewById(R.id.tvName);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnread = itemView.findViewById(R.id.tvUnread);
            tvClosed = itemView.findViewById(R.id.tvClosed);
            tvTyping = itemView.findViewById(R.id.tvTyping);
            tvTagged = itemView.findViewById(R.id.tvTagged);
            tvUnassigned = itemView.findViewById(R.id.tvUnassigned);
            tvAgentName = itemView.findViewById(R.id.tvAgentName);
            tvOverlay = itemView.findViewById(R.id.tvOverlay);
            rlChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(rlChat, itemView);
                }
            });

            tvOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder{
        public TextView tvHeader;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }
    public static class ProgressBarItem {

    }

    private static class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        public ProgressBarViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface Callback{
        void onClick(int position, int fragmentType, Conversation conversation);
    }
}
