package product.clicklabs.jugnoo.home.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class BadFeedbackFragment extends Fragment implements FlurryEventNames, Constants{

	private LinearLayout relativeLayoutRoot;


	private View rootView;
    private HomeActivity activity;
    private TextView textViewSkip, textViewImprove, textViewDesc, textViewMatter;
    private Button buttonSupport;

    @Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//        FlurryAgent.onEvent(BadFeedbackFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bad_feedback, container, false);


        activity = (HomeActivity) getActivity();

		relativeLayoutRoot = (LinearLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        textViewSkip = (TextView)rootView.findViewById(R.id.textViewSkip);textViewSkip.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewImprove = (TextView)rootView.findViewById(R.id.textViewImprove);textViewImprove.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewDesc = (TextView)rootView.findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.mavenMedium(activity));
        textViewMatter = (TextView)rootView.findViewById(R.id.textViewMatter);textViewMatter.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        buttonSupport = (Button)rootView.findViewById(R.id.buttonSupport);buttonSupport.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        textViewDesc.setText(Data.autoData.getRideSummaryBadText());

        buttonSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = 1;
                activity.submitFeedbackToDriverAsync(activity, Data.autoData.getcEngagementId(), Data.autoData.getcDriverId(),
                        rating, "", "");
                activity.startActivity(new Intent(activity, SupportActivity.class));
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performBackpressed();
                activity.skipFeedbackForCustomerAsync(activity, Data.autoData.getcEngagementId());
                FlurryEventLogger.event(FEEDBACK_AFTER_RIDE_NO);
                FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "ride completed", "skip");
                activity.flurryEventGAForTransaction();
            }
        });


		return rootView;
	}



    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
        System.gc();
	}


}
