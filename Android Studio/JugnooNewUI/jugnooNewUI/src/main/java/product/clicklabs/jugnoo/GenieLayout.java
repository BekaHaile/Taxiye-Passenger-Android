package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;

/**
 * Created by clicklabs on 6/25/15.
 */
public class GenieLayout implements FlurryEventNames{

    public GenieLayout(Context context) {
        this.context = context;
    }

    private Context context;
    private AbsoluteLayout absoluteLayout;
    private ImageView imageViewJugnoo;
    private RelativeLayout animLayout;
    private RelativeLayout relativeUpward, relativeDownward;

    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();

    private int appsAnim = 0;
    private long animDuration = 300;

    private ImageView imageViewMeals1, imageViewFatafat1, imageViewMeals2, imageViewFatafat2;
    private boolean mealsAnimating1 = false, fatafatAnimating1 = false, mealsAnimating2 = false, fatafatAnimating2 = false;

    public void addGenieLayout(View rootView, final RelativeLayout relativeLayoutMainCheckout) {

        new ASSL(context, 1134, 720, false);

        absoluteLayout = (AbsoluteLayout) rootView.findViewById(R.id.rootAbsoluteLayout);
        imageViewJugnoo = (ImageView) rootView.findViewById(R.id.imageViewJugnoo);
        animLayout = (RelativeLayout) rootView.findViewById(R.id.animLayout);

        AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) imageViewJugnoo.getLayoutParams();
        params.x = (int) (520 * ASSL.Xscale());
        params.y = (int) (840 * ASSL.Yscale());
        absoluteLayout.updateViewLayout(imageViewJugnoo, params);

        AbsoluteLayout.LayoutParams paramsAnimLayout = (AbsoluteLayout.LayoutParams) animLayout.getLayoutParams();
        paramsAnimLayout.x = (int) (535 * ASSL.Xscale());
        paramsAnimLayout.y = (int) (570 * ASSL.Yscale());
        absoluteLayout.updateViewLayout(animLayout, paramsAnimLayout);

        absoluteLayout.setVisibility(View.VISIBLE);

        relativeUpward = (RelativeLayout) rootView.findViewById(R.id.relativeUpward);
        relativeDownward = (RelativeLayout) rootView.findViewById(R.id.relativeDownward);

        // the imageViews for meals and fatafat
        imageViewMeals1 = (ImageView) rootView.findViewById(R.id.imageViewMeals1);
        imageViewFatafat1 = (ImageView) rootView.findViewById(R.id.imageViewFatafat1);

        imageViewMeals2 = (ImageView) rootView.findViewById(R.id.imageViewMeals2);
        imageViewFatafat2 = (ImageView) rootView.findViewById(R.id.imageViewFatafat2);

        szWindow.x = (int) (ASSL.Xscale() * 720);
        szWindow.y = (int) (ASSL.Yscale() * 1134);


//        imageViewJugnoo.setOnTouchListener(new View.OnTouchListener() {
//
//            long time_start = 0, time_end = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//
//                int x_cord = (int) event.getRawX();
//                int y_cord = (int) event.getRawY();
//                int x_cord_Destination, y_cord_Destination;
//
//                AbsoluteLayout.LayoutParams paramsGenie = getParamsF();
//
//                switch (event.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        time_start = System.currentTimeMillis();
//
//                        x_init_cord = x_cord;
//                        y_init_cord = y_cord;
//
//                        x_init_margin = getParamsF().x;
//                        y_init_margin = getParamsF().y;
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        int x_diff_move = x_cord - x_init_cord;
//                        int y_diff_move = y_cord - y_init_cord;
//
//                        float diff = Math.max(x_diff_move, y_diff_move);
//
//                        if (Math.abs(diff) > 10) {
//                            if (animLayout.getVisibility() == View.VISIBLE) {
//                                animLayout.setVisibility(View.GONE);
//                                clearAnims();
//                                clearAnims2();
//                                Log.d("anim layout", "cleared");
//                            }
//                        }
//                        x_cord_Destination = x_init_margin + x_diff_move;
//                        y_cord_Destination = y_init_margin + y_diff_move;
//
//                        if (y_cord_Destination < 0) {
//                            y_cord_Destination = 0;
//                        } else if (y_cord_Destination + (imageViewJugnoo.getHeight()) > szWindow.y) {
//                            y_cord_Destination = szWindow.y - (imageViewJugnoo.getHeight());
//                        }
//
//                        paramsGenie.x = x_cord_Destination;
//                        paramsGenie.y = y_cord_Destination;
//
//                        absoluteLayout.updateViewLayout(imageViewJugnoo, paramsGenie);
//
//                        break;
//
//
//                    case MotionEvent.ACTION_UP:
//                        int x_diff = x_cord - x_init_cord;
//                        int y_diff = y_cord - y_init_cord;
//
//                        x_cord_Destination = x_init_margin + x_diff;
//                        y_cord_Destination = y_init_margin + y_diff;
//
//                        int x_start;
//                        x_start = x_cord_Destination;
//
//                        if (Math.abs(x_diff) < 10 && Math.abs(y_diff) < 10) {
//                            time_end = System.currentTimeMillis();
//                            chathead_click();
//                        } else {
//                            if (((paramsGenie.y + imageViewJugnoo.getHeight()) > (szWindow.y - ASSL.Yscale() * 230))) {
//                                getToDefaultPosition(paramsGenie.x, paramsGenie.y);
//                            } else if (paramsGenie.y < (ASSL.Yscale() * 210)) {
//                                shiftDownwardsFromTaskbar(paramsGenie.x, paramsGenie.y);
//                            } else {
//                                resetPosition(x_start);
//                            }
//                        }
//                        break;
//                }
//                return true;
//            }
//        });


        imageViewJugnoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chathead_click();
                FlurryEventLogger.event(JUGNOO_GENIE);
            }
        });

        imageViewMeals1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mealsAnimating1) {
                    try {
                        CustomAppLauncher.launchApp((Activity) context, AccessTokenGenerator.MEALS_PACKAGE);
                        clearAnims();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FlurryEventLogger.event(JUGNOO_GENIE_MEALS);
                }
            }
        });

        imageViewMeals2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mealsAnimating2) {
                    try {
                        CustomAppLauncher.launchApp((Activity) context, AccessTokenGenerator.MEALS_PACKAGE);
                        clearAnims();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FlurryEventLogger.event(JUGNOO_GENIE_MEALS);
                }
            }
        });

        imageViewFatafat1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!fatafatAnimating1) {
                    try {
                        CustomAppLauncher.launchApp((Activity) context, AccessTokenGenerator.FATAFAT_PACKAGE);
                        clearAnims();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FlurryEventLogger.event(JUGNOO_GENIE_FATAFAT);
                }
            }
        });

        imageViewFatafat2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!fatafatAnimating2) {
                    try {
                        CustomAppLauncher.launchApp((Activity) context, AccessTokenGenerator.FATAFAT_PACKAGE);
                        clearAnims();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FlurryEventLogger.event(JUGNOO_GENIE_FATAFAT);
                }
            }
        });
    }

    public void setVisibility(int visibility){
        absoluteLayout.setVisibility(visibility);
    }

    private AbsoluteLayout.LayoutParams getParamsF() {
        return (AbsoluteLayout.LayoutParams) imageViewJugnoo.getLayoutParams();
    }

    private AbsoluteLayout.LayoutParams getParamsAnimLayout() {
        return (AbsoluteLayout.LayoutParams) animLayout.getLayoutParams();
    }


    private void chathead_click() {
        updateAnimLayoutParams();

        if (getParamsF().y < szWindow.y / 2) {
            appsAnim = -1;
        } else {
            appsAnim = 1;
        }

        animLayout.setVisibility(View.VISIBLE);
        if (appsAnim == 1) {
            // when the icon is in the lower half
            relativeUpward.setVisibility(View.VISIBLE);
            relativeDownward.setVisibility(View.GONE);
            if (!mealsAnimating1) {
                if (imageViewMeals1.getTag() != "shown") {
                    startShowAnimMeals1();
                } else {
                    startHideAnimMeals1();
                }
            }
            if (!fatafatAnimating1) {
                if (imageViewFatafat1.getTag() != "shown") {
                    startShowAnimFatafat1();
                } else {
                    startHideAnimFatafat1();
                }
            }
        } else if (appsAnim == -1) {
            // when the icon is in the upper half
            relativeUpward.setVisibility(View.GONE);
            relativeDownward.setVisibility(View.VISIBLE);
            if (!mealsAnimating2) {
                if (imageViewMeals2.getTag() != "shown") {
                    startShowAnimMeals2();
                } else {
                    startHideAnimMeals2();
                }
            }
            if (!fatafatAnimating2) {
                if (imageViewFatafat2.getTag() != "shown") {
                    startShowAnimFatafat2();
                } else {
                    startHideAnimFatafat2();
                }
            }
        }
    }



    private void resetPosition(int x_cord_now) {
        int w = imageViewJugnoo.getWidth();

        if (animLayout.getVisibility() == View.VISIBLE) {
            animLayout.setVisibility(View.GONE);
            clearAnims();
            clearAnims2();
        }


        if (x_cord_now == 0 || x_cord_now == szWindow.x - w) {

        } else if (x_cord_now + w / 2 <= szWindow.x / 2) {
            shiftTowardsLeft(x_cord_now);

        } else if (x_cord_now + w / 2 > szWindow.x / 2) {
            shiftTowardsRight(x_cord_now);
        }
    }

    private void updateAnimLayoutParams() {
        if (getParamsF().y < szWindow.y / 2) {
            final AbsoluteLayout.LayoutParams paramsA = getParamsAnimLayout();
            paramsA.x = getParamsF().x + (int) (ASSL.Xscale() * 15);
            paramsA.y = getParamsF().y + (int) (ASSL.Yscale() * 20);
            absoluteLayout.updateViewLayout(animLayout, paramsA);
            appsAnim = -1;
        } else {
            final AbsoluteLayout.LayoutParams paramsA = getParamsAnimLayout();
            paramsA.x = getParamsF().x + (int) (ASSL.Xscale() * 15);
            paramsA.y = ((imageViewJugnoo.getHeight() + getParamsF().y - animLayout.getHeight()) > 0) ? (imageViewJugnoo.getHeight() + getParamsF().y - animLayout.getHeight()) : 0;
            absoluteLayout.updateViewLayout(animLayout, paramsA);
            appsAnim = 1;
        }
//        saveGenieParams();
    }





