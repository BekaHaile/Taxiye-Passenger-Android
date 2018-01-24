package com.sabkuchfresh.home;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.retrofit.OfferingsVisibilityResponse;
import retrofit.RetrofitError;

/**
 * Created by Parminder Saini on 19/01/18.
 */

public class OfferingsVisibilityController {
    private HashMap<String,String> requestParams;
    private Activity activity;
    private LatLng currentOfferingsLatLng;//The location of offerings according to which offerings are displayed as in app currently.
    private LatLng lastRequestedLatLng;//Last requested LatLng while current api in progress.
    private boolean isApiInProgress;
    private FABViewTest fabViewTest;
    private MenuBar menuBar;




    public OfferingsVisibilityController(Activity activity, @NonNull LatLng latLng, @NonNull FABViewTest fabViewTest, MenuBar menuBar){
        this.activity = activity;
        this.fabViewTest = fabViewTest;
        this.currentOfferingsLatLng=latLng;
        this.menuBar = menuBar;
        requestParams = new HashMap<>();

    }




    public void fetchOfferingsCorrespondingToCurrentAddress(final LatLng changedLatLng){
        //return if new latLng are null or they are same as the current latlng
        if(changedLatLng==null /*|| currentOfferingsLatLng.equals(changedLatLng)*/){
            return;

        }

        lastRequestedLatLng= changedLatLng;
        if(isApiInProgress){
            return;
        }

        requestParams.put(Constants.KEY_LATITUDE,String.valueOf(changedLatLng.latitude));
        requestParams.put(Constants.KEY_LONGITUDE,String.valueOf(changedLatLng.longitude));
        new ApiCommon<OfferingsVisibilityResponse>(activity).execute(requestParams, ApiName.OFFERING_VISBILITY_API
               , new APICommonCallback<OfferingsVisibilityResponse>() {
                   @Override
                   public boolean onNotConnected() {
                       isApiInProgress = false;
                       return true;
                   }

                   @Override
                   public boolean onException(Exception e) {
                       isApiInProgress = false;
                       return true;
                   }

                   @Override
                   public void onSuccess(OfferingsVisibilityResponse fetchOfferingsVisibilityResponse, String message, int flag) {
                       isApiInProgress = false;
                       currentOfferingsLatLng = changedLatLng;
                       if(activity instanceof FreshActivity){
                           ((FreshActivity)activity).setOfferingsVisibility(fetchOfferingsVisibilityResponse.getData());
                       }


                       if(!currentOfferingsLatLng.equals(lastRequestedLatLng)){
                           fetchOfferingsCorrespondingToCurrentAddress(lastRequestedLatLng);
                       }
                   }

                   @Override
                   public boolean onError(OfferingsVisibilityResponse fetchOfferingsVisibilityResponse, String message, int flag) {
                       isApiInProgress = false;
                       return true;
                   }

                   @Override
                   public boolean onFailure(RetrofitError error) {
                       isApiInProgress = false;
                       return true;
                   }

                   @Override
                   public void onNegativeClick() {

                   }
               });
    }




}
