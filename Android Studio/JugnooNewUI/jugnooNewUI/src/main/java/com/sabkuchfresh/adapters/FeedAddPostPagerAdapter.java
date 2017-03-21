package com.sabkuchfresh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sabkuchfresh.fragments.FeedChildAskFragment;
import com.sabkuchfresh.fragments.FeedChildReviewFragment;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public class FeedAddPostPagerAdapter extends FragmentStatePagerAdapter {


    private FeedChildReviewFragment feedChildReviewFragment;
    private FeedChildAskFragment feedChildAskFragment;

    public FeedAddPostPagerAdapter(FragmentManager fm) {
        super(fm);
        feedChildReviewFragment=FeedChildReviewFragment.newInstance();
        feedChildAskFragment= FeedChildAskFragment.newInstance();
    }




    @Override
    public Fragment getItem(int position) {
        return position==0?feedChildAskFragment:feedChildReviewFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
