package com.sabkuchfresh.pros.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.pros.ui.adapters.ProsCatalogueAdapter;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 15/06/17.
 */

public class ProsHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction, GACategory {

	private View rootView;
	private RelativeLayout relativeLayoutNoMenus;
	private FreshActivity activity;
	private RecyclerView rvProsMain;
	private ProsCatalogueAdapter categoriesAdapter;
	private TextView textViewNothingFound;
	private SwipeRefreshLayout swipeContainer;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_pros_home, container, false);
		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);
		activity.setDeliveryAddressView(rootView);
		activity.getDeliveryAddressView().tvDeliveryAddress.setText(R.string.service_address);
		activity.getDeliveryAddressView().tvConfirmAddress.setText(R.string.label_confirm_service_address);

		swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
		swipeContainer.setOnRefreshListener(this);
		swipeContainer.setColorSchemeResources(R.color.white);
		swipeContainer.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);

		relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
		((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

		textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
		relativeLayoutNoMenus.setVisibility(View.GONE);
		rvProsMain = (RecyclerView) rootView.findViewById(R.id.rvProsMain);
		rvProsMain.setItemAnimator(new DefaultItemAnimator());
		final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
			@Override
			public int getSpanSize(int position) {
				switch (categoriesAdapter.getItemViewType(position)){
					case ProsCatalogueAdapter.ORDER_ITEM:
						return 3;
					case ProsCatalogueAdapter.MAIN_ITEM:
						return 1;
					default:
						return 1;
				}
			}
		});

		rvProsMain.setLayoutManager(gridLayoutManager);
		categoriesAdapter = new ProsCatalogueAdapter(activity, new ProsCatalogueAdapter.Callback() {
			@Override
			public void onItemClick(ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum) {
				if(prosCatalogueDatum.getIsEnabled() == 0){
					Utils.showToast(activity, getString(R.string.coming_soon_to_your_city));
				} else {
					activity.getTransactionUtils().addProsProductsFragment(activity, activity.getRelativeLayoutContainer(), prosCatalogueDatum);
					activity.getFabViewTest().hideJeanieHelpInSession();
				}
				try {
					GAUtils.event(PROS, HOME+SUPER+CATEGORY+CLICKED, prosCatalogueDatum.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNeedHelpClick(ProsCatalogueData.CurrentOrder recentOrder) {
				activity.getHomeUtil().openFuguOrSupport(activity, activity.getRelativeLayoutContainer(),
						recentOrder.getJobId(), recentOrder.getSupportCategory(), recentOrder.getJobPickupDatetime(),
						ProductType.PROS.getOrdinal());
			}

			@Override
			public void onViewDetailsClick(ProsCatalogueData.CurrentOrder recentOrder) {
				activity.getTransactionUtils().addProsOrderStatusFragment(activity, activity.getRelativeLayoutContainer(), recentOrder.getJobId(), ProductType.PROS.getOrdinal());
			}
		}, rvProsMain);

		rvProsMain.setAdapter(categoriesAdapter);

		relativeLayoutNoMenus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.PROS);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			if(!hidden){
				categoriesAdapter.notifyDataSetChanged();
				activity.setAddressTextToLocationPlaceHolder();
				activity.fragmentUISetup(this);

				activity.getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(activity.isProsTaskCreated()){
							activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.PROS);
							activity.setProsTaskCreated(false);
						} else if(activity.isRefreshCart()){
							activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.PROS);
							activity.setRefreshCart(false);
						}
					}
				}, 200);
			}

			if(relativeLayoutNoMenus.getVisibility() == View.VISIBLE){
				activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
				activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
				activity.llCheckoutBarSetVisibilityDirect(View.GONE);
			}
		} catch (Exception e) {
		}
	}


	ProgressDialog finalProgressDialog;
	public void getSuperCategoriesAPI(boolean showDialog) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				ProgressDialog progressDialog = null;
				finalProgressDialog = null;
				if(showDialog) {
					progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));
				}
				finalProgressDialog = progressDialog;

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());

				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().getAppCatalogue(params, new Callback<ProsCatalogueData>() {
					@Override
					public void success(final ProsCatalogueData prosCatalogueData, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						if(finalProgressDialog != null){
							finalProgressDialog.dismiss();
						}
						try {
							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, prosCatalogueData.getFlag(), prosCatalogueData.getError(), prosCatalogueData.getMessage())) {
								if (prosCatalogueData.getFlag() == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()) {
									oSnapNotAvailableCase(prosCatalogueData.getMessage());
								} else if (prosCatalogueData.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
									setDataToUI(prosCatalogueData);
								} else {
									DialogPopup.alertPopup(activity, "", prosCatalogueData.getMessage());
									stopOhSnap();
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogSuperCategoriesAPI(DialogErrorType.SERVER_ERROR);
							stopOhSnap();
						}
						swipeContainer.setRefreshing(false);
					}

					@Override
					public void failure(RetrofitError error) {
						if(finalProgressDialog != null){
							finalProgressDialog.dismiss();
						}
						retryDialogSuperCategoriesAPI(DialogErrorType.CONNECTION_LOST);
						stopOhSnap();
						swipeContainer.setRefreshing(false);
						Log.e("getAppCatalogue", "error>"+error);
					}
				});
			}
			else {
				retryDialogSuperCategoriesAPI(DialogErrorType.NO_NET);
				swipeContainer.setRefreshing(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			swipeContainer.setRefreshing(false);
		}
	}


	private void setDataToUI(ProsCatalogueData prosCatalogueData){
		activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
		activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);

		stopOhSnap();
		rvProsMain.smoothScrollToPosition(0);

		List<ProsCatalogueData.ProsCatalogueDatum> list = prosCatalogueData.getData().get(prosCatalogueData.getData().size()-1);
		Collections.sort(list, new Comparator<ProsCatalogueData.ProsCatalogueDatum>() {
			@Override
			public int compare(ProsCatalogueData.ProsCatalogueDatum o1, ProsCatalogueData.ProsCatalogueDatum o2) {
				if(o1.getPriority() != null && o2.getPriority() != null){
					return o1.getPriority().compareTo(o2.getPriority());
				}
				return 0;
			}
		});

		categoriesAdapter.setList(list, (ArrayList<ProsCatalogueData.CurrentOrder>) prosCatalogueData.getCurrentOrders());
	}


	private void retryDialogSuperCategoriesAPI(DialogErrorType dialogErrorType){
		swipeContainer.setVisibility(View.VISIBLE);
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						getSuperCategoriesAPI(true);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}

	public void oSnapNotAvailableCase(String message){
		activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
		activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
		relativeLayoutNoMenus.setVisibility(View.VISIBLE);
		textViewNothingFound.setText(!TextUtils.isEmpty(message) ? message : getString(R.string.nothing_found_near_you));
		activity.llCheckoutBarSetVisibilityDirect(View.GONE);
	}

	public void stopOhSnap(){
		relativeLayoutNoMenus.setVisibility(View.GONE);
		activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
		activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
		activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
	}

	@Override
	public void onRefresh() {
		getSuperCategoriesAPI(false);
	}

	private boolean resumed = false;
	@Override
	public void onResume() {
		super.onResume();
		if(!isHidden() && resumed) {
			activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.PROS);
		}
		resumed = true;
		try {
			if (Prefs.with(activity).getInt(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, 0) > 0) {
				activity.getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						activity.openFeedback(Config.getProsClientId());
					}
				}, 300);
				if(finalProgressDialog != null){
					finalProgressDialog.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}