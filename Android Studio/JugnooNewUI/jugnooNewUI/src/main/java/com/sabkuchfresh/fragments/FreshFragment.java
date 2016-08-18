package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sabkuchfresh.adapters.FreshCategoryFragmentsAdapter;
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
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.PushDialog;
import com.sabkuchfresh.utils.Utils;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener, PushDialog.Callback {

	private final String TAG = FreshFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private FreshCategoryFragmentsAdapter freshCategoryFragmentsAdapter;

	private View rootView;
    private FreshActivity activity;
    private boolean tabClickFlag = false;

    private FreshDeliverySlotsDialog freshDeliverySlotsDialog;
    private ArrayList<SortResponseModel> slots = new ArrayList<>();
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

        if(!TextUtils.isEmpty(Data.userData.getUserId())) {
            MyApplication.getInstance().branch.setIdentity(Data.userData.getUserId());
        }

		activity.fragmentUISetup(this);
        activity.getTopBar().getImageViewSearch().setVisibility(View.VISIBLE);
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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




        setSortingList();
        getAllProducts(true);

        if(Data.userData.getFatafatUserData().pendingFeedback == 1) {
			//TODO fresh feedback fragment open here
            Data.userData.getFatafatUserData().pendingFeedback = 0;
        }

        if(Data.userData.getPromoSuccess() == 0) {
            showPromoFailedAtSignupDialog();
        } else if(Data.isfatafat == AppConstant.IsFatafatEnabled.NOT_ENABLED) {
            Data.isfatafat = AppConstant.IsFatafatEnabled.ENABLED;
            showPopup();
        } else if(Data.userData.getFatafatUserData().getPopupData() != null) {
            pushDialog = new PushDialog(activity, this);
            pushDialog.show(Data.userData.getFatafatUserData().getPopupData());
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
			activity.fragmentUISetup(this);
            activity.resumeMethod();
		}
	}

	public void getAllProducts(final boolean loader) {
		try {
            this.loader = loader;
			if(AppStatus.getInstance(activity).isOnline(activity)) {
                if(loader)
				    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                params.put(Constants.IS_FATAFAT, "1");

				Log.i(TAG, "getAllProducts params=" + params.toString());

				RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
					@Override
					public void success(ProductsResponse productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
                                int sortedBy = jObj.getInt(Constants.SORTED_BY);
                                //Prefs.with(activity).save(Constants.SORTED_BY, sortedBy);
                                setSortingList();
                                slots.get(sortedBy).setCheck(true);
								activity.setProductsResponse(productsResponse);
								if(activity.getProductsResponse() != null
										&& activity.getProductsResponse().getCategories() != null) {
									activity.updateCartFromSP();
									activity.updateCartValuesGetTotalPrice();
									freshCategoryFragmentsAdapter.setCategories(activity.getProductsResponse().getCategories());
									tabs.setViewPager(viewPager);
                                    viewPager.setCurrentItem(Data.tabLinkIndex);
                                    Data.tabLinkIndex = 0;
									tabs.setBackgroundColor(activity.getResources().getColor(R.color.white_light_grey));

									if(productsResponse.getShowMessage() != null
											&& productsResponse.getShowMessage().equals(1)) {
										new FreshNoDeliveriesDialog(activity, new FreshNoDeliveriesDialog.Callback() {
											@Override
											public void onDismiss() {

											}
										}).show(message);
									}
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
                        if(!loader)
                            mBus.post(new SwipeCheckout(1));
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
						DialogPopup.dismissLoadingDialog();
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
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
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
}
