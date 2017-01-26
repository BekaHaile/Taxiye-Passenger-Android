package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.FreshSearchResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@SuppressLint("ValidFragment")
public class FreshSearchFragment extends Fragment {

	private RelativeLayout rlRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;
	private TextView textViewPlaceholder;

	private View rootView;
    private FreshActivity activity;
    private int currentGroupId = 1, superCategoryId = -1, cityId = 1;
	private ArrayList<SubItem> subItemsInSearch;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
		super.onStop();
    }

	public static FreshSearchFragment newInstance(int superCategoryId, int cityId){
		FreshSearchFragment freshSearchFragment = new FreshSearchFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_SUPER_CATEGORY_ID, superCategoryId);
		bundle.putInt(Constants.KEY_CITY_ID, cityId);
		freshSearchFragment.setArguments(bundle);
		return freshSearchFragment;
	}
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_search, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		searchText = "";

		rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
		try {
			if(rlRoot != null) {
				new ASSL(activity, rlRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        Utils.setupUI(rlRoot, activity);

		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);

		textViewPlaceholder = (TextView) rootView.findViewById(R.id.textViewPlaceholder); textViewPlaceholder.setTypeface(Fonts.mavenRegular(activity));
		textViewPlaceholder.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
				try {
					activity.getTopBar().etSearch.requestFocus();
					InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.showSoftInput(activity.getTopBar().etSearch, InputMethodManager.SHOW_IMPLICIT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }, 3);

		if(subItemsInSearch == null) {
			subItemsInSearch = new ArrayList<>();
		}

		superCategoryId = getArguments().getInt(Constants.KEY_SUPER_CATEGORY_ID, -1);
		cityId = getArguments().getInt(Constants.KEY_CITY_ID, 1);


		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
				subItemsInSearch, null, 0,
				FreshCategoryItemsAdapter.OpenMode.INVENTORY,
				new FreshCategoryItemsAdapter.Callback() {
					@Override
					public void onPlusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPriceFMG(subItem);
					}

					@Override
					public void onMinusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPriceFMG(subItem);
					}

					@Override
					public void onDeleteClicked(int position, SubItem subItem) {
					}

					@Override
					public boolean checkForMinus(int position, SubItem subItem) {
						return true;
					}

					@Override
					public void minusNotDone(int position, SubItem subItem) {

					}

					@Override
					public boolean checkForAdd(int position, SubItem subItem) {
						return activity.checkForAdd();
					}
				}, AppConstant.ListType.HOME, FlurryEventNames.SEARCH_SCREEN, currentGroupId);
		recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);


		/*activity.getTopBar().etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
				try {
					if (s.length() > 0) {
						new SubItemsSearchAsync().execute(s.toString());
					} else {
						clearArrays();
						freshCategoryItemsAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        });*/

		activity.getTopBar().etSearch.setText("");

		activity.getTopBar().ivSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				activity.getTopBar().etSearch.setText("");
            }
        });

		return rootView;
	}

	public void clearArrays(){
		subItemsInSearch.clear();
		tokenSearched = "";
		listHashMap.clear();
	}

	private String tokenSearched = "";
	private HashMap<String, List<SubItem>> listHashMap = new HashMap<>();
	private class SubItemsSearchAsync extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try{
				if(activity.getProductsResponse() != null
						&& activity.getProductsResponse().getCategories() != null) {
					String token = params[0];
					subItemsInSearch.clear();
					if(!token.equalsIgnoreCase(tokenSearched)) {
						if(token.length() == 1 && token.length() > tokenSearched.length()) {
							searchFromStart(token);
						}
						else if(token.length() > 1 && token.length() > tokenSearched.length()){
							if(listHashMap.containsKey(tokenSearched)) {
								List<SubItem> subItems = getSubItemsInSearch(token, listHashMap.get(tokenSearched));
								subItemsInSearch.addAll(subItems);
								if(subItems.size() > 0) {
									listHashMap.put(token, subItems);
								}
							} else {
								searchFromStart(token);
							}
						}
						else if(token.length() < tokenSearched.length()){
							if(listHashMap.containsKey(token)){
								subItemsInSearch.addAll(listHashMap.get(token));
							} else{
								searchFromStart(token);
							}
							listHashMap.remove(tokenSearched);
						}
						tokenSearched = token;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			try {
				freshCategoryItemsAdapter.notifyDataSetChanged();
				if(subItemsInSearch.size() > 0){
					textViewPlaceholder.setVisibility(View.GONE);
				} else{
					textViewPlaceholder.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private List<SubItem> getSubItemsInSearch(String token, List<SubItem> subItemsForSearch){
		List<SubItem> subItems = new ArrayList<>();
		for (SubItem subItem : subItemsForSearch) {
			String[] arr = token.toLowerCase(Locale.ENGLISH).split(" ");
			boolean matched = false;
			for (String tok : arr) {
				if (subItem.getSubItemName().toLowerCase(Locale.ENGLISH).contains(tok)) {
					matched = true;
				} else {
					matched = false;
					break;
				}
			}
			if (matched) {
				subItems.add(subItem);
			}
		}
		return subItems;
	}

	private void searchFromStart(String token){
		ArrayList<SubItem> subItems = new ArrayList<>();
		for (Category category : activity.getProductsResponse().getCategories()) {
			subItems.addAll(getSubItemsInSearch(token, category.getSubItems()));
		}
		subItemsInSearch.addAll(subItems);
		if(subItems.size() > 0) {
			listHashMap.put(token, subItems);
		}
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
						activity.getTopBar().etSearch.requestFocus();
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(activity.getTopBar().etSearch, InputMethodManager.SHOW_IMPLICIT);
                    } catch(Exception e){}
                }
            }, 700);
			activity.fragmentUISetup(this);
			if(activity.getCartChangedAtCheckout()){
				activity.updateCartFromSP();
				activity.updateCartFromSPFMG(subItemsInSearch);
				freshCategoryItemsAdapter.notifyDataSetChanged();
				activity.updateCartValuesGetTotalPrice();
			}
			activity.setCartChangedAtCheckout(false);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					activity.setMinOrderAmountText(FreshSearchFragment.this);
				}
			}, 300);
