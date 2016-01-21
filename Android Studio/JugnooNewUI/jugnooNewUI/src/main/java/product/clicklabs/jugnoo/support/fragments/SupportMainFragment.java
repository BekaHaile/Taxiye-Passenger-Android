package product.clicklabs.jugnoo.support.fragments;

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
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.SupportFAq;
import product.clicklabs.jugnoo.support.SupportActivity;
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
	private ArrayList<SupportFAq> supportFAqs;

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
		activity.setTitle(activity.getResources().getString(R.string.support_main_title));

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

		ArrayList<SupportFAq.QuestionAnswer> questionAnswers = new ArrayList<>();
		SupportFAq supportFAq = new SupportFAq(1, "Account", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is account", "this is account"));
		supportFAqs = new ArrayList<>();
		supportFAqs.add(supportFAq);
		supportFAq = new SupportFAq(2, "General", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is General", "this is General"));
		supportFAqs.add(supportFAq);
		supportFAq = new SupportFAq(3, "Usage", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is usage", "this is usage"));
		supportFAqs.add(supportFAq);
		supportFAq = new SupportFAq(4, "Biling", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is Biling", "this is Biling"));
		supportFAqs.add(supportFAq);
		supportFAq = new SupportFAq(4, "Promotions and Coupons", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is Promotions and Coupons", "this is Promotions and Coupons"));
		supportFAqs.add(supportFAq);
		supportFAq = new SupportFAq(4, "Abuse of Service", questionAnswers);
		questionAnswers.add(supportFAq.new QuestionAnswer("What is Abuse of Service", "this is Abuse of Service"));
		supportFAqs.add(supportFAq);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(supportFAqs, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, SupportFAq supportFAq) {
						activity.getSupportFragmentManager().beginTransaction()
								.add(activity.getLinearLayoutContainer().getId(),
										new SupportFAQQuesFragment(supportFAq), SupportFAQQuesFragment.class.getName())
								.addToBackStack(SupportFAQQuesFragment.class.getName())
								.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
										.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
								.commitAllowingStateLoss();
					}
				});
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
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.setTitle(activity.getResources().getString(R.string.support_main_title));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
	}



}
