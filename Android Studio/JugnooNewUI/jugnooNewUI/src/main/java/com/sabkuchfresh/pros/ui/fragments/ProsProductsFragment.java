package com.sabkuchfresh.pros.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.ui.adapters.ProsProductsAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
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

public class ProsProductsFragment extends Fragment implements GAAction, GACategory {
    private final String TAG = ProsProductsFragment.class.getSimpleName();

    private ProsProductsAdapter productsAdapter;
    private RecyclerView rvProducts;

    private View rootView;
    private FreshActivity activity;

    private ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum;

    public static ProsProductsFragment newInstance(ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum){
        ProsProductsFragment fragment = new ProsProductsFragment();
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        bundle.putString(Constants.KEY_CATALOGUE_DATUM, gson.toJson(prosCatalogueDatum,
                ProsCatalogueData.ProsCatalogueDatum.class));

        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArguments(){
        Gson gson = new Gson();
        prosCatalogueDatum = gson.fromJson(getArguments().getString(Constants.KEY_CATALOGUE_DATUM), ProsCatalogueData.ProsCatalogueDatum.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pros_products, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        parseArguments();
        activity.getTopBar().title.setText(prosCatalogueDatum.getName());

        rvProducts = (RecyclerView) rootView.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(activity));
        rvProducts.setItemAnimator(new DefaultItemAnimator());
        rvProducts.setHasFixedSize(false);
        productsAdapter = new ProsProductsAdapter(activity, new ProsProductsAdapter.Callback() {
            @Override
            public void onProductClick(ProsProductData.ProsProductDatum prosProductDatum) {
                activity.getTransactionUtils().addProsCheckoutFragment(activity, activity.getRelativeLayoutContainer(), prosProductDatum);
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
            activity.getTopBar().title.setText(prosCatalogueDatum.getName());
        }
    }

    public void getAllProducts(final boolean loader, final LatLng latLng) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                ProgressDialog progressDialog = null;
                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());
                params.put(Constants.KEY_PARENT_CATEGORY_ID, String.valueOf(prosCatalogueDatum.getCatalogueId()));

                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                Log.i(TAG, "getAllProducts params=" + params.toString());

                new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getProsApiService().getProductsForCategory(params, new Callback<ProsProductData>() {
                    @Override
                    public void success(ProsProductData productsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, productsResponse.getFlag(), productsResponse.getError(), productsResponse.getMessage())) {
                                if (productsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    setDataToList(productsResponse);
                                } else {
                                    DialogPopup.alertPopup(activity, "", productsResponse.getMessage());
                                }
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

    private void setDataToList(ProsProductData prosProductData){
        List<ProsProductData.ProsProductDatum> list = prosProductData.getData();
        Collections.sort(list, new Comparator<ProsProductData.ProsProductDatum>() {
            @Override
            public int compare(ProsProductData.ProsProductDatum o1, ProsProductData.ProsProductDatum o2) {
                if(o1.getPriority() != null && o2.getPriority() != null){
                    return o1.getPriority().compareTo(o2.getPriority());
                }
                return 0;
            }
        });

        productsAdapter.setResults(list);
    }

}
