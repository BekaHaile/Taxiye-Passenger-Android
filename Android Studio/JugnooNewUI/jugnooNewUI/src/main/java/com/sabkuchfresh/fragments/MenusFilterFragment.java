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
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

import static product.clicklabs.jugnoo.Constants.ACCEPTONLINE;
import static product.clicklabs.jugnoo.Constants.FREEDELIVERY;
import static product.clicklabs.jugnoo.Constants.OFFERSDISCOUNT;
import static product.clicklabs.jugnoo.Constants.PUREVEGETARIAN;


public class MenusFilterFragment extends Fragment implements GAAction{

	private final String TAG = MenusFilterFragment.class.getSimpleName();
	private ImageView ivBack;
	private TextView tvReset;
	private RelativeLayout rlRoot;
	private TextView textViewSortBy, textViewPopularity, textViewDistance, textViewPrice;
	private CardView cardViewSort;
	private RelativeLayout relativeLayoutPopularity, relativeLayoutDistance, relativeLayoutPrice;

	private RelativeLayout relativeLayoutAcceptOnline, relativeLayoutOffersDiscount, relativeLayoutPureVeg, relativeLayoutFreeDelivery;
	private TextView textViewQuickFilters, textViewAcceptOnline, textViewOffersDiscount, textViewPureVeg, textViewFreeDelivery;
	private CardView cardViewQuickFilter;
	private ImageView imageViewAcceptOnline, imageViewOffersDiscount, imageViewPureVeg, imageViewFreeDelivery;
	private ArrayList<String> quickFilterLocal = new ArrayList<>();


	private ImageView imageViewRadioPopularity, imageViewRadioDistance, imageViewRadioPrice;

	private TextView textViewCuisines, textViewSelectCuisinesValue;
	private CardView cardViewCuisines;

	private TextView textViewMinimumOrder;
	private CardView cardViewMinimumOrder;
	private RelativeLayout relativeLayoutMO150, relativeLayoutMO250, relativeLayoutMO500;
	private ImageView imageViewMO150, imageViewMO250, imageViewMO500;

	private TextView textViewDeliveryTime;
	private CardView cardViewDeliveryTime;
	private RelativeLayout relativeLayoutDT30, relativeLayoutDT45, relativeLayoutDT60;
	private ImageView imageViewDT30, imageViewDT45, imageViewDT60;

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

		rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
		try {
			if(rlRoot != null) {
				new ASSL(activity, rlRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GAUtils.trackScreenView(MENUS+FILTERS);

		ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
		tvReset = (TextView) rootView.findViewById(R.id.tvReset);
		setResetClickListener();

		textViewQuickFilters = (TextView) rootView.findViewById(R.id.textViewQuickFilters); textViewQuickFilters.setTypeface(Fonts.mavenMedium(activity));
		textViewAcceptOnline = (TextView) rootView.findViewById(R.id.textViewAcceptOnline); textViewAcceptOnline.setTypeface(Fonts.mavenMedium(activity));
		textViewOffersDiscount = (TextView) rootView.findViewById(R.id.textViewOffersDiscount); textViewOffersDiscount.setTypeface(Fonts.mavenMedium(activity));
		textViewPureVeg = (TextView) rootView.findViewById(R.id.textViewPureVeg); textViewPureVeg.setTypeface(Fonts.mavenMedium(activity));
		textViewFreeDelivery = (TextView) rootView.findViewById(R.id.textViewFreeDelivery); textViewFreeDelivery.setTypeface(Fonts.mavenMedium(activity));
		cardViewQuickFilter = (CardView) rootView.findViewById(R.id.cardViewQuickFilter);
		relativeLayoutAcceptOnline = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAcceptOnline);
		relativeLayoutOffersDiscount = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOffersDiscount);
		relativeLayoutPureVeg = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPureVeg);
		relativeLayoutFreeDelivery = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFreeDelivery);
		imageViewAcceptOnline = (ImageView) rootView.findViewById(R.id.imageViewAcceptOnline);
		imageViewOffersDiscount = (ImageView) rootView.findViewById(R.id.imageViewOffersDiscount);
		imageViewPureVeg = (ImageView) rootView.findViewById(R.id.imageViewPureVeg);
		imageViewFreeDelivery = (ImageView) rootView.findViewById(R.id.imageViewFreeDelivery);

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
		((TextView)rootView.findViewById(R.id.textViewSelectCuisines)).setTypeface(Fonts.mavenMedium(activity));
		textViewSelectCuisinesValue = (TextView) rootView.findViewById(R.id.textViewSelectCuisinesValue); textViewSelectCuisinesValue.setTypeface(Fonts.mavenMedium(activity));
		textViewSelectCuisinesValue.setVisibility(View.GONE);






		textViewMinimumOrder = (TextView) rootView.findViewById(R.id.textViewMinimumOrder); textViewMinimumOrder.setTypeface(Fonts.mavenMedium(activity));
		cardViewMinimumOrder = (CardView) rootView.findViewById(R.id.cardViewMinimumOrder);
		relativeLayoutMO150 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMO150);
		relativeLayoutMO250 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMO250);
		relativeLayoutMO500 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMO500);
		imageViewMO150 = (ImageView) rootView.findViewById(R.id.imageViewMO150);
		imageViewMO250 = (ImageView) rootView.findViewById(R.id.imageViewMO250);
		imageViewMO500 = (ImageView) rootView.findViewById(R.id.imageViewMO500);
		((TextView) rootView.findViewById(R.id.textViewMO150)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewMO250)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewMO500)).setTypeface(Fonts.mavenMedium(activity));


		textViewDeliveryTime = (TextView) rootView.findViewById(R.id.textViewDeliveryTime); textViewDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
		cardViewDeliveryTime = (CardView) rootView.findViewById(R.id.cardViewDeliveryTime);
		relativeLayoutDT30 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDT30);
		relativeLayoutDT45 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDT45);
		relativeLayoutDT60 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDT60);
		imageViewDT30 = (ImageView) rootView.findViewById(R.id.imageViewDT30);
		imageViewDT45 = (ImageView) rootView.findViewById(R.id.imageViewDT45);
		imageViewDT60 = (ImageView) rootView.findViewById(R.id.imageViewDT60);
		((TextView) rootView.findViewById(R.id.textViewDT30)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewDT45)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewDT60)).setTypeface(Fonts.mavenMedium(activity));

		buttonApply = (Button) rootView.findViewById(R.id.buttonApply); buttonApply.setTypeface(Fonts.mavenRegular(activity));

		activity.getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Utils.hideSoftKeyboard(activity, textViewDeliveryTime);
			}
		}, 200);


		relativeLayoutPopularity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSortBySelected(getSortBySelected() != SortType.POPULARITY ? SortType.POPULARITY : SortType.NONE);
				updateSortTypeUI();
			}
		});


		relativeLayoutDistance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSortBySelected(getSortBySelected() != SortType.DISTANCE ? SortType.DISTANCE : SortType.NONE);
				updateSortTypeUI();
			}
		});

		relativeLayoutPrice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSortBySelected(getSortBySelected() != SortType.PRICE ? SortType.PRICE : SortType.NONE);
				updateSortTypeUI();
			}
		});


		relativeLayoutAcceptOnline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(quickFilterLocal !=null && quickFilterLocal.size() !=0 && quickFilterLocal.contains(ACCEPTONLINE))
				{
					quickFilterLocal.remove(ACCEPTONLINE);
				} else {
					quickFilterLocal.add(ACCEPTONLINE);
				}
				imageViewAcceptOnline.setImageResource(quickFilterLocal.contains(ACCEPTONLINE)  ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
			}
		});

		relativeLayoutOffersDiscount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(quickFilterLocal !=null && quickFilterLocal.size() !=0 && quickFilterLocal.contains(OFFERSDISCOUNT))
				{
					quickFilterLocal.remove(OFFERSDISCOUNT);
				} else {
					quickFilterLocal.add(OFFERSDISCOUNT);
				}
				imageViewOffersDiscount.setImageResource(quickFilterLocal.contains(OFFERSDISCOUNT)  ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
			}
		});


		relativeLayoutPureVeg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(quickFilterLocal !=null && quickFilterLocal.size() !=0 && quickFilterLocal.contains(PUREVEGETARIAN))
				{
					quickFilterLocal.remove(PUREVEGETARIAN);
				} else {
					quickFilterLocal.add(PUREVEGETARIAN);
				}
				imageViewPureVeg.setImageResource(quickFilterLocal.contains(PUREVEGETARIAN)  ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
			}
		});

		relativeLayoutFreeDelivery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(quickFilterLocal !=null && quickFilterLocal.size() !=0 && quickFilterLocal.contains(FREEDELIVERY))
				{
					quickFilterLocal.remove(FREEDELIVERY);
				} else {
					quickFilterLocal.add(FREEDELIVERY);
				}
				imageViewFreeDelivery.setImageResource(quickFilterLocal.contains(FREEDELIVERY)  ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
			}
		});


		relativeLayoutMO150.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setMoSelected(getMoSelected() != MinOrder.MO150 ? MinOrder.MO150 : MinOrder.NONE);
				updateMinOrderUI();
			}
		});

		relativeLayoutMO250.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setMoSelected(getMoSelected() != MinOrder.MO250 ? MinOrder.MO250 : MinOrder.NONE);
				updateMinOrderUI();
			}
		});

		relativeLayoutMO500.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setMoSelected(getMoSelected() != MinOrder.MO500 ? MinOrder.MO500 : MinOrder.NONE);
				updateMinOrderUI();
			}
		});





		relativeLayoutDT30.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDtSelected(getDtSelected() != DeliveryTime.DT30 ? DeliveryTime.DT30 : DeliveryTime.NONE);
				updateDeliveryTimeUI();
			}
		});

		relativeLayoutDT45.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDtSelected(getDtSelected() != DeliveryTime.DT45 ? DeliveryTime.DT45 : DeliveryTime.NONE);
				updateDeliveryTimeUI();
			}
		});

		relativeLayoutDT60.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDtSelected(getDtSelected() != DeliveryTime.DT60 ? DeliveryTime.DT60 : DeliveryTime.NONE);
				updateDeliveryTimeUI();
			}
		});

		buttonApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getCuisinesSelected().clear();
				for(FilterCuisine filterCuisine : activity.getFilterCuisinesLocal()){
					if(filterCuisine.getSelected() == 1){
						activity.getCuisinesSelected().add(filterCuisine.getName());
					}
				}

				activity.getQuickFilterSelected().clear();
				activity.getQuickFilterSelected().addAll(quickFilterLocal);

				activity.setSortBySelected(getSortBySelected());
				activity.setDtSelected(getDtSelected());
				activity.setMoSelected(getMoSelected());

				String quickFilters = null;
				if (quickFilterLocal!=null) {
					for(String qf : quickFilterLocal){
                        quickFilters= qf+", ";
                    }
					if(quickFilters!=null) {
						GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.QUICK_FILTER, quickFilters.substring(0, quickFilters.length() - 2));
					}
				}


				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.SORT_BY, String.valueOf(activity.getSortBySelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.MINIMUM_ORDER, String.valueOf(activity.getMoSelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.DELIVERY_TIME, String.valueOf(activity.getDtSelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS , GAAction.APPLY_BUTTON + GAAction.CLICKED);


				activity.performBackPressed(false);
			}
		});

		cardViewCuisines.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getTransactionUtils().openMenusFilterCuisinesFragment(activity, activity.getRelativeLayoutContainer());
			}
		});


		setSortBySelected(activity.getSortBySelected());
		setDtSelected(activity.getDtSelected());
		setMoSelected(activity.getMoSelected());
		quickFilterLocal.clear();
		quickFilterLocal.addAll(activity.getQuickFilterSelected());

		updateSortTypeUI();
		updateMinOrderUI();
		updateDeliveryTimeUI();
		updateQuickFilterUI();


		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity != null){
					activity.performBackPressed(true);
				}
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
			setFiltersText();
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rlRoot);
        System.gc();
	}


	private void updateQuickFilterUI()
	{
		imageViewAcceptOnline.setImageResource(quickFilterLocal.contains(ACCEPTONLINE)  ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
		imageViewOffersDiscount.setImageResource(quickFilterLocal.contains(OFFERSDISCOUNT) ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
		imageViewPureVeg.setImageResource(quickFilterLocal.contains(PUREVEGETARIAN) ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
		imageViewFreeDelivery.setImageResource(quickFilterLocal.contains(FREEDELIVERY) ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
	}


	private void updateSortTypeUI(){
		imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_normal);
		if(getSortBySelected() == SortType.POPULARITY){
			imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(getSortBySelected() == SortType.DISTANCE){
			imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(getSortBySelected() == SortType.PRICE){
			imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_selected);
		}
	}

	private void updateMinOrderUI(){
		imageViewMO150.setVisibility(getMoSelected() == MinOrder.MO150 ? View.VISIBLE : View.GONE);
		imageViewMO250.setVisibility(getMoSelected() == MinOrder.MO250 ? View.VISIBLE : View.GONE);
		imageViewMO500.setVisibility(getMoSelected() == MinOrder.MO500 ? View.VISIBLE : View.GONE);
	}


	private void updateDeliveryTimeUI(){
		imageViewDT30.setVisibility(getDtSelected() == DeliveryTime.DT30 ? View.VISIBLE : View.GONE);
		imageViewDT45.setVisibility(getDtSelected() == DeliveryTime.DT45 ? View.VISIBLE : View.GONE);
		imageViewDT60.setVisibility(getDtSelected() == DeliveryTime.DT60 ? View.VISIBLE : View.GONE);
	}

	public SortType getSortBySelected() {
		return sortBySelected;
	}

	public void setSortBySelected(SortType sortBySelected) {
		this.sortBySelected = sortBySelected;
	}

	public MinOrder getMoSelected() {
		return moSelected;
	}

	public void setMoSelected(MinOrder moSelected) {
		this.moSelected = moSelected;
	}

	public DeliveryTime getDtSelected() {
		return dtSelected;
	}

	public void setDtSelected(DeliveryTime dtSelected) {
		this.dtSelected = dtSelected;
	}


	public enum SortType{
		NONE(-1), POPULARITY(0), DISTANCE(1), PRICE(2), ONLINEPAYMENTACCEPTED(3);

		private int ordinal;
		SortType(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	public enum MinOrder{
		NONE(-1), MO150(150), MO250(250), MO500(500);

		private int ordinal;
		MinOrder(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	public enum DeliveryTime{
		NONE(-1), DT30(30), DT45(45), DT60(60);

		private int ordinal;
		DeliveryTime(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}


	private void setResetClickListener(){
		if(getView() != null) {
			tvReset.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setSortBySelected(SortType.NONE);
					setMoSelected(MinOrder.NONE);
					setDtSelected(DeliveryTime.NONE);
					for (FilterCuisine filterCuisine : activity.getFilterCuisinesLocal()) {
						filterCuisine.setSelected(0);
					}

					quickFilterLocal.clear();
					updateQuickFilterUI();

					updateSortTypeUI();
					updateMinOrderUI();
					updateDeliveryTimeUI();
					setFiltersText();

					GAUtils.event(GAAction.MENUS, GAAction.FILTERS, GAAction.RESET_BUTTON + GAAction.CLICKED);

				}
			});
		}
	}

	private void setFiltersText(){
		textViewSelectCuisinesValue.setText("");
		for(FilterCuisine filterCuisine : activity.getFilterCuisinesLocal()){
			if(filterCuisine.getSelected() == 1){
				textViewSelectCuisinesValue.append(filterCuisine.getName()+", ");
			}
		}
		if(textViewSelectCuisinesValue.getText().length() > 2){
			textViewSelectCuisinesValue.setText(textViewSelectCuisinesValue.getText().toString()
					.substring(0, textViewSelectCuisinesValue.getText().length()-2));
		}
		textViewSelectCuisinesValue.setVisibility(textViewSelectCuisinesValue.getText().length() > 0 ? View.VISIBLE : View.GONE);
	}

	public void setCuisinesList(){
		if(activity.getMenusResponse() != null && activity.getMenusResponse().getFilters() != null
				&& activity.getMenusResponse().getFilters().getCuisines() != null
				&& activity.getMenusResponse().getFilters().getCuisines().size() > 0){
			cardViewCuisines.setVisibility(View.VISIBLE);
			textViewCuisines.setVisibility(View.VISIBLE);
			activity.getFilterCuisinesLocal().clear();
			for(int i=0; i<activity.getMenusResponse().getFilters().getCuisines().size(); i++){
				String cuisine = activity.getMenusResponse().getFilters().getCuisines().get(i);
				if(activity.getCuisinesSelected().contains(cuisine)){
					activity.getFilterCuisinesLocal().add(new FilterCuisine(cuisine, 1));
				} else {
					activity.getFilterCuisinesLocal().add(new FilterCuisine(cuisine, 0));
				}
			}
			setFiltersText();
		} else {
			cardViewCuisines.setVisibility(View.GONE);
			textViewCuisines.setVisibility(View.GONE);
		}
	}

}
