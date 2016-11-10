package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/10/16.
 */
public class PromoCouponsAdapter extends BaseAdapter {

	private int layoutRID;
	private Activity activity;
	private ArrayList<PromoCoupon> offerList = new ArrayList<>();
	private Callback callback;
	private LayoutInflater mInflater;

	public PromoCouponsAdapter(Activity activity, int layoutRID, ArrayList<PromoCoupon> offerList, Callback callback) {
		this.activity = activity;
		this.layoutRID = layoutRID;
		this.offerList = offerList;
		this.callback = callback;
		this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setList(ArrayList<PromoCoupon> offerList){
		this.offerList = offerList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return offerList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(layoutRID, null);
			holder = new ViewHolder(convertView, activity);

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


	public void onBindViewHolder(PromoCouponsAdapter.ViewHolder holder, int position) {
		PromoCoupon promoCoupon = offerList.get(position);

		holder.textViewOfferName.setText(promoCoupon.getTitle());
		if(callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)){
			holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
		} else{
			holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
		}

		holder.relative.setTag(holder);
		holder.textViewTNC.setTag(position);

		holder.textViewTNC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.B_OFFER+"_"
                            +FirebaseEvents.OFFER_T_N_C, bundle);
                    FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_offer", "t&c");
					int position = (int) v.getTag();
					PromoCoupon promoCoupon = offerList.get(position);
					if (promoCoupon instanceof CouponInfo) {
						DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo) promoCoupon).description, true, true, false);
					} else if (promoCoupon instanceof PromotionInfo) {
						DialogPopup.alertPopupLeftOriented(activity, "", ((PromotionInfo) promoCoupon).terms, false, true, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		holder.relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int position = ((ViewHolder) v.getTag()).id;
					PromoCoupon promoCoupon = offerList.get(position);
					if (callback.getSelectedCoupon() != null && callback.getSelectedCoupon().matchPromoCoupon(promoCoupon)){
						callback.setSelectedCoupon(-1);
					} else {
						callback.setSelectedCoupon(position);
						callback.onCouponSelected();
					}
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+FirebaseEvents.PROMOTIONS+"_"+FirebaseEvents.COUPON_PROMOTION, bundle);
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}


	class ViewHolder {
		public int id;
		public RelativeLayout relative;
		public ImageView imageViewRadio;
		public TextView textViewOfferName, textViewTNC;
		public ViewHolder(View itemView, Activity activity) {
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			imageViewRadio = (ImageView)itemView.findViewById(R.id.imageViewRadio);
			textViewOfferName = (TextView) itemView.findViewById(R.id.textViewOfferName);
			textViewOfferName.setTypeface(Fonts.mavenRegular(activity));
			textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
			textViewTNC.setTypeface(Fonts.mavenLight(activity));
		}
	}

	public interface Callback{
		void onCouponSelected();
		PromoCoupon getSelectedCoupon();
		void setSelectedCoupon(int position);
	}

}
