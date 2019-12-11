package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.adapters.MenusFilterCuisinesAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CuisineResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
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


public class MenusFilterFragment extends Fragment implements GAAction, MenusFilterCuisinesAdapter.Callback{

	private final String TAG = MenusFilterFragment.class.getSimpleName();
	private ImageView ivBack;
	private TextView tvReset;
	private RelativeLayout rlRoot;

	private RecyclerView rvFilters, rvSort;
	private MenusFilterAdapter adapterFilters, adapterSort;

	private TextView textViewSelectCuisines, textViewSelectCuisinesValue;
	private RelativeLayout rlCuisines;


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

	private int lastCategoryIdForCuisines;


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

		GAUtils.trackScreenView(activity.getGaCategory()+FILTERS);

		scrollViewRoot = (ScrollView) rootView.findViewById(R.id.scrollViewRoot);
		ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
		tvReset = (TextView) rootView.findViewById(R.id.tvReset);
		etSearchCuisine = (EditText) rootView.findViewById(R.id.etSearchCuisine); etSearchCuisine.setVisibility(View.GONE);
		ivSearchCuisine = (ImageView) rootView.findViewById(R.id.ivSearchCuisine); ivSearchCuisine.setVisibility(View.GONE);
		llCuisinesList = (LinearLayout) rootView.findViewById(R.id.llCuisinesList); llCuisinesList.setVisibility(View.GONE);
		recyclerViewCuisinesList = (RecyclerView) rootView.findViewById(R.id.recyclerViewCuisinesList);
		recyclerViewCuisinesList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
		menusFilterCuisinesAdapter = new MenusFilterCuisinesAdapter(activity.getCuisinesAll(), etSearchCuisine, this);
		recyclerViewCuisinesList.setAdapter(menusFilterCuisinesAdapter);


		rvFilters = (RecyclerView) rootView.findViewById(R.id.rvFilters);
		rvFilters.setLayoutManager(new LinearLayoutManager(activity));
		rvFilters.setNestedScrollingEnabled(false);
		rvSort = (RecyclerView) rootView.findViewById(R.id.rvSort);
		rvSort.setLayoutManager(new LinearLayoutManager(activity));
		rvSort.setNestedScrollingEnabled(false);
		adapterFilters = new MenusFilterAdapter(activity.getFiltersAll(), false, rvFilters);
		rvFilters.setAdapter(adapterFilters);
		adapterSort = new MenusFilterAdapter(activity.getSortAll(), true, rvSort);
		rvSort.setAdapter(adapterSort);

		rlCuisines = (RelativeLayout) rootView.findViewById(R.id.rlCuisines);
		textViewSelectCuisines = (TextView) rootView.findViewById(R.id.textViewSelectCuisines);
		textViewSelectCuisinesValue = (TextView) rootView.findViewById(R.id.textViewSelectCuisinesValue);
		textViewSelectCuisinesValue.setVisibility(View.GONE);
		setCuisinesList();


		buttonApply = (Button) rootView.findViewById(R.id.buttonApply); buttonApply.setTypeface(Fonts.mavenRegular(activity));
		buttonApply.setVisibility(View.GONE);

