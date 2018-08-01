package product.clicklabs.jugnoo.widgets.slider;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 4/29/17.
 */

public abstract class PaySlider {




    public LinearLayout llPayViewContainer;
    @BindView(R.id.rlSliderContainer)
    public RelativeLayout rlSliderContainer;
    @BindView(R.id.viewAlpha)
    public View viewAlpha;
    @BindView(R.id.relativeLayoutSlider)
    public RelativeLayout relativeLayoutSlider;
    @BindView(R.id.tvSlide)
    public TextView tvSlide;
    @BindView(R.id.sliderText)
    public TextView sliderText;
    @BindView(R.id.buttonPlaceOrder)
    public Button buttonPlaceOrder;
    private RelativeLayout.LayoutParams paramsF;
    private long animDuration = 150;

    public PaySlider(View view){
        llPayViewContainer = (LinearLayout) view;
        ButterKnife.bind(this,view);
        llPayViewContainer.setVisibility(View.VISIBLE);
        rlSliderContainer.setVisibility(View.VISIBLE);
        tvSlide.setText(tvSlide.getContext().getString(R.string.confirm).toUpperCase());
        sliderText.setText(R.string.swipe_to_confirm);
        setUpPayBar();
        paramsF = (RelativeLayout.LayoutParams) tvSlide.getLayoutParams();
    }

    public PaySlider(View view,String buttonText, String sliderMessage){
        llPayViewContainer = (LinearLayout) view;
        ButterKnife.bind(this,view);
        llPayViewContainer.setVisibility(View.VISIBLE);
        rlSliderContainer.setVisibility(View.VISIBLE);
        tvSlide.setText(buttonText);
        sliderText.setText(sliderMessage);
        setUpPayBar();
        paramsF = (RelativeLayout.LayoutParams) tvSlide.getLayoutParams();
    }

    private void setUpPayBar() {
        tvSlide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        break;

                    case MotionEvent.ACTION_MOVE:
                        if((event.getRawX()-getRelativeSliderLeftMargin()) > (tvSlide.getWidth()/2) && (event.getRawX()-getRelativeSliderLeftMargin()) < relativeLayoutSlider.getWidth()-(tvSlide.getWidth()/2)){
                            paramsF.leftMargin = (int) layoutX(event.getRawX()-getRelativeSliderLeftMargin());
                            paramsF.setMarginStart((int) layoutX(event.getRawX()-getRelativeSliderLeftMargin()));
                            relativeLayoutSlider.updateViewLayout(tvSlide, paramsF);
                            sliderText.setVisibility(View.VISIBLE);
                            float percent = (event.getRawX()-getRelativeSliderLeftMargin()) / (relativeLayoutSlider.getWidth()-tvSlide.getWidth());
                            viewAlpha.setAlpha(percent);
                            if(percent > 0.6f){
                                sliderText.setVisibility(View.GONE);
                            } else{
                                sliderText.setVisibility(View.VISIBLE);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if ((event.getRawX()-getRelativeSliderLeftMargin()) < (relativeLayoutSlider.getWidth()-(tvSlide.getWidth()/2))*0.6f) {
                            setSlideInitial();
                        } else{
                            animateSliderButton(paramsF.getMarginStart(), relativeLayoutSlider.getWidth()-tvSlide.getWidth());
                            relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_confirm_color_bg);
                            rlSliderContainer.setBackgroundResource(R.color.slider_green);
                            sliderText.setVisibility(View.GONE);
                            viewAlpha.setAlpha(1.0f);
                            onPayClick();

                        }
                        break;
                }

                return true;
            }
        });
    }

    public void setSlideInitial(){
        animateSliderButton(paramsF.getMarginStart(), 0);
        rlSliderContainer.setBackgroundResource(R.drawable.bg_rectangle_gradient_normal);
        relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_color_bg);
        sliderText.setVisibility(View.VISIBLE);
        viewAlpha.setAlpha(0.0f);
    }

    private void animateSliderButton(final int currMargin, final float newMargin){
        float diff = newMargin - (float)currMargin;
        Animation translateAnim = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, diff,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnim.setDuration(animDuration);
        translateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnim.setFillAfter(false);
        translateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvSlide.clearAnimation();
                paramsF.leftMargin = (int) newMargin;
                paramsF.setMarginStart((int) newMargin);
                relativeLayoutSlider.updateViewLayout(tvSlide, paramsF);
                tvSlide.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvSlide.clearAnimation();
        tvSlide.setEnabled(false);
        tvSlide.startAnimation(translateAnim);
    }

    public void fullAnimate(){
        animateSliderButton(paramsF.getMarginStart(), relativeLayoutSlider.getWidth()-tvSlide.getWidth());
        relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_confirm_color_bg);
        rlSliderContainer.setBackgroundResource(R.color.slider_green);
        sliderText.setVisibility(View.GONE);
        viewAlpha.setAlpha(1.0f);
    }
    private float getRelativeSliderLeftMargin(){
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)relativeLayoutSlider.getLayoutParams();
        return relativeParams.getMarginStart();
    }

    public boolean isSliderInIntialStage(){
        return paramsF.getMarginStart()==0;
    }

    private float layoutX(float rawX){
        return rawX - sliderButtonWidth()/2f;
    }
    private float sliderButtonWidth(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvSlide.getLayoutParams();
        return (float)params.width;
    }

    public abstract void onPayClick();
}
