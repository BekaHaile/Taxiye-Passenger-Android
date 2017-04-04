package com.sabkuchfresh.feed.ui.api;

import com.sabkuchfresh.feed.models.FeedCommonResponse;

import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public interface APICommonCallback<T extends FeedCommonResponse> {

    boolean onNotConnected();

    boolean onException(Exception e);


    void  onSuccess(T t,String message,int flag);

    boolean onError(T t,String message,int flag);

    boolean onFailure(RetrofitError error);



}
