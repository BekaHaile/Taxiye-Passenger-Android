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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


@SuppressLint("ValidFragment")
public class FreshSearchFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;
	private TextView textViewPlaceholder;

	private View rootView;
    private FreshActivity activity;
    private int currentGroupId = 1;
	private ArrayList<SubItem> subItemsInSearch;


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

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        Utils.setupUI(rootView.findViewById(R.id.linearLayoutRoot), activity);

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

		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
				subItemsInSearch, null, 0,
				FreshCategoryItemsAdapter.OpenMode.INVENTORY,
				new FreshCategoryItemsAdapter.Callback() {
					@Override
					public void onPlusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
					}

					@Override
					public void onMinusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
					}

					@Override
					public void onDeleteClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
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


		activity.getTopBar().etSearch.addTextChangedListener(new TextWatcher() {
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
        });

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
		try {
			freshCategoryItemsAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			if(activity.getTopBar().etSearch.getText().toString().trim().length() > 0){
				new SubItemsSearchAsync().execute(activity.getTopBar().etSearch.getText().toString().trim());
			}
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
