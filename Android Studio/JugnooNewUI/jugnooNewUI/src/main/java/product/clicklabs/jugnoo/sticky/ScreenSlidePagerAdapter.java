package product.clicklabs.jugnoo.sticky;

import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by socomo on 12/15/15.
 */


public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 2;
    public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return ScreenSlidePageFragment.create(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
