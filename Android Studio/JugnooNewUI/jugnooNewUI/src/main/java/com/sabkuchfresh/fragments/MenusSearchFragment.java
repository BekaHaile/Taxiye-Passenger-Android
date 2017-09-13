package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.MenusCategoryCategoriesAdapter;
import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;


@SuppressLint("ValidFragment")
public class MenusSearchFragment extends Fragment implements GACategory, GAAction {

	private NestedScrollView rlRoot;

	private RecyclerView recyclerViewCategoryItems;
	private RecyclerView recyclerViewCategories;
	private MenusCategoryItemsAdapter menusCategoryItemsAdapter;
	private MenusCategoryCategoriesAdapter menusCategoryCategoriesAdapter;
	private TextView textViewPlaceholder;

	private View rootView;
    private FreshActivity activity;
	private ArrayList<Item> itemsInSearch;
	private ArrayList<Category> categoriesSearched;
	private int isVegToggle;
	private TextView labelItems,labelCategories;
	private CardView cardViewCategories,cardViewItems;


	@Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
		super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus_search, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		GAUtils.trackScreenView(activity.getGaCategory()+HOME+SEARCH);

		rlRoot = (NestedScrollView) rootView.findViewById(R.id.rlRoot);
		try {
			if(rlRoot != null) {
				new ASSL(activity, rlRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        Utils.setupUI(rlRoot, activity);
		cardViewCategories = (CardView) rootView.findViewById(R.id.cv_categories);
		cardViewItems = (CardView) rootView.findViewById(R.id.cv_category_items);
		labelItems = (TextView) rootView.findViewById(R.id.label_items);
		labelCategories = (TextView) rootView.findViewById(R.id.label_categories);
		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategories = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategories);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);
		recyclerViewCategories.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategories.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setNestedScrollingEnabled(false);
		recyclerViewCategories.setNestedScrollingEnabled(false);
		textViewPlaceholder = (TextView) rootView.findViewById(R.id.textViewPlaceholder); textViewPlaceholder.setTypeface(Fonts.mavenRegular(activity));
		textViewPlaceholder.setVisibility(View.GONE);

        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
				try {
					activity.getTopBar().etSearch.setText("");
					activity.getTopBar().etSearch.requestFocus();
					InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.showSoftInput(activity.getTopBar().etSearch, InputMethodManager.SHOW_IMPLICIT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }, 3);


		if(itemsInSearch == null) {
			itemsInSearch = new ArrayList<>();
		}
		if(categoriesSearched == null){
			categoriesSearched = new ArrayList<>();
		}
		isVegToggle = Prefs.with(activity).getInt(Constants.KEY_SP_IS_VEG_TOGGLE, 0);

		menusCategoryItemsAdapter = new MenusCategoryItemsAdapter(activity, itemsInSearch,
				new MenusCategoryItemsAdapter.Callback() {
					@Override
					public boolean checkForAdd(int position, Item item, MenusCategoryItemsAdapter.CallbackCheckForAdd callbackCheckForAdd) {
						return activity.checkForAdd(position, item, callbackCheckForAdd);
					}

					@Override
					public void onPlusClicked(int position, Item item, boolean isNewItemAdded) {
						activity.updateCartValuesGetTotalPrice();
					}

					@Override
					public void onMinusClicked(int position, Item item) {
						activity.updateCartValuesGetTotalPrice();
					}

					@Override
					public void onMinusFailed(int position, Item item) {
						DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
								activity.getString(R.string.you_have_to_decrease_quantity_from_checkout),
								activity.getString(R.string.view_cart), activity.getString(R.string.cancel),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										activity.llCheckoutBar.performClick();
									}
								}, new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}, true, false);
					}

					@Override
					public MenusResponse.Vendor getVendorOpened() {
						return activity.getVendorOpened();
					}

					@Override
					public void onCategoryClick(Category category) {
						if(category != null){
							activity.setScrollToCategoryId(category.getCategoryId());
							activity.onBackPressed();
						}
					}
				});
		menusCategoryCategoriesAdapter = new MenusCategoryCategoriesAdapter(activity, categoriesSearched, new MenusCategoryCategoriesAdapter.Callback() {
			@Override
			public void onCategoryClick(Category category) {
				if(category != null){
					activity.setScrollToCategoryId(category.getCategoryId());
					activity.onBackPressed();
				}
			}
		});
		menusCategoryCategoriesAdapter.setList(null);
		recyclerViewCategories.setAdapter(menusCategoryCategoriesAdapter);
		recyclerViewCategoryItems.setAdapter(menusCategoryItemsAdapter);


		activity.getTopBar().ivSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTopBar().etSearch.setText("");
            }
        });


		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			if(activity != null) {
				activity.clearEtFocus();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearArrays(){
		itemsInSearch.clear();
		tokenSearched = "";
		listHashMap.clear();
	}

	public void searchItems(String s){
		try {
			if (s.length() > 0) {
				new ItemsSearchAsync().execute(s);
			} else {
				clearArrays();
				menusCategoryItemsAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String tokenSearched = "";
	private HashMap<String, List<Item>> listHashMap = new HashMap<>();
	private class ItemsSearchAsync extends AsyncTask<String, Integer, String> {

		private String token;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try{
				if(activity.getMenuProductsResponse() != null
						&& activity.getMenuProductsResponse().getCategories() != null) {
					token = params[0];
					if(!token.equalsIgnoreCase(tokenSearched)) {
						itemsInSearch.clear();
						if(token.length() == 1 && token.length() > tokenSearched.length()) {
							searchFromStart(token);
						}
						else if(token.length() > 1 && token.length() > tokenSearched.length()){
							if(listHashMap.containsKey(tokenSearched)) {
								List<Item> items = getItemsInSearch(token, listHashMap.get(tokenSearched));
								itemsInSearch.addAll(getFilteredSet(items));
								if(items.size() > 0) {
									listHashMap.put(token, items);
								}
							} else {
								searchFromStart(token);
							}
						}
						else if(token.length() < tokenSearched.length()){
							if(listHashMap.containsKey(token)){
								itemsInSearch.addAll(getFilteredSet(listHashMap.get(token)));
							} else{
								searchFromStart(token);
							}
							listHashMap.remove(tokenSearched);
						}
						tokenSearched = token;

						categoriesSearched.clear();
						for (int i = 0; i < activity.getMenuProductsResponse().getCategories().size(); i++) {
							Category category = activity.getMenuProductsResponse().getCategories().get(i);
							if(category.getCategoryName().toLowerCase().contains(token)
									&& (isVegToggle == 0 || category.getVegItemsCount() > 0)){
								Category category1 = new Category(category.getCategoryId(), category.getCategoryName(), i);
								categoriesSearched.add(category1);
							}
						}
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
				menusCategoryItemsAdapter.setList(itemsInSearch, true, null);
				menusCategoryCategoriesAdapter.setList(categoriesSearched);
				if(menusCategoryItemsAdapter.getItemCount() > 0 || menusCategoryCategoriesAdapter.getItemCount()>0){
					textViewPlaceholder.setVisibility(View.GONE);
				} else{
					textViewPlaceholder.setVisibility(View.VISIBLE);
				}

				if(menusCategoryItemsAdapter.getItemCount() == 0) {
					GAUtils.event(GACategory.MENUS, SEARCH + NOT_FOUND, token);
				}
				labelItems.setVisibility(menusCategoryItemsAdapter.getItemCount()>0?View.VISIBLE:View.GONE);
				cardViewItems.setVisibility(menusCategoryItemsAdapter.getItemCount()>0?View.VISIBLE:View.GONE);
				labelCategories.setVisibility(menusCategoryCategoriesAdapter.getItemCount()>0?View.VISIBLE:View.GONE);
				cardViewCategories.setVisibility(menusCategoryCategoriesAdapter.getItemCount()>0?View.VISIBLE:View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private List<Item> getItemsInSearch(String token, List<Item> itemsForSearch){
		List<Item> items = new ArrayList<>();
		for (Item subItem : itemsForSearch) {
			String[] arr = token.toLowerCase(Locale.ENGLISH).split(" ");
			boolean matched = false;
			for (String tok : arr) {
				if (subItem.getItemName().toLowerCase(Locale.ENGLISH).contains(tok)) {
					matched = true;
				} else {
					matched = false;
					break;
				}
			}
			if (matched) {
				items.add(subItem);
			}
		}
		return items;
	}

	private void searchFromStart(String token){
		ArrayList<Item> items = new ArrayList<>();
		for (int i=0; i<activity.getMenuProductsResponse().getCategories().size(); i++) {
			Category category = activity.getMenuProductsResponse().getCategories().get(i);
			if(category.getSubcategories() != null){
				for(int j=0; j<category.getSubcategories().size(); j++){
					Subcategory subcategory = category.getSubcategories().get(j);
					for(int k=0; k<subcategory.getItems().size(); k++){
						Item item = subcategory.getItems().get(k);
						item.setCategoryPos(i);
						item.setSubCategoryPos(j);
						item.setItemPos(k);
					}
					items.addAll(getItemsInSearch(token, subcategory.getItems()));
				}
			} else if(category.getItems() != null){
				for(int k=0; k<category.getItems().size(); k++){
					Item item = category.getItems().get(k);
					item.setCategoryPos(i);
					item.setSubCategoryPos(-1);
					item.setItemPos(k);
				}
				items.addAll(getItemsInSearch(token, category.getItems()));
			}
		}
		itemsInSearch.addAll(getFilteredSet(items));
		if(items.size() > 0) {
			listHashMap.put(token, items);
		}
	}

	private Set<Item> getFilteredSet(List<Item> items){
		Set<Item> set = new TreeSet<>(new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				if (o1.getRestaurantItemId().equals(o2.getRestaurantItemId())) {
					return 0;
				}
				return 1;
			}
		});
		set.addAll(items);
		return set;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			menusCategoryItemsAdapter.notifyDataSetChanged();
			menusCategoryCategoriesAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!hidden){
            activity.getHandler().postDelayed(new Runnable() {
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
			if(activity.getTopBar().etSearch.getText().toString().trim().length() > 0){
				new ItemsSearchAsync().execute(activity.getTopBar().etSearch.getText().toString().trim());
			}
			activity.setMinOrderAmountText(MenusSearchFragment.this);

		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rlRoot);
        System.gc();
	}


}