//    public void saveGenieParams() {
//        GeniePositonsSaver.writeGenieParams(getParamsF().x, getParamsF().y);
//    }
//
//    public void setGenieParams() {
//        AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) imageViewJugnoo.getLayoutParams();
//
//        int[] paramsSaved = GeniePositonsSaver.readGenieParams(context);
//
//        if (paramsSaved[1] < (ASSL.Yscale() * 186)) {
//            Log.d("Genie needs to shifted", "");
//            shiftDownwardsFromTaskbar(paramsSaved[0],
//                paramsSaved[1]);
//        } else {
//            params.x = paramsSaved[0];
//            params.y = paramsSaved[1];
//        }
//
//        absoluteLayout.updateViewLayout(imageViewJugnoo, params);
//        updateAnimLayoutParams();
//    }



    public void callGetToDefaultPosition(){
        AbsoluteLayout.LayoutParams paramsGenie = getParamsF();
        getToDefaultPosition(paramsGenie.x, paramsGenie.y);
    }

    private void getToDefaultPosition(final int x_cord_now, int y_cord_now) {

        Log.d("get to", "default");

        Animation translateAnimation = null;
            translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, -(x_cord_now - (int) (520 * ASSL.Xscale())),
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, -(y_cord_now - (int) (840 * ASSL.Yscale())));

        translateAnimation.setDuration(350);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AbsoluteLayout.LayoutParams paramsGenie = getParamsF();
                paramsGenie.x = (int) (520 * ASSL.Xscale());
                paramsGenie.y = (int) (840 * ASSL.Yscale());
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, paramsGenie);
                updateAnimLayoutParams();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);

    }

    private void shiftDownwardsFromTaskbar(final int x_cord_now, int y_cord_now) {

        Animation translateAnimation = null;

        if (x_cord_now + imageViewJugnoo.getWidth() / 2 < szWindow.x / 2) {
            translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, -x_cord_now,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (int) (ASSL.Yscale() * 210) - y_cord_now);
        } else {
            translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, szWindow.x - x_cord_now - imageViewJugnoo.getWidth(),
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (int) (ASSL.Yscale() * 210) - y_cord_now);
        }

        translateAnimation.setDuration(350);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AbsoluteLayout.LayoutParams params = getParamsF();
                if (x_cord_now + imageViewJugnoo.getWidth() / 2 < szWindow.x / 2) {
                    params.x = 0;
                } else {
                    params.x = szWindow.x - imageViewJugnoo.getWidth();
                }

                params.y = (int) (ASSL.Yscale() * 210);
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                updateAnimLayoutParams();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);

    }

    private void shiftTowardsLeft(int x_cord_now) {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, -(x_cord_now + imageViewJugnoo.getWidth() / 4),
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(270);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
                params.x = -imageViewJugnoo.getWidth() / 4;
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                bounceBackLeft();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);

    }

    private void shiftTowardsRight(int x_cord_now) {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, (szWindow.x - x_cord_now) - (3 * imageViewJugnoo.getWidth() / 4),
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(270);
        Log.d("szWindow.x/2 = ", "" + String.valueOf(szWindow.x / 2));
        Log.d("x_cord_now", "" + x_cord_now);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
                params.x = szWindow.x - (3 * imageViewJugnoo.getWidth() / 4);
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                bounceBackRight();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);

    }

    private void bounceBackRight() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, -imageViewJugnoo.getWidth() / 4,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(180);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
                params.x = szWindow.x - imageViewJugnoo.getWidth();
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                getBackSafeRight();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);
    }

    private void bounceBackLeft() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, imageViewJugnoo.getWidth() / 4,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(180);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
