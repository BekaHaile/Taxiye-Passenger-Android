package product.clicklabs.jugnoo;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.utils.GeniePositonsSaver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.SimpleAnimator;


/**
 * Created by clicklabs on 6/22/15.
 */


public class GenieService extends Service implements View.OnClickListener {

    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView;
    private LinearLayout txtView, txt_linearlayout;
    private ImageView chatheadImg, removeImg;
    private TextView txt1;

    private RelativeLayout relativeUpward, relativeDownward;
    private View convertView;
    private int appsAnim = 0;

    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;

    private View closeView;
    private RelativeLayout relativeLayoutCloseInner;
    private AbsoluteLayout absoluteLayoutClose, absoluteLayoutChatHead;
    private ImageView imageViewClose;

    private Handler closeHandler;
    private Runnable closeRunnable;

    private boolean closeAnimating = false;

    private ImageView imageViewMeals1, imageViewFatafat1, imageViewMeals2, imageViewFatafat2,
            imageViewAutos1, imageViewAutos2;
    private boolean mealsAnimating1 = false, fatafatAnimating1 = false, mealsAnimating2 = false,
            fatafatAnimating2 = false, autosAnimating1 = false, autosAnimating2 = false;

    public long animDuration = 350;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        new ASSL(this, 1134, 720, true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // convertView holds the animation layout
        convertView = inflater.inflate(R.layout.anim_layout, null);

        // the animation occurs upwards or downwards, so there are two layouts for the same
        relativeUpward = (RelativeLayout) convertView.findViewById(R.id.relativeUpward);
        relativeDownward = (RelativeLayout) convertView.findViewById(R.id.relativeDownward);

        // the imageViews for meals ,fatafat and autos
        imageViewMeals1 = (ImageView) convertView.findViewById(R.id.imageViewMeals1);
        imageViewFatafat1 = (ImageView) convertView.findViewById(R.id.imageViewFatafat1);
        imageViewAutos1 = (ImageView) convertView.findViewById(R.id.imageViewAutos1);

        imageViewMeals2 = (ImageView) convertView.findViewById(R.id.imageViewMeals2);
        imageViewFatafat2 = (ImageView) convertView.findViewById(R.id.imageViewFatafat2);
        imageViewAutos2 = (ImageView) convertView.findViewById(R.id.imageViewAutos2);

        imageViewMeals1.setOnClickListener(this);
        imageViewFatafat1.setOnClickListener(this);
        imageViewAutos1.setOnClickListener(this);

        imageViewMeals2.setOnClickListener(this);
        imageViewFatafat2.setOnClickListener(this);
        imageViewAutos2.setOnClickListener(this);



        final WindowManager.LayoutParams paramsA = new WindowManager.LayoutParams(
                112,
                550,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsA.gravity = Gravity.TOP | Gravity.LEFT;
        paramsA.x = 0;
        paramsA.y = 0;

        windowManager.addView(convertView, paramsA);
        ASSL.DoMagic(convertView);
        windowManager.updateViewLayout(convertView, convertView.getLayoutParams());
        clearAnims();
        convertView.setVisibility(View.GONE);


        // for the trash icon at the bottom -

        removeView = (RelativeLayout) inflater.inflate(R.layout.remove, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        paramRemove.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        removeView.setVisibility(View.GONE);
        relativeLayoutCloseInner = (RelativeLayout) removeView.findViewById(R.id.relativeLayoutCloseInner);
        absoluteLayoutClose = (AbsoluteLayout) removeView.findViewById(R.id.absoluteLayoutClose);
        removeImg = (ImageView) removeView.findViewById(R.id.remove_img);

        windowManager.addView(removeView, paramRemove);
        ASSL.DoMagic(removeView);

        windowManager.updateViewLayout(removeView, removeView.getLayoutParams());
        AbsoluteLayout.LayoutParams imageViewCloseParams = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
        imageViewCloseParams.x = (int) (310 * ASSL.Xscale());
        imageViewCloseParams.y = (int) (50 * ASSL.Yscale());
        absoluteLayoutClose.updateViewLayout(removeImg, imageViewCloseParams);
        removeView.setVisibility(View.GONE);

        // - for the trash icon at the bottom

        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        absoluteLayoutChatHead = (AbsoluteLayout) chatheadView.findViewById(R.id.absoluteLayoutChatHead);
        chatheadImg = (ImageView) chatheadView.findViewById(R.id.chathead_img);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(chatheadView, params);

        ASSL.DoMagic(chatheadView);


        chatheadView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    isLongclick = true;
                    showCloseView();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 500);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        if (txtView != null) {
                            txtView.setVisibility(View.GONE);
                            myHandler.removeCallbacks(myRunnable);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        float diff = Math.max(x_diff_move, y_diff_move);

                        if (diff > Math.abs(5)) {
                            if (convertView.getVisibility() == View.VISIBLE) {
                                convertView.setVisibility(View.GONE);
                                clearAnims();
                                clearAnims2();
                            }
                        }

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {

                            // 1. We need to clear the animations first.
                            clearAnims();
                            clearAnims2();
                            // 2. We check if the chat head is in the range of trash icon or not.
                            int x_bound_left = (szWindow.x - removeImg.getWidth()) / 2 - (int) (ASSL.Xscale() * 80);
                            int x_bound_right = (szWindow.x + removeImg.getWidth()) / 2 + (int) (ASSL.Xscale() * 20);

                            int y_bound_top = szWindow.y - (removeImg.getHeight() + getStatusBarHeight()) - (int) (ASSL.Yscale() * 200);

                            if ((x_cord_Destination >= x_bound_left && x_cord_Destination <= x_bound_right) && y_cord_Destination >= y_bound_top) {
                                inBounded = true;
                                Log.d("Chat head is now", "Inbounded");

                                // this is working perfectly fine
                                layoutParams.x = (szWindow.x - chatheadView.getWidth()) / 2;
                                layoutParams.y = szWindow.y - (removeImg.getHeight() + getStatusBarHeight() + getStatusBarHeight());


                                final AbsoluteLayout.LayoutParams param_remove = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
                                param_remove.height = (int) (remove_img_height * 1.5);
                                param_remove.width = (int) (remove_img_width * 1.5);

                                int x_cord_remove = (int) (((szWindow.x - (remove_img_width + remove_img_width / 2)) / 2));
                                int y_cord_remove = (int) (absoluteLayoutClose.getHeight() / 2 - remove_img_height);
                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;
                                absoluteLayoutClose.updateViewLayout(removeImg, param_remove);


                                // chak de phatte !

                                removeImg.setVisibility(View.VISIBLE);


                                windowManager.updateViewLayout(chatheadView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                moveCloseView(x_cord_Destination, y_cord_Destination);
                            }

                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(chatheadView, layoutParams);

                        break;
                    case MotionEvent.ACTION_UP:

                        isLongclick = false;

                        handler_longClick.removeCallbacks(runnable_longClick);
                        hideCloseView(remove_img_height, remove_img_width);

                        if (inBounded) {
                            stopService(new Intent(GenieService.this, GenieService.class));
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff;
                        y_cord_Destination = y_init_margin + y_diff;

                        int x_start;
                        x_start = x_cord_Destination;

                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        updateAnimLayoutParams();

                        if (Math.abs(x_diff) < 10 && Math.abs(y_diff) < 10) {
                            time_end = System.currentTimeMillis();
                            showAllJugnooApps();
                        } else {
                            resetGeniePostion(x_start);
                        }

                        inBounded = false;


                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        reflectParams();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewMeals1:
                if (!mealsAnimating1) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.MEALS_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewMeals2:
                if (!mealsAnimating2) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.MEALS_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewFatafat1:
                if (!fatafatAnimating1) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.FATAFAT_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewFatafat2:
                if (!fatafatAnimating2) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.FATAFAT_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewAutos1:
                if (!autosAnimating1) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.AUTOS_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewAutos2:
                if (!autosAnimating2) {
                    try {
                        CustomAppLauncher.launchApp(GenieService.this, AccessTokenGenerator.AUTOS_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                clearAnims2();
            }
        }, 500);

    }

    private WindowManager.LayoutParams getChatHeadParams() {
        return (WindowManager.LayoutParams) chatheadView.getLayoutParams();
    }

    /**
     * this function resets the genie position to left or right. It first checks whether the genie
     * is in the right half of the screen or left and then calls the respective functions.
     *
     * @param x_cord_now - this variable contains the current x co-ordinate of genie.
     */
    //
    //
    private void resetGeniePostion(int x_cord_now) {
        int w = chatheadView.getWidth();

        if (convertView.getVisibility() == View.VISIBLE) {
            convertView.setVisibility(View.GONE);
            clearAnims();
            clearAnims2();
        }

        if (x_cord_now == 0 || x_cord_now == szWindow.x - w) {

        } else if (x_cord_now + w / 2 <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(x_cord_now);

        } else if (x_cord_now + w / 2 > szWindow.x / 2) {
            isLeft = false;
            moveToRight(x_cord_now);
        }
    }

    /**
     * the animation which shows all the Jugnoo Products occurs inside a layout called
     * anim_layout.xml.We update the parameters of this layout accordingly when we move the genie
     * all over the screen.
     */
    private void updateAnimLayoutParams() {
        if (getChatHeadParams().y < szWindow.y / 2) {
            final WindowManager.LayoutParams paramsA = (WindowManager.LayoutParams) convertView.getLayoutParams();
            paramsA.x = getChatHeadParams().x + (int) (ASSL.Xscale() * 10);
            paramsA.y = getChatHeadParams().y;
            windowManager.updateViewLayout(convertView, paramsA);
            appsAnim = -1;
        } else {
            final WindowManager.LayoutParams paramsA = (WindowManager.LayoutParams) convertView.getLayoutParams();
            paramsA.x = getChatHeadParams().x + (int) (ASSL.Xscale() * 10);
            paramsA.y = ((chatheadImg.getHeight() + getChatHeadParams().y - convertView.getHeight()) > 0) ? (chatheadImg.getHeight() + getChatHeadParams().y - convertView.getHeight()) : 0;
            windowManager.updateViewLayout(convertView, paramsA);
            appsAnim = 1;
        }
    }

    /**
     * this function is meant for showing the trash icon and the shadow animation from bottom
     * when the user long clicks the Jugnoo Genie icon
     */
    public void showCloseView() {
        removeView.setVisibility(View.VISIBLE);
        SimpleAnimator mShadowFadeIn = new SimpleAnimator(relativeLayoutCloseInner, R.anim.fade_in);
        SimpleAnimator mShowAnim = new SimpleAnimator(removeImg, R.anim.slide_up);
        mShadowFadeIn.startAnimation();
        mShowAnim.startAnimation(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                closeAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // this function hides the trash icon and the shadow from bottom when Genie is untouched
    public void hideCloseView(int remove_img_height, int remove_img_width) {

        removeView.setVisibility(View.GONE);
        removeView.setPadding(0, 0, 0, 0);
        removeImg.getLayoutParams().height = remove_img_height;
        removeImg.getLayoutParams().width = remove_img_width;
        SimpleAnimator mShadowFadeOut = new SimpleAnimator(relativeLayoutCloseInner, R.anim.fade_out);
        SimpleAnimator mHideAnim = new SimpleAnimator(removeImg, R.anim.slide_down);
        mShadowFadeOut.startAnimation();
        mHideAnim.startAnimation(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                closeAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeAnimating = false;
                closeView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //

    /**
     * this function is meant to move the trash icon in accordance with the movement of Jugnoo
     * genie. We actually move the trash icon on scaled down values of Genie's motion.
     *
     * @param x - the current x co-ordinate of genie.
     * @param y - the current y co-ordinate of genie.
     */
    public void moveCloseView(int x, int y) {
        if (!closeAnimating) {
            int scaledX = x / 10;
            int scaledY = y / 10;
            AbsoluteLayout.LayoutParams imageViewCloseParams = (AbsoluteLayout.LayoutParams) removeImg.getLayoutParams();
            imageViewCloseParams.x = (int) ((288 * ASSL.Xscale()) + scaledX);
            imageViewCloseParams.y = (int) (scaledY);
            absoluteLayoutClose.updateViewLayout(removeImg, imageViewCloseParams);
        }
    }

    /**
     * this function moves the Jugnoo genie to the left default position.
     *
     * @param x_cord_now - the current x co-ordinate of genie.
     */
    private void moveToLeft(int x_cord_now) {

        final int x = x_cord_now;
        final long start = System.currentTimeMillis();
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(chatheadView, mParams);

                saveGenieParams(mParams);

                updateAnimLayoutParams();
                Log.v("timeTaken", "left " + (System.currentTimeMillis() - start));
            }
        }.start();

    }


    public void saveGenieParams(WindowManager.LayoutParams params) {
        GeniePositonsSaver.writeGenieParams(params.x, params.y);
    }


    /**
     *
     */
    private void reflectParams() {
        WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
        int[] paramsSaved = GeniePositonsSaver.readGenieParams(this);

        mParams.x = paramsSaved[0];
        mParams.y = paramsSaved[1];
        windowManager.updateViewLayout(chatheadView, mParams);

    }




    /**
     * this function moves the Jugnoo genie to the right default position.
     *
     * @param x_cord_now - the current x co-ordinate of genie.
     */
    private void moveToRight(int x_cord_now) {
        final int x = x_cord_now;
        final long start = System.currentTimeMillis();
        new CountDownTimer(350, 2) {
            //        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
//                long step = Math.abs((t - 1000) / 5);
//                long step = (1000 - t) / 10;
                long step = (500 - t) / 5;
//                long step = Math.abs((t - 500) / 5);
                mParams.x = szWindow.x + (int) (double
                        ) bounceValue(step, x) - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);

                saveGenieParams(mParams);

                updateAnimLayoutParams();
                Log.v("timeTaken", "right " + (System.currentTimeMillis() - start));
            }
        }.start();

    }

    /**
     * this function calculates the bounce value for the motion of genie by a mathematical function.
     *
     * @param step
     * @param scale
     * @return - it returns the bounce value
     */
    private double bounceValue(long step, long scale) {
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    // this function calculates the status bar height
    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    /**
     * this function performs an animation when the genie is clicked
     */
    private void showAllJugnooApps() {

        Log.d("chat head is clicked", "yeah!");
        convertView.setVisibility(View.VISIBLE);
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

            if (!autosAnimating1) {
                if (imageViewAutos1.getTag() != "shown") {
                    startShowAnimAutos1();
                } else {
                    startHideAnimAutos1();
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

            if (!autosAnimating2) {
                if (imageViewAutos2.getTag() != "shown") {
                    startShowAnimAutos2();
                } else {
                    startHideAnimAutos2();
                }
            }
        }

    }

    private void startHideAnimAutos2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -390);
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
                autosAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos2.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 390)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos2.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos2.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos2.clearAnimation();
                imageViewAutos2.setLayoutParams(layoutParams);
                imageViewAutos2.setTag("");
                imageViewAutos2.setVisibility(View.GONE);
                autosAnimating2 = false;
            }
        });

        imageViewAutos2.clearAnimation();
        imageViewAutos2.startAnimation(animationSet);
    }

    private void startShowAnimAutos2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 390);
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
                autosAnimating2 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos2.getLayoutParams());
