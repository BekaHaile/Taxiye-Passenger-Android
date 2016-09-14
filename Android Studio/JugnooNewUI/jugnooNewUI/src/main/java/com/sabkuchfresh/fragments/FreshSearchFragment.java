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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;


@SuppressLint("ValidFragment")
public class FreshSearchFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;
	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewBack;
	private TextView textViewPlaceholder;

	private View rootView;
    private FreshActivity activity;
    private int currentGroupId = 1;
	private ArrayList<SubItem> subItemsInSearch;

	public FreshSearchFragment(){}

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

		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.mavenLight(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross);
		textViewPlaceholder = (TextView) rootView.findViewById(R.id.textViewPlaceholder); textViewPlaceholder.setTypeface(Fonts.mavenRegular(activity));
		progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross.setVisibility(View.GONE);
		textViewPlaceholder.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
				try {
					editTextSearch.requestFocus();
					InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }, 3);


        imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

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
				}, AppConstant.ListType.OTHER, FlurryEventNames.SEARCH_SCREEN, currentGroupId);
		recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);


		editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imageViewSearchCross.setVisibility(View.VISIBLE);
                    new SubItemsSearchAsync().execute(s.toString());
                    FlurryEventLogger.event(FlurryEventNames.SEARCH_SCREEN, FlurryEventNames.PRODUCT_SEARCH, s.toString());
                } else {
                    imageViewSearchCross.setVisibility(View.GONE);
                    subItemsInSearch.clear();
                    freshCategoryItemsAdapter.notifyDataSetChanged();
                }
            }
        });

		imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performBackPressed();
            }
        });


		return rootView;
	}



	private class SubItemsSearchAsync extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBarSearch.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			try{
				String token = params[0];
				if(activity.getProductsResponse() != null
						&& activity.getProductsResponse().getCategories() != null) {
					subItemsInSearch.clear();
					for (Category category : activity.getProductsResponse().getCategories()) {
						for (SubItem subItem : category.getSubItems()) {
							if (subItem.getSubItemName().toLowerCase(Locale.ENGLISH).contains(token.toLowerCase(Locale.ENGLISH))) {
								subItemsInSearch.add(subItem);
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
			progressBarSearch.setVisibility(View.GONE);
			freshCategoryItemsAdapter.notifyDataSetChanged();
			if(subItemsInSearch.size() > 0){
				textViewPlaceholder.setVisibility(View.GONE);
			} else{
				textViewPlaceholder.setVisibility(View.VISIBLE);
			}
		}

	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
        freshCategoryItemsAdapter.notifyDataSetChanged();
		if(!hidden){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        editTextSearch.requestFocus();
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
                    } catch(Exception e){}
                }
            }, 700);
			activity.fragmentUISetup(this);
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
