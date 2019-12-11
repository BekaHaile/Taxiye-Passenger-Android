package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sabkuchfresh.adapters.FreshCategoryItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Prefs;


@SuppressLint("ValidFragment")
public class FreshCategoryItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GACategory, GAAction {

	private LinearLayout llRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;

	private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
	private ImageView noMealsView;


    protected Bus mBus;

	public static FreshCategoryItemsFragment newInstance(int position, int isVendorMenu){
		FreshCategoryItemsFragment frag = new FreshCategoryItemsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_CATEGORY_POSITION, position);
		bundle.putInt(Constants.KEY_IS_VENDOR_MENU, isVendorMenu);
		frag.setArguments(bundle);
		return frag;
	}


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
				int isVendorMenu = bundle.getInt(Constants.KEY_IS_VENDOR_MENU);

				activity = (FreshActivity) getActivity();
                mBus = (activity).getBus();
				llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
				try {
					if (llRoot != null) {
						new ASSL(activity, llRoot, 1134, 720, false);
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
                mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
                mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

				int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
				if(type == AppConstant.ApplicationType.MENUS) {
					mSwipeRefreshLayout.setEnabled(false);
				} else {
					mSwipeRefreshLayout.setEnabled(true);
				}

				freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
						(ArrayList<SubItem>) activity.getProductsResponse().getCategories().get(position).getSubItems(),
						activity.getProductsResponse().getCategories().get(position).getCategoryBanner(),
						activity.getProductsResponse().getCategories().get(position).getShowCategoryBanner(),
						FreshCategoryItemsAdapter.OpenMode.INVENTORY,
						new FreshCategoryItemsAdapter.Callback() {
							@Override
							public void onPlusClicked(int position, SubItem subItem) {
								if(activity.getTotalPrice() <= 0){
									activity.saveDeliveryAddressModel();
								}
								activity.saveSubItemToDeliveryStoreCart(subItem);
								activity.updateCartValuesGetTotalPrice();
								if(activity.getFreshFragment() != null && activity.getFreshFragment().getSuperCategory() != null) {
									if (subItem.getSubItemQuantitySelected() == 1) {
										GAUtils.event(FRESH, activity.getFreshFragment().getSuperCategory().getSuperCategoryName(), ITEM + ADDED);
									} else {
										GAUtils.event(FRESH, activity.getFreshFragment().getSuperCategory().getSuperCategoryName(), ITEM + INCREASED);
									}
								}
							}

							@Override
							public void onMinusClicked(int position, SubItem subItem) {
								activity.saveSubItemToDeliveryStoreCart(subItem);
								activity.updateCartValuesGetTotalPrice();
								if(activity.getFreshFragment() != null && activity.getFreshFragment().getSuperCategory() != null) {
									GAUtils.event(FRESH, activity.getFreshFragment().getSuperCategory().getSuperCategoryName(), ITEM + DECREASED);
								}
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
						} ,AppConstant.ListType.OTHER, HOME, 1,this);
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
        ASSL.closeActivity(llRoot);
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
