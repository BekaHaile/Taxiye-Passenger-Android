package product.clicklabs.jugnoo.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


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
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offering_benefits, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewHolder(v, context);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ViewHolder && benefits != null) {
			final ViewHolder viewHolder = ((ViewHolder)holder);
			String offerring = benefitsOfferings.get(position);
			viewHolder.tvOfferingBenefits.setText(benefits.get(position).replace(";;;", "\n"));
			if(offerring.equalsIgnoreCase(Config.getAutosClientId())){
				viewHolder.tvOfferingName.setText(context.getString(R.string.rides));
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_autos);
			}
			else if(offerring.equalsIgnoreCase(Config.getFreshClientId())){
				viewHolder.tvOfferingName.setText(context.getString(R.string.fresh));
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_fresh);
			}
			else if(offerring.equalsIgnoreCase(Config.getMealsClientId())){
				viewHolder.tvOfferingName.setText(context.getString(R.string.meals));
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_meals);
			}
			else if(offerring.equalsIgnoreCase(Config.getGroceryClientId())){
				viewHolder.tvOfferingName.setText(context.getString(R.string.grocery));
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_grocery);
			}
			else if(offerring.equalsIgnoreCase(Config.getMenusClientId())){
				viewHolder.tvOfferingName.setText(context.getString(R.string.menus));
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_menus);
			}

			viewHolder.ivSep.setVisibility((position < getItemCount()-1) ? View.VISIBLE : View.GONE);

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

	@Override
	public int getItemCount() {
		return benefitsOfferings == null ? 0 : benefitsOfferings.size();
	}

	@Override
	public int getItemViewType(int position) {
		/*if (isPositionFooter(position)) {
			return TYPE_FOOTER;
		}*/
		return TYPE_ITEM;
	}

	private boolean isPositionFooter(int position) {
		return position == benefits.size();
	}



	static class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView ivOfferring, ivSep;
		public TextView tvOfferingName, tvOfferingBenefits;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			ivOfferring = (ImageView) itemView.findViewById(R.id.ivOfferring);
			tvOfferingName = (TextView) itemView.findViewById(R.id.tvOfferingName); tvOfferingName.setTypeface(Fonts.mavenMedium(context));
			tvOfferingBenefits = (TextView) itemView.findViewById(R.id.tvOfferingBenefits);tvOfferingBenefits.setTypeface(Fonts.mavenRegular(context));
			ivSep = (ImageView) itemView.findViewById(R.id.ivSep);
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