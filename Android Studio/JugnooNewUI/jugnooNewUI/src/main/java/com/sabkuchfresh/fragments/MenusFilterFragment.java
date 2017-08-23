package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.MenusFilterCuisinesAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CuisineResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.Constants.ACCEPTONLINE;
import static product.clicklabs.jugnoo.Constants.FREEDELIVERY;
import static product.clicklabs.jugnoo.Constants.OFFERSDISCOUNT;
import static product.clicklabs.jugnoo.Constants.PUREVEGETARIAN;


public class MenusFilterFragment extends Fragment implements GAAction, MenusFilterCuisinesAdapter.Callback{

	private final String TAG = MenusFilterFragment.class.getSimpleName();
	private ImageView ivBack;
	private TextView tvReset;
	private RelativeLayout rlRoot;
	private TextView textViewSortBy, textViewPopularity, textViewDistance, textViewPrice;
	private RelativeLayout relativeLayoutPopularity, relativeLayoutDistance, relativeLayoutPrice, relativeLayoutDeliveryTime;

	private RelativeLayout relativeLayoutAcceptOnline, relativeLayoutOffersDiscount, relativeLayoutPureVeg, relativeLayoutFreeDelivery;
	private TextView textViewQuickFilters, textViewAcceptOnline, textViewOffersDiscount, textViewPureVeg, textViewFreeDelivery;
	private ImageView imageViewAcceptOnline, imageViewOffersDiscount, imageViewPureVeg, imageViewFreeDelivery;


	private ImageView imageViewRadioPopularity, imageViewRadioDistance, imageViewRadioPrice, imageViewRadioDeliveryTime;

	private TextView textViewCuisines, textViewSelectCuisinesValue;
	private RelativeLayout cardViewCuisines;

	private TextView textViewMinimumOrder;
	private RelativeLayout relativeLayoutMO150, relativeLayoutMO250, relativeLayoutMO500;
	private ImageView imageViewMO150, imageViewMO250, imageViewMO500;

	private TextView textViewDeliveryTime;
	private RelativeLayout relativeLayoutDT30, relativeLayoutDT45, relativeLayoutDT60;
	private ImageView imageViewDT30, imageViewDT45, imageViewDT60;

	private Button buttonApply;

	private View rootView;
	private FreshActivity activity;


    public MenusFilterFragment(){}


