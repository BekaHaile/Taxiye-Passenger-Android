package product.clicklabs.jugnoo.sticky;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Gurmail S Kang on 12/1/15.
 */
public class TutorialFragment extends Fragment {



    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;
    View dotOneView , dotTwoView, dotThreeView;
    TextView skipTxt;
    private int numOfPages = 1;
    private LinearLayout linearLayoutDot;

    public TutorialFragment(int numOfPages) {
        this.numOfPages = numOfPages;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View contentView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        new ASSL(getActivity(), (RelativeLayout)contentView.findViewById(R.id.rv), 1134, 720, false);

        linearLayoutDot = (LinearLayout) contentView.findViewById(R.id.linearLayoutDot);
        dotOneView = (View) contentView.findViewById(R.id.dot_one);
        dotTwoView = (View) contentView.findViewById(R.id.dot_two);
        dotThreeView = (View) contentView.findViewById(R.id.dot_three);

        if(numOfPages == 1){
            linearLayoutDot.setVisibility(View.GONE);
        } else{
            linearLayoutDot.setVisibility(View.VISIBLE);
        }


        skipTxt = (TextView) contentView.findViewById(R.id.skip_text);
        skipTxt.setVisibility(View.GONE);
        mPager =(ViewPager) contentView.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager(), numOfPages);
        mPager.setAdapter(mPagerAdapter);
        dotOneView.setBackgroundResource(R.drawable.circle_yellow);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    dotOneView.setBackgroundResource(R.drawable.circle_yellow);
                    dotTwoView.setBackgroundResource(R.drawable.circle_white);
                    dotThreeView.setBackgroundResource(R.drawable.circle_white);
                    skipTxt.setVisibility(View.GONE);
                } else if (position == 1) {
                    //mainLayout.setClickable(true);
                    dotTwoView.setBackgroundResource(R.drawable.circle_yellow);
                    dotOneView.setBackgroundResource(R.drawable.circle_white);
                    dotThreeView.setBackgroundResource(R.drawable.circle_white);
                    skipTxt.setVisibility(View.GONE);
                } else if (position == 2){
                    dotThreeView.setBackgroundResource(R.drawable.circle_yellow);
                    dotOneView.setBackgroundResource(R.drawable.circle_white);
                    dotTwoView.setBackgroundResource(R.drawable.circle_white);
                    skipTxt.setVisibility(View.GONE);
                }
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                //getActivity().invalidateOptionsMenu();
            }
        });

//        mainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });

        /*mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });*/

        return contentView;

    }





}
