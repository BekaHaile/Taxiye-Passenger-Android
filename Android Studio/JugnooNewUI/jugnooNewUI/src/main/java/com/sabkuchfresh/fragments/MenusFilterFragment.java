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

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;
import com.dpizarro.autolabel.library.LabelValues;
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


	private SortType sortBySelected = SortType.NONE;
	private MinOrder moSelected = MinOrder.NONE;
	private DeliveryTime dtSelected = DeliveryTime.NONE;


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
				.withLabelPadding(R.dimen.padding_2dp)
				.build();

		cuisinesView.setSettings(autoLabelUISettings);

		cuisinesView.addLabel(new LabelValues(0, "Biriyani", 0));
		cuisinesView.addLabel(new LabelValues(0, "Biriyani", 0));
		cuisinesView.addLabel(new LabelValues(0, "Biriyani", 0));
		cuisinesView.setOnLabelClickListener(new AutoLabelUI.OnLabelClickListener() {
			@Override
			public void onClickLabel(Label labelClicked) {

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


		relativeLayoutPopularity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sortBySelected = sortBySelected != SortType.POPULARITY ? SortType.POPULARITY : SortType.NONE;
				updateSortTypeUI();
			}
		});

		relativeLayoutDistance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sortBySelected = sortBySelected != SortType.DISTANCE ? SortType.DISTANCE : SortType.NONE;
				updateSortTypeUI();
			}
		});

		relativeLayoutPrice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sortBySelected = sortBySelected != SortType.PRICE ? SortType.PRICE : SortType.NONE;
				updateSortTypeUI();
			}
		});




		textViewMO150.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moSelected = moSelected != MinOrder.MO150 ? MinOrder.MO150 : MinOrder.NONE;
				updateMinOrderUI();
			}
		});

		textViewMO250.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moSelected = moSelected != MinOrder.MO250 ? MinOrder.MO250 : MinOrder.NONE;
				updateMinOrderUI();
			}
		});

		textViewMO500.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moSelected = moSelected != MinOrder.MO500 ? MinOrder.MO500 : MinOrder.NONE;
				updateMinOrderUI();
			}
		});





		textViewDT30.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dtSelected = dtSelected != DeliveryTime.DT30 ? DeliveryTime.DT30 : DeliveryTime.NONE;
				updateDeliveryTimeUI();
			}
		});

		textViewDT45.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dtSelected = dtSelected != DeliveryTime.DT45 ? DeliveryTime.DT45 : DeliveryTime.NONE;
				updateDeliveryTimeUI();
			}
		});

		textViewDT60.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dtSelected = dtSelected != DeliveryTime.DT60 ? DeliveryTime.DT60 : DeliveryTime.NONE;
				updateDeliveryTimeUI();
			}
		});


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


	private void updateSortTypeUI(){
		imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_normal);
		if(sortBySelected == SortType.POPULARITY){
			imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(sortBySelected == SortType.DISTANCE){
			imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(sortBySelected == SortType.PRICE){
			imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_selected);
		}
	}

	private void updateMinOrderUI(){
		textViewMO150.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewMO150.setTextColor(activity.getResources().getColor(R.color.text_color));
		textViewMO250.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewMO250.setTextColor(activity.getResources().getColor(R.color.text_color));
		textViewMO500.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewMO500.setTextColor(activity.getResources().getColor(R.color.text_color));

		if(moSelected == MinOrder.MO150){
			textViewMO150.setBackgroundResource(R.drawable.background_theme_round);
			textViewMO150.setTextColor(activity.getResources().getColor(R.color.white));
		}
		else if(moSelected == MinOrder.MO250){
			textViewMO250.setBackgroundResource(R.drawable.background_theme_round);
			textViewMO250.setTextColor(activity.getResources().getColor(R.color.white));
		}
		else if(moSelected == MinOrder.MO500){
			textViewMO500.setBackgroundResource(R.drawable.background_theme_round);
			textViewMO500.setTextColor(activity.getResources().getColor(R.color.white));
		}
	}


	private void updateDeliveryTimeUI(){
		textViewDT30.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewDT30.setTextColor(activity.getResources().getColor(R.color.text_color));
		textViewDT45.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewDT45.setTextColor(activity.getResources().getColor(R.color.text_color));
		textViewDT60.setBackgroundResource(R.drawable.background_white_rounded_bordered);
		textViewDT60.setTextColor(activity.getResources().getColor(R.color.text_color));

		if(dtSelected == DeliveryTime.DT30){
			textViewDT30.setBackgroundResource(R.drawable.background_theme_round);
			textViewDT30.setTextColor(activity.getResources().getColor(R.color.white));
		}
		else if(dtSelected == DeliveryTime.DT45){
			textViewDT45.setBackgroundResource(R.drawable.background_theme_round);
			textViewDT45.setTextColor(activity.getResources().getColor(R.color.white));
		}
		else if(dtSelected == DeliveryTime.DT60){
			textViewDT60.setBackgroundResource(R.drawable.background_theme_round);
			textViewDT60.setTextColor(activity.getResources().getColor(R.color.white));
		}
	}




	private enum SortType{
		NONE(-1), POPULARITY(0), DISTANCE(1), PRICE(2);

		private int ordinal;
		SortType(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	private enum MinOrder{
		NONE(-1), MO150(0), MO250(1), MO500(2);

		private int ordinal;
		MinOrder(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	private enum DeliveryTime{
		NONE(-1), DT30(0), DT45(1), DT60(2);

		private int ordinal;
		DeliveryTime(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

}
