package com.sabkuchfresh.pros.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.ui.adapters.ProsProductsAdapter;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 15/06/17.
 */

public class ProsProductsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction, GACategory {
    private final String TAG = ProsProductsFragment.class.getSimpleName();

    private ProsProductsAdapter productsAdapter;
    private RecyclerView rvProducts;

    private View rootView;
    private FreshActivity activity;

    private ArrayList<SubItem> subItems = new ArrayList<>();

    private SuperCategoriesData.SuperCategory superCategory;

    public static ProsProductsFragment newInstance(SuperCategoriesData.SuperCategory superCategory){
        ProsProductsFragment fragment = new ProsProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_SUPER_CATEGORY, superCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArguments(){
        superCategory = (SuperCategoriesData.SuperCategory) getArguments().getSerializable(Constants.KEY_SUPER_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pros_products, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        parseArguments();
        activity.getTopBar().title.setText(superCategory.getSuperCategoryName());

        rvProducts = (RecyclerView) rootView.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(activity));
        rvProducts.setItemAnimator(new DefaultItemAnimator());
        rvProducts.setHasFixedSize(false);
        productsAdapter = new ProsProductsAdapter(activity, subItems, new ProsProductsAdapter.Callback() {
            @Override
            public void onProductClick(int position, SubItem subItem) {

            }
        }, rvProducts);
        rvProducts.setAdapter(productsAdapter);

        getAllProducts(true, activity.getSelectedLatLng());

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    @Override
    public void onRefresh() {
        getAllProducts(false, activity.getSelectedLatLng());
    }

    public void getAllProducts(final boolean loader, final LatLng latLng) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                ProgressDialog progressDialog = null;
                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.STORE_ID, "" + 2);
                params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                params.put(Constants.INTERATED, "1");
                Log.i(TAG, "getAllProducts params=" + params.toString());

                new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
                    @Override
                    public void success(ProductsResponse productsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                subItems.clear();
                                subItems.addAll(productsResponse.getCategories().get(0).getSubItems());
                                productsAdapter.setResults(subItems);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllProducts(true, activity.getSelectedLatLng());
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


}
