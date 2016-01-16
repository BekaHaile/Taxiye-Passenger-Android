package product.clicklabs.jugnoo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SupportActivity;
import product.clicklabs.jugnoo.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.SupportFAQ;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportMainFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotalValue;
	private TextView textViewStartValue, textViewEndValue, textViewViewPreviousTrips;

	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;
	private ArrayList<SupportFAQ> supportFAQs;

	private View rootView;
    private SupportActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportMainFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_main, container, false);

        activity = (SupportActivity) getActivity();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		linearLayoutRideShortInfo = (LinearLayout)rootView.findViewById(R.id.linearLayoutRideShortInfo);
		textViewDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewDriverCarNumber = (TextView)rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenLight(activity));

		((TextView)rootView.findViewById(R.id.textViewTripTotal)).setTypeface(Fonts.mavenLight(activity));
		textViewTripTotalValue = (TextView)rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		((TextView)rootView.findViewById(R.id.textViewStart)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewEnd)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		textViewStartValue = (TextView)rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndValue = (TextView)rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenLight(activity));
		textViewViewPreviousTrips = (TextView)rootView.findViewById(R.id.textViewViewPreviousTrips); textViewViewPreviousTrips.setTypeface(Fonts.mavenLight(activity));

		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		ArrayList<SupportFAQ.QuestionAnswer> questionAnswers = new ArrayList<>();
		SupportFAQ supportFAQ = new SupportFAQ(1, "Account", questionAnswers);
		questionAnswers.add(supportFAQ.new QuestionAnswer("What is account", "this is account"));
		supportFAQs = new ArrayList<>();
		supportFAQs.add(supportFAQ);
		supportFAQItemsAdapter = new SupportFAQItemsAdapter(supportFAQs, activity, R.layout.list_item_support_faq);
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		linearLayoutRideShortInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		textViewViewPreviousTrips.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});




		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
	}



}
