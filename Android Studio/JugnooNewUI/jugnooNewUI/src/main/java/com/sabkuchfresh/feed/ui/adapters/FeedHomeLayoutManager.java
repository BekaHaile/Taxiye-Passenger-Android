package com.sabkuchfresh.feed.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

/**
 * Created by Parminder Singh on 4/26/17.
 */

public class FeedHomeLayoutManager extends LinearLayoutManager {
    public FeedHomeLayoutManager(Context context) {
        super(context);
    }



    @Override
    public boolean canScrollVertically() {
         if(findFirstCompletelyVisibleItemPosition()==0){
             return findLastCompletelyVisibleItemPosition() != getItemCount()-1 && super.canScrollVertically();
        }
        else
            return super.canScrollVertically();



    }
}
