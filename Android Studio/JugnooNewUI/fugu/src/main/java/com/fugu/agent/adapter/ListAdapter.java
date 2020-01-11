package com.fugu.agent.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fugu.FuguColorConfig;
import com.fugu.R;
import com.fugu.agent.model.broadcastResponse.Tag;
import com.fugu.database.CommonData;

import java.util.List;

/**
 * Created by gurmail on 26/07/18.
 *
 * @author gurmail
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Tag> arrayList;
    private LayoutInflater inflater;
    private SelectedId selectedId;
    private FuguColorConfig fuguColorConfig;

    public ListAdapter(Context context, List<Tag> arrayList, SelectedId selectedId) {
        this.arrayList = arrayList;
        this.selectedId = selectedId;
        inflater = LayoutInflater.from(context);
        fuguColorConfig = CommonData.getColorConfig();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hippo_textview_for_spinner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag data = arrayList.get(position);
        holder.textView.setText(data.getTagName());
        holder.textView.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
        holder.radioButton.setChecked(data.isSelected());
//        if(data.isSelected()) {
//            holder.textView.setTypeface(null, Typeface.BOLD);
//        } else {
//            holder.textView.setTypeface(null, Typeface.NORMAL);
//        }
        holder.textView.setTypeface(null, Typeface.NORMAL);
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

            radioButton.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    boolean isFlag = false;
                    int id = -2;
                    if (pos == 0) {
                        isFlag = true;
                        if(arrayList.get(0).isSelected())
                            isFlag = false;

                        for (int i = 0; i < arrayList.size(); i++) {
                            arrayList.get(i).setSelected(isFlag);
                        }
                        if(arrayList.get(0).isSelected()) {
                            id = -1;
                        }
                        if(selectedId != null) {
                            selectedId.selectedTeamId(id, arrayList.get(0));
                        }
                    } else {
                        if(arrayList.get(pos).isSelected()) {
                            if(arrayList.get(0).isSelected())
                                isFlag = true;
                            for (int i = 0; i < arrayList.size(); i++) {
                                arrayList.get(i).setSelected(false);
                            }
                            if(isFlag) {
                                arrayList.get(pos).setSelected(!arrayList.get(pos).isSelected());
                                id = arrayList.get(pos).getTagId();
                            } else {
                                id = -2;
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                arrayList.get(i).setSelected(false);
                            }
                            arrayList.get(pos).setSelected(!arrayList.get(pos).isSelected());
                            id = arrayList.get(pos).getTagId();
                        }

                        if(selectedId != null) {
                            selectedId.selectedTeamId(id, arrayList.get(getAdapterPosition()));
                        }

                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface SelectedId {
        void selectedTeamId(int id, Tag tag);
    }

}
