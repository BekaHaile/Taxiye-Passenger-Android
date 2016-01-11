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
 * Created by socomo on 1/8/16.
 */
public class SlidingBottomOffersFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private LinearLayout linearLayoutRoot, linearLayoutPromotion1, linearLayoutPromotion2;
    private TextView textViewPromotion1, textViewPromotion2;
    private HomeActivity activity;
    private ImageView radioPromotion1, radioPromotion2;

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

        try {
            update(activity.getSlidingBottomPanel().getPromoCoupons());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void promotionSelection(ImageView selected, ImageView unSelected){
        selected.setImageResource(R.drawable.radio_selected_icon);
        unSelected.setImageResource(R.drawable.radio_unselected_icon);
    }

    public void update(ArrayList<PromoCoupon> promoCoupons){
        try {
            if(promoCoupons != null && promoCoupons.size() >= 2){
                linearLayoutPromotion1.setVisibility(View.VISIBLE);
                linearLayoutPromotion2.setVisibility(View.VISIBLE);

                setPromoCouponText(textViewPromotion1, promoCoupons.get(0));
                setPromoCouponText(textViewPromotion2, promoCoupons.get(1));

            } else if(promoCoupons != null && promoCoupons.size() == 1){
                linearLayoutPromotion1.setVisibility(View.VISIBLE);
                linearLayoutPromotion2.setVisibility(View.GONE);

                setPromoCouponText(textViewPromotion1, promoCoupons.get(0));
            } else{
                linearLayoutPromotion1.setVisibility(View.GONE);
                linearLayoutPromotion2.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPromoCouponText(TextView textView, PromoCoupon promoCoupon){
        if(promoCoupon instanceof CouponInfo){
            textView.setText(((CouponInfo)promoCoupon).title);
        } else if(promoCoupon instanceof PromotionInfo){
            textView.setText(((PromotionInfo)promoCoupon).title);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayoutPromotion1:
                promotionSelection(radioPromotion1, radioPromotion2);
                break;

            case R.id.linearLayoutPromotion2:
                promotionSelection(radioPromotion2, radioPromotion1);
                break;
        }
    }
}