//			if(activity.getTopBar().etSearch.getText().toString().trim().length() > 0){
//				new SubItemsSearchAsync().execute(activity.getTopBar().etSearch.getText().toString().trim());
//			}
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rlRoot);
        System.gc();
	}




	public void searchFreshItems(String s){
		searchText = s.trim();
		if(searchText.length() > 2) {
			searchFreshItemsAutoComplete(searchText);
		} else if(searchText.length() == 0){
			subItemsInSearch.clear();
			freshCategoryItemsAdapter.notifyDataSetChanged();
			textViewPlaceholder.setVisibility(View.GONE);
		}
	}

	private String searchText;
	private boolean refreshingAutoComplete = false;
	public void searchFreshItemsAutoComplete(final String searchText) {
		try {
			if(!refreshingAutoComplete) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {
						HashMap<String, String> params = new HashMap<>();
						params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
						params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
						params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
						params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
						params.put(Constants.KEY_CITY_ID, String.valueOf(cityId));
						params.put(Constants.INTERATED, "1");
						params.put(Constants.KEY_SEARCH_TEXT, searchText);
						params.put(Constants.KEY_SUPER_CATEGORY_ID, String.valueOf(superCategoryId));

						refreshingAutoComplete = true;

						new HomeUtil().putDefaultParams(params);
						RestClient.getFreshApiService().getItemSearch(params, new retrofit.Callback<FreshSearchResponse>() {
							@Override
							public void success(FreshSearchResponse freshSearchResponse, Response response) {
								String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
								try {
									String message = freshSearchResponse.getMessage();
										if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == freshSearchResponse.getFlag()) {
											subItemsInSearch.clear();
											for(SuperCategoriesData.SuperCategory superCategory : freshSearchResponse.getSuperCategories()){
												for(Category category : superCategory.getCategories()){
													subItemsInSearch.addAll(category.getSubItems());
												}
											}
											activity.updateCartFromSPFMG(subItemsInSearch);
											freshCategoryItemsAdapter.notifyDataSetChanged();
											if(subItemsInSearch.size() > 0){
												textViewPlaceholder.setVisibility(View.GONE);
											} else{
												textViewPlaceholder.setVisibility(View.VISIBLE);
											}
										} else {

										}
								} catch (Exception exception) {
									exception.printStackTrace();
								}
								refreshingAutoComplete = false;
								recallSearch(searchText);
							}

							@Override
							public void failure(RetrofitError error) {
								Log.e("search fragment", "fetchRestaurantViaSearch error" + error.toString());
								refreshingAutoComplete = false;
								recallSearch(searchText);
							}
						});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			refreshingAutoComplete = false;
			recallSearch(searchText);
		}
	}

	private void recallSearch(String previousSearchText){
		if (!searchText.trim().equalsIgnoreCase(previousSearchText)) {
			searchFreshItems(searchText);
		}
	}


}
