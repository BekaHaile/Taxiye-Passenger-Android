package product.clicklabs.jugnoo.promotion.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 5/14/16.
 */
public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    public PromotionsAdapter(Activity activity, ArrayList<PromoCoupon> promoCoupons) {
        this.activity = activity;
        this.promoCoupons = promoCoupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_coupon, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, 128);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(PromotionsAdapter.ViewHolder holder, int position) {
        PromoCoupon promoCoupon = promoCoupons.get(position);

        holder.textViewCouponTitle.setText(promoCoupon.getTitle());
        if(promoCoupon instanceof CouponInfo){
            holder.textViewExpiryDate.setText(String.format(activity.getResources().getString(R.string.valid_until_format),
                    DateOperations.getDate(DateOperations.utcToLocalWithTZFallback(((CouponInfo)promoCoupon).expiryDate))));
        } else if(promoCoupon instanceof PromotionInfo){
            holder.textViewExpiryDate.setText(String.format(activity.getResources().getString(R.string.valid_until_format),
                    DateOperations.getDate(DateOperations.utcToLocalWithTZFallback(((PromotionInfo)promoCoupon).endOn))));
        }
        holder.relative.setTag(position);
        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                PromoCoupon promoCoupon = promoCoupons.get(pos);
                if(promoCoupon instanceof CouponInfo){
                    DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo)promoCoupon).description, true, true, false);
                    FlurryEventLogger.event(activity, FlurryEventNames.TNC_VIEWS);
                } else if(promoCoupon instanceof PromotionInfo){
                    DialogPopup.alertPopupLeftOriented(activity, "", ((PromotionInfo)promoCoupon).terms, false, true, true);
                    FlurryEventLogger.event(activity, FlurryEventNames.TNC_VIEWS_PROMO);
                }
            }
        });

	}

    @Override
    public int getItemCount() {
        return promoCoupons == null ? 0 : promoCoupons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout relative;
        public TextView textViewCouponTitle, textViewExpiryDate;
        public ImageView imageViewCut;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            textViewCouponTitle = (TextView)itemView.findViewById(R.id.textViewCouponTitle);
            textViewCouponTitle.setTypeface(Fonts.mavenLight(activity));
            textViewExpiryDate = (TextView)itemView.findViewById(R.id.textViewExpiryDate);
            textViewExpiryDate.setTypeface(Fonts.mavenLight(activity));
            imageViewCut = (ImageView) itemView.findViewById(R.id.imageViewCut);
        }
    }
}
