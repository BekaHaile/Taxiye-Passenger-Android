package product.clicklabs.jugnoo.promotion.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.utils.DateOperations;


/**
 * Created by Shankar on 5/14/16.
 */
public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.ViewHolder> implements GAAction, GACategory, ItemListener {

	private Activity activity;
	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private RecyclerView recyclerView;
	private String offeringName, clientId;

	public PromotionsAdapter(Activity activity, ArrayList<PromoCoupon> promoCoupons, RecyclerView recyclerView, String offeringName, String clientId) {
		this.activity = activity;
		this.promoCoupons = promoCoupons;
		this.recyclerView = recyclerView;
		this.offeringName = offeringName;
		this.clientId = clientId;
	}

	public void setList(ArrayList<PromoCoupon> promoCoupons, String offeringName, String clientId){
		this.promoCoupons = promoCoupons;
		notifyDataSetChanged();
		this.offeringName = offeringName;
		this.clientId = clientId;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offering_single_promotion, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PromoCoupon promoCoupon = promoCoupons.get(position);
//		activity.getString(R.string.valid_until_format,
//				DateOperations.getDate(DateOperations.utcToLocalWithTZFallback(promoCoupon.getExpiryDate())))

		String expireDate = DateOperations.convertDateOnlyViaFormatSlash(DateOperations.utcToLocalWithTZFallback(promoCoupon.getExpiryDate()));
		SpannableStringBuilder title = new SpannableStringBuilder(promoCoupon.getTitle());
		SpannableStringBuilder validUntilDate = new SpannableStringBuilder(activity.getString(R.string.valid_until_format, expireDate));

		final StyleSpan boldTitleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
		final StyleSpan boldDateSpan = new StyleSpan(android.graphics.Typeface.BOLD);
		final RelativeSizeSpan relativeValidDateSpan = new RelativeSizeSpan(0.8f);
		final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color_87));

		title.setSpan(boldTitleSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		validUntilDate.setSpan(relativeValidDateSpan, 0, validUntilDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		validUntilDate.setSpan(foregroundColorSpan, 0, validUntilDate.length()-expireDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		validUntilDate.setSpan(boldDateSpan, validUntilDate.length()-expireDate.length(), validUntilDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		holder.textViewCouponTitle.setText(title);
		holder.textViewCouponTitle.append("\n\n");
		holder.textViewCouponTitle.append(validUntilDate);

		holder.tvPromoCount.setText(promoCoupon.getRepeatedCount()+"X");
		holder.tvMultipleOffersBg.setVisibility(promoCoupon.getRepeatedCount() > 1 ? View.VISIBLE : View.GONE);
		holder.tvPromoCount.setVisibility(promoCoupon.getRepeatedCount() > 1 ? View.VISIBLE : View.GONE);
		holder.tvMultipleOffersBg.setText(holder.textViewCouponTitle.getText());

		int paddingL = activity.getResources().getDimensionPixelSize(R.dimen.dp_7);
		int paddingR = activity.getResources().getDimensionPixelSize(R.dimen.dp_7);
		if(position == 0){
			paddingL = activity.getResources().getDimensionPixelSize(R.dimen.dp_20);
		} else if(position == getItemCount()-1) {
			paddingR = activity.getResources().getDimensionPixelSize(R.dimen.dp_20);
		}
		RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.relative.getLayoutParams();
		layoutParams.setMargins(paddingL, layoutParams.topMargin, paddingR, layoutParams.bottomMargin);
		holder.relative.setLayoutParams(layoutParams);

	}

	@Override
	public int getItemCount() {
		return promoCoupons == null ? 0 : promoCoupons.size();
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		try {
			int pos = recyclerView.getChildLayoutPosition(parentView);
			if(pos != RecyclerView.NO_POSITION) {
				switch (viewClicked.getId()) {
					case R.id.relative:
						PromoCoupon promoCoupon = promoCoupons.get(pos);
						if(activity instanceof PromotionActivity){
							((PromotionActivity)activity).openPromoDescriptionFragment(offeringName, clientId, promoCoupon);
						}
						GAUtils.event(SIDE_MENU, PROMOTIONS + OFFER + TNC + CLICKED, promoCoupon.getTitle());
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.tvMultipleOffersBg)
		TextView tvMultipleOffersBg;
		@Bind(R.id.textViewCouponTitle)
		TextView textViewCouponTitle;
		@Bind(R.id.tvPromoCount)
		TextView tvPromoCount;
		@Bind(R.id.relative)
		RelativeLayout relative;

		ViewHolder(final View view, final ItemListener itemListener) {
			super(view);
			ButterKnife.bind(this, view);
			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(relative, view);
				}
			});
		}
	}
}
