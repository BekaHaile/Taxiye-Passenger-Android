package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.PromotionsActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.SupportActivity;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;


/**
 * Created by Ankit on 7/17/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    NotificationData notification;
    private ArrayList<NotificationData> notificationList = new ArrayList<>();

    public NotificationAdapter(ArrayList<NotificationData> notificationList, Activity activity, int rowLayout) {
        this.notificationList = notificationList;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        notification = notificationList.get(position);

        holder.descriptionTxt.setText(notification.getMessage());
        holder.container.setTag(position);

		float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());

		try {
			if(notification.getNotificationImage().equalsIgnoreCase("")){
				holder.notificationImage.setVisibility(View.GONE);
			}
			else{
				holder.notificationImage.setVisibility(View.VISIBLE);
				//Picasso.with(activity).load(notification.getNotificationImage()).into(holder.notificationImage);
                Picasso.with(activity).load(notification.getNotificationImage()).transform(new CircleTransform()).into(holder.notificationImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = (int) v.getTag();
                    openDeepLink(notificationList.get(position).getDeepIndex());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

	}

    private void openDeepLink(String deepLink){
        try{
            int deepInt = Integer.parseInt(deepLink);
            Intent intent = new Intent();
            if(AppLinkIndex.INVITE_AND_EARN.getOrdinal() == deepInt){
                intent.setClass(activity, ShareActivity.class);
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.JUGNOO_CASH.getOrdinal() == deepInt){
                intent.setClass(activity, PaymentActivity.class);
                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
                activity.startActivity(intent);
            }
            else if(AppLinkIndex.PROMOTIONS.getOrdinal() == deepInt){
                if(AppStatus.getInstance(activity).isOnline(activity)) {
                    intent.setClass(activity, PromotionsActivity.class);
                    activity.startActivity(intent);
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
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
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout container;
        public ImageView notificationImage;
        public TextView descriptionTxt;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.container);
            notificationImage = (ImageView)itemView.findViewById(R.id.notification_image);
            descriptionTxt = (TextView) itemView.findViewById(R.id.description);
            //codeTxt = (TextView) itemView.findViewById(R.id.code_text);
            //dateTxt = (TextView)itemView.findViewById(R.id.date_text);

            descriptionTxt.setTypeface(Fonts.mavenLight(activity));
        }
    }
}
