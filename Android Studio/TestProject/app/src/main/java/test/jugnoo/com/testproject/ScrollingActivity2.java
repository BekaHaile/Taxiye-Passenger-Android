package test.jugnoo.com.testproject;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScrollingActivity2 extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {


    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private LinearLayout llCartContainer;
    private RelativeLayout layout_rest_details;
    private ImageView ivSearch;
    private TextView tv_title;
    private TextView tv_rest_title;
    private TextView tv_rest_reviews;
    private ImageView imageViewRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        tv_title = (TextView) findViewById(R.id.tv_title);
        imageViewRestaurant = (ImageView) findViewById(R.id.iv_rest_image);
        tv_rest_title = (TextView) findViewById(R.id.tv_rest_title);
        tv_rest_reviews = (TextView) findViewById(R.id.tv_rest_reviews);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout) ;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        llCartContainer = (LinearLayout) findViewById(R.id.llCartContainer);
        layout_rest_details = (RelativeLayout) findViewById(R.id.layout_rest_details);
        appBarLayout.addOnOffsetChangedListener(this);


    }

    private State mCurrentState = State.IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {


                int calculatedAlpha = -verticalOffset * 250 / appBarLayout.getTotalScrollRange();


                toolbar.getBackground().setAlpha(calculatedAlpha);
                llCartContainer.getBackground().setAlpha(calculatedAlpha);
                tv_rest_title.setTextColor(tv_rest_title.getTextColors().withAlpha(255 - calculatedAlpha));
                tv_rest_reviews.setTextColor(tv_rest_reviews.getTextColors().withAlpha(255 - calculatedAlpha));
                tv_title.setTextColor(tv_title.getTextColors().withAlpha(calculatedAlpha));



                if (mCurrentState != State.IDLE) {

                    onStateChanged(appBarLayout, State.IDLE);
                }


                mCurrentState = State.IDLE;
            }
    }


    public void onStateChanged(AppBarLayout appBarLayout, State state) {
        switch (state) {
            case EXPANDED:
                tv_title.setVisibility(View.INVISIBLE);
                llCartContainer.getBackground().setAlpha(0);
                toolbar.getBackground().setAlpha(0);
            //    ivSearch.setImageResource(R.drawable.searc_icon);
                break;
            case COLLAPSED:
                layout_rest_details.setVisibility(View.GONE);
                llCartContainer.getBackground().setAlpha(255);
                toolbar.getBackground().setAlpha(255);
                break;
            case IDLE:
                layout_rest_details.setVisibility(View.VISIBLE);
                tv_title.setVisibility(View.VISIBLE);
            //    ivSearch.setImageResource(R.drawable.search);
                break;
        }


    }

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    AlphaAnimation animation1;


}
