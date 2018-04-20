package com.sabkuchfresh.pros.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
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

public class ProsCatalogueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

	private Context context;
	private List<ProsCatalogueData.ProsCatalogueDatum> prosCatalogueDatumList;
	private ArrayList<ProsCatalogueData.CurrentOrder> recentOrders;
	private ArrayList<String> possibleStatus;
	private Callback callback;
	public static final int MAIN_ITEM = 1;
	public static final int ORDER_ITEM = 2;
	private RecyclerView recyclerView;
	private int gridItemsMargin;
	public ProsCatalogueAdapter(Context context, Callback callback, RecyclerView recyclerView) {
		gridItemsMargin = (int) getPxValue(1,context);
		this.context = context;
		this.callback = callback;
		this.recyclerView = recyclerView;
		this.possibleStatus = new ArrayList<>();
		this.possibleStatus.add(context.getString(R.string.booking_n_received));
		this.possibleStatus.add(context.getString(R.string.pro_n_assigned));
		this.possibleStatus.add(context.getString(R.string.service_n_started));
		this.possibleStatus.add(context.getString(R.string.service_n_finished));
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
		private RelativeLayout layoutFrameCategory;

		public ViewHolderCategory(final View view, final ItemListener itemListener) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
			tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
			layoutFrameCategory = (RelativeLayout) view.findViewById(R.id.layout_frame_category);
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
				statusHolder.tvServiceName.setText(recentOrder.getJobNameSplitted()+" ("+context.getString(R.string.service_hash)+recentOrder.getJobId()+")");
				statusHolder.tvServiceDateTime.setText(context.getString(R.string.service_date_colon_format,
						DateOperations.convertDateViaFormatTZ(recentOrder.getJobPickupDatetime())));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if(mholder instanceof ViewHolderCategory) {
			position = position - getRecentOrdersSize();
			int row_no=(position+1)/3;
			int col_no=(position+1)%3;
			boolean setTopMargin,setSideMargins;
			setSideMargins=col_no==2;
			setTopMargin=row_no==0;

			ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum = prosCatalogueDatumList.get(position);
			ViewHolderCategory holder = ((ViewHolderCategory) mholder);
			holder.tvSuperCategoryName.setText(prosCatalogueDatum.getName());
			holder.llRoot.setPaddingRelative(setSideMargins?gridItemsMargin:0,setTopMargin?gridItemsMargin:0
									,setSideMargins?gridItemsMargin:0,gridItemsMargin);

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

				case R.id.llMain:
					callback.onViewDetailsClick(recentOrders.get(pos));
					break;
			}
		}
	}


	private void showPossibleStatus(ArrayList<String> possibleStatus, int status, ViewHolderStatus statusHolder) {
		setDefaultState(statusHolder);
		int selectedSize = context.getResources().getDimensionPixelSize(R.dimen.dp_17);

		// status conversion
		status = getProsOrderState(context, status).first;

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
		@Bind(R.id.llMain)
		LinearLayout llMain;

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
			llMain.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(llMain, view);
				}
			});
		}
	}


	public static Pair<Integer, String> getProsOrderState(Context context, int status){
		String statusName = "";
		if(status == ProsOrderStatus.UNASSIGNED.getOrdinal()
				|| status == ProsOrderStatus.DECLINE.getOrdinal()
				|| status == ProsOrderStatus.UPCOMING.getOrdinal()){
			status = 0;
			statusName = context.getString(R.string.booking_received);
		} else if(status == ProsOrderStatus.ACCEPTED.getOrdinal()
				|| status == ProsOrderStatus.STARTED.getOrdinal()){
			status = 1;
			statusName = context.getString(R.string.pro_assigned);
		} else if(status == ProsOrderStatus.ARRIVED.getOrdinal()){
			status = 2;
			statusName = context.getString(R.string.service_started);
		} else if(status == ProsOrderStatus.ENDED.getOrdinal()){
			status = 3;
			statusName = context.getString(R.string.service_finished);
		}
		else if(status == ProsOrderStatus.FAILED.getOrdinal()){
			statusName = context.getString(R.string.service_failed);
		}
		else if(status == ProsOrderStatus.DECLINE.getOrdinal()){
			status = 0;
			statusName = context.getString(R.string.service_declined);
		}
		else if(status == ProsOrderStatus.CANCEL.getOrdinal()){
			statusName = context.getString(R.string.service_cancelled);
		}
		else if(status == ProsOrderStatus.DELETED.getOrdinal()){
			statusName = context.getString(R.string.service_deleted);
		}
		else if(status == ProsOrderStatus.IGNORED.getOrdinal()){
			statusName = context.getString(R.string.service_ignored);
		}
		else if(status == ProsOrderStatus.SEEN_BY_AGENT.getOrdinal()){
			statusName = context.getString(R.string.service_seen_by_agent);
		}
		return new Pair<>(status, statusName);
	}

	public float getPxValue(int dp, Context activity){
	   return	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, activity.getResources().getDisplayMetrics());
	}

}