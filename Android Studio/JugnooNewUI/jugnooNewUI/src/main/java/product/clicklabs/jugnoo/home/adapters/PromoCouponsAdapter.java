package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/10/16.
 */
public class PromoCouponsAdapter extends RecyclerView.Adapter<PromoCouponsAdapter.ViewHolder> {

	private HomeActivity activity;
	private ArrayList<PromoCoupon> offerList = new ArrayList<>();
	private Callback callback;

	public PromoCouponsAdapter(HomeActivity activity, ArrayList<PromoCoupon> offerList, Callback callback) {
		this.activity = activity;
		this.offerList = offerList;
		this.callback = callback;
	}

	@Override
	public PromoCouponsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_promo_coupon, parent, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(640, 84);
		v.setLayoutParams(layoutParams);

		ASSL.DoMagic(v);
		return new ViewHolder(v, activity);
	}

	@Override
	public void onBindViewHolder(PromoCouponsAdapter.ViewHolder holder, int position) {
		PromoCoupon promoCoupon = offerList.get(position);

		holder.textViewOfferName.setText(promoCoupon.getTitle());
		if(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon() != null &&
				activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon().id == promoCoupon.id){
			holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
		} else{
			holder.imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
		}

		holder.relative.setTag(position);
		holder.imageViewRadio.setTag(position);
		holder.textViewTNC.setTag(position);

		holder.textViewTNC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int position = (int) v.getTag();
					PromoCoupon promoCoupon = offerList.get(position);
					if (promoCoupon instanceof CouponInfo) {
						DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo) promoCoupon).description);
					} else if (promoCoupon instanceof PromotionInfo) {
						DialogPopup.alertPopupHtml(activity, "", ((PromotionInfo) promoCoupon).terms);
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
					int position = (int) v.getTag();
					PromoCoupon promoCoupon = offerList.get(position);
					if (activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon().id == promoCoupon.id) {
						activity.getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);
					} else {
						activity.getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(position);
						callback.onCouponSelected();
					}
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public int getItemCount() {
		return offerList == null ? 0 : offerList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public ImageView imageViewRadio;
		public TextView textViewOfferName, textViewTNC;
		public ViewHolder(View itemView, Activity activity) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			imageViewRadio = (ImageView)itemView.findViewById(R.id.imageViewRadio);
			textViewOfferName = (TextView) itemView.findViewById(R.id.textViewOfferName);
			textViewOfferName.setTypeface(Fonts.mavenLight(activity));
			textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
			textViewTNC.setTypeface(Fonts.mavenLight(activity));
		}
	}

	public interface Callback{
		void onCouponSelected();
	}

}
