package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


@SuppressLint("ValidFragment")
public class FreshCategoryItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private RelativeLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;

	private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView noMealsView;

	public FreshCategoryItemsFragment(){
	}

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
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

		try {
			Bundle bundle = getArguments();
			if(bundle.containsKey(Constants.KEY_CATEGORY_POSITION)) {
				int position = bundle.getInt(Constants.KEY_CATEGORY_POSITION);

				activity = (FreshActivity) getActivity();
                mBus = (activity).getBus();
				linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
				try {
					if (linearLayoutRoot != null) {
						new ASSL(activity, linearLayoutRoot, 1134, 720, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				noMealsView = (ImageView) rootView.findViewById(R.id.noMealsView);
				noMealsView.setVisibility(View.GONE);

				recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
				recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
				recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
				recyclerViewCategoryItems.setHasFixedSize(false);

                mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
                mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
                mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
                mSwipeRefreshLayout.setEnabled(true);

				freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
						(ArrayList<SubItem>) activity.getProductsResponse().getCategories().get(position).getSubItems(),
						activity.getProductsResponse().getCategories().get(position).getCategoryBanner(),
						activity.getProductsResponse().getCategories().get(position).getShowCategoryBanner(),
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
							}
						} ,AppConstant.ListType.HOME, FlurryEventNames.HOME_SCREEN, 1);
				recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

    /**
     * Method used to update list data
     */
    public void updateDetail() {
        freshCategoryItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mBus.post(new SwipeCheckout(0));
    }

    @Subscribe
    public void updateSwipe(SwipeCheckout swipe) {
        if(swipe.flag == 1) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}