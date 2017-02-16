package product.clicklabs.jugnoo.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.AddOnItemsAdapter;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class StarMembershipAdapter extends BaseAdapter {

	private List<SubscriptionData.SubscriptionBenefits> benefits;
	private Context context;
	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_ITEM = 1;
	private Callback callback;
	private LayoutInflater mInflater;

	public StarMembershipAdapter(Context context, List<SubscriptionData.SubscriptionBenefits> benefits, Callback callback) {
		this.benefits = benefits;
		this.context = context;
		this.callback = callback;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ViewHolder && benefits != null) {
			final ViewHolder viewHolder = ((ViewHolder)holder);

			viewHolder.tvOfferingBenefits.setText(benefits.get(position).getDescription().replaceAll(";;;", "\n"));
			viewHolder.tvOfferingName.setText(benefits.get(position).getTitle());

			if(benefits.get(position).getProductType() == ProductType.AUTO.getOrdinal()){
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_autos);
			}
			else if(benefits.get(position).getProductType() == ProductType.FRESH.getOrdinal()){
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_fresh);
			}
			else if(benefits.get(position).getProductType() == ProductType.MEALS.getOrdinal()){
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_meals);
			}
			else if(benefits.get(position).getProductType() == ProductType.GROCERY.getOrdinal()){
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_grocery);
			}
			else if(benefits.get(position).getProductType() == ProductType.MENUS.getOrdinal()){
				viewHolder.ivOfferring.setImageResource(R.drawable.ic_fab_menus);
			}

			//viewHolder.ivSep.setVisibility((position < getCount()-1) ? View.VISIBLE : View.GONE);

		}
	}

	@Override
	public int getCount() {
		return benefits == null ? 0 : benefits.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_offering_benefits, null);
			holder = new ViewHolder(convertView, context);

			holder.relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.id = position;
		onBindViewHolder(holder, position);

		return convertView;
	}


	private boolean isPositionFooter(int position) {
		return position == benefits.size();
	}



	static class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView ivOfferring, ivSep;
		public TextView tvOfferingName, tvOfferingBenefits;
		public RelativeLayout relative;
		public int id;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			ivOfferring = (ImageView) itemView.findViewById(R.id.ivOfferring);
			tvOfferingName = (TextView) itemView.findViewById(R.id.tvOfferingName); tvOfferingName.setTypeface(Fonts.mavenMedium(context));
			tvOfferingBenefits = (TextView) itemView.findViewById(R.id.tvOfferingBenefits);tvOfferingBenefits.setTypeface(Fonts.mavenRegular(context));
			ivSep = (ImageView) itemView.findViewById(R.id.ivSep);
		}
	}

	public interface Callback{
		void onUnsubscribe();
	}

}