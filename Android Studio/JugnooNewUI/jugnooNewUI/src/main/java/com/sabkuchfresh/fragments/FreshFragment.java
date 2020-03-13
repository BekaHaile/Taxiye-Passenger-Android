package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.adapters.FreshCategoryFragmentsAdapter;
import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshNoDeliveriesDialog;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.DeliveryStore;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
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
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener, PushDialog.Callback,
        SwipeRefreshLayout.OnRefreshListener, GAAction, GACategory{

	private final String TAG = FreshFragment.class.getSimpleName();
	private LinearLayout llRoot;
    private LinearLayout mainLayout;
    private LinearLayout noFreshsView;
	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private ImageView ivShadowBelowTab, ivShadowAboveTab, ivEditStore;
	private FreshCategoryFragmentsAdapter freshCategoryFragmentsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
	private View rootView;
    private FreshActivity activity;
    private boolean tabClickFlag = false;
    private TextView tvStoreName;

    private ArrayList<SubItem> freshData = new ArrayList<>();
    private boolean loader = true, resumed = false;
    protected Bus mBus;
    PushDialog pushDialog;
    private RelativeLayout rlSelectedStore;

	private SuperCategoriesData.SuperCategory superCategory;

	public static FreshFragment newInstance(SuperCategoriesData.SuperCategory superCategory){
		Gson gson = new Gson();
		FreshFragment fragment = new FreshFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.KEY_SUPER_CATEGORY, gson.toJson(superCategory, SuperCategoriesData.SuperCategory.class));
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onStart() {
		mBus.register(this);
		super.onStart();
	}

	@Override
	public void onStop() {
		mBus.unregister(this);
		super.onStop();
	}

	private void parseArguments(){
		Gson gson = new Gson();
		Bundle bundle = getArguments();
		superCategory = gson.fromJson(bundle.getString(Constants.KEY_SUPER_CATEGORY, Constants.EMPTY_JSON_OBJECT), SuperCategoriesData.SuperCategory.class);
	}

    public SuperCategoriesData.SuperCategory getSuperCategory() {
        return superCategory;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh, container, false);

		parseArguments();

        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();
        Data.AppType = AppConstant.ApplicationType.FRESH;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.FRESH);

		try {
			if(!TextUtils.isEmpty(Data.userData.getUserId())) {
				MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		activity.fragmentUISetup(this);
		llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
		try {
			if(llRoot != null) {
				new ASSL(activity, llRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView);

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		freshCategoryFragmentsAdapter = new FreshCategoryFragmentsAdapter(activity, getChildFragmentManager());
		viewPager.setAdapter(freshCategoryFragmentsAdapter);
		ivShadowBelowTab = (ImageView) rootView.findViewById(R.id.ivShadowBelowTab);
		ivShadowAboveTab = (ImageView) rootView.findViewById(R.id.ivShadowAboveTab);
        rlSelectedStore = (RelativeLayout) rootView.findViewById(R.id.rlSelectedStore);
		rlSelectedStore.setVisibility(View.GONE);
        tvStoreName = (TextView) rootView.findViewById(R.id.tvStoreName);
        ivEditStore = (ImageView) rootView.findViewById(R.id.ivEditStore);

		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		tabs.setTextColorResource(R.color.text_color_dark_1, R.color.text_color);
		tabs.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
		tabs.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        tabs.setOnMyTabClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
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
                }
				try {
					GAUtils.event(FRESH, superCategory.getSuperCategoryName(), TABS_SWIPPED);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



        getAllProducts(true, activity.getSelectedLatLng());
		activity.getTopBar().title.setText(superCategory.getSuperCategoryName());

		GAUtils.trackScreenView(activity.getGaCategory()+superCategory.getSuperCategoryName());

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

        rlSelectedStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getProductsResponse().getDeliveryStores() != null
                        && activity.getProductsResponse().getDeliveryStores().size() > 1){
                    activity.getTransactionUtils().openDeliveryStoresFragment(activity, activity.getRelativeLayoutContainer());
					try {GAUtils.event(activity.getGaCategory(), superCategory.getSuperCategoryName(), SELECT_STORE+CLICKED);} catch (Exception e) {}
				} else{
                    Utils.showToast(activity, activity.getString(R.string.no_other_store_available));
                }

            }
        });

		return rootView;
	}

    public TextView getTvStoreName() {
        return tvStoreName;
    }

    @Override
	public void onResume() {
		super.onResume();
		if(!isHidden() && resumed) {
		}
		resumed = true;
	}

	private void showPromoFailedAtSignupDialog(){
        try{
            if(Data.userData.getPromoSuccess() == 0) {
                DialogPopup.alertPopupWithListener(activity, "",
                        Data.userData.getPromoMessage(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MenuAdapter.onClickAction(MenuInfoTags.OFFERS.getTag(),activity,activity.getSelectedLatLng());
                            }
                        });
                Data.userData.setPromoSuccess(1);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void showPopup() {
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
            }
        }).showNoDeliveryDialog();
    }



    @Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			freshCategoryFragmentsAdapter.notifyDataSetChanged();
			tabs.notifyDataSetChanged();
			activity.fragmentUISetup(this);
			activity.getTopBar().title.setText(superCategory.getSuperCategoryName());
            activity.resumeMethod();
            if(activity.getCartChangedAtCheckout()){
				activity.updateItemListFromSPDB();
				onUpdateListEvent(new UpdateMainList(true));
				activity.updateCartValuesGetTotalPrice();
			}
			activity.setCartChangedAtCheckout(false);
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
					activity.setMinOrderAmountText(FreshFragment.this);
					if(activity.isRefreshCart() && !activity.refreshCart2){
                        getAllProducts(true, activity.getSelectedLatLng());
					}
					activity.setRefreshCart(false);
                }
            }, 300);
		}
	}

	public void getAllProducts(final boolean loader, final LatLng latLng) {
		try {
            this.loader = loader;
			if(MyApplication.getInstance().isOnline()) {
                ProgressDialog progressDialog = null;
                if(loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.IS_FATAFAT, "1");
                params.put(Constants.KEY_CLIENT_ID, ""+ Config.getFreshClientId());
                params.put(Constants.INTERATED, "1");
				params.put(Constants.KEY_SUPER_CATEGORY_ID, String.valueOf(superCategory.getSuperCategoryId()));
				if(superCategory.getIsAd() != null && superCategory.getIsAd() != 0){
					params.put(Constants.KEY_IS_AD, String.valueOf(superCategory.getIsAd()));
				}
				if(superCategory.getVendorId() != null && superCategory.getVendorId() != 0){
					params.put(Constants.KEY_VENDOR_ID, String.valueOf(superCategory.getVendorId()));
				} else{
					params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getLastCartVendorId()));
				}
				Log.i(TAG, "getAllProducts params=" + params.toString());

				new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
					@Override
					public void success(ProductsResponse productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						try {
							if(finalProgressDialog != null)
								finalProgressDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(!loader)
							mBus.post(new SwipeCheckout(1));
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                noFreshsView.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                mainLayout.setVisibility(View.VISIBLE);
								int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(flag == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                    mainLayout.setVisibility(View.GONE);
									activity.performBackPressed(false);
									activity.getFreshHomeFragment().oSnapNotAvailableCase(message);
                                }
                                else {
                                    activity.setProductsResponse(productsResponse);
                                    activity.setMinOrderAmountText(FreshFragment.this);
									activity.setMenuRefreshLatLng(new LatLng(latLng.latitude, latLng.longitude));
                                    activity.setSortingList(FreshFragment.this);

                                    if(activity.getProductsResponse() != null
                                            && activity.getProductsResponse().getCategories() != null) {
										tabs.setVisibility(View.VISIBLE);
										ivShadowAboveTab.setVisibility(View.VISIBLE);
										ivShadowBelowTab.setVisibility(View.VISIBLE);
										if(activity.getProductsResponse().getCategories().size() == 0){
											noFreshsView.setVisibility(View.VISIBLE);
											mSwipeRefreshLayout.setVisibility(View.VISIBLE);
											mainLayout.setVisibility(View.GONE);
											tabs.setVisibility(View.GONE);
											ivShadowAboveTab.setVisibility(View.GONE);
										} else if(activity.getProductsResponse().getCategories().size() == 1){
											tabs.setVisibility(View.GONE);
											ivShadowBelowTab.setVisibility(View.GONE);
											ivShadowAboveTab.setVisibility(View.VISIBLE);
										}

                                        for(DeliveryStore deliveryStore : activity.getProductsResponse().getDeliveryStores()){
											if(deliveryStore.getIsSelected() == 1){
												if(!deliveryStore.getVendorId().equals(activity.getOpenedVendorId())
														&& activity.getCart().getCartItems(activity.getOpenedVendorId()).size() > 0){
													final DeliveryStore deliveryStoreLast = activity.getOpenedDeliveryStore();
													if(deliveryStoreLast != null) {
														DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
																getString(R.string.you_have_selected_cart_from_this_vendor_clear_cart, deliveryStoreLast.getVendorName()),
																getString(R.string.clear_cart),
																getString(R.string.cancel),
																new View.OnClickListener() {
																	@Override
																	public void onClick(View v) {
																		if (deliveryStoreLast != null) {
																			activity.getCart().getSubItemHashMap(deliveryStoreLast.getVendorId()).clear();
																			getAllProducts(true, activity.getSelectedLatLng());
																		}
																	}
																},
																new View.OnClickListener() {
																	@Override
																	public void onClick(View v) {
																		activity.performBackPressed(false);
																	}
																}, false, false);
													}
												} else {
													activity.setOpenedVendorIdName(deliveryStore.getVendorId(), deliveryStore);
												}
												break;
											}
										}

										if(activity.getOpenedDeliveryStore() != null
											&& !TextUtils.isEmpty(activity.getOpenedDeliveryStore().getVendorName())){
											tvStoreName.setText(activity.getOpenedDeliveryStore().getVendorName());
										}

										activity.updateItemListFromSPDB();
										activity.updateCartValuesGetTotalPrice();
                                        if(activity.getProductsResponse().getDeliveryStores() != null
                                                && activity.getProductsResponse().getDeliveryStores().size() > 1){
                                            ivEditStore.setVisibility(View.VISIBLE);
                                        } else{
                                            ivEditStore.setVisibility(View.GONE);
                                        }
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
                                        }
                                        if(productsResponse.getShowMessage() != null
                                                && productsResponse.getShowMessage().equals(1)) {
                                            new FreshNoDeliveriesDialog(activity, new FreshNoDeliveriesDialog.Callback() {
                                                @Override
                                                public void onDismiss() {

                                                }
                                            }).show(message);
                                        }

										rlSelectedStore.setVisibility(activity.getProductsResponse().getDeliveryStores() != null
												&& activity.getProductsResponse().getDeliveryStores().size() > 1 ? View.VISIBLE : View.GONE);
                                    }
									activity.updateSortSelectedFromAPI(FreshFragment.this, jObj);
                                }
							} else {
                                noFreshsView.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                mainLayout.setVisibility(View.GONE);
                            }
						} catch (Exception exception) {
							exception.printStackTrace();
						}
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
        mainLayout.setVisibility(View.GONE);
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						getAllProducts(loader, activity.getSelectedLatLng());
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
        ASSL.closeActivity(llRoot);
        System.gc();
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
            getAllProducts(false, activity.getSelectedLatLng());
        }
    }

    @Override
    public void onTabClicked(int position) {
        tabClickFlag = true;
    }

    @Override
    public void onButtonClicked(int deepIndex) {
		//TOD implement deep links


    }

    @Override
    public void onRefresh() {
        getAllProducts(false, activity.getSelectedLatLng());
    }

}
