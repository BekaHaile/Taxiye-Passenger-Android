package com.fugu.agent.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fugu.FuguColorConfig;
import com.fugu.R;
import com.fugu.agent.model.broadcastStatus.BroadcastInfo;
import com.fugu.database.CommonData;
import com.fugu.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by gurmail on 30/07/18.
 *
 * @author gurmail
 */

public class BroadcastStatusAdapter extends RecyclerView.Adapter<BroadcastStatusAdapter.ViewHolder> {

    private static final String TAG = BroadcastStatusAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<BroadcastInfo> arrayList = new ArrayList<>();
    private FuguColorConfig fuguColorConfig;

    public BroadcastStatusAdapter(ArrayList<BroadcastInfo> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        fuguColorConfig = CommonData.getColorConfig();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hippo_item_broadcast_status, parent, false);
        return new BroadcastStatusAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BroadcastInfo info = arrayList.get(position);
        holder.textViewStatusValue.setText(info.getStatus() == 1 ? "SUCCESS" : "PENDING");
        holder.textViewFromValue.setText(info.getFullName());
        holder.textViewTitleValue.setText(info.getBroadcastTitle());
        holder.textViewMsgValue.setText(info.getUserFirstMessage());
        holder.textViewDateValue.setText(DateUtils.getInstance().convertToLocal(info.getCreatedAt(), DateUtils.STANDARD_DATE_ONLY_FORMAT));
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewStatus, textViewStatusValue, textViewFrom, textViewFromValue, textViewTitle, textViewTitleValue,
                textViewMsg, textViewMsgValue, textViewDate, textViewDateValue;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id. textViewStatus);
            textViewFrom = itemView.findViewById(R.id. textViewFrom);
            textViewTitle = itemView.findViewById(R.id. textViewTitle);
            textViewMsg = itemView.findViewById(R.id. textViewMsg);
            textViewDate = itemView.findViewById(R.id. textViewDate);

            textViewStatusValue = itemView.findViewById(R.id. textViewStatusValue);
            textViewFromValue = itemView.findViewById(R.id. textViewFromValue);
            textViewTitleValue = itemView.findViewById(R.id. textViewTitleValue);
            textViewMsgValue = itemView.findViewById(R.id. textViewMsgValue);
            textViewDateValue = itemView.findViewById(R.id. textViewDateValue);

            textViewStatusValue.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            textViewFromValue.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            textViewTitleValue.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            textViewMsgValue.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
            textViewDateValue.setTextColor(fuguColorConfig.getFuguTextColorPrimary());

            textViewStatus.setTextColor(fuguColorConfig.getFuguTextColorSecondary());
            textViewFrom.setTextColor(fuguColorConfig.getFuguTextColorSecondary());
            textViewTitle.setTextColor(fuguColorConfig.getFuguTextColorSecondary());
            textViewMsg.setTextColor(fuguColorConfig.getFuguTextColorSecondary());
            textViewDate.setTextColor(fuguColorConfig.getFuguTextColorSecondary());

        }
    }
}
