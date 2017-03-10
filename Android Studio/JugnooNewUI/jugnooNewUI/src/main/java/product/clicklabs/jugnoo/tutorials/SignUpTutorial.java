package product.clicklabs.jugnoo.tutorials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.activities.PayTutorial;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by ankit on 09/03/17.
 */

public class SignUpTutorial extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter vpPayTutorialAdapter;
    private int[] layouts;
    private TextView tvSkip;
    private int numOfPages;
    private View rootView;
    private Button bNext;

    public static SignUpTutorial newInstance(int pages) {
        Bundle bundle = new Bundle();
        SignUpTutorial fragment = new SignUpTutorial();
        bundle.putInt(Constants.KEY_TUTORIAL_NO_OF_PAGES, pages);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void fetchArguments(){
        Bundle bundle = getArguments();
        numOfPages = bundle.getInt(Constants.KEY_TUTORIAL_NO_OF_PAGES, 2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_signup_tutorial, container, false);

        fetchArguments();

        viewPager = (ViewPager) rootView.findViewById(R.id.vpPayTutorial);
        tvSkip = (TextView) rootView.findViewById(R.id.tvSkip);
        bNext = (Button) rootView.findViewById(R.id.bNext);

        layouts = new int[]{
                R.layout.fragment_signup_jeanie_tutorial,
                R.layout.fragment_signup_left_menu_tutorial};

        vpPayTutorialAdapter = new PayTutorialViewPagerAdapter();
        viewPager.setAdapter(vpPayTutorialAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(SignUpTutorial.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((HomeActivity)getActivity()).getRelativeLayoutContainer().setVisibility(View.GONE);
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 0){
                    viewPager.setCurrentItem(1, true);
                } else if(viewPager.getCurrentItem() == 1){
                    startActivity(new Intent(getActivity(), NewUserChutiyapaa.class));
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return true;
            }
        });

        return rootView;
    }




    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                // btnNext.setText(getString(R.string.start));
                ((HomeActivity)getActivity()).drawerLayout.openDrawer(GravityCompat.START);
            } else {
                // still pages are left
                ((HomeActivity)getActivity()).drawerLayout.closeDrawer(GravityCompat.START);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter
     */
    public class PayTutorialViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public PayTutorialViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);

            /*TextView tv1, tv2, tv3, tv4;
            if (position == 0)
            {
                tv1 = (TextView) view.findViewById(R.id.tv1);
                tv2 = (TextView) view.findViewById(R.id.tv2);
                tv3 = (TextView) view.findViewById(R.id.tv3);
                tv4 = (TextView) view.findViewById(R.id.tv4);
            }
            else if(position == 1)
            {
                tv1 = (TextView) view.findViewById(R.id.tv1);
                tv2 = (TextView) view.findViewById(R.id.tv2);

            }*/

            view.setLayoutParams(new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT));
            ASSL.DoMagic(view);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
