package com.fugu.agent.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fugu.FuguColorConfig;
import com.fugu.R;
import com.fugu.agent.model.broadcastResponse.User;
import com.fugu.database.CommonData;

import java.util.List;

/**
 * Created by gurmail on 26/07/18.
 *
 * @author gurmail
 */
public class FleetListAdapter extends RecyclerView.Adapter<FleetListAdapter.ViewHolder> {

    private List<User> arrayList;
    private LayoutInflater inflater;
    private int type = 1;
    private FuguColorConfig fuguColorConfig;


    public FleetListAdapter(Context context, int type, List<User> arrayList) {
        this.arrayList = arrayList;
        this.type = type;
        inflater = LayoutInflater.from(context);
        fuguColorConfig = CommonData.getColorConfig();
    }

    @Override
    public FleetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hippo_textview_for_spinner, parent, false);
        return new FleetListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FleetListAdapter.ViewHolder holder, int position) {
        User data = arrayList.get(position);
        holder.textView.setText(data.getFullName());
        holder.textView.setTextColor(fuguColorConfig.getFuguTextColorPrimary());

        if(type == 3) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
//            if(data.isSelected()) {
//                holder.textView.setTypeface(null, Typeface.BOLD);
//            } else {
//                holder.textView.setTypeface(null, Typeface.NORMAL);
//            }
        }
        holder.textView.setTypeface(null, Typeface.NORMAL);
        holder.checkBox.setChecked(data.isSelected());
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        RadioButton radioButton;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_textview);
            checkBox = itemView.findViewById(R.id.cb_item_view);
            radioButton = itemView.findViewById(R.id.rb_item_view);
            mainLayout = itemView.findViewById(R.id.main_layout);

            radioButton.setVisibility(View.GONE);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if (pos == 0) {
                        boolean flag = !arrayList.get(0).isSelected();
                        for (int i = 0; i < arrayList.size(); i++) {
                            arrayList.get(i).setSelected(flag);
                        }
                    } else {
                        arrayList.get(pos).setSelected(!arrayList.get(pos).isSelected());
                        if (!arrayList.get(pos).isSelected()) {
                            arrayList.get(0).setSelected(false);
                        } else {
                            for (int i = 1; i < arrayList.size(); i++) {
                                if (!arrayList.get(i).isSelected()) {
                                    arrayList.get(0).setSelected(false);
                                    break;
                                } else {
                                    arrayList.get(0).setSelected(true);
                                }
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
