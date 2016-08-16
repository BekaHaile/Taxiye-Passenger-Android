package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.R;
import com.sabkuchfresh.retrofit.model.NotificationData;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.DateOperations;
import com.sabkuchfresh.utils.Fonts;

import java.util.ArrayList;


/**
 * Created by Ankit on 7/17/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity activity;
    private int rowLayout;
    private ArrayList<NotificationData> notificationList;
    private int totalNotifications;
    private Callback callback;

    public NotificationAdapter(Activity activity, int rowLayout, int totalNotifications, Callback callback) {
        this.notificationList = new ArrayList<>();
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.totalNotifications = totalNotifications;
        this.callback = callback;
    }

    public void notifyList(int totalNotifications, ArrayList<NotificationData> notificationList, boolean refresh){
        this.totalNotifications = totalNotifications;
        if(refresh){
            this.notificationList.clear();
        }
        this.notificationList.addAll(notificationList);
        this.notifyDataSetChanged();
    }

    public int getListSize(){
        return notificationList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_show_more, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewFooterHolder(v, activity);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        if(viewholder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewholder;
            NotificationData notification = notificationList.get(position);

            holder.textViewTitle.setText(notification.getTitle());
            holder.textViewDescription.setText(notification.getMessage());
            holder.textViewTime.setText(DateOperations
                    .convertDateViaFormat(DateOperations.utcToLocalTZ(notification.getTimePushArrived())));
            holder.linearRoot.setTag(position);
            holder.linearLayoutText.setTag(position);

            try {
                if (notification.getNotificationImage().equalsIgnoreCase("")) {
                    holder.linearLayoutNotificationImage.setVisibility(View.GONE);
                    if (notification.isExpanded()) {
                        holder.textViewTitle.setSingleLine(false);
                        holder.textViewDescription.setSingleLine(false);
                    } else {
                        holder.textViewTitle.setSingleLine(true);
                        holder.textViewDescription.setSingleLine(true);
                    }
                } else {
                    if (notification.isExpanded()) {
                        holder.linearLayoutNotificationImage.setVisibility(View.VISIBLE);
                        holder.textViewTitle.setSingleLine(false);
                        holder.textViewDescription.setSingleLine(false);
                    } else {
                        holder.linearLayoutNotificationImage.setVisibility(View.GONE);
                        holder.textViewTitle.setSingleLine(true);
                        holder.textViewDescription.setSingleLine(true);
                    }

                    //Picasso.with(activity).load(notification.getNotificationImage()).into(holder.notificationImage);
                    //Picasso.with(activity).load(notification.getNotificationImage()).transform(new CircleTransform()).into(holder.notificationImage);
                    Picasso.with(activity).load(notification.getNotificationImage())
                            .placeholder(R.drawable.ic_fresh_noti_placeholder)
                            .error(R.drawable.ic_fresh_noti_placeholder)

//                        .transform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.TOP))
                            .into(holder.imageViewNotification);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        notificationList.get(position).setExpanded(!notificationList.get(position).isExpanded());
                        notifyItemChanged(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(viewholder instanceof ViewFooterHolder){
            ViewFooterHolder holder = (ViewFooterHolder) viewholder;
            holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onShowMoreClick();
                }
            });
        }

	}

    @Override
    public int getItemCount() {
        if(notificationList == null || notificationList.size() == 0){
            return 0;
        }
        else{
            if(totalNotifications > notificationList.size()){
                return notificationList.size() + 1;
            } else{
                return notificationList.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == notificationList.size();
    }

//    private void openDeepLink(String deepLink){
//        try{
//            int deepInt = Integer.parseInt(deepLink);
//            Intent intent = new Intent();
//            if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == deepInt){
//                intent.setClass(activity, ShareActivity.class);
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == deepInt){
//                intent.setClass(activity, PaymentActivity.class);
//                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.PROMOTIONS.getOrdinal() == deepInt){
//                if(AppStatus.getInstance(activity).isOnline(activity)) {
//                    intent.setClass(activity, ShareActivity.class);
//                    activity.startActivity(intent);
//                } else {
//                    DialogPopup.dialogNoInternet(activity,
//                            Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
//                            new Utils.AlertCallBackWithButtonsInterface() {
//                                @Override
//                                public void positiveClick(View v) {
//                                }
//                                @Override
//                                public void neutralClick(View v) {
//                                }
//                                @Override
//                                public void negativeClick(View v) {
//                                }
//                            });
//                }
//            }
//            else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == deepInt){
//                intent.setClass(activity, RideTransactionsActivity.class);
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.SUPPORT.getOrdinal() == deepInt){
//                intent.setClass(activity, SupportActivity.class);
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.ABOUT.getOrdinal() == deepInt){
//                intent.setClass(activity, AboutActivity.class);
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.ACCOUNT.getOrdinal() == deepInt){
//                intent.setClass(activity, AccountActivity.class);
//                activity.startActivity(intent);
//            }
//            else if(AppLinkIndex.GAME_PAGE.getOrdinal() == deepInt){
//                if (Data.userData.getGamePredictEnable() == 1) {
//                    intent.setClass(activity, T20Activity.class);
//                    activity.startActivity(intent);
//                }
//            } else if(AppLinkIndex.FRESH_PAGE.getOrdinal() == deepInt){
//                activity.startActivity(new Intent(activity, FreshActivity.class));
//                activity.overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
//            }
//            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearRoot, linearLayoutText, linearLayoutNotificationImage;
        public ImageView imageViewNotification;
        public TextView textViewTitle, textViewTime, textViewDescription;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            linearRoot = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            linearLayoutText = (LinearLayout) itemView.findViewById(R.id.linearLayoutText);
            linearLayoutNotificationImage = (LinearLayout) itemView.findViewById(R.id.linearLayoutNotificationImage);
            imageViewNotification = (ImageView)itemView.findViewById(R.id.imageViewNotification);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            textViewTitle.setSingleLine(true);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            textViewTime.setTypeface(Fonts.mavenLight(activity));
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            textViewDescription.setTypeface(Fonts.mavenLight(activity));
            textViewDescription.setSingleLine(true);
        }
    }

    public class ViewFooterHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutShowMore;
        public TextView textViewShowMore;
        public ViewFooterHolder(View convertView, Activity context) {
            super(convertView);
            relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
            textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.mavenLight(context));
            textViewShowMore.setText(context.getResources().getString(R.string.show_more));
        }
    }

    public interface Callback{
        void onShowMoreClick();
    }
}
