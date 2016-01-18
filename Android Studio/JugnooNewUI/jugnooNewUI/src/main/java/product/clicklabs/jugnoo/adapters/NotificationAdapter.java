package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


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

		float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());

		try {
			if(notification.getNotificationImage().equalsIgnoreCase("")){
				holder.notificationImage.setVisibility(View.GONE);
			}
			else{
				holder.notificationImage.setVisibility(View.VISIBLE);
				Picasso.with(activity).load(notification.getNotificationImage()).into(holder.notificationImage);
			}
		} catch (Exception e) {
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
        public TextView descriptionTxt, codeTxt, dateTxt;
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
