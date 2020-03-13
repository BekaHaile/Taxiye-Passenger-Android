package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;


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

    private String msg = "";

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
            msg = notification.getTitle();
            holder.textViewTitle.setText(notification.getTitle());
            holder.textViewDescription.setText(notification.getMessage());
            holder.textViewTime.setText(DateOperations
                    .convertDateViaFormat(DateOperations.utcToLocalTZ(notification.getTimePushArrived())));
            holder.linearRoot.setTag(position);
            holder.linearLayoutText.setTag(position);

            try {
                if (notification.getNotificationImage().equalsIgnoreCase("")) {
                    holder.linearLayoutNotificationImage.setVisibility(View.GONE);
                    holder.textViewTitle.setSingleLine(false);
                    holder.textViewDescription.setSingleLine(false);
                    /*if (notification.isExpanded()) {
                        holder.textViewTitle.setSingleLine(false);
                        holder.textViewDescription.setSingleLine(false);
                    } else {
                        holder.textViewTitle.setSingleLine(true);
                        holder.textViewDescription.setSingleLine(true);
                    }*/
                } else {
                    holder.linearLayoutNotificationImage.setVisibility(View.VISIBLE);
                    holder.textViewTitle.setSingleLine(false);
                    holder.textViewDescription.setSingleLine(false);
                    /*if (notification.isExpanded()) {
                        holder.linearLayoutNotificationImage.setVisibility(View.VISIBLE);
                        holder.textViewTitle.setSingleLine(false);
                        holder.textViewDescription.setSingleLine(false);
                    } else {
                        holder.linearLayoutNotificationImage.setVisibility(View.GONE);
                        holder.textViewTitle.setSingleLine(true);
                        holder.textViewDescription.setSingleLine(true);
                    }*/

                    //Picasso.with(activity).load(notification.getNotificationImage()).into(holder.notificationImage);
                    //Picasso.with(activity).load(notification.getNotificationImage()).transform(new CircleTransform()).into(holder.notificationImage);
                    Picasso.with(activity).load(notification.getNotificationImage())
                            .placeholder(R.drawable.ic_notification_placeholder)
                            .error(R.drawable.ic_notification_placeholder)
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
                        /*notificationList.get(position).setExpanded(!notificationList.get(position).isExpanded());
                        notifyItemUnchecked(position);*/
                        openDeepLink(notificationList.get(position).getDeepIndex(),
                                notificationList.get(position).getUrl(),
                                notificationList.get(position).getPostId(),
                                notificationList.get(position).getPostNotificationId());
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

    private void openDeepLink(int deepInt, String url, int postId, int postNotificationId){
        try{
            //int deepInt = Integer.parseInt(deepLink);
            Intent intent = new Intent();
            if(url != null && !"".equalsIgnoreCase(url)){
                Utils.openUrl(activity, url);
            }
            else if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == deepInt){
                intent.setClass(activity, ShareActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == deepInt){
                intent.setClass(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.PROMOTIONS.getOrdinal() == deepInt){
                if(MyApplication.getInstance().isOnline()) {
                    intent.setClass(activity, PromotionActivity.class);
                    activity.startActivity(intent);
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                }
                                @Override
                                public void neutralClick(View v) {
                                }
                                @Override
                                public void negativeClick(View v) {
                                }
                            });
                }
            }
            else if(AppLinkIndex.RIDE_HISTORY.getOrdinal() == deepInt){
                intent.setClass(activity, RideTransactionsActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.SUPPORT.getOrdinal() == deepInt){
                intent.setClass(activity, SupportActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.ABOUT.getOrdinal() == deepInt){
                intent.setClass(activity, AboutActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.ACCOUNT.getOrdinal() == deepInt){
                intent.setClass(activity, AccountActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.GAME_PAGE.getOrdinal() == deepInt){
                if (Data.userData.getGamePredictEnable() == 1) {
                    intent.setClass(activity, T20Activity.class);
                    activity.startActivity(intent);
                }
            } else if(AppLinkIndex.FRESH_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFreshClientId(), new LatLng(Data.latitude, Data.longitude), true);
            } else if(AppLinkIndex.MEAL_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMealsClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.AUTO_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getAutosClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.GROCERY_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getGroceryClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.MENUS_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getMenusClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getDeliveryCustomerClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.PAY_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getPayClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.JUGNOO_STAR.getOrdinal() == deepInt){
                activity.startActivity(new Intent(activity, JugnooStarSubscribedActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(AppLinkIndex.SUBSCRIPTION_PLAN_OPTION_SCREEN.getOrdinal() == deepInt){
                activity.startActivity(new Intent(activity, JugnooStarActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else if(AppLinkIndex.WALLET_TRANSACTIONS.getOrdinal() == deepInt){
                intent.setClass(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_WALLET_TRANSACTIONS, 1);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.FEED_PAGE.getOrdinal() == deepInt){
                if(postId > 0){
                    Prefs.with(activity).save(Constants.SP_POST_ID_TO_OPEN, postId);
                    Prefs.with(activity).save(Constants.SP_POST_NOTIFICATION_ID_TO_OPEN, postNotificationId);
                }
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getFeedClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            else if(AppLinkIndex.PROS_PAGE.getOrdinal() == deepInt){
                MyApplication.getInstance().getAppSwitcher().switchApp(activity, Config.getProsClientId(), new LatLng(Data.latitude, Data.longitude), true);
            }
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


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
            textViewTitle.setTypeface(Fonts.avenirNext(activity));
            textViewTitle.setSingleLine(true);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            textViewTime.setTypeface(Fonts.mavenMedium(activity));
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            textViewDescription.setTypeface(Fonts.mavenMedium(activity));
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
