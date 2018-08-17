package product.clicklabs.jugnoo.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomFareFragment extends Fragment implements GAAction, GACategory{

    private View rootView;
    private HomeActivity activity;
    private LinearLayout linearLayoutRoot;
    private RelativeLayout relativeLayoutPriorityTip;
    private TextView textViewPriorityTipValue, textViewKM, textViewKMValue, textViewMinValue, textViewFareEstimage, textViewThreshold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_fare, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutPriorityTip = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutPriorityTip);
        textViewPriorityTipValue = (TextView)rootView.findViewById(R.id.textViewPriorityTipValue);textViewPriorityTipValue.setTypeface(Fonts.mavenRegular(activity));
        textViewKM = (TextView)rootView.findViewById(R.id.textViewKM); textViewKM.setTypeface(Fonts.mavenRegular(activity));
        ((TextView)rootView.findViewById(R.id.textViewMin)).setTypeface(Fonts.mavenRegular(activity));
        textViewKMValue = (TextView)rootView.findViewById(R.id.textViewKMValue);textViewKMValue.setTypeface(Fonts.mavenRegular(activity));
        textViewMinValue = (TextView)rootView.findViewById(R.id.textViewMinValue);textViewMinValue.setTypeface(Fonts.mavenRegular(activity));
        textViewFareEstimage = (TextView)rootView.findViewById(R.id.textViewFareEstimage);textViewFareEstimage.setTypeface(Fonts.mavenRegular(activity));
        textViewThreshold = (TextView)rootView.findViewById(R.id.textViewThreshold);textViewThreshold.setTypeface(Fonts.mavenRegular(activity));

        update();

        textViewFareEstimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent intent = new Intent(activity, FareEstimateActivity.class);
                intent.putExtra(Constants.KEY_REGION, gson.toJson(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected(), Region.class));
                intent.putExtra(Constants.KEY_RIDE_TYPE, RideTypeValue.NORMAL.getOrdinal());
                intent.putExtra(Constants.KEY_COUPON_SELECTED,activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon());

                try {
                    intent.putExtra(Constants.KEY_LATITUDE, activity.map.getCameraPosition().target.latitude);
                    intent.putExtra(Constants.KEY_LONGITUDE, activity.map.getCameraPosition().target.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //activity.startActivity(intent);
                activity.startActivityForResult(intent, 4);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                GAUtils.event(RIDES, HOME, FARE_ESTIMATE+CLICKED);
            }
        });

        return rootView;
    }

    public void update(){
        try {
            textViewKMValue.setText(Utils.formatCurrencyValue(Data.autoData.getFareStructure().getCurrency(), Data.autoData.getFareStructure().farePerKm, false));
            textViewMinValue.setText(Utils.formatCurrencyValue(Data.autoData.getFareStructure().getCurrency(), Data.autoData.getFareStructure().farePerMin, false));
            textViewKM.setText(activity.getString(R.string.per_format, Utils.getDistanceUnit(Data.autoData.getFareStructure().getDistanceUnit())));


            textViewThreshold.setVisibility(View.GONE);
            if(!"".equalsIgnoreCase(Data.autoData.getFareStructure().getDisplayFareText(activity))){
                textViewThreshold.setVisibility(View.VISIBLE);
                textViewThreshold.setText(Data.autoData.getFareStructure().getDisplayFareText(activity));
            }


            if(Data.autoData.getFareFactor() > 1.0){
                relativeLayoutPriorityTip.setVisibility(View.VISIBLE);
                textViewPriorityTipValue.setText(Data.autoData.getFareFactor()+"X");
            } else{
                relativeLayoutPriorityTip.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
