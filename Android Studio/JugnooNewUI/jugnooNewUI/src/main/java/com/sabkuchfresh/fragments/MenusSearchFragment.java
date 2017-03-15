package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;


@SuppressLint("ValidFragment")
public class MenusSearchFragment extends Fragment {

	private RelativeLayout rlRoot;

	private RecyclerView recyclerViewCategoryItems;
	private MenusCategoryItemsAdapter menusCategoryItemsAdapter;
	private TextView textViewPlaceholder;

	private View rootView;
    private FreshActivity activity;
	private ArrayList<Item> itemsInSearch;


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
        rootView = inflater.inflate(R.layout.fragment_fresh_search, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

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
										activity.getTopBar().getLlCartContainer().performClick();
									}
								}, new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}, true, false);
					}
				});
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try{
				if(activity.getMenuProductsResponse() != null
						&& activity.getMenuProductsResponse().getCategories() != null) {
					String token = params[0];
					if(!token.equalsIgnoreCase(tokenSearched)) {
						itemsInSearch.clear();
						if(token.length() == 1 && token.length() > tokenSearched.length()) {
							searchFromStart(token);
						}
						else if(token.length() > 1 && token.length() > tokenSearched.length()){
							if(listHashMap.containsKey(tokenSearched)) {
								List<Item> items = getItemsInSearch(token, listHashMap.get(tokenSearched));
								itemsInSearch.addAll(items);
								if(items.size() > 0) {
									listHashMap.put(token, items);
								}
							} else {
								searchFromStart(token);
							}
						}
						else if(token.length() < tokenSearched.length()){
							if(listHashMap.containsKey(token)){
								itemsInSearch.addAll(listHashMap.get(token));
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
				menusCategoryItemsAdapter.notifyDataSetChanged();
				if(itemsInSearch.size() > 0){
					textViewPlaceholder.setVisibility(View.GONE);
				} else{
					textViewPlaceholder.setVisibility(View.VISIBLE);
				}
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
		itemsInSearch.addAll(items);
		if(items.size() > 0) {
			listHashMap.put(token, items);
		}
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			menusCategoryItemsAdapter.notifyDataSetChanged();
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
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(rlRoot);
        System.gc();
	}


}
