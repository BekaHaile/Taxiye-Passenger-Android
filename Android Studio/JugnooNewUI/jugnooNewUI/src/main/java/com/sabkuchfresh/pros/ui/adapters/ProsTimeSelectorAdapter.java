package com.sabkuchfresh.pros.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.pros.models.TimeDisplay;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 04/07/17.
 */

public class ProsTimeSelectorAdapter extends RecyclerView.Adapter<ProsTimeSelectorAdapter.ViewHolderTime> implements ItemListener {

	private ArrayList<TimeDisplay> timeDisplays;
	private RecyclerView recyclerView;
	private Callback callback;

	public ProsTimeSelectorAdapter(ArrayList<TimeDisplay> timeDisplays, RecyclerView recyclerView, Callback callback) {
		this.timeDisplays = timeDisplays;
		this.recyclerView = recyclerView;
		this.callback = callback;
	}

	public void setList(ArrayList<TimeDisplay> timeDisplays) {
		this.timeDisplays = timeDisplays;
		notifyDataSetChanged();
	}

	@Override
	public ViewHolderTime onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_time_display, parent, false);
		return new ViewHolderTime(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolderTime holder, int position) {
		try {
			TimeDisplay timeDisplay = timeDisplays.get(position);
			holder.ivCheck.setImageResource(timeDisplay.isSelected() ? R.drawable.ic_radio_button_selected : R.drawable.ic_radio_button_normal);
			holder.tvDisplay.setText(timeDisplay.getDisplay());
			holder.ivSep.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getItemCount() {
		return timeDisplays == null ? 0 : timeDisplays.size();
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int position = recyclerView.getChildLayoutPosition(parentView);
		if (position != RecyclerView.NO_POSITION) {
			switch (viewClicked.getId()){
				case R.id.relative:
					if(callback.onTimeDisplaySelected(timeDisplays.get(position))) {
						for (int i = 0; i < timeDisplays.size(); i++) {
							timeDisplays.get(i).setSelected(i == position);
						}
						notifyDataSetChanged();
					}
					break;
			}
		}
	}

	class ViewHolderTime extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public ImageView ivCheck;
		public TextView tvDisplay;
		public ImageView ivSep;

		public ViewHolderTime(final View itemView, final ItemListener itemListener) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
			tvDisplay = (TextView) itemView.findViewById(R.id.tvDisplay);
			ivSep = (ImageView) itemView.findViewById(R.id.ivSep);
			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(relative, itemView);
				}
			});
		}
	}

	public interface Callback {
		boolean onTimeDisplaySelected(TimeDisplay timeDisplay);
	}
}
