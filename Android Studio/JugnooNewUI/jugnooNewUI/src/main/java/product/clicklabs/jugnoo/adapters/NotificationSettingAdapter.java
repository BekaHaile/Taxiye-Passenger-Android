package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.NotificationSettingResponseModel;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by gurmail on 10/08/16.
 */
public class NotificationSettingAdapter extends RecyclerView.Adapter<NotificationSettingAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    private ArrayList<NotificationSettingResponseModel.NotificationPrefData> notificationPrefDatas;
    private Callback callback;

    public NotificationSettingAdapter(Activity activity, int rowLayout, Callback callback) {
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.callback = callback;
        notificationPrefDatas = new ArrayList<>();
    }

    public void setResults(ArrayList<NotificationSettingResponseModel.NotificationPrefData> items){
        this.notificationPrefDatas = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(NotificationSettingAdapter.ViewHolder holder, int position) {
        NotificationSettingResponseModel.NotificationPrefData notificationSetting = notificationPrefDatas.get(position);

        holder.textViewNotiTitle.setText(notificationSetting.getTitle());
        holder.textViewNoticontent.setText(notificationSetting.getContent());

        holder.root.setTag(position);

        if(notificationSetting.getStatus() == 0){
            holder.imageViewStatus.setImageResource(R.drawable.jugnoo_sticky_off);
        } else{
            holder.imageViewStatus.setImageResource(R.drawable.jugnoo_sticky_on);
        }

        if(notificationSetting.getIsEditable() == 0) {
            holder.imageViewStatus.setAlpha(0.3f);
        } else{
            holder.imageViewStatus.setAlpha(1.0f);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int clickedPosition = (int) v.getTag();
                    if(notificationPrefDatas.get(clickedPosition).getIsEditable() == 1) {
                        callback.onClick(clickedPosition, notificationPrefDatas.get(clickedPosition));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationPrefDatas == null ? 0 : notificationPrefDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public TextView textViewNotiTitle, textViewNoticontent;
        public ImageView imageViewStatus;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
            textViewNotiTitle = (TextView)itemView.findViewById(R.id.textViewNotiTitle);
            textViewNotiTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

            textViewNoticontent = (TextView)itemView.findViewById(R.id.textViewNotiText);
            textViewNoticontent.setTypeface(Fonts.mavenMedium(activity));

            imageViewStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
        }
    }

    public interface Callback {
        void onClick(int position, NotificationSettingResponseModel.NotificationPrefData prefData);
    }


    public ArrayList<NotificationSettingResponseModel.NotificationPrefData> getNotificationPrefDatas() {
        return notificationPrefDatas;
    }

    public void setNotificationPrefDatas(ArrayList<NotificationSettingResponseModel.NotificationPrefData> notificationPrefDatas) {
        this.notificationPrefDatas = notificationPrefDatas;
    }

}
