package product.clicklabs.jugnoo.promotion.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.models.Promo;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 5/1/17.
 */
public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.ViewHolder> implements GAAction, GACategory, ItemListener {

	private Activity activity;
	private ArrayList<Promo> promosList = new ArrayList<>();
	private RecyclerView recyclerView;
	private Callback callback;

	public PromoAdapter(Activity activity, ArrayList<Promo> promosList, RecyclerView recyclerView, Callback callback) {
		this.activity = activity;
		this.promosList = promosList;
		this.recyclerView = recyclerView;
		this.callback = callback;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_promo, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Promo promo = promosList.get(position);
		if(promo.getLineColorRes() == -1){
			holder.ivOfferingLine.setBackgroundResource(R.drawable.ic_promo_all_line);
			holder.tvOfferingName.setTextColor(activity.getResources().getBoolean(R.bool.show_jugnoo_promo_icon)?ContextCompat.getColor(activity, R.color.promo_all_text_color):ContextCompat.getColor(activity, R.color.fresh_promotions_green));
			holder.tvPromoInfo.setVisibility(View.VISIBLE);
			holder.tvPromoInfo.setText(R.string.applied_on_all_offerings);
			if(promo.getPromoCoupon().getRepeatedCount() > 1){
				holder.tvPromoInfo.append(", "+activity.getString(R.string.can_be_used_format, String.valueOf(promo.getPromoCoupon().getRepeatedCount())));
			}
		} else {
			holder.ivOfferingLine.setBackgroundColor(ContextCompat.getColor(activity, promo.getLineColorRes()));
			holder.tvOfferingName.setTextColor(activity.getResources().getBoolean(R.bool.show_jugnoo_promo_icon)?ContextCompat.getColor(activity, promo.getLineColorRes()):ContextCompat.getColor(activity, R.color.fresh_promotions_green));
			holder.tvPromoInfo.setText(activity.getString(R.string.can_be_used_format, String.valueOf(promo.getPromoCoupon().getRepeatedCount())));
			holder.tvPromoInfo.setVisibility(promo.getPromoCoupon().getRepeatedCount() > 1 ? View.VISIBLE : View.GONE);
		}
		holder.ivOfferingIcon.setImageResource(activity.getResources().getBoolean(R.bool.show_jugnoo_promo_icon)?promo.getIconRes():R.drawable.ic_promotion);
		holder.tvOfferingName.setText(promo.getName());
		holder.tvPromoTitle.setText(promo.getPromoCoupon().getTitle());

		String expireDate = DateOperations.convertDateOnlyViaFormatMonthFull(DateOperations.utcToLocalWithTZFallback(promo.getPromoCoupon().getExpiryDate()));
		try {
			SpannableStringBuilder validUntilDate = new SpannableStringBuilder(activity.getString(R.string.valid_until_format, expireDate));
			final StyleSpan boldDateSpan = new StyleSpan(Typeface.BOLD);
			validUntilDate.setSpan(boldDateSpan, validUntilDate.length()-expireDate.length(), validUntilDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvPromoExpireTime.setText(validUntilDate);
		} catch (Exception e) {
			e.printStackTrace();
			holder.tvPromoExpireTime.setText(activity.getString(R.string.valid_until_format, expireDate));
		}

	}

	@Override
	public int getItemCount() {
		return promosList == null ? 0 : promosList.size();
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if(pos != RecyclerView.NO_POSITION){
			switch(viewClicked.getId()){
				case R.id.rl:
					if(callback != null){
						callback.onPromoClick(promosList.get(pos));
					}
					break;
			}
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		@BindView(R.id.rl)
		RelativeLayout rl;
		@BindView(R.id.ivOfferingLine)
		ImageView ivOfferingLine;
		@BindView(R.id.ivOfferingIcon)
		ImageView ivOfferingIcon;
		@BindView(R.id.tvOfferingName)
		TextView tvOfferingName;
		@BindView(R.id.tvPromoTitle)
		TextView tvPromoTitle;
		@BindView(R.id.tvPromoExpireTime)
		TextView tvPromoExpireTime;
		@BindView(R.id.tvPromoInfo)
		TextView tvPromoInfo;

		ViewHolder(final View view, final ItemListener itemListener) {
			super(view);
			ButterKnife.bind(this, view);
			rl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(rl, view);
				}
			});
			tvOfferingName.setTypeface(Fonts.mavenMedium(view.getContext()), Typeface.BOLD);
			tvPromoTitle.setTypeface(Fonts.mavenMedium(view.getContext()), Typeface.BOLD);
		}
	}

	public interface Callback{
		void onPromoClick(Promo promo);
	}
}
