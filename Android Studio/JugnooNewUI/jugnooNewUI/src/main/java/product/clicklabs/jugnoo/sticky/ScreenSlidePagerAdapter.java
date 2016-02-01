package product.clicklabs.jugnoo.sticky;

import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by socomo on 12/15/15.
 */


public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private int numOfPages = 1;
    public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm, int numOfPages) {
        super(fm);
        this.numOfPages = numOfPages;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return ScreenSlidePageFragment.create(position, numOfPages);
    }

    @Override
    public int getCount() {
        return numOfPages;
    }
}
