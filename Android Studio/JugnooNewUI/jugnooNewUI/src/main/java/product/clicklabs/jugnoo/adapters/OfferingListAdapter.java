package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;

/**
 * Created by socomo on 12/7/16.
 */

public class OfferingListAdapter extends RecyclerView.Adapter<OfferingListAdapter.ViewHolder> implements ItemListener, GAAction, GACategory {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Offering> offerings;
	private RecyclerView recyclerView;
	private Callback callback;
	private int minHeightOfEachCell;
	private int maxHeightGraphLayout;

	public OfferingListAdapter(Context context, ArrayList<Offering> offerings, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.recyclerView = recyclerView;
		this.offerings = offerings;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		minHeightOfEachCell = context.getResources().getDimensionPixelSize(R.dimen.dp_100);
		maxHeightGraphLayout = context.getResources().getDimensionPixelSize(R.dimen.dp_150);
	}

	@Override
	public int getItemCount() {
		return offerings == null ? 0 : offerings.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = layoutInflater.inflate(R.layout.list_item_home_switcher, parent, false);
		RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) convertView.getLayoutParams();
		int height = parent.getMeasuredHeight() / offerings.size();
		if(height< minHeightOfEachCell){
			height = minHeightOfEachCell;
		}
		layoutParams.height = height;
		convertView.setLayoutParams(layoutParams);
		return new ViewHolder(convertView, this);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Offering offering = offerings.get(position);
		holder.ivOffering.setImageResource(offering.getIconRes());
		holder.tvOfferingName.setText(offering.getName());
		holder.tvOfferingDesc.setText(offering.getDesc());
		holder.ivGraphImage.setImageResource(offering.getGraphIcon());
		holder.bgLayout.setBackgroundDrawable(ContextCompat.getDrawable(context,offering.getItemBackground()));
		if(offering.getClientId().equals(Config.getFeedClientId())){
			holder.viewGraphMargin.setVisibility(View.GONE);
		}else
		{
			holder.viewGraphMargin.setVisibility(View.VISIBLE);
		}



	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildAdapterPosition(parentView);
		if (pos != RecyclerView.NO_POSITION) {
			try {
				double latitude = (((HomeSwitcherActivity) context).getIntent().getDoubleExtra(Constants.KEY_LATITUDE, callback.getLatLng().latitude));
				double longitude = (((HomeSwitcherActivity) context).getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, callback.getLatLng().longitude));
				Bundle bundle = ((HomeSwitcherActivity) context).getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
				MyApplication.getInstance().getAppSwitcher().switchApp(((HomeSwitcherActivity) context), offerings.get(pos).getClientId(),
						((HomeSwitcherActivity) context).getIntent().getData(),
						new LatLng(latitude, longitude), bundle, false, false, false);
				GAUtils.event(JUGNOO, HOME + PAGE, offerings.get(pos).getName()+" "+CLICKED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	 class ViewHolder extends RecyclerView.ViewHolder {
		public CardView rlRoot;
		public ImageView ivOffering,ivGraphImage;
		public TextView tvOfferingName, tvOfferingDesc;
		private LinearLayout layoutGraph;
		private View viewGraphMargin;
		 private LinearLayout bgLayout;
		public ViewHolder(final View itemView, final ItemListener itemListener) {
			super(itemView);
			rlRoot = (CardView) itemView.findViewById(R.id.rlRoot);
			bgLayout = (LinearLayout) itemView.findViewById(R.id.bgHomeSwitcher);
			ivOffering = (ImageView) itemView.findViewById(R.id.ivOffering);
			ivGraphImage = (ImageView) itemView.findViewById(R.id.iv_graph_image);
			tvOfferingName = (TextView) itemView.findViewById(R.id.tvOfferingName); tvOfferingName.setTypeface(tvOfferingName.getTypeface(), Typeface.BOLD);
			tvOfferingDesc = (TextView) itemView.findViewById(R.id.tvOfferingDesc);
			layoutGraph = (LinearLayout) itemView.findViewById(R.id.layout_graph);
			viewGraphMargin =  itemView.findViewById(R.id.iv_graph_margin);
			rlRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(rlRoot, itemView);
				}
			});
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutGraph.getLayoutParams();

			if(layoutParams.height>maxHeightGraphLayout){
				params.height = maxHeightGraphLayout;
			}else{
				params.height = LinearLayout.LayoutParams.MATCH_PARENT;
			}
			layoutGraph.setLayoutParams(params);

		}
	}

	public interface Callback {
		LatLng getLatLng();
	}

	public static class Offering {
		private String clientId, name, desc;
		private int iconRes, graphIcon,itemBackground;

		public Offering(String clientId, String name, String desc, int iconRes, int graphIcon, int itemBackground) {
			this.clientId = clientId;
			this.name = name;
			this.desc = desc;
			this.iconRes = iconRes;
			this.graphIcon= graphIcon;
			this.itemBackground= itemBackground;
		}

		public int getItemBackground() {
			return itemBackground;
		}

		public String getClientId() {
			return clientId;
		}

		public String getName() {
			return name;
		}

		public int getIconRes() {
			return iconRes;
		}

		public String getDesc() {
			return desc;
		}

		public int getGraphIcon() {
			return graphIcon;
		}
	}


}
