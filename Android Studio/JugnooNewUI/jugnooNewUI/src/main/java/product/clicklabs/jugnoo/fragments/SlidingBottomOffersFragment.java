package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomOffersFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private LinearLayout linearLayoutRoot, linearLayoutPromotion1, linearLayoutPromotion2;
    private TextView textViewPromotion1, textViewPromotion2;
    private HomeActivity activity;
    private ImageView radioPromotion1, radioPromotion2;
    private LinearLayout linearLayoutNoOffers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_offers, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayoutPromotion1 = (LinearLayout)rootView.findViewById(R.id.linearLayoutPromotion1);
        linearLayoutPromotion2 = (LinearLayout)rootView.findViewById(R.id.linearLayoutPromotion2);
        textViewPromotion1 = (TextView)rootView.findViewById(R.id.textViewPromotion1);textViewPromotion1.setTypeface(Fonts.mavenLight(getActivity()));
        textViewPromotion2 = (TextView)rootView.findViewById(R.id.textViewPromotion2);textViewPromotion2.setTypeface(Fonts.mavenLight(getActivity()));
        radioPromotion1 = (ImageView)rootView.findViewById(R.id.radioPromotion1);
        radioPromotion2 = (ImageView)rootView.findViewById(R.id.radioPromotion2);
        linearLayoutPromotion1.setOnClickListener(this);
        linearLayoutPromotion2.setOnClickListener(this);
        linearLayoutNoOffers = (LinearLayout)rootView.findViewById(R.id.linearLayoutNoOffers);
        ((TextView)rootView.findViewById(R.id.textViewWhoops)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView)rootView.findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenLight(activity));

        try {
            update(activity.getSlidingBottomPanel().getPromoCoupons());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void promotionSelection(ImageView selected, ImageView unSelected){
        if(selected != null){
            selected.setImageResource(R.drawable.radio_selected_icon);
        }
        if(unSelected != null) {
            unSelected.setImageResource(R.drawable.radio_unselected_icon);
        }
    }

    public void update(ArrayList<PromoCoupon> promoCoupons){
        try {
            if(promoCoupons != null && promoCoupons.size() >= 2){
                linearLayoutPromotion1.setVisibility(View.VISIBLE);
                linearLayoutPromotion2.setVisibility(View.VISIBLE);
                linearLayoutNoOffers.setVisibility(View.GONE);

                setPromoCouponText(textViewPromotion1, promoCoupons.get(0));
                setPromoCouponText(textViewPromotion2, promoCoupons.get(1));

            } else if(promoCoupons != null && promoCoupons.size() == 1){
                linearLayoutPromotion1.setVisibility(View.VISIBLE);
                linearLayoutPromotion2.setVisibility(View.GONE);
                linearLayoutNoOffers.setVisibility(View.GONE);

                setPromoCouponText(textViewPromotion1, promoCoupons.get(0));
            } else{
                linearLayoutPromotion1.setVisibility(View.GONE);
                linearLayoutPromotion2.setVisibility(View.GONE);
                linearLayoutNoOffers.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPromoCouponText(TextView textView, PromoCoupon promoCoupon){
        textView.setTag(promoCoupon.id);
        if(promoCoupon instanceof CouponInfo) {
            textView.setText(((CouponInfo)promoCoupon).title);
        } else if(promoCoupon instanceof PromotionInfo){
            textView.setText(((PromotionInfo)promoCoupon).title);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayoutPromotion1:
                if(activity.getSlidingBottomPanel().getSelectedCoupon().id == (int)textViewPromotion1.getTag()){
                    promotionSelection(null, radioPromotion1);
                    activity.getSlidingBottomPanel().setSelectedCoupon(-1);
                } else {
                    promotionSelection(radioPromotion1, radioPromotion2);
                    activity.getSlidingBottomPanel().setSelectedCoupon(0);
                }
                break;

            case R.id.linearLayoutPromotion2:
                if(activity.getSlidingBottomPanel().getSelectedCoupon().id == (int)textViewPromotion2.getTag()){
                    promotionSelection(null, radioPromotion2);
                    activity.getSlidingBottomPanel().setSelectedCoupon(-1);
                } else {
                    promotionSelection(radioPromotion2, radioPromotion1);
                    activity.getSlidingBottomPanel().setSelectedCoupon(1);
                }
                break;
        }
    }
}
