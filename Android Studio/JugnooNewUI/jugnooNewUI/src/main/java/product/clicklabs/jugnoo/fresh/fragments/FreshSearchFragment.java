package product.clicklabs.jugnoo.fresh.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.adapters.FreshCategoryItemsAdapter;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;


@SuppressLint("ValidFragment")
public class FreshSearchFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;
	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross;

	private View rootView;
    private FreshActivity activity;

	private ArrayList<SubItem> subItemsInSearch;

	public FreshSearchFragment(){}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshSearchFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
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

		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);

		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.mavenLight(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross);
		progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross.setVisibility(View.GONE);

		if(subItemsInSearch == null) {
			subItemsInSearch = new ArrayList<>();
		}

		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
				subItemsInSearch,
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
				});
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

		Utils.showSoftKeyboard(activity, editTextSearch);


		return rootView;
	}


	private class SubItemsSearchAsync extends AsyncTask<String, Integer, String>{

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
		}

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
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
