package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	private int dp15, dp2, dp35, dp45, dp10;

	public OfferingListAdapter(Context context, ArrayList<Offering> offerings, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.recyclerView = recyclerView;
		this.offerings = offerings;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		dp15 = context.getResources().getDimensionPixelSize(R.dimen.dp_15);
		dp2 = context.getResources().getDimensionPixelSize(R.dimen.dp_2);
		dp35 = context.getResources().getDimensionPixelSize(R.dimen.dp_35);
		dp45 = context.getResources().getDimensionPixelSize(R.dimen.dp_45);
		dp10 = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
	}

	@Override
	public int getItemCount() {
		return offerings == null ? 0 : offerings.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = layoutInflater.inflate(R.layout.list_item_home_switcher, parent, false);
		return new ViewHolder(convertView, this);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Offering offering = offerings.get(position);
		holder.ivOffering.setImageResource(offering.getIconRes());
		holder.tvOfferingName.setText(offering.getName());
		holder.tvOfferingDesc.setText(offering.getDesc());
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.rlRoot.getLayoutParams();
		if(position == 0){
			params.setMargins(params.leftMargin, dp15, params.rightMargin, dp2);
		} else if(position == getItemCount()-1){
			params.setMargins(params.leftMargin, dp2, params.rightMargin, dp15);
		} else {
			params.setMargins(params.leftMargin, dp2, params.rightMargin, dp2);
		}
		holder.rlRoot.setLayoutParams(params);

		RelativeLayout.LayoutParams paramsIV = (RelativeLayout.LayoutParams) holder.ivOffering.getLayoutParams();
		if(offering.getClientId().equalsIgnoreCase(Config.getAutosClientId())){
			paramsIV.width = dp45;
			paramsIV.height = dp45;
			paramsIV.setMargins(dp10, paramsIV.topMargin, dp10, paramsIV.bottomMargin);
		} else {
			paramsIV.width = dp35;
			paramsIV.height = dp35;
			paramsIV.setMargins(dp15, paramsIV.topMargin, dp15, paramsIV.bottomMargin);
		}
		holder.ivOffering.setLayoutParams(paramsIV);
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

	static class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout rlRoot;
		public ImageView ivOffering;
		public TextView tvOfferingName, tvOfferingDesc;

		public ViewHolder(final View itemView, final ItemListener itemListener) {
			super(itemView);
			rlRoot = (RelativeLayout) itemView.findViewById(R.id.rlRoot);
			ivOffering = (ImageView) itemView.findViewById(R.id.ivOffering);
			tvOfferingName = (TextView) itemView.findViewById(R.id.tvOfferingName); tvOfferingName.setTypeface(tvOfferingName.getTypeface(), Typeface.BOLD);
			tvOfferingDesc = (TextView) itemView.findViewById(R.id.tvOfferingDesc);
			rlRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(rlRoot, itemView);
				}
			});
		}
	}

	public interface Callback {
		LatLng getLatLng();
	}

	public static class Offering {
		private String clientId, name, desc;
		private int iconRes;

		public Offering(String clientId, String name, String desc, int iconRes) {
			this.clientId = clientId;
			this.name = name;
			this.desc = desc;
			this.iconRes = iconRes;
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
	}
}
