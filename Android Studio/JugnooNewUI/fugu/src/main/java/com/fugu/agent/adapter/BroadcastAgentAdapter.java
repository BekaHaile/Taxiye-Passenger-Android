package com.fugu.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.R;
import com.fugu.agent.model.broadcastStatus.BroadcastInfo;

import java.util.ArrayList;

/**
 * Created by gurmail on 23/07/18.
 *
 * @author gurmail
 */

public class BroadcastAgentAdapter extends RecyclerView.Adapter<BroadcastAgentAdapter.ViewHolder> {
    private static final String TAG = BroadcastAgentAdapter.class.getSimpleName();
    private int adapterType = 0;
    private Context context;


    public BroadcastAgentAdapter(ArrayList<BroadcastInfo> arrayList) {

    }

    @Override
    public BroadcastAgentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.hippo_item_broadcast_layout, parent, false);
        return new BroadcastAgentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(""+position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.main_layout);
            textView = itemView.findViewById(R.id.alertTextView);
            checkBox = itemView.findViewById(R.id.alertCheckbox);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
