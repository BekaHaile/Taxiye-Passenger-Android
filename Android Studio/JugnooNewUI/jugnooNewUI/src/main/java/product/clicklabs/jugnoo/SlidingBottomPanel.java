package product.clicklabs.jugnoo;

import android.app.Activity;
import android.view.View;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanel {

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private String TAG = "slidingPanel";

    public SlidingBottomPanel(Activity activity, View view) {
        initComponents(view);
    }

    private void initComponents(View view){
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (260 * ASSL.Yscale()));
        slidingUpPanelLayout.setPanelHeight((int) (110 * ASSL.Yscale()));

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
    }

    public void slideOnClick(View view){
        if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else {
            switch (view.getId()) {
                case R.id.linearLayoutCash:
                        Log.v("on click", "linearLayoutCash");
                    break;

                case R.id.linearLayoutFare:
                        Log.v("on click", "linearLayoutFare");
                    break;

                case R.id.linearLayoutOffers:
                        Log.v("on click", "linearLayoutOffers");
                    break;
            }
        }
    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout(){
        return slidingUpPanelLayout;
    }

}
