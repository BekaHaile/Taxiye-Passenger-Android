package product.clicklabs.jugnoo.sticky;

import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by socomo on 12/15/15.
 */


public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private int numOfPages = 1;
    private ScreenSlidePageFragment.Callback callback;
    public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm, int numOfPages,
                                   ScreenSlidePageFragment.Callback callback) {
        super(fm);
        this.numOfPages = numOfPages;
        this.callback = callback;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return ScreenSlidePageFragment.create(position, numOfPages, callback);
    }

    @Override
    public int getCount() {
        return numOfPages;
    }
}
