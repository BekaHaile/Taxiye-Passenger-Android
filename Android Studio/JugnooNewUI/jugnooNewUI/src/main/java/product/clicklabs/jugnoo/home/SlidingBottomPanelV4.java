package product.clicklabs.jugnoo.home;

import android.view.View;
import android.widget.ImageView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.fragments.RequestRideOptionsFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.NudgeClient;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanelV4 {

    private HomeActivity activity;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageView imageViewExtraForSliding;
    private RequestRideOptionsFragment requestRideOptionsFragment;
    private ImageView imageViewPriorityTip;

    private final String TAG = SlidingBottomPanelV4.class.getSimpleName();

    public SlidingBottomPanelV4(HomeActivity activity, View view) {
        this.activity = activity;
        initComponents(view);
    }

    private void initComponents(View view) {
        //SlidingUp Layout
        requestRideOptionsFragment = ((RequestRideOptionsFragment) activity.getSupportFragmentManager().findFragmentById(R.id.frag));
        imageViewExtraForSliding = (ImageView)view.findViewById(R.id.imageViewExtraForSliding);
        imageViewPriorityTip = (ImageView) view.findViewById(R.id.imageViewPriorityTip);

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (280 * ASSL.Yscale()));
        slidingUpPanelLayout.setPanelHeight((int) (125 * ASSL.Yscale()));

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                imageViewExtraForSliding.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

        imageViewExtraForSliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        update();

    }

    public void update() {
        try {
            if (Data.promoCoupons != null) {
                if (Data.promoCoupons.size() > 0) {
                    nudgeCouponsEvent();
                } else {
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_NO_COUPONS, null);
                }
                requestRideOptionsFragment.initSelectedCoupon();
            }
            requestRideOptionsFragment.updatePaymentOption();
            requestRideOptionsFragment.updateRegionsUI();
            requestRideOptionsFragment.updateFareStructureUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }


    public void nudgeCouponsEvent(){
        try {
            JSONObject map = new JSONObject();
            JSONArray coups = new JSONArray();
            JSONArray coupsP = new JSONArray();
            for(PromoCoupon pc : Data.promoCoupons){
                if(requestRideOptionsFragment.isPaytmCoupon(pc)){
                    coupsP.put(pc.getTitle());
                } else{
                    coups.put(pc.getTitle());
                }
            }
            map.put(Constants.KEY_COUPONS, coups.toString());
            NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_COUPON_AVAILABLE, map);

            if(coupsP.length() > 0) {
                map.put(Constants.KEY_COUPONS, coupsP.toString());
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_COUPON_AVAILABLE, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestRideOptionsFragment getRequestRideOptionsFragment(){
        return requestRideOptionsFragment;
    }

    public void updateFareFactorUI(int supplyCount){
        if(supplyCount == 1) {
            if (Data.userData.fareFactor > 1 || Data.userData.fareFactor < 1) {
                imageViewPriorityTip.setVisibility(View.VISIBLE);
            } else {
                imageViewPriorityTip.setVisibility(View.GONE);
            }
        } else{
            imageViewPriorityTip.setVisibility(View.GONE);
        }
    }

}
