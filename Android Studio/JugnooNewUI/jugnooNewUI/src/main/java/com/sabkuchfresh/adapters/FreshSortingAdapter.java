package com.sabkuchfresh.adapters;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SortResponseModel;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class FreshSortingAdapter extends RecyclerView.Adapter<FreshSortingAdapter.ViewHolderSort> implements ItemListener{

	private FreshActivity activity;
	private ArrayList<SortResponseModel> sortArray;
	private RecyclerView recyclerView;
	private Callback callback;

	public FreshSortingAdapter(FreshActivity activity, ArrayList<SortResponseModel> sortArray, RecyclerView recyclerView, Callback callback) {
		this.activity = activity;
		this.sortArray = sortArray;
		this.recyclerView = recyclerView;
		this.callback = callback;
	}

	public void setList(ArrayList<SortResponseModel> sortArray) {
		this.sortArray = sortArray;
		notifyDataSetChanged();
	}

	@Override
	public ViewHolderSort onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_sort, parent, false);
		return new ViewHolderSort(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolderSort holder, int position) {
		try {
			SortResponseModel slot = sortArray.get(position);

			holder.tvSortType.setText(slot.name);
			if (!slot.check) {
				holder.tvSortType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				holder.tvSortType.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
			} else {
				holder.tvSortType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_fresh_sort_tick_svg, 0);
				holder.tvSortType.setTextColor(ContextCompat.getColor(activity, R.color.theme_color));
			}

			holder.viewDivider.setVisibility(position == sortArray.size() - 1 ? View.GONE : View.VISIBLE);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getItemCount() {
		return sortArray == null ? 0 : sortArray.size();
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int position = recyclerView.getChildLayoutPosition(parentView);
		if (position != RecyclerView.NO_POSITION) {
			switch (viewClicked.getId()){
				case R.id.linear:
					callback.onSlotSelected(position, sortArray.get(position));
					for (int i = 0; i < sortArray.size(); i++) {
						sortArray.get(i).setCheck(i == position);
					}
					notifyDataSetChanged();
					break;
			}
		}
	}


	class ViewHolderSort extends RecyclerView.ViewHolder {
		public LinearLayout linear;
		public TextView tvSortType;
		public View viewDivider;

		public ViewHolderSort(final View itemView, final ItemListener itemListener) {
			super(itemView);
			linear = (LinearLayout) itemView.findViewById(R.id.linear);
			tvSortType = (TextView) itemView.findViewById(R.id.tvSortType);
			viewDivider = itemView.findViewById(R.id.viewDivider);
			linear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(linear, itemView);
				}
			});
		}
	}


	public interface Callback {
		void onSlotSelected(int position, SortResponseModel sort);
	}

}
