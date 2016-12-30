package product.clicklabs.jugnoo.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by aneesh on 10/4/15.
 */
public class StarMembershipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<String> benefits;
	private ArrayList<String> benefitsOfferings;
	private Context context;
	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_ITEM = 1;
	private Callback callback;

	public StarMembershipAdapter(Context context, ArrayList<String> benefits, ArrayList<String> benefitsOfferings, Callback callback) {
		this.benefits = benefits;
		this.benefitsOfferings = benefitsOfferings;
		this.context = context;
		this.callback = callback;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_FOOTER) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_subscribe_bottom, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewFooterHolder(v, context);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_membership, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewHolder(v, context);
		}
	}

	@Override
	public int getItemCount() {
		if (benefits == null || benefits.size() == 0) {
			return 0;
		} else {
			return benefits.size() + 1;
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionFooter(position)) {
			return TYPE_FOOTER;
		}
		return TYPE_ITEM;
	}

	private boolean isPositionFooter(int position) {
		return position == benefits.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ViewHolder && benefits != null) {
			final ViewHolder viewHolder = ((ViewHolder)holder);

			viewHolder.tvBenefitName.setText(benefits.get(position).toString());
			viewHolder.tvOffering.setText(benefitsOfferings.get(position).toString());
			if(position == benefits.size()-1){
				viewHolder.divider.setVisibility(View.GONE);
			} else{
				viewHolder.divider.setVisibility(View.VISIBLE);
			}
		} else if (holder instanceof ViewFooterHolder) {
			ViewFooterHolder viewHolder = (ViewFooterHolder) holder;
			((ViewFooterHolder) holder).tvUnsubscribe.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onUnsubscribe();
				}
			});
		}
	}



	static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvOffering, tvBenefitName;
		public View divider;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			tvOffering = (TextView) itemView.findViewById(R.id.tvOffering); tvOffering.setTypeface(Fonts.mavenMedium(context));
			tvBenefitName = (TextView) itemView.findViewById(R.id.tvBenefitName);tvBenefitName.setTypeface(Fonts.mavenMedium(context));
			divider = (View) itemView.findViewById(R.id.divider);
		}
	}

	public class ViewFooterHolder extends RecyclerView.ViewHolder {
		public TextView tvUnsubscribe;
		public LinearLayout llContainer;

		public ViewFooterHolder(View convertView, Context context) {
			super(convertView);
			llContainer = (LinearLayout) convertView.findViewById(R.id.llContainer);
			tvUnsubscribe = (TextView) convertView.findViewById(R.id.tvUnsubscribe);
			tvUnsubscribe.setTypeface(Fonts.mavenLight(context));
		}
	}

	public interface Callback{
		void onUnsubscribe();
	}

}