package com.sabkuchfresh.feed.ui.api;

import com.sabkuchfresh.feed.models.FeedCommonResponse;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public abstract class APICommonCallback<T extends FeedCommonResponse> {

    public boolean onNotConnected(){
        return false;
    }

    public boolean onException(Exception e){
        return false;
    }


    public abstract void onSuccess(T t, String message, int flag);

    public abstract boolean onError(T t, String message, int flag);

    public boolean onFailure(Exception error){
        return false;
    }


    public void onNegativeClick(){}


    public void onFinish() {}

    public boolean isRawResponseNeeded() {
    	return false;
    }
    public void rawResponse(String rawResponse) {}

}
