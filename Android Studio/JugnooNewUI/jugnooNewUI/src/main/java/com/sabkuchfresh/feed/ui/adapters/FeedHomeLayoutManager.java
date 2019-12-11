package com.sabkuchfresh.feed.ui.adapters;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.sabkuchfresh.home.FreshActivity;

/**
 * Created by Parminder Singh on 4/26/17.
 */

public class FeedHomeLayoutManager extends LinearLayoutManager {
    private  Rect rect = new Rect();
    private FreshActivity freshActivity;
    public FeedHomeLayoutManager(Context context) {
        super(context);
        freshActivity = (FreshActivity) context;
    }



    @Override
    public boolean canScrollVertically() {
        //When 1st pos is visible completely and last position is Visible completely and appBar is visible completely don't allow scroll

        if(findFirstCompletelyVisibleItemPosition()==0 && findLastCompletelyVisibleItemPosition()!=-1 && freshActivity.currentOffsetFeedHomeAppBar !=-freshActivity.appBarLayout.getTotalScrollRange()){

            View view = getChildAt(findLastCompletelyVisibleItemPosition());
            if(view==null)
                return super.canScrollVertically();

            if(view.getGlobalVisibleRect(rect) && view.getHeight() == rect.height() && view.getWidth() == rect.width() ) {
                return findLastCompletelyVisibleItemPosition() != getItemCount()-1 && super.canScrollVertically();
            } else{
                return super.canScrollVertically();
            }


        }
        else
            return super.canScrollVertically();



    }
}