	private ScrollView scrollViewRoot;
	private EditText etSearchCuisine;
	private ImageView ivSearchCuisine;
	private LinearLayout llCuisinesList;
	private RecyclerView recyclerViewCuisinesList;
	private MenusFilterCuisinesAdapter menusFilterCuisinesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus_filter, container, false);

        activity = (FreshActivity) getActivity();

		rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
		try {
			if(rlRoot != null) {
				new ASSL(activity, rlRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GAUtils.trackScreenView(MENUS+FILTERS);

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
		tvReset = (TextView) rootView.findViewById(R.id.tvReset);
		etSearchCuisine = (EditText) rootView.findViewById(R.id.etSearchCuisine); etSearchCuisine.setVisibility(View.GONE);
		ivSearchCuisine = (ImageView) rootView.findViewById(R.id.ivSearchCuisine); ivSearchCuisine.setVisibility(View.GONE);
		llCuisinesList = (LinearLayout) rootView.findViewById(R.id.llCuisinesList); llCuisinesList.setVisibility(View.GONE);
		recyclerViewCuisinesList = (RecyclerView) rootView.findViewById(R.id.recyclerViewCuisinesList);
		recyclerViewCuisinesList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
		menusFilterCuisinesAdapter = new MenusFilterCuisinesAdapter(activity, activity.getFilterCuisinesLocal(), etSearchCuisine, this);
		recyclerViewCuisinesList.setAdapter(menusFilterCuisinesAdapter);
		setCuisinesList();
		textViewQuickFilters = (TextView) rootView.findViewById(R.id.textViewQuickFilters); textViewQuickFilters.setTypeface(Fonts.mavenMedium(activity));
		textViewAcceptOnline = (TextView) rootView.findViewById(R.id.textViewAcceptOnline); textViewAcceptOnline.setTypeface(Fonts.mavenMedium(activity));
		textViewOffersDiscount = (TextView) rootView.findViewById(R.id.textViewOffersDiscount); textViewOffersDiscount.setTypeface(Fonts.mavenMedium(activity));
		textViewPureVeg = (TextView) rootView.findViewById(R.id.textViewPureVeg); textViewPureVeg.setTypeface(Fonts.mavenMedium(activity));
		textViewFreeDelivery = (TextView) rootView.findViewById(R.id.textViewFreeDelivery); textViewFreeDelivery.setTypeface(Fonts.mavenMedium(activity));
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
		relativeLayoutPopularity = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPopularity);
		relativeLayoutDistance = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDistance);
		relativeLayoutPrice = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPrice);
		relativeLayoutDeliveryTime = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDeliveryTime);
		imageViewRadioPopularity = (ImageView) rootView.findViewById(R.id.imageViewRadioPopularity);
		imageViewRadioDistance = (ImageView) rootView.findViewById(R.id.imageViewRadioDistance);
		imageViewRadioPrice = (ImageView) rootView.findViewById(R.id.imageViewRadioPrice);
		imageViewRadioDeliveryTime = (ImageView) rootView.findViewById(R.id.imageViewRadioDeliveryTime);

		textViewCuisines = (TextView) rootView.findViewById(R.id.textViewCuisines); textViewCuisines.setTypeface(Fonts.mavenMedium(activity));
		cardViewCuisines = (RelativeLayout) rootView.findViewById(R.id.cardViewCuisines);
		((TextView)rootView.findViewById(R.id.textViewSelectCuisines)).setTypeface(Fonts.mavenMedium(activity));
		textViewSelectCuisinesValue = (TextView) rootView.findViewById(R.id.textViewSelectCuisinesValue); textViewSelectCuisinesValue.setTypeface(Fonts.mavenMedium(activity));
		textViewSelectCuisinesValue.setVisibility(View.GONE);






		textViewMinimumOrder = (TextView) rootView.findViewById(R.id.textViewMinimumOrder); textViewMinimumOrder.setTypeface(Fonts.mavenMedium(activity));
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
		buttonApply.setVisibility(View.GONE);

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
				setSortBySelected(getSortBySelected() != SortType.PRICE_RANGE ? SortType.PRICE_RANGE : SortType.NONE);
				updateSortTypeUI();
			}
		});

		relativeLayoutDeliveryTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSortBySelected(getSortBySelected() != SortType.DELIVERY_TIME ? SortType.DELIVERY_TIME : SortType.NONE);
				updateSortTypeUI();
			}
		});


		relativeLayoutAcceptOnline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getQuickFilterSelected().size() !=0 && activity.getQuickFilterSelected().contains(ACCEPTONLINE)) {
					activity.getQuickFilterSelected().remove(ACCEPTONLINE);
				} else {
					activity.getQuickFilterSelected().add(ACCEPTONLINE);
				}
				imageViewAcceptOnline.setImageResource(activity.getQuickFilterSelected().contains(ACCEPTONLINE)  ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
				applyRealTimeFilters();
				gaEventQuickFilters();
			}
		});

		relativeLayoutOffersDiscount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getQuickFilterSelected().size() !=0 && activity.getQuickFilterSelected().contains(OFFERSDISCOUNT)) {
					activity.getQuickFilterSelected().remove(OFFERSDISCOUNT);
				} else {
					activity.getQuickFilterSelected().add(OFFERSDISCOUNT);
				}
				imageViewOffersDiscount.setImageResource(activity.getQuickFilterSelected().contains(OFFERSDISCOUNT)  ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
				applyRealTimeFilters();
				gaEventQuickFilters();
			}
		});


		relativeLayoutPureVeg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getQuickFilterSelected().size() !=0 && activity.getQuickFilterSelected().contains(PUREVEGETARIAN)) {
					activity.getQuickFilterSelected().remove(PUREVEGETARIAN);
				} else {
					activity.getQuickFilterSelected().add(PUREVEGETARIAN);
				}
				imageViewPureVeg.setImageResource(activity.getQuickFilterSelected().contains(PUREVEGETARIAN)  ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
				applyRealTimeFilters();
				gaEventQuickFilters();
			}
		});

		relativeLayoutFreeDelivery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getQuickFilterSelected().size() !=0 && activity.getQuickFilterSelected().contains(FREEDELIVERY)) {
					activity.getQuickFilterSelected().remove(FREEDELIVERY);
				} else {
					activity.getQuickFilterSelected().add(FREEDELIVERY);
				}
				imageViewFreeDelivery.setImageResource(activity.getQuickFilterSelected().contains(FREEDELIVERY)  ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
				applyRealTimeFilters();
				gaEventQuickFilters();
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
					filterCuisine.setSelected(1);
						activity.getCuisinesSelected().add(filterCuisine);

				}

				applyRealTimeFilters();
				gaEventQuickFilters();


				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.SORT_BY, String.valueOf(activity.getSortBySelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.MINIMUM_ORDER, String.valueOf(activity.getMoSelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.DELIVERY_TIME, String.valueOf(activity.getDtSelected()));
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS , GAAction.APPLY_BUTTON + GAAction.CLICKED);


				if(activity != null){
					activity.getDrawerLayout().closeDrawer(GravityCompat.END);
				}
			}
		});

		cardViewCuisines.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				scrollViewRoot.setVisibility(View.GONE);
				llCuisinesList.setVisibility(View.VISIBLE);
				etSearchCuisine.setVisibility(View.VISIBLE);
				ivSearchCuisine.setVisibility(View.VISIBLE);
				tvReset.setVisibility(View.GONE);

				if(activity.getFilterCuisinesLocal()==null){
					getAllCuisines(true,activity.getSelectedLatLng());

				}
				etSearchCuisine.setText("");
				recyclerViewCuisinesList.scrollToPosition(0);
			}
		});



		updateSortTypeUI();
		updateMinOrderUI();
		updateDeliveryTimeUI();
		updateQuickFilterUI();


		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPress(true);
			}
		});
		tvReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSortBySelected(SortType.NONE);
				setMoSelected(MinOrder.NONE);
				setDtSelected(DeliveryTime.NONE);
				for (FilterCuisine filterCuisine : activity.getFilterCuisinesLocal()) {
					filterCuisine.setSelected(0);
				}
				activity.getCuisinesSelected().clear();
				for(FilterCuisine filterCuisine : activity.getFilterCuisinesLocal()){
					if(filterCuisine.getSelected() == 1){
						activity.getCuisinesSelected().add(filterCuisine);
					}
				}

				activity.getQuickFilterSelected().clear();
				updateQuickFilterUI();

				updateSortTypeUI();
				updateMinOrderUI();
				updateDeliveryTimeUI();
				setFiltersText();

				applyRealTimeFilters();
				GAUtils.event(GAAction.MENUS, GAAction.FILTERS, GAAction.RESET_BUTTON + GAAction.CLICKED);

			}
		});



		return rootView;
	}



	private void updateQuickFilterUI() {
		imageViewAcceptOnline.setImageResource(activity.getQuickFilterSelected().contains(ACCEPTONLINE)  ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		imageViewOffersDiscount.setImageResource(activity.getQuickFilterSelected().contains(OFFERSDISCOUNT) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		imageViewPureVeg.setImageResource(activity.getQuickFilterSelected().contains(PUREVEGETARIAN) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
		imageViewFreeDelivery.setImageResource(activity.getQuickFilterSelected().contains(FREEDELIVERY) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
	}


	private void updateSortTypeUI(){
		imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_normal);
		imageViewRadioDeliveryTime.setImageResource(R.drawable.ic_radio_button_normal);
		if(getSortBySelected() == SortType.POPULARITY){
			imageViewRadioPopularity.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(getSortBySelected() == SortType.DISTANCE){
			imageViewRadioDistance.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(getSortBySelected() == SortType.PRICE_RANGE){
			imageViewRadioPrice.setImageResource(R.drawable.ic_radio_button_selected);
		} else if(getSortBySelected() == SortType.DELIVERY_TIME){
			imageViewRadioDeliveryTime.setImageResource(R.drawable.ic_radio_button_selected);
		}
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.SORT_BY, String.valueOf(activity.getSortBySelected()));
	}

	private void updateMinOrderUI(){
		imageViewMO150.setVisibility(getMoSelected() == MinOrder.MO150 ? View.VISIBLE : View.GONE);
		imageViewMO250.setVisibility(getMoSelected() == MinOrder.MO250 ? View.VISIBLE : View.GONE);
		imageViewMO500.setVisibility(getMoSelected() == MinOrder.MO500 ? View.VISIBLE : View.GONE);
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.MINIMUM_ORDER, String.valueOf(activity.getMoSelected()));
	}


	private void updateDeliveryTimeUI(){
		imageViewDT30.setVisibility(getDtSelected() == DeliveryTime.DT30 ? View.VISIBLE : View.GONE);
		imageViewDT45.setVisibility(getDtSelected() == DeliveryTime.DT45 ? View.VISIBLE : View.GONE);
		imageViewDT60.setVisibility(getDtSelected() == DeliveryTime.DT60 ? View.VISIBLE : View.GONE);
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.DELIVERY_TIME, String.valueOf(activity.getDtSelected()));
	}

	public SortType getSortBySelected() {
		return activity.getSortBySelected();
	}

	public void setSortBySelected(SortType sortBySelected) {
		activity.setSortBySelected(sortBySelected);
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.SORT_BY, String.valueOf(activity.getSortBySelected()));
		applyRealTimeFilters();
	}

	public MinOrder getMoSelected() {
		return activity.getMoSelected();
	}

	public void setMoSelected(MinOrder moSelected) {
		activity.setMoSelected(moSelected);
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.MINIMUM_ORDER, String.valueOf(activity.getMoSelected()));
		applyRealTimeFilters();
	}

	public DeliveryTime getDtSelected() {
		return activity.getDtSelected();
	}

	public void setDtSelected(DeliveryTime dtSelected) {
		activity.setDtSelected(dtSelected);
		GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.DELIVERY_TIME, String.valueOf(activity.getDtSelected()));
		applyRealTimeFilters();
	}


	public enum SortType{
		NONE(-1), POPULARITY(0), DISTANCE(1), PRICE_RANGE(2), ONLINEPAYMENTACCEPTED(3), DELIVERY_TIME(4);

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


	private void setFiltersText(){
		textViewSelectCuisinesValue.setText("");
		if (activity.getCuisinesSelected()!=null) {
			for(FilterCuisine filterCuisine : activity.getCuisinesSelected()){

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
		if(activity.getFilterCuisinesLocal()!=null && activity.getFilterCuisinesLocal().size()>0){
			cardViewCuisines.setVisibility(View.VISIBLE);
			textViewCuisines.setVisibility(View.VISIBLE);
			ArrayList<FilterCuisine> selectedList = new ArrayList<>();
			for(FilterCuisine cuisine:activity.getFilterCuisinesLocal()){


				if(activity.getCuisinesSelected().contains(cuisine)){
					selectedList.add(new FilterCuisine(cuisine.getName(),cuisine.getId(), 1));
				} else {
					selectedList.add(new FilterCuisine(cuisine.getName(),cuisine.getId(), 0));
				}
			}
			activity.getFilterCuisinesLocal().clear();
			activity.setFilterCuisinesLocal(selectedList);

		}else{
		/*	cardViewCuisines.setVisibility(View.GONE);
			textViewCuisines.setVisibility(View.GONE);*/
		}
		if(menusFilterCuisinesAdapter!=null)
		menusFilterCuisinesAdapter.setList(activity.getFilterCuisinesLocal());


	}


	public void performBackPress(boolean closeDrawer){
		if(llCuisinesList.getVisibility() == View.VISIBLE){
			scrollViewRoot.setVisibility(View.VISIBLE);
			llCuisinesList.setVisibility(View.GONE);
			etSearchCuisine.setVisibility(View.GONE);
			ivSearchCuisine.setVisibility(View.GONE);
			tvReset.setVisibility(View.VISIBLE);
			setFiltersText();
			Utils.hideKeyboard(activity);
			return;
		}
		if(closeDrawer && activity != null){
			activity.getDrawerLayout().closeDrawer(GravityCompat.END);
		}
	}

	@Override
	public void onCuisineClicked(FilterCuisine filterCuisine) {
		if(filterCuisine.getSelected() == 1){
			activity.getCuisinesSelected().add(filterCuisine);
		} else {
			activity.getCuisinesSelected().remove(filterCuisine);
		}
		applyRealTimeFilters();
	}

	private void gaEventQuickFilters(){
		String quickFilters = null;
		for (String qf : activity.getQuickFilterSelected()) {
			quickFilters = qf + ", ";
		}
		if (quickFilters != null) {
			GAUtils.event(GAAction.MENUS, GAAction.FILTERS + GAAction.QUICK_FILTER, quickFilters.substring(0, quickFilters.length() - 2));
		}
	}

	private void applyRealTimeFilters(){
		if(activity != null){
			activity.filtersChanged = true;
//			activity.applyRealTimeFilters();
//			activity.filtersChanged = false;
		}
	}

	public void getAllCuisines(final boolean loader, final LatLng latLng) {

		try {
			if (MyApplication.getInstance().isOnline()) {
				HashMap<String,String> params = new HashMap<>();

				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
				params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
				params.put(Constants.INTERATED, "1");
				new HomeUtil().putDefaultParams(params);
				ProgressDialog progressDialog = null;

				if (loader)
					progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

				final ProgressDialog finalProgressDialog = progressDialog;
				RestClient.getMenusApiService().nearbyCuisines(params, new Callback<CuisineResponse>() {
					@Override
					public void success(CuisineResponse cuisineResponse, Response response) {

						try {
							if(finalProgressDialog !=null)
								finalProgressDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = cuisineResponse.getMessage();
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == cuisineResponse.getFlag()) {


									if(cuisineResponse.getRanges()!=null){
										activity.setFilterCuisinesLocal(cuisineResponse.getRanges());
										setCuisinesList();
										setFiltersText();
									}

								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {

							exception.printStackTrace();
						}

					}

					@Override
					public void failure(RetrofitError error) {
						try {
							if(finalProgressDialog !=null)
								finalProgressDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
						retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader);

					}
				});
			} else {

				retryDialog(DialogErrorType.NO_NET, latLng, loader);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader) {
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						getAllCuisines(loader, latLng);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}
}
