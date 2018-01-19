package com.sabkuchfresh.home;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.retrofit.FetchOfferingsVisibilityResponse;
import retrofit.RetrofitError;

/**
 * Created by Parminder Saini on 19/01/18.
 */

public class OfferingsVisibilityController {
    private HashMap<String,String> requestParams;
    private Activity activity;
    private LatLng currentOfferingsLatLng;//The location of offerings according to which offerings are displayed as in app currently.
    private ApiCommon<FetchOfferingsVisibilityResponse> apiCommon;



    public OfferingsVisibilityController(Activity activity,@NonNull LatLng latLng){
        this.currentOfferingsLatLng=latLng;
        requestParams = new HashMap<>();
        apiCommon =  new ApiCommon<FetchOfferingsVisibilityResponse>(activity).showLoader(false);

    }


    public void fetchOfferingsCorrespondingToCurrentAddress(LatLng changedLatLng){
        //return if new latlng are null or they are same as the current latlng
        if(changedLatLng==null || currentOfferingsLatLng.equals(changedLatLng)){
            return;

        }
        requestParams.put(Constants.KEY_LATITUDE,String.valueOf(changedLatLng.latitude));
        requestParams.put(Constants.KEY_LONGITUDE,String.valueOf(changedLatLng.longitude));
        apiCommon.execute(requestParams, ApiName.OFFERING_VISBILITY_API
               , new APICommonCallback<FetchOfferingsVisibilityResponse>() {
                   @Override
                   public boolean onNotConnected() {
                       return true;
                   }

                   @Override
                   public boolean onException(Exception e) {
                       return true;
                   }

                   @Override
                   public void onSuccess(FetchOfferingsVisibilityResponse fetchOfferingsVisibilityResponse, String message, int flag) {

                   }

                   @Override
                   public boolean onError(FetchOfferingsVisibilityResponse fetchOfferingsVisibilityResponse, String message, int flag) {
                       return true;
                   }

                   @Override
                   public boolean onFailure(RetrofitError error) {
                       return true;
                   }

                   @Override
                   public void onNegativeClick() {

                   }
               });
    }




}
