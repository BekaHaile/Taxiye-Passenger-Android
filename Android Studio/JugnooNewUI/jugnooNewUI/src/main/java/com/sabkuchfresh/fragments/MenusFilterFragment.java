package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class MenusFilterFragment extends Fragment{

	private final String TAG = MenusFilterFragment.class.getSimpleName();
	private ScrollView scrollViewRoot;
	private TextView textViewSortBy, textViewPopularity, textViewDistance, textViewPrice;
	private CardView cardViewSort;
	private RelativeLayout relativeLayoutPopularity, relativeLayoutDistance, relativeLayoutPrice;
	private ImageView imageViewRadioPopularity, imageViewRadioDistance, imageViewRadioPrice;

	private TextView textViewCuisines;
	private CardView cardViewCuisines;
	private AutoLabelUI cuisinesView;

	private TextView textViewMinimumOrder;
	private CardView cardViewDeliveryTime;
	private TextView textViewDT30, textViewMO250, textViewMO500;

	private TextView textViewDeliveryTime;
	private CardView cardViewMinimumOrder;
	private TextView textViewMO150, textViewDT45, textViewDT60;

	private Button buttonApply;

	private View rootView;
	private FreshActivity activity;


    public MenusFilterFragment(){}
    protected Bus mBus;

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
		super.onStop();
        mBus.unregister(this);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus_filter, container, false);

        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();

		activity.fragmentUISetup(this);
		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		try {
			if(scrollViewRoot != null) {
				new ASSL(activity, scrollViewRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewSortBy = (TextView) rootView.findViewById(R.id.textViewSortBy); textViewSortBy.setTypeface(Fonts.mavenMedium(activity));
		textViewPopularity = (TextView) rootView.findViewById(R.id.textViewPopularity); textViewPopularity.setTypeface(Fonts.mavenMedium(activity));
		textViewDistance = (TextView) rootView.findViewById(R.id.textViewDistance); textViewDistance.setTypeface(Fonts.mavenMedium(activity));
		textViewPrice = (TextView) rootView.findViewById(R.id.textViewPrice); textViewPrice.setTypeface(Fonts.mavenMedium(activity));
		cardViewSort = (CardView) rootView.findViewById(R.id.cardViewSort);
		relativeLayoutPopularity = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPopularity);
		relativeLayoutDistance = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDistance);
		relativeLayoutPrice = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPrice);
		imageViewRadioPopularity = (ImageView) rootView.findViewById(R.id.imageViewRadioPopularity);
		imageViewRadioDistance = (ImageView) rootView.findViewById(R.id.imageViewRadioDistance);
		imageViewRadioPrice = (ImageView) rootView.findViewById(R.id.imageViewRadioPrice);

		textViewCuisines = (TextView) rootView.findViewById(R.id.textViewCuisines); textViewCuisines.setTypeface(Fonts.mavenMedium(activity));
		cardViewCuisines = (CardView) rootView.findViewById(R.id.cardViewCuisines);
		cuisinesView = (AutoLabelUI) rootView.findViewById(R.id.cuisinesView);
		AutoLabelUISettings autoLabelUISettings = new AutoLabelUISettings.Builder()
				.withMaxLabels(500)
				.withBackgroundResource(R.drawable.background_white_rounded_bordered)
				.withLabelsClickables(true)
				.withShowCross(false)
				.withTextColor(R.color.text_color)
				.withTextSize(R.dimen.text_size_11dp)
				.withLabelPadding(R.dimen.padding_10dp)
				.build();

		cuisinesView.setSettings(autoLabelUISettings);

		cuisinesView.addLabel("Biriyani");
		cuisinesView.addLabel("Biriyani");
		cuisinesView.addLabel("Biriyani");
		cuisinesView.setOnLabelClickListener(new AutoLabelUI.OnLabelClickListener() {
			@Override
			public void onClickLabel(Label labelClicked) {
				Toast.makeText(getActivity(), labelClicked.getText() , Toast.LENGTH_SHORT).show();
			}
		});




		textViewMinimumOrder = (TextView) rootView.findViewById(R.id.textViewMinimumOrder); textViewMinimumOrder.setTypeface(Fonts.mavenMedium(activity));
		cardViewMinimumOrder = (CardView) rootView.findViewById(R.id.cardViewMinimumOrder);
		textViewMO150 = (TextView) rootView.findViewById(R.id.textViewMO150); textViewMO150.setTypeface(Fonts.mavenMedium(activity));
		textViewMO250 = (TextView) rootView.findViewById(R.id.textViewMO250); textViewMO250.setTypeface(Fonts.mavenMedium(activity));
		textViewMO500 = (TextView) rootView.findViewById(R.id.textViewMO500); textViewMO500.setTypeface(Fonts.mavenMedium(activity));


		textViewDeliveryTime = (TextView) rootView.findViewById(R.id.textViewDeliveryTime); textViewDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
		cardViewDeliveryTime = (CardView) rootView.findViewById(R.id.cardViewDeliveryTime);
		textViewDT30 = (TextView) rootView.findViewById(R.id.textViewDT30); textViewDT30.setTypeface(Fonts.mavenMedium(activity));
		textViewDT45 = (TextView) rootView.findViewById(R.id.textViewDT45); textViewDT45.setTypeface(Fonts.mavenMedium(activity));
		textViewDT60 = (TextView) rootView.findViewById(R.id.textViewDT60); textViewDT60.setTypeface(Fonts.mavenMedium(activity));




		return rootView;
	}


	@Override
	public void onResume() {
		super.onResume();
	}


    @Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(scrollViewRoot);
        System.gc();
	}

}
