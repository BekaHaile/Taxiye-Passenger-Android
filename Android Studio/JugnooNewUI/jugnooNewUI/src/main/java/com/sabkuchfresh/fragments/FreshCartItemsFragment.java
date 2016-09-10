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
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Prefs;


@SuppressLint("ValidFragment")
public class FreshCartItemsFragment extends Fragment implements FlurryEventNames, SwipeRefreshLayout.OnRefreshListener {

	private RelativeLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;

	private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int currentGroupId = 1;

	public FreshCartItemsFragment(){}

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
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        Data.AppType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);

		try {
			if(Data.AppType == AppConstant.ApplicationType.MEALS) {
				for (int i = 0; i < activity.subItemsInCart.size(); i++) {
					MyApplication.getInstance().getCleverTapUtils().addToCart(activity.subItemsInCart.get(i).getSubItemName(),
							activity.subItemsInCart.get(i).getSubItemId(), activity.subItemsInCart.get(i).getSubItemQuantitySelected(), activity.subItemsInCart.get(i).getPrice(),
							AppConstant.ApplicationType.MEALS);
				}
			} else {
				for (int i = 0; i < activity.subItemsInCart.size(); i++) {
					MyApplication.getInstance().getCleverTapUtils().addToCart(activity.subItemsInCart.get(i).getSubItemName(),
							activity.subItemsInCart.get(i).getSubItemId(), activity.subItemsInCart.get(i).getSubItemQuantitySelected(), activity.subItemsInCart.get(i).getPrice(),
							AppConstant.ApplicationType.FRESH);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setEnabled(false);

		if(activity.subItemsInCart == null) {
			activity.subItemsInCart = new ArrayList<>();
		}
		activity.subItemsInCart.clear();

		if(activity.getProductsResponse() != null
				&& activity.getProductsResponse().getCategories() != null) {
			for (Category category : activity.getProductsResponse().getCategories()) {
				for (SubItem subItem : category.getSubItems()) {
					if (subItem.getSubItemQuantitySelected() > 0) {
						activity.subItemsInCart.add(subItem);
					}
				}
                if(Data.AppType == AppConstant.ApplicationType.MEALS) {
                    currentGroupId = category.getCurrentGroupId();
                }
			}
		}
//		if(Data.AppType == AppConstant.ApplicationType.MEALS) {
//			for (int i = 0; i < activity.subItemsInCart.size(); i++) {
//				MyApplication.getInstance().getCleverTapUtils().addToCart(activity.subItemsInCart.get(i).getSubItemName(),
//						activity.subItemsInCart.get(i).getSubItemId(), activity.subItemsInCart.get(i).getSubItemQuantitySelected(), activity.subItemsInCart.get(i).getPrice(),
//						AppConstant.ApplicationType.MEALS);
//			}
//		} else {
//			for (int i = 0; i < activity.subItemsInCart.size(); i++) {
//				MyApplication.getInstance().getCleverTapUtils().addToCart(activity.subItemsInCart.get(i).getSubItemName(),
//						activity.subItemsInCart.get(i).getSubItemId(), activity.subItemsInCart.get(i).getSubItemQuantitySelected(), activity.subItemsInCart.get(i).getPrice(),
//						AppConstant.ApplicationType.FRESH);
//			}
//		}


		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
				activity.subItemsInCart, null, 0,
				FreshCategoryItemsAdapter.OpenMode.CART,
				new FreshCategoryItemsAdapter.Callback() {
					@Override
					public void onPlusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
					}

					@Override
					public void onMinusClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
						if(subItem.getSubItemQuantitySelected() == 0){
							activity.subItemsInCart.remove(position);
							checkIfEmpty();
						}
					}

					@Override
					public void onDeleteClicked(int position, SubItem subItem) {
						activity.updateCartValuesGetTotalPrice();
						if(subItem.getSubItemQuantitySelected() == 0){
							activity.subItemsInCart.remove(position);
							checkIfEmpty();
						}
					}
				}, AppConstant.ListType.OTHER, FlurryEventNames.REVIEW_CART, currentGroupId);



		recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);


		NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_CART_CLICKED, null);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}

	public void deleteCart() {
		for(SubItem subItem : activity.subItemsInCart){
			subItem.setSubItemQuantitySelected(0);
		}
		activity.updateCartValuesGetTotalPrice();
		activity.subItemsInCart.clear();
		freshCategoryItemsAdapter.notifyDataSetChanged();
		checkIfEmpty();

	}

	private void checkIfEmpty(){
		if(activity.subItemsInCart.size() == 0){
			activity.performBackPressed();
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


    @Override
    public void onRefresh() {

    }
}
