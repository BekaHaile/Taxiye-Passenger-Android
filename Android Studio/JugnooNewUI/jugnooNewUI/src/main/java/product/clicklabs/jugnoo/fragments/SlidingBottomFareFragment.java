package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.R;

/**
 * Created by socomo on 1/8/16.
 */
public class SlidingBottomFareFragment extends Fragment{

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_activity, container, false);


        return rootView;
    }
}
