package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCategoryFragmentsAdapter;
import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshDeliverySlotsDialog;
import com.sabkuchfresh.home.FreshNoDeliveriesDialog;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener, PushDialog.Callback,
        SwipeRefreshLayout.OnRefreshListener{

	private final String TAG = FreshFragment.class.getSimpleName();
	private RelativeLayout linearLayoutRoot;
    private LinearLayout mainLayout;
    private LinearLayout noFreshsView;
    private TextView swipe_text;
	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private FreshCategoryFragmentsAdapter freshCategoryFragmentsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
	private View rootView;
    private FreshActivity activity;
    private boolean tabClickFlag = false;

    private RelativeLayout searchLayout;

    private FreshDeliverySlotsDialog freshDeliverySlotsDialog;
    private ArrayList<SortResponseModel> slots = new ArrayList<>();
    private ArrayList<SubItem> freshData = new ArrayList<>();
    public FreshFragment(){}
    private boolean loader = true;
    protected Bus mBus;
    PushDialog pushDialog;

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
        Data.AppType = AppConstant.ApplicationType.FRESH;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);
        activity.setSwipeAvailable(true);

		try {
			if(!TextUtils.isEmpty(Data.userData.getUserId())) {
				MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		activity.fragmentUISetup(this);
		linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        searchLayout = (RelativeLayout) rootView.findViewById(R.id.searchLayout);
        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView);
        swipe_text = (TextView) rootView.findViewById(R.id.swipe_text);
        swipe_text.setTypeface(Fonts.mavenRegular(activity));

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		freshCategoryFragmentsAdapter = new FreshCategoryFragmentsAdapter(activity, getChildFragmentManager());
		viewPager.setAdapter(freshCategoryFragmentsAdapter);

		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);
		tabs.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        tabs.setOnMyTabClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        MealAdapter freshAdapter = new MealAdapter(activity, freshData);
        recyclerView.setAdapter(freshAdapter);

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

                try {
                    if (activity.getProductsResponse() != null
                            && activity.getProductsResponse().getCategories() != null) {
                        NudgeClient.trackEventUserId(activity,
                                String.format(FlurryEventNames.NUDGE_FRESH_CATEGORY_CLICKED_FORMAT,
                                        activity.getProductsResponse().getCategories().get(position).getCategoryName()), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.searchItem();
            }
        });

        setSortingList();
        getAllProducts(true);

        try {
            if(Data.getFreshData() != null && Data.getFreshData().pendingFeedback == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.openFeedback();
                    }
                }, 300);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		try {
			if(Data.userData.getPromoSuccess() == 0) {
				showPromoFailedAtSignupDialog();
			} else if(Data.getFreshData().getIsFatafatEnabled() == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
				Data.getFreshData().setIsFatafatEnabled(AppConstant.IsFatafatEnabled.ENABLED);
				showPopup();
			} else if(Data.getFreshData().getPopupData() != null) {
				pushDialog = new PushDialog(activity, this);
				pushDialog.show(Data.getFreshData().getPopupData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootView;
	}





    private void showPromoFailedAtSignupDialog(){
        try{
            if(Data.userData.getPromoSuccess() == 0) {
                DialogPopup.alertPopupWithListener(activity, "",
                        Data.userData.getPromoMessage(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.getMenuBar().menuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag());
                            }
                        });
                Data.userData.setPromoSuccess(1);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void showPopup() {
        //123
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
                //activity.orderComplete();
            }
        }).show();

    }

    private void setSortingList() {
        slots.clear();
        slots.add(new SortResponseModel(0, "A-Z", false));
        slots.add(new SortResponseModel(1, "Popularity", false));
        slots.add(new SortResponseModel(2, "Price: Low to High", false));
        slots.add(new SortResponseModel(3, "Price: High to Low", false));


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
		}
	}

	public void getAllProducts(final boolean loader) {
		try {
            this.loader = loader;
			if(AppStatus.getInstance(activity).isOnline(activity)) {
                ProgressDialog progressDialog = null;
                if(loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                params.put(Constants.IS_FATAFAT, "1");
                params.put(Constants.KEY_CLIENT_ID, ""+ Config.getFreshClientId());
                params.put(Constants.INTERATED, "1");
				Log.i(TAG, "getAllProducts params=" + params.toString());

                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
					@Override
					public void success(ProductsResponse productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                noFreshsView.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                if(!isHidden()) {
                                    activity.hideBottomBar(true);
                                    activity.getTopBar().below_shadow.setVisibility(View.GONE);
                                }
                                mainLayout.setVisibility(View.VISIBLE);
								int flag = jObj.getInt(Constants.KEY_FLAG);
                                activity.setProductsResponse(productsResponse);
                                setSortingList();
                                if(Data.freshSort == -1) {
                                    int sortedBy = jObj.getInt(Constants.SORTED_BY);
                                    Data.freshSort = sortedBy;
                                    slots.get(sortedBy).setCheck(true);
                                } else {
                                    slots.get(Data.freshSort).setCheck(true);
                                    activity.onSortEvent(new SortSelection(Data.freshSort));
                                }

								if(activity.getProductsResponse() != null
										&& activity.getProductsResponse().getCategories() != null) {
									activity.updateCartFromSP();
									activity.updateCartValuesGetTotalPrice();
                                    if(loader) {
                                        freshCategoryFragmentsAdapter.setCategories(activity.getProductsResponse().getCategories());
                                        tabs.setViewPager(viewPager);
                                        viewPager.setCurrentItem(Data.tabLinkIndex);
                                        Data.tabLinkIndex = 0;
                                        tabs.setBackgroundColor(activity.getResources().getColor(R.color.white_light_grey));
                                    } else {
										freshCategoryFragmentsAdapter.setCategories(activity.getProductsResponse().getCategories());
                                    }
									tabs.notifyDataSetChanged();

                                    if(activity.updateCart) {
                                        activity.updateCart = false;
                                        activity.openCart();
                                        activity.relativeLayoutCart.performClick();
                                    }
									if(productsResponse.getShowMessage() != null
											&& productsResponse.getShowMessage().equals(1)) {
										new FreshNoDeliveriesDialog(activity, new FreshNoDeliveriesDialog.Callback() {
											@Override
											public void onDismiss() {

											}
										}).show(message);
									}
								}
							} else {
                                noFreshsView.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                activity.hideBottomBar(false);
                                mainLayout.setVisibility(View.GONE);
                            }
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(!loader)
                            mBus.post(new SwipeCheckout(1));
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                        if(!loader)
                            mBus.post(new SwipeCheckout(1));
					}
				});
			}
			else {
                if(!loader)
                    mBus.post(new SwipeCheckout(1));
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
            if(!loader)
                mBus.post(new SwipeCheckout(1));
		}
        mSwipeRefreshLayout.setRefreshing(false);
	}

	private void retryDialog(DialogErrorType dialogErrorType){
        noFreshsView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        activity.hideBottomBar(false);
        mainLayout.setVisibility(View.GONE);
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						getAllProducts(loader);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


    public FreshDeliverySlotsDialog getFreshDeliverySlotsDialog() {

        if (freshDeliverySlotsDialog == null) {
            freshDeliverySlotsDialog = new FreshDeliverySlotsDialog(activity, slots,
                    new FreshDeliverySlotsDialog.FreshDeliverySortDialogCallback() {
                        @Override
                        public void onOkClicked(int position) {
                            //setSelectedSlotToView();
//                            activity.sortArray(position);
                            Data.freshSort = position;
                            activity.getBus().post(new SortSelection(position));
                        }
                    });
        }
        return freshDeliverySlotsDialog;
    }

    @Subscribe
    public void onUpdateListEvent(UpdateMainList event) {

        if(event.flag) {
            // Update pager adapter

                for (int i = 0; i < viewPager.getChildCount(); i++) {
                    Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + i);
                    // based on the current position you can then cast the page to the correct
                    // class and call the method:
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
            getAllProducts(false);
        }
    }

    @Override
    public void onTabClicked(int position) {
        Log.d(TAG, "onTabClicked = "+position);
        tabClickFlag = true;
        FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.TAP);
    }

    @Override
    public void onButtonClicked(int deepIndex) {
		//TOD implement deep links


    }

    @Override
    public void onRefresh() {
        getAllProducts(false);
    }
}