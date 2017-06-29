package com.sabkuchfresh.pros.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.pros.models.ProsOrderStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.DateOperations;

/**
 * Created by shankar on 1/20/17.
 */

public class ProsSuperCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

	private Context context;
	private List<ProsCatalogueData.ProsCatalogueDatum> prosCatalogueDatumList;
	private ArrayList<ProsCatalogueData.CurrentOrder> recentOrders;
	private ArrayList<String> possibleStatus;
	private Callback callback;
	public static final int MAIN_ITEM = 1;
	public static final int ORDER_ITEM = 2;
	private RecyclerView recyclerView;

	public ProsSuperCategoriesAdapter(Context context, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.callback = callback;
		this.recyclerView = recyclerView;
		this.possibleStatus = new ArrayList<>();
		this.possibleStatus.add("Booking Accepted");
		this.possibleStatus.add("Pro Assigned");
		this.possibleStatus.add("Job Started");
		this.possibleStatus.add("Job Finished");
	}

	public synchronized void setList(List<ProsCatalogueData.ProsCatalogueDatum> elements,
									 ArrayList<ProsCatalogueData.CurrentOrder> recentOrders) {
		this.prosCatalogueDatumList = elements;
		this.recentOrders = recentOrders;
		notifyDataSetChanged();
	}

	public class ViewHolderCategory extends RecyclerView.ViewHolder {
		public LinearLayout llRoot;
		public ImageView ivSuperCategoryImage;
		public TextView tvSuperCategoryName;
		public View viewBG;

		public ViewHolderCategory(final View view, final ItemListener itemListener) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
			tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
			viewBG = view.findViewById(R.id.viewBG);
			llRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(llRoot, view);
				}
			});
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ORDER_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pros_order_status, parent, false);
			return new ViewHolderStatus(v, this);
		} else if (viewType == MAIN_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.list_item_pros_super_category, parent, false);
			return new ViewHolderCategory(view, this);
		}
		throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
		if (mholder instanceof ViewHolderStatus) {
			ViewHolderStatus statusHolder = ((ViewHolderStatus) mholder);
			try {
				ProsCatalogueData.CurrentOrder recentOrder = recentOrders.get(position);
				for(int i=0; i<statusHolder.relativeStatusBar.getChildCount(); i++) {
					if(statusHolder.relativeStatusBar.getChildAt(i) instanceof ViewGroup) {
						ViewGroup viewGroup = (ViewGroup)(statusHolder.relativeStatusBar.getChildAt(i));
						for(int j=0; j<viewGroup.getChildCount(); j++) {
							viewGroup.getChildAt(j).setVisibility(View.GONE);
						}
					} else {
						statusHolder.relativeStatusBar.getChildAt(i).setVisibility(View.GONE);
					}
				}
				showPossibleStatus(possibleStatus, recentOrder.getJobStatus(), statusHolder);
				statusHolder.tvServiceName.setText(recentOrder.getJobNameSplitted()+" (Order #"+recentOrder.getJobId()+")");
				statusHolder.tvServiceDateTime.setText(context.getString(R.string.service_date_colon_format,
						DateOperations.convertDateViaFormat(DateOperations.utcToLocalTZ(recentOrder.getJobPickupDatetime()))));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if(mholder instanceof ViewHolderCategory) {
			position = position - getRecentOrdersSize();
			ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum = prosCatalogueDatumList.get(position);
			ViewHolderCategory holder = ((ViewHolderCategory) mholder);
			holder.tvSuperCategoryName.setText(prosCatalogueDatum.getName());

			try {
				if (!TextUtils.isEmpty(prosCatalogueDatum.getImageUrl())) {
					Picasso.with(context).load(prosCatalogueDatum.getImageUrl())
							.placeholder(R.drawable.ic_fresh_new_placeholder)
							.error(R.drawable.ic_fresh_new_placeholder)
							.into(holder.ivSuperCategoryImage);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				Picasso.with(context).load(R.drawable.ic_fresh_new_placeholder)
						.into(holder.ivSuperCategoryImage);
			}

			if (prosCatalogueDatum.getIsEnabled() == 0) {
				holder.viewBG.setBackgroundResource(R.drawable.bg_white_60_selector_color);
			} else {
				holder.viewBG.setBackgroundResource(R.drawable.bg_transparent_white_60_selector);
			}
		}
	}

	@Override
	public int getItemCount() {
		return getRecentOrdersSize() + (prosCatalogueDatumList == null ? 0 : prosCatalogueDatumList.size());
	}

	@Override
	public int getItemViewType(int position) {
		if (position < getRecentOrdersSize()) {
			return ORDER_ITEM;
		} else {
			return MAIN_ITEM;
		}
	}

	private int getRecentOrdersSize() {
		return recentOrders == null ? 0 : recentOrders.size();
	}

	public interface Callback {
		void onItemClick(ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum);
		void onViewDetailsClick(ProsCatalogueData.CurrentOrder recentOrder);
		void onNeedHelpClick(ProsCatalogueData.CurrentOrder recentOrder);
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if (pos != RecyclerView.NO_POSITION) {
			switch (viewClicked.getId()) {
				case R.id.llRoot:
					callback.onItemClick(prosCatalogueDatumList.get(pos-getRecentOrdersSize()));
					break;

				case R.id.tvNeedHelp:
					callback.onNeedHelpClick(recentOrders.get(pos));
					break;

				case R.id.tvViewDetails:
					callback.onViewDetailsClick(recentOrders.get(pos));
					break;
			}
		}
	}


	private void showPossibleStatus(ArrayList<String> possibleStatus, int status, ViewHolderStatus statusHolder) {
		setDefaultState(statusHolder);
		int selectedSize = context.getResources().getDimensionPixelSize(R.dimen.dp_17);

		// status conversion
		status = getProsOrderState(status).first;

		switch (possibleStatus.size()) {
			case 4:
				statusHolder.tvStatus3.setVisibility(View.VISIBLE);
				statusHolder.ivStatus3.setVisibility(View.VISIBLE);
				statusHolder.lineStatus3.setVisibility(View.VISIBLE);
				statusHolder.tvStatus3.setText(possibleStatus.get(3));
				if (status == 3) {
					statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status_green);
					statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				} else if (status > 3) {
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
					statusHolder.ivStatus3.setLayoutParams(layoutParams);
					statusHolder.ivStatus3.setBackgroundResource(R.drawable.ic_order_status_green);
					statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				}
			case 3:
				statusHolder.tvStatus2.setVisibility(View.VISIBLE);
				statusHolder.ivStatus2.setVisibility(View.VISIBLE);
				statusHolder.lineStatus2.setVisibility(View.VISIBLE);
				statusHolder.tvStatus2.setText(possibleStatus.get(2));
				if (status == 2) {
					statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
					statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				} else if (status > 2) {
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
					statusHolder.ivStatus2.setLayoutParams(layoutParams);
					statusHolder.ivStatus2.setBackgroundResource(R.drawable.ic_order_status_green);
					statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				}
			case 2:
				statusHolder.tvStatus1.setVisibility(View.VISIBLE);
				statusHolder.ivStatus1.setVisibility(View.VISIBLE);
				statusHolder.lineStatus1.setVisibility(View.VISIBLE);
				statusHolder.tvStatus1.setText(possibleStatus.get(1));
				if (status == 1) {
					statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
					statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				} else if (status > 1) {
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
					statusHolder.ivStatus1.setLayoutParams(layoutParams);
					statusHolder.ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
					statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(context, R.color.order_status_green));
				}
			case 1:
				statusHolder.tvStatus0.setVisibility(View.VISIBLE);
				statusHolder.ivStatus0.setVisibility(View.VISIBLE);
				statusHolder.tvStatus0.setText(possibleStatus.get(0));
				if (status == 0) {
					statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status_green);
				} else if (status > 0) {
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
					statusHolder.ivStatus0.setLayoutParams(layoutParams);
					statusHolder.ivStatus0.setBackgroundResource(R.drawable.ic_order_status_green);
				}
				break;
		}
	}

	private void setDefaultState(ViewHolderStatus statusHolder) {
		int selectedSize = context.getResources().getDimensionPixelSize(R.dimen.dp_12);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
		statusHolder.ivStatus3.setBackgroundResource(R.drawable.circle_order_status);
		statusHolder.ivStatus3.setLayoutParams(layoutParams);
		statusHolder.lineStatus3.setBackgroundColor(ContextCompat.getColor(context, R.color.rank_5));
		statusHolder.ivStatus2.setBackgroundResource(R.drawable.circle_order_status);
		statusHolder.ivStatus2.setLayoutParams(layoutParams);
		statusHolder.lineStatus2.setBackgroundColor(ContextCompat.getColor(context, R.color.rank_5));
		statusHolder.ivStatus1.setBackgroundResource(R.drawable.circle_order_status);
		statusHolder.ivStatus1.setLayoutParams(layoutParams);
		statusHolder.lineStatus1.setBackgroundColor(ContextCompat.getColor(context, R.color.rank_5));
		statusHolder.ivStatus0.setBackgroundResource(R.drawable.circle_order_status);
		statusHolder.ivStatus0.setLayoutParams(layoutParams);
	}

	public class ViewHolderStatus extends RecyclerView.ViewHolder {
		@Bind(R.id.tvServiceName)
		TextView tvServiceName;
		@Bind(R.id.ivPaidVia)
		ImageView ivPaidVia;
		@Bind(R.id.tvPaidViaValue)
		TextView tvPaidViaValue;
		@Bind(R.id.tvServiceDateTime)
		TextView tvServiceDateTime;
		@Bind(R.id.ivStatus0)
		ImageView ivStatus0;
		@Bind(R.id.lineStatus1)
		View lineStatus1;
		@Bind(R.id.ivStatus1)
		ImageView ivStatus1;
		@Bind(R.id.lineStatus2)
		View lineStatus2;
		@Bind(R.id.ivStatus2)
		ImageView ivStatus2;
		@Bind(R.id.lineStatus3)
		View lineStatus3;
		@Bind(R.id.ivStatus3)
		ImageView ivStatus3;
		@Bind(R.id.llOrderPath)
		LinearLayout llOrderPath;
		@Bind(R.id.tvStatus0)
		TextView tvStatus0;
		@Bind(R.id.tvStatus1)
		TextView tvStatus1;
		@Bind(R.id.tvStatus2)
		TextView tvStatus2;
		@Bind(R.id.tvStatus3)
		TextView tvStatus3;
		@Bind(R.id.relativeStatusBar)
		RelativeLayout relativeStatusBar;
		@Bind(R.id.tvNeedHelp)
		TextView tvNeedHelp;
		@Bind(R.id.tvViewDetails)
		TextView tvViewDetails;

		ViewHolderStatus(final View view, final ItemListener itemListener) {
			super(view);
			ButterKnife.bind(this, view);
			tvNeedHelp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(tvNeedHelp, view);
				}
			});
			tvViewDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(tvViewDetails, view);
				}
			});
		}
	}


	public static Pair<Integer, String> getProsOrderState(int status){
		String statusName = "";
		if(status == ProsOrderStatus.UNASSIGNED.getOrdinal()){
			status = 0;
			statusName = "Booking Accepted";
		} else if(status == ProsOrderStatus.UPCOMING.getOrdinal()
				|| status == ProsOrderStatus.ACCEPTED.getOrdinal()){
			status = 1;
			statusName = "Pro Assigned";
		} else if(status == ProsOrderStatus.STARTED.getOrdinal()
				|| status == ProsOrderStatus.ARRIVED.getOrdinal()){
			status = 2;
			statusName = "Job Started";
		} else if(status == ProsOrderStatus.ENDED.getOrdinal()){
			status = 3;
			statusName = "Job Finished";
		}
		else if(status == ProsOrderStatus.FAILED.getOrdinal()){
			statusName = "Job Failed";
		}
		else if(status == ProsOrderStatus.DECLINE.getOrdinal()){
			statusName = "Job Declined";
		}
		else if(status == ProsOrderStatus.CANCEL.getOrdinal()){
			statusName = "Job Cancelled";
		}
		else if(status == ProsOrderStatus.DELETED.getOrdinal()){
			statusName = "Job Deleted";
		}
		else if(status == ProsOrderStatus.IGNORED.getOrdinal()){
			statusName = "Job Ignored";
		}
		else if(status == ProsOrderStatus.SEEN_BY_AGENT.getOrdinal()){
			statusName = "Job seen by agent";
		}
		return new Pair<>(status, statusName);
	}

}