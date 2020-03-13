package com.sabkuchfresh.feed.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sabkuchfresh.feed.ui.fragments.FeedChildAskFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedChildReviewFragment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public class FeedAddPostPagerAdapter extends FragmentStatePagerAdapter {


    private FeedChildReviewFragment feedChildReviewFragment;
    private FeedChildAskFragment feedChildAskFragment;
    private Fragment activeFragment;
    private int itemCount = 2;

    public FeedAddPostPagerAdapter(FragmentManager fm, FeedDetail feedDetail) {
        super(fm);

        if (feedDetail != null) {
            itemCount = 1;

            if (feedDetail.getFeedType() == FeedDetail.FeedType.POST)
                activeFragment = FeedChildAskFragment.newInstance(feedDetail);
            else
                activeFragment = FeedChildReviewFragment.newInstance(feedDetail);


        } else {
            feedChildReviewFragment = new FeedChildReviewFragment();
            feedChildAskFragment = new FeedChildAskFragment();
        }

    }


    @Override
    public Fragment getItem(int position) {

        if (itemCount == 1) {
            return activeFragment;
        }
        return position == 0 ? feedChildAskFragment : feedChildReviewFragment;
    }

    @Override
    public int getCount() {
        return itemCount;
    }
}