//                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos2.setLayoutParams(layoutParams);
                imageViewAutos2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
//                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.setMargins(0, (int) (ASSL.Yscale() * 390), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos2.clearAnimation();
                imageViewAutos2.setLayoutParams(layoutParams);
                imageViewAutos2.setTag("shown");
                autosAnimating2 = false;
            }
        });

        imageViewAutos2.clearAnimation();
        imageViewAutos2.startAnimation(animationSet);

    }

    private void startHideAnimAutos1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 350);
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
                autosAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 50)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos1.clearAnimation();
                imageViewAutos1.setLayoutParams(layoutParams);
                imageViewAutos1.setTag("");
                imageViewAutos1.setVisibility(View.GONE);
                autosAnimating1 = false;
            }
        });

        imageViewAutos1.clearAnimation();
        imageViewAutos1.startAnimation(animationSet);
    }

    private void startShowAnimAutos1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -350);
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
                autosAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
//                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos1.setLayoutParams(layoutParams);
                imageViewAutos1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
//                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.setMargins(0, (int) (ASSL.Yscale() * 50), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewAutos1.clearAnimation();
                imageViewAutos1.setLayoutParams(layoutParams);
                imageViewAutos1.setTag("shown");
                autosAnimating1 = false;
            }
        });

        imageViewAutos1.clearAnimation();
        imageViewAutos1.startAnimation(animationSet);
    }

    public void startShowAnimMeals1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -110);
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
//                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
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
//                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 150)), 0, 0);
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 290)), 0, 0);
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

    public void startShowAnimFatafat1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -230);
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
//                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 280)), 0, 0);
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
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
//                layoutParams.setMargins(0, (int) (ASSL.Yscale() * 20), 0, 0);
                layoutParams.setMargins(0, (int) (ASSL.Yscale() * 170), 0, 0);
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

    public void startHideAnimMeals1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 110);
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
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 290)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
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


    public void startHideAnimFatafat1() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 220);
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
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 180)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.clearAnimation();
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setTag("");
                imageViewFatafat1.setVisibility(View.GONE);
                fatafatAnimating1 = false;
                convertView.setVisibility(View.GONE);
            }
        });

        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.startAnimation(animationSet);
    }

    public void startShowAnimMeals2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 150);
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
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 150)), 0, 0);
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


    public void startShowAnimFatafat2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 270);
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
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 270)), 0, 0);
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


    public void startHideAnimMeals2() {
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


    public void startHideAnimFatafat2() {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -270);
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
                layoutParams.setMargins(0, ((int) (ASSL.Yscale() * 270)), 0, 0);
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
//                convertView.setVisibility(View.GONE);
            }
        });

        imageViewFatafat2.clearAnimation();
        imageViewFatafat2.startAnimation(animationSet);
    }


    public void clearAnims() {
        RelativeLayout.LayoutParams layoutParamsM = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
        layoutParamsM.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
        layoutParamsM.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewMeals1.clearAnimation();
        imageViewMeals1.setLayoutParams(layoutParamsM);
        imageViewMeals1.setTag("");
        imageViewMeals1.setVisibility(View.GONE);
        mealsAnimating1 = false;

        RelativeLayout.LayoutParams layoutParamsF = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
        layoutParamsF.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.setLayoutParams(layoutParamsF);
        imageViewFatafat1.setTag("");
        imageViewFatafat1.setVisibility(View.GONE);
        fatafatAnimating1 = false;

        RelativeLayout.LayoutParams layoutParamsA = new RelativeLayout.LayoutParams(imageViewAutos1.getLayoutParams());
        layoutParamsF.setMargins(0, ((int) (ASSL.Yscale() * 400)), 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewAutos1.clearAnimation();
        imageViewAutos1.setLayoutParams(layoutParamsA);
        imageViewAutos1.setTag("");
        imageViewAutos1.setVisibility(View.GONE);
        autosAnimating1 = false;
    }

    public void clearAnims2() {
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

        RelativeLayout.LayoutParams layoutParamsA = new RelativeLayout.LayoutParams(imageViewAutos2.getLayoutParams());
        layoutParamsF.setMargins(0, 0, 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewAutos2.clearAnimation();
        imageViewAutos2.setLayoutParams(layoutParamsA);
        imageViewAutos2.setTag("");
        imageViewAutos2.setVisibility(View.GONE);
        autosAnimating2 = false;
    }


    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*// TODO Auto-generated method stub
        Log.d(Utility.LogTag, "ChatHeadService.onStartCommand() -> iLife=" + iLife);

        Bundle bd = intent.getExtras();
        if (bd != null) {
            final String sMsg = bd.getString(Utility.EXTRA_MSG);
            Log.d(Utility.LogTag, "ChatHeadService.onStartCommand() -> EXTRA_MSG=" + sMsg);

            if (iLife > 0)
                showMsg(sMsg);
            else {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMsg(sMsg);
                    }
                }, 300);
            }
        }

        iLife++;*/
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {
            if (chatheadView != null) {
                windowManager.removeView(chatheadView);
            }
        } catch (Exception E) {

        }


        try {
            if (removeView != null) {
                windowManager.removeView(removeView);
            }
        } catch (Exception E) {

        }

        try {
            if (convertView != null) {
                windowManager.removeView(convertView);
            }
        } catch (Exception E) {

        }


    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
//        Log.d(Utility.LogTag, "ChatHeadService.onBind()");
        return null;
    }





}

