package com.sabkuchfresh.feed.ui.api;

import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public interface APICommonCallback {

    boolean onNotConnected();

    boolean onException(Exception e);


     <T> void  onSuccess(T t,String message,int flag);

    <T>  boolean onError(T t,String message,int flag);

    boolean onFailure(RetrofitError error);



}
