package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
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
	private int nonScrollHeightAvailable;
	private static final float FONT_UPPER_LIMIT = 18.0f;
	private static  final float FONT_LOWER_LIMIT = 16.0f;

	public OfferingListAdapter(Context context, ArrayList<Offering> offerings, Callback callback, RecyclerView recyclerView, int measuredHeight) {
		this.context = context;
		this.recyclerView = recyclerView;
		this.offerings = offerings;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		minHeightOfEachCell = context.getResources().getDimensionPixelSize(R.dimen.dp_80);
		maxHeightGraphLayout = context.getResources().getDimensionPixelSize(R.dimen.dp_150);
		int marginHeight = 0;
		if(offerings!=null){
			 marginHeight = offerings.size() * context.getResources().getDimensionPixelSize(R.dimen.margin_bottom_home_switcher_item);
		}
		this.nonScrollHeightAvailable= measuredHeight-marginHeight;
	}



	@Override
	public int getItemCount() {
		return offerings == null ? 0 : offerings.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = layoutInflater.inflate(R.layout.list_item_home_switcher, parent, false);
		RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) convertView.getLayoutParams();
		int divideRatio = offerings.size();
		if(offerings.size()<3){
			divideRatio = 3;
		}

		int height = nonScrollHeightAvailable / divideRatio;
		if(height< minHeightOfEachCell){
			height = minHeightOfEachCell;
		}
		layoutParams.height = height;
		convertView.setLayoutParams(layoutParams);
		return new ViewHolder(convertView, this);
	}


	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Offering offering = offerings.get(position);
		holder.ivOffering.setImageResource(offering.getIconRes());
		holder.tvOfferingName.setText(offering.getName());
		holder.tvOfferingDesc.setText(offering.getDesc());
		if(offering.getClientId().equals(Config.getFeedClientId())){
			holder.viewGraphMargin.setVisibility(View.GONE);
		}else
		{
			holder.viewGraphMargin.setVisibility(View.VISIBLE);
		}
		holder.ivGraphImage.setImageResource(offering.getGraphIcon());
		holder.bgLayout.setBackgroundDrawable(ContextCompat.getDrawable(context,offering.getItemBackground()));




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
			tvOfferingName = (TextView) itemView.findViewById(R.id.tvOfferingName);
			tvOfferingDesc = (TextView) itemView.findViewById(R.id.tvOfferingDesc);
			layoutGraph = (LinearLayout) itemView.findViewById(R.id.iv_graph_layout);
			viewGraphMargin =  itemView.findViewById(R.id.iv_graph_margin);
			rlRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(rlRoot, itemView);
				}
			});
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutGraph.getLayoutParams();
			float fontSize;
			if(layoutParams.height>maxHeightGraphLayout){
				params.height = maxHeightGraphLayout;
				fontSize= FONT_UPPER_LIMIT;
			}else{
				params.height = LinearLayout.LayoutParams.MATCH_PARENT;
				fontSize=FONT_UPPER_LIMIT * (layoutParams.height * 1.0f)/maxHeightGraphLayout;
				if(fontSize<FONT_LOWER_LIMIT)fontSize =FONT_LOWER_LIMIT;
			}
			tvOfferingName.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
			tvOfferingDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
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
