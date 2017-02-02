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

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class StarMembershipAdapter extends BaseAdapter {

	private ArrayList<String> benefits;
	private ArrayList<String> benefitsOfferings;
	private Context context;
	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_ITEM = 1;
	private Callback callback;
	private LayoutInflater mInflater;

	public StarMembershipAdapter(Context context, ArrayList<String> benefits, ArrayList<String> benefitsOfferings, Callback callback) {
		this.benefits = benefits;
		this.benefitsOfferings = benefitsOfferings;
		this.context = context;
		this.callback = callback;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

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

			viewHolder.ivSep.setVisibility((position < getCount()-1) ? View.VISIBLE : View.GONE);

		}
	}

	@Override
	public int getCount() {
		return benefitsOfferings == null ? 0 : benefitsOfferings.size();
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