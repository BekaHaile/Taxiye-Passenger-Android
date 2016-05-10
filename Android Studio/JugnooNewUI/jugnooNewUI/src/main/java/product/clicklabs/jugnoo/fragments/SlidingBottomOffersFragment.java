package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomOffersFragment extends Fragment {

    private View rootView;
    private LinearLayout linearLayoutRoot;
    private HomeActivity activity;
    private LinearLayout linearLayoutNoOffers;
    private RecyclerView recyclerViewOffers;
    private OffersAdapter offersAdapter;

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

        linearLayoutNoOffers = (LinearLayout)rootView.findViewById(R.id.linearLayoutNoOffers);
        ((TextView)rootView.findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenLight(activity));

        recyclerViewOffers = (RecyclerView) rootView.findViewById(R.id.offers_recycler);
        recyclerViewOffers.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
        recyclerViewOffers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOffers.setHasFixedSize(false);

        setOfferAdapter(Data.promoCoupons);

        try {
            update(Data.promoCoupons);
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

    public void setOfferAdapter(ArrayList<PromoCoupon> offerList){
        try {
            offersAdapter = new OffersAdapter(offerList);
            recyclerViewOffers.setAdapter(offersAdapter);
            activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setScrollableView(recyclerViewOffers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(ArrayList<PromoCoupon> promoCoupons){
        try {
            if(promoCoupons != null && promoCoupons.size() >= 2){
                linearLayoutNoOffers.setVisibility(View.GONE);
            } else if(promoCoupons != null && promoCoupons.size() == 1){
                linearLayoutNoOffers.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        offersAdapter.notifyDataSetChanged();
        if(offersAdapter.getItemCount() > 0){
            recyclerViewOffers.setVisibility(View.VISIBLE);
            linearLayoutNoOffers.setVisibility(View.GONE);
        } else{
            recyclerViewOffers.setVisibility(View.GONE);
            linearLayoutNoOffers.setVisibility(View.VISIBLE);
        }
    }

    private void setPromoCouponText(TextView textView, PromoCoupon promoCoupon){
        textView.setTag(promoCoupon.id);
        textView.setText(promoCoupon.getTitle());
    }


    public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

        private ArrayList<PromoCoupon> offerList = new ArrayList<>();

        public OffersAdapter(ArrayList<PromoCoupon> offerList) {
            this.offerList = offerList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_list_item, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }

        @Override
        public void onBindViewHolder(OffersAdapter.ViewHolder holder, int position) {
            PromoCoupon promoCoupon = offerList.get(position);

            setPromoCouponText(holder.textViewPromotion1, promoCoupon);
            if(activity.getSlidingBottomPanel().getSelectedCoupon() != null &&
                    activity.getSlidingBottomPanel().getSelectedCoupon().id == promoCoupon.id){
                holder.radioPromotion1.setImageResource(R.drawable.radio_selected_icon);
            } else{
                holder.radioPromotion1.setImageResource(R.drawable.radio_unselected_icon);
            }

            holder.linearLayoutPromotion1.setTag(position);
            holder.radioPromotion1.setTag(position);
            holder.textViewTNC.setTag(position);

            holder.textViewTNC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        PromoCoupon promoCoupon = offerList.get(position);
                        if (promoCoupon instanceof CouponInfo) {
							DialogPopup.alertPopupLeftOriented(activity, "", ((CouponInfo)promoCoupon).description);
						} else if (promoCoupon instanceof PromotionInfo) {
							DialogPopup.alertPopupHtml(activity, "", ((PromotionInfo)promoCoupon).terms);
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.linearLayoutPromotion1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        PromoCoupon promoCoupon = offerList.get(position);
                        if (activity.getSlidingBottomPanel().getSelectedCoupon().id == promoCoupon.id) {
							activity.getSlidingBottomPanel().setSelectedCoupon(-1);
						} else {
							activity.getSlidingBottomPanel().setSelectedCoupon(position);
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
            public RelativeLayout linearLayoutPromotion1;
            public ImageView radioPromotion1;
            public TextView textViewPromotion1, textViewTNC;
            public ViewHolder(View itemView, Activity activity) {
                super(itemView);
                linearLayoutPromotion1 = (RelativeLayout) itemView.findViewById(R.id.linearLayoutPromotion1);
                radioPromotion1 = (ImageView)itemView.findViewById(R.id.radioPromotion1);
                textViewPromotion1 = (TextView) itemView.findViewById(R.id.textViewPromotion1);
                textViewPromotion1.setTypeface(Fonts.mavenLight(activity));
                textViewTNC = (TextView)itemView.findViewById(R.id.textViewTNC);
                textViewTNC.setTypeface(Fonts.mavenLight(activity));
            }
        }
    }
}