		activity.getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Utils.hideSoftKeyboard(activity, etSearchCuisine);
			}
		}, 200);


		buttonApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getCuisinesSelected().clear();
				if (activity.getCuisinesAll() != null) {
					for(FilterCuisine filterCuisine : activity.getCuisinesAll()){
                        filterCuisine.setSelected(1);
						activity.getCuisinesSelected().add(filterCuisine);
                    }
				}

				applyRealTimeFilters();
				gaEventQuickFilters();


				GAUtils.event(activity.getGaCategory(), GAAction.FILTERS + GAAction.SORT_BY, String.valueOf(activity.getSortBySelected()));
				GAUtils.event(activity.getGaCategory(), GAAction.FILTERS , GAAction.APPLY_BUTTON + GAAction.CLICKED);


				if(activity != null){
					activity.getDrawerLayout().closeDrawer(GravityCompat.END);
				}
			}
		});

		rlCuisines.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				scrollViewRoot.setVisibility(View.GONE);
				llCuisinesList.setVisibility(View.VISIBLE);
				etSearchCuisine.setVisibility(View.VISIBLE);
				ivSearchCuisine.setVisibility(View.VISIBLE);
				tvReset.setVisibility(View.GONE);

				if(activity.getCuisinesAll() == null){
					getAllCuisines(true, activity.getSelectedLatLng());

				}
				etSearchCuisine.setText("");
				recyclerViewCuisinesList.scrollToPosition(0);
			}
		});



		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPress(true);
			}
		});
		tvReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetAllFilters();
				GAUtils.event(activity.getGaCategory(), GAAction.FILTERS, GAAction.RESET_BUTTON + GAAction.CLICKED);
			}
		});


		return rootView;
	}

	public void resetAllFilters() {
		if (activity.getCuisinesAll()!=null) {
            for (FilterCuisine filterCuisine : activity.getCuisinesAll()) {
filterCuisine.setSelected(0);
}
        }
		activity.getCuisinesSelected().clear();
		activity.setSortBySelected(null);
		activity.getFilterSelected().clear();

		adapterSort.notifyDataSetChanged();
		adapterFilters.notifyDataSetChanged();
		menusFilterCuisinesAdapter.notifyDataSetChanged();
		setCuisinesList();

		applyRealTimeFilters();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(activity.getMenusResponse() != null) {
			updateDataLists(activity.getMenusResponse());
		}
	}

	public void setCuisinesList(){
		String cuisinesSelectedText = "";
		if(activity.getCuisinesAll() != null) {
			for (FilterCuisine filterCuisine : activity.getCuisinesAll()) {
				filterCuisine.setSelected(activity.getCuisinesSelected().contains(filterCuisine) ? 1 : 0);
				if(filterCuisine.getSelected() == 1) {
					cuisinesSelectedText = cuisinesSelectedText + filterCuisine.getName() + ", ";
				}
			}
		} else {
			for (FilterCuisine filterCuisine : activity.getCuisinesSelected()) {
				cuisinesSelectedText = cuisinesSelectedText + filterCuisine.getName() + ", ";
			}
		}
		if (menusFilterCuisinesAdapter != null) {
			menusFilterCuisinesAdapter.setList(activity.getCuisinesAll());
		}
		textViewSelectCuisinesValue.setText(cuisinesSelectedText);
		if(textViewSelectCuisinesValue.getText().length() > 2){
			textViewSelectCuisinesValue.setText(textViewSelectCuisinesValue.getText().toString()
					.substring(0, textViewSelectCuisinesValue.getText().length()-2));
		}
		textViewSelectCuisinesValue.setVisibility(textViewSelectCuisinesValue.getText().length() > 0 ? View.VISIBLE : View.GONE);
	}


	public void performBackPress(boolean closeDrawer){
		if(llCuisinesList.getVisibility() == View.VISIBLE){
			scrollViewRoot.setVisibility(View.VISIBLE);
			llCuisinesList.setVisibility(View.GONE);
			etSearchCuisine.setVisibility(View.GONE);
			ivSearchCuisine.setVisibility(View.GONE);
			tvReset.setVisibility(View.VISIBLE);
			setCuisinesList();
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
		for (MenusResponse.KeyValuePair filter : activity.getFilterSelected()) {
			quickFilters = filter.getKey() + ", ";
		}
		if (quickFilters != null) {
			GAUtils.event(activity.getGaCategory(), GAAction.FILTERS + GAAction.QUICK_FILTER, quickFilters.substring(0, quickFilters.length() - 2));
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
				params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
				params.put(Constants.INTERATED, "1");
				if(activity.getCategoryIdOpened() > 0) {
					params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(activity.getCategoryIdOpened()));
				}
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
										lastCategoryIdForCuisines = activity.getCategoryIdOpened();
										activity.setCuisinesAll(cuisineResponse.getRanges());
										setCuisinesList();
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

	public void updateDataLists(MenusResponse menusResponse) {
		ArrayList<MenusResponse.KeyValuePair> filters = new ArrayList<>();
		ArrayList<MenusResponse.KeyValuePair> sorting = new ArrayList<>();
		if(menusResponse.getFilters()!=null){
			filters.addAll(menusResponse.getFilters());

		}
		if(menusResponse.getSorting()!=null){
			sorting.addAll(menusResponse.getSorting());

		}

		if(activity.getCategoryIdOpened() > 0 && activity.getCategoryOpened()!=null ){
			MenusResponse.Category category = activity.getCategoryOpened();
			if(category.getFilters()!=null){
				filters.addAll(category.getFilters());

			}
			if(category.getSorting()!=null){
				sorting.addAll(category.getSorting());

			}
		}
		if(lastCategoryIdForCuisines != activity.getCategoryIdOpened()) {
			activity.setCuisinesAll(null);
		}

		activity.setFiltersAll(filters);
		activity.setSortAll(sorting);

		setCuisinesList();
		adapterFilters.setList(activity.getFiltersAll());
		adapterSort.setList(activity.getSortAll());
	}


	class MenusFilterAdapter extends RecyclerView.Adapter<MenusFilterAdapter.ViewHolder> implements ItemListener {

		private ArrayList<MenusResponse.KeyValuePair> filters;
		private boolean isSort;
		private RecyclerView recyclerView;

		public MenusFilterAdapter(ArrayList<MenusResponse.KeyValuePair> filters, boolean isSort, RecyclerView recyclerView) {
			this.filters = filters;
			this.isSort = isSort;
			this.recyclerView = recyclerView;
		}

		public void setList(ArrayList<MenusResponse.KeyValuePair> filters) {
			this.filters = filters;
			notifyDataSetChanged();
		}

		@Override
		public MenusFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_filter_cuisine, parent, false);
			return new ViewHolder(v, this);
		}

		@Override
		public void onBindViewHolder(MenusFilterAdapter.ViewHolder holder, int position) {
			try {
				holder.textViewCuisine.setText(Utils.capEachWord(filters.get(position).getValue()));
				holder.imageViewSep.setVisibility(position == getItemCount()-1 ? View.GONE : View.VISIBLE);

				if(isSort){
					holder.imageViewCheck.setImageResource(filters.get(position).equals(activity.getSortBySelected()) ? R.drawable.ic_radio_button_selected : R.drawable.ic_radio_button_normal);
				} else {
					holder.imageViewCheck.setImageResource(activity.getFilterSelected().contains(filters.get(position)) ? R.drawable.ic_checkbox_orange_checked : R.drawable.check_box_unchecked);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getItemCount() {
			return filters == null ? 0 : filters.size();
		}

		@Override
		public void onClickItem(View viewClicked, View parentView) {
			int pos = recyclerView.getChildLayoutPosition(parentView);
			if(pos != RecyclerView.NO_POSITION){
				switch (viewClicked.getId()){
					case R.id.relative:
						if(isSort){
							if(activity.getSortBySelected() != null && activity.getSortBySelected().equals(filters.get(pos))){
								activity.setSortBySelected(null);
							} else {
								activity.setSortBySelected(filters.get(pos));
							}
							notifyDataSetChanged();
							applyRealTimeFilters();
							GAUtils.event(activity.getGaCategory(), GAAction.FILTERS + GAAction.SORT_BY, filters.get(pos).getKey());
						} else {
							if(activity.getFilterSelected().contains(filters.get(pos))){
								activity.getFilterSelected().remove(filters.get(pos));
							} else {
								activity.getFilterSelected().add(filters.get(pos));
							}
							notifyDataSetChanged();
							applyRealTimeFilters();
							gaEventQuickFilters();
						}
						break;
				}
			}
		}

		class ViewHolder extends RecyclerView.ViewHolder {
			private RelativeLayout relative;
			private ImageView imageViewCheck, imageViewSep;
			private TextView textViewCuisine;

			public ViewHolder(final View itemView, final ItemListener itemListener) {
				super(itemView);
				relative = (RelativeLayout) itemView.findViewById(R.id.relative);
				imageViewCheck = (ImageView) itemView.findViewById(R.id.imageViewCheck);
				imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
				textViewCuisine = (TextView) itemView.findViewById(R.id.textViewCuisine);
				relative.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						itemListener.onClickItem(relative, itemView);
					}
				});
			}
		}
	}
}
