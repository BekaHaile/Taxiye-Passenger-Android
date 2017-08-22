package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.MenusFilterCuisinesAdapter;
import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CuisineResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusFilterCuisinesFragment extends Fragment{
    private final String TAG = MenusFilterCuisinesFragment.class.getSimpleName();

    private LinearLayout linearLayoutRoot;
    private EditText editTextCuisine;
    private RecyclerView recyclerViewCuisines;
    private Button buttonDone;
    private MenusFilterCuisinesAdapter menusFilterCuisinesAdapter;

    private View rootView;
    private FreshActivity activity;

    public MenusFilterCuisinesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus_filter_cuisines, container, false);

        activity = (FreshActivity) getActivity();

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        editTextCuisine = (EditText) rootView.findViewById(R.id.editTextCuisine); editTextCuisine.setTypeface(Fonts.mavenMedium(activity));
        recyclerViewCuisines = (RecyclerView) rootView.findViewById(R.id.recyclerViewCuisines);
        recyclerViewCuisines.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCuisines.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCuisines.setHasFixedSize(false);
        buttonDone = (Button) rootView.findViewById(R.id.buttonDone); buttonDone.setTypeface(Fonts.mavenMedium(activity));



        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getFilterCuisinesLocal()!=null)
                   activity.getFilterCuisinesLocal().clear();
                if(menusFilterCuisinesAdapter!=null && menusFilterCuisinesAdapter.getCuisines()!=null){
                    for(int i=0; i<menusFilterCuisinesAdapter.getCuisines().size(); i++){
                        activity.getFilterCuisinesLocal().add(menusFilterCuisinesAdapter.getCuisines().get(i));
                    }
                }

                activity.performBackPressed(false);
            }
        });

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getAllCuisines(true,activity.getSelectedLatLng());
        return rootView;
    }

    private void setFilterAdpater(ArrayList<FilterCuisine> ranges) {
        menusFilterCuisinesAdapter = new MenusFilterCuisinesAdapter(activity,ranges, editTextCuisine, null);
        recyclerViewCuisines.setAdapter(menusFilterCuisinesAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }


    public void getAllCuisines(final boolean loader, final LatLng latLng) {

        try {
            if (MyApplication.getInstance().isOnline()) {
               HashMap<String,String> params = new HashMap<>();

                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                params.put(Constants.INTERATED, "1");
                new HomeUtil().putDefaultParams(params);
                ProgressDialog progressDialog = null;

                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getMenusApiService().nearbyCuisines(params, new Callback<CuisineResponse>() {
                    @Override
                    public void success(CuisineResponse cuisineResponse, Response response) {

                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = cuisineResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == cuisineResponse.getFlag()) {
                                    try {
                                        if(finalProgressDialog !=null)
                                        finalProgressDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if(cuisineResponse.getRanges()!=null){
                                        setFilterAdpater(cuisineResponse.getRanges());

                                    }



                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader);

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET, latLng, loader);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllCuisines(loader, latLng);
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
