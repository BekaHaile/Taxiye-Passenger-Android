package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomFareFragment extends Fragment{

    private View rootView;
    private HomeActivity activity;
    private LinearLayout linearLayoutRoot;
    private RelativeLayout relativeLayoutPriorityTip;
    private TextView textViewPriorityTipValue, textViewKMValue, textViewMinValue, textViewFareEstimage, textViewThreshold;

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
        ((TextView)rootView.findViewById(R.id.textViewKM)).setTypeface(Fonts.mavenLight(activity));
        ((TextView)rootView.findViewById(R.id.textViewMin)).setTypeface(Fonts.mavenLight(activity));
        textViewKMValue = (TextView)rootView.findViewById(R.id.textViewKMValue);textViewKMValue.setTypeface(Fonts.mavenRegular(activity));
        textViewMinValue = (TextView)rootView.findViewById(R.id.textViewMinValue);textViewMinValue.setTypeface(Fonts.mavenRegular(activity));
        textViewFareEstimage = (TextView)rootView.findViewById(R.id.textViewFareEstimage);textViewFareEstimage.setTypeface(Fonts.mavenLight(activity));
        textViewThreshold = (TextView)rootView.findViewById(R.id.textViewThreshold);textViewThreshold.setTypeface(Fonts.mavenLight(activity));

        update();

        textViewFareEstimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, FareEstimateActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.FARE_ESTIMATE);
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_GET_FARE_ESTIMATE);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FARE_ESTIMATE_CLICKED, null);
                FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "Home Screen", "get fare estimate");
            }
        });

        return rootView;
    }

    public void update(){
        try {
            textViewKMValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                    Utils.getMoneyDecimalFormat().format(Data.fareStructure.farePerKm)));
            textViewMinValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                    Utils.getMoneyDecimalFormat().format(Data.fareStructure.farePerMin)));
            if(Data.fareStructure.thresholdDistance > 1.0){
                textViewThreshold.setVisibility(View.VISIBLE);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                textViewThreshold.setText(String.format(activity.getResources()
                        .getString(R.string.fare_threshold_distance_message_format),
                        decimalFormat.format(Data.fareStructure.thresholdDistance)));
            } else{
                textViewThreshold.setVisibility(View.GONE);
            }
            if(Data.userData.fareFactor > 1.0){
                relativeLayoutPriorityTip.setVisibility(View.VISIBLE);
                textViewPriorityTipValue.setText(Data.userData.fareFactor+"X");
            } else{
                relativeLayoutPriorityTip.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
