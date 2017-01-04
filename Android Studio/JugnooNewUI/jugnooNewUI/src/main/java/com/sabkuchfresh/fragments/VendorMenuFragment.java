package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.FreshCategoryFragmentsAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshSortingDialog;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Log;


public class VendorMenuFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener {

	private final String TAG = VendorMenuFragment.class.getSimpleName();
	private RelativeLayout linearLayoutRoot;
    private LinearLayout mainLayout;
    private LinearLayout noFreshsView;
	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private FreshCategoryFragmentsAdapter freshCategoryFragmentsAdapter;
	private View rootView;
    private FreshActivity activity;
    private boolean tabClickFlag = false;

    private FreshSortingDialog freshSortingDialog;
    private ArrayList<SortResponseModel> slots = new ArrayList<>();
    public VendorMenuFragment(){}
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
        rootView = inflater.inflate(R.layout.fragment_fresh, container, false);

        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();
        activity.setSwipeAvailable(false);

		activity.fragmentUISetup(this);
		linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView);

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		freshCategoryFragmentsAdapter = new FreshCategoryFragmentsAdapter(activity, getChildFragmentManager());
		viewPager.setAdapter(freshCategoryFragmentsAdapter);

		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);
		tabs.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        tabs.setOnMyTabClickListener(this);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(tabClickFlag) {
                    tabClickFlag = false;
                } else {
                    Log.d(TAG, "onPageSelected = "+position);
                    FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.SWIPE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


		success(activity.getProductsResponse());

		getFreshSortingDialog();

		return rootView;
	}


	@Override
	public void onResume() {
		super.onResume();
		if(!isHidden()) {
			activity.setRefreshCart(false);
		}
	}


    @Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
        activity.setSwipeAvailable(true);
		if(!hidden){
			freshCategoryFragmentsAdapter.notifyDataSetChanged();
			tabs.notifyDataSetChanged();
			activity.fragmentUISetup(this);
            activity.resumeMethod();
			if(activity.isRefreshCart()){

			}
			activity.setRefreshCart(false);
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}



    @Subscribe
    public void onUpdateListEvent(UpdateMainList event) {

        if(event.flag) {
            // Update pager adapter

                for (int i = 0; i < viewPager.getChildCount(); i++) {
                    Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + i);
                    if (page != null) {
                        ((FreshCategoryItemsFragment) page).updateDetail();
                    }
                }
                try {
                    freshCategoryFragmentsAdapter.notifyDataSetChanged();
					tabs.notifyDataSetChanged();
                }catch(Exception e) {
                    e.printStackTrace();

                }

        }
    }

    @Subscribe
    public void onSwipe(SwipeCheckout swipe) {
        if(swipe.flag == 0) {

        }
    }

    @Override
    public void onTabClicked(int position) {
        Log.d(TAG, "onTabClicked = "+position);
        tabClickFlag = true;
        FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.TAP);
    }


	void success(ProductsResponse productsResponse) {
		try {
			noFreshsView.setVisibility(View.GONE);
			if (!isHidden()) {
				activity.showBottomBar(true);
				activity.getTopBar().below_shadow.setVisibility(View.GONE);
			} else {
				Fragment fragment = activity.getTopFragment();
				if (fragment != null && fragment instanceof VendorMenuFragment) {
					activity.showBottomBar(false);
					activity.getTopBar().below_shadow.setVisibility(View.VISIBLE);
				}
			}
			mainLayout.setVisibility(View.VISIBLE);
			activity.setProductsResponse(productsResponse);

			if (activity.getProductsResponse() != null
					&& activity.getProductsResponse().getCategories() != null) {
				activity.updateCartFromSP();
				activity.updateCartValuesGetTotalPrice();
				freshCategoryFragmentsAdapter.setCategories(activity.getProductsResponse().getCategories());
				tabs.setViewPager(viewPager);
				viewPager.setCurrentItem(Data.tabLinkIndex);
				Data.tabLinkIndex = 0;
				tabs.setBackgroundColor(activity.getResources().getColor(R.color.white_light_grey));
				tabs.notifyDataSetChanged();

				if (activity.updateCart) {
					activity.updateCart = false;
					activity.openCart();
					activity.getRelativeLayoutCartNew().performClick();
				}
			}


		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}


	public FreshSortingDialog getFreshSortingDialog() {
		try {
			if (freshSortingDialog == null) {
				setSortingList();
				slots.get(1).check = true;
				activity.freshSort = slots.get(1).id;
				activity.onSortEvent(new SortSelection(activity.freshSort));
				freshSortingDialog = new FreshSortingDialog(activity, slots,
						new FreshSortingDialog.FreshDeliverySortDialogCallback() {
							@Override
							public void onOkClicked(int position) {
								activity.freshSort = position;
								activity.getBus().post(new SortSelection(position));
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freshSortingDialog;
	}

	private void setSortingList() {
		slots.clear();
		slots.add(new SortResponseModel(0, "A-Z", false));
//		slots.add(new SortResponseModel(1, "Popularity", false));
		slots.add(new SortResponseModel(2, "Price: Low to High", false));
		slots.add(new SortResponseModel(3, "Price: High to Low", false));
	}

	public FreshCategoryFragmentsAdapter getFreshCategoryFragmentsAdapter() {
		return freshCategoryFragmentsAdapter;
	}
}