//                params.x = (int)(10 * ASSL.Xscale());
                params.x = 0;
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                getBackSafeLeft();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);
    }

    private void getBackSafeRight() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, (10 * ASSL.Xscale()),
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(90);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
                params.x = szWindow.x - imageViewJugnoo.getWidth() + (int) (10 * ASSL.Xscale());
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                updateAnimLayoutParams();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);
    }

    private void getBackSafeLeft() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, (-10 * ASSL.Xscale()),
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnimation.setDuration(90);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("animation", "started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("animation", "ended");
                AbsoluteLayout.LayoutParams params = getParamsF();
                params.x = (int) (-10 * ASSL.Xscale());
                imageViewJugnoo.clearAnimation();
                absoluteLayout.updateViewLayout(imageViewJugnoo, params);
                updateAnimLayoutParams();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageViewJugnoo.clearAnimation();
        imageViewJugnoo.startAnimation(translateAnimation);
    }

    private void startShowAnimMeals1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -140);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.clearAnimation();
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setTag("shown");
                mealsAnimating1 = false;
            }
        });

        imageViewMeals1.clearAnimation();
        imageViewMeals1.startAnimation(animationSet);
    }

    private void startShowAnimFatafat1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -280);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.clearAnimation();
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setTag("shown");
                fatafatAnimating1 = false;
            }
        });

        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.startAnimation(animationSet);
    }

    private void startHideAnimMeals1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 140);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.clearAnimation();
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setTag("");
                imageViewMeals1.setVisibility(View.GONE);
                mealsAnimating1 = false;
            }
        });

        imageViewMeals1.clearAnimation();
        imageViewMeals1.startAnimation(animationSet);
    }

    private void startHideAnimFatafat1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 280);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.clearAnimation();
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setTag("");
                imageViewFatafat1.setVisibility(View.GONE);
                fatafatAnimating1 = false;
                animLayout.setVisibility(View.GONE); // changed
            }
        });

        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.startAnimation(animationSet);
    }

    private void clearAnims() {
        RelativeLayout.LayoutParams layoutParamsM = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
        layoutParamsM.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
        layoutParamsM.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewMeals1.clearAnimation();
        imageViewMeals1.setLayoutParams(layoutParamsM);
        imageViewMeals1.setTag("");
        imageViewMeals1.setVisibility(View.GONE);
        mealsAnimating1 = false;

        RelativeLayout.LayoutParams layoutParamsF = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
        layoutParamsF.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.setLayoutParams(layoutParamsF);
        imageViewFatafat1.setTag("");
        imageViewFatafat1.setVisibility(View.GONE);
        fatafatAnimating1 = false;
    }

    private void startShowAnimMeals2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 140);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals2.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals2.setLayoutParams(layoutParams);
                imageViewMeals2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals2.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals2.clearAnimation();
                imageViewMeals2.setLayoutParams(layoutParams);
                imageViewMeals2.setTag("shown");
                mealsAnimating2 = false;
            }
        });

        imageViewMeals2.clearAnimation();
        imageViewMeals2.startAnimation(animationSet);
    }

    private void startShowAnimFatafat2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 280);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat2.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat2.setLayoutParams(layoutParams);
                imageViewFatafat2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat2.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat2.clearAnimation();
                imageViewFatafat2.setLayoutParams(layoutParams);
                imageViewFatafat2.setTag("shown");
                fatafatAnimating2 = false;
            }
        });

        imageViewFatafat2.clearAnimation();
        imageViewFatafat2.startAnimation(animationSet);
    }

    private void startHideAnimMeals2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -140);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals2.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals2.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals2.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals2.clearAnimation();
                imageViewMeals2.setLayoutParams(layoutParams);
                imageViewMeals2.setTag("");
                imageViewMeals2.setVisibility(View.GONE);
                mealsAnimating2 = false;
            }
        });

        imageViewMeals2.clearAnimation();
        imageViewMeals2.startAnimation(animationSet);
    }

    private void startHideAnimFatafat2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -280);
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDuration);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDuration);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat2.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat2.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat2.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat2.clearAnimation();
                imageViewFatafat2.setLayoutParams(layoutParams);
                imageViewFatafat2.setTag("");
                imageViewFatafat2.setVisibility(View.GONE);
                fatafatAnimating2 = false;
                animLayout.setVisibility(View.GONE);
            }
        });

        imageViewFatafat2.clearAnimation();
        imageViewFatafat2.startAnimation(animationSet);
    }

    private void clearAnims2() {
        RelativeLayout.LayoutParams layoutParamsM = new RelativeLayout.LayoutParams(imageViewMeals2.getLayoutParams());
        layoutParamsM.setMargins(0, 0, 0, 0);
        layoutParamsM.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewMeals2.clearAnimation();
        imageViewMeals2.setLayoutParams(layoutParamsM);
        imageViewMeals2.setTag("");
        imageViewMeals2.setVisibility(View.GONE);
        mealsAnimating2 = false;

        RelativeLayout.LayoutParams layoutParamsF = new RelativeLayout.LayoutParams(imageViewFatafat2.getLayoutParams());
        layoutParamsF.setMargins(0, 0, 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewFatafat2.clearAnimation();
        imageViewFatafat2.setLayoutParams(layoutParamsF);
        imageViewFatafat2.setTag("");
        imageViewFatafat2.setVisibility(View.GONE);
        fatafatAnimating2 = false;
    }


    public void hideAnims(){
        if (getParamsF().y < szWindow.y / 2) {
            appsAnim = -1;
        } else {
            appsAnim = 1;
        }

        animLayout.setVisibility(View.VISIBLE);
        if (appsAnim == 1) {
            // when the icon is in the lower half
            relativeUpward.setVisibility(View.VISIBLE);
            relativeDownward.setVisibility(View.GONE);
            if (!mealsAnimating1) {
                if (imageViewMeals1.getTag() == "shown") {
                    startHideAnimMeals1();
                }
            }
            if (!fatafatAnimating1) {
                if (imageViewFatafat1.getTag() == "shown") {
                    startHideAnimFatafat1();
                }
            }
        } else if (appsAnim == -1) {
            // when the icon is in the upper half
            relativeUpward.setVisibility(View.GONE);
            relativeDownward.setVisibility(View.VISIBLE);
            if (!mealsAnimating2) {
                if (imageViewMeals2.getTag() == "shown") {
                    startHideAnimMeals2();
                }
            }
            if (!fatafatAnimating2) {
                if (imageViewFatafat2.getTag() == "shown") {
                    startHideAnimFatafat2();
                }
            }
        }
    }

    public boolean areJugnooIconsVisible() {
        if (!mealsAnimating1 || !mealsAnimating2) {
            if (imageViewMeals1.getTag() == "shown" || imageViewMeals2.getTag() == "shown") {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void clearAllAnims(){
        clearAnims();
        clearAnims2();
    }

}
