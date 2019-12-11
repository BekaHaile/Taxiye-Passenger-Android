package com.sabkuchfresh.feed.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.feed.models.NotificationDatum;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;


/**
 *
 * Created by Shankar on 3/31/17.
 */
public class FeedNotificationsAdapter extends RecyclerView.Adapter<FeedNotificationsAdapter.ViewHolderNotif> implements ItemListener {

    private FreshActivity activity;
    private ArrayList<NotificationDatum> notificationData;
    private Callback callback;
    private RecyclerView recyclerView;

    public FeedNotificationsAdapter(FreshActivity activity, ArrayList<NotificationDatum> notificationData,
                                    RecyclerView recyclerView, Callback callback) {
        this.activity = activity;
        this.notificationData = notificationData;
        this.recyclerView = recyclerView;
        this.callback = callback;
    }

    public void setList(ArrayList<NotificationDatum> slots){
        this.notificationData = slots;
        notifyDataSetChanged();
    }

    @Override
    public FeedNotificationsAdapter.ViewHolderNotif onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed_notification, parent, false);
        return new ViewHolderNotif(v, this);
    }

    @Override
    public void onBindViewHolder(FeedNotificationsAdapter.ViewHolderNotif holder, int position) {
        try {
            NotificationDatum datum = notificationData.get(position);
            if(!TextUtils.isEmpty(datum.getUserImage())){
                Picasso.with(activity).load(datum.getUserImage())
                        .resize(Utils.convertDpToPx(activity, 65), Utils.convertDpToPx(activity, 65))
                        .centerCrop().transform(new CircleTransform()).into(holder.ivUserImage);
            } else {
                holder.ivUserImage.setImageResource(R.drawable.placeholder_img);
            }

            holder.tvNotificationText.setText(Utils.trimHTML(Utils.fromHtml(datum.getNotificationText())));
            holder.tvNotificationTime.setText(FeedHomeAdapter.getTimeToDisplay(datum.getUpdatedAt(), activity.isTimeAutomatic));

            if(datum.isRead()){
                holder.relative.setBackgroundResource(R.drawable.bg_white_menu_color_selector);
            } else {
                holder.relative.setBackgroundResource(R.drawable.bg_pale_orange_menu_color_selector);
            }

            if(datum.getActivityType() == NotificationDatum.ACTIVITY_TYPE_COMMENT){
                holder.tvNotificationTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_active_new, 0, 0, 0);
            } else if(datum.getActivityType() == NotificationDatum.ACTIVITY_TYPE_LIKE){
                holder.tvNotificationTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_active_new, 0, 0, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return notificationData == null ? 0 : notificationData.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if(pos != RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {
                case R.id.relative:
                    callback.onNotificationClick(pos, notificationData.get(pos));
                    break;
            }
        }
    }


    class ViewHolderNotif extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public View vSep;
        ImageView ivUserImage;
        TextView tvNotificationText, tvNotificationTime;
        ViewHolderNotif(final View itemView, final ItemListener itemListener) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            vSep = itemView.findViewById(R.id.vSep);
            ivUserImage = (ImageView) itemView.findViewById(R.id.ivUserImage);
            tvNotificationText = (TextView)itemView.findViewById(R.id.tvNotificationText);
            tvNotificationTime = (TextView)itemView.findViewById(R.id.tvNotificationTime);
            relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(v, itemView);
                }
            });
        }
    }

    public interface Callback {
        void onNotificationClick(int position, NotificationDatum notificationDatum);
    }
}
