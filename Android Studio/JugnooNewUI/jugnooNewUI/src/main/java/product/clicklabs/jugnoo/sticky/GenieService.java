package product.clicklabs.jugnoo.sticky;


import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.GeniePositonsSaver;
import product.clicklabs.jugnoo.utils.LocationFetcherBG;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SimpleAnimator;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by clicklabs on 6/22/15.
 */


public class GenieService extends Service implements View.OnClickListener, FlurryEventNames {

    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView;
    private LinearLayout txtView, txt_linearlayout;
    private ImageView chatheadImg, removeImg;
    private TextView txt1;
    private RelativeLayout relativeLayoutJeaniePopup;

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

    private RelativeLayout relativeLayoutInner;
    private String accessToken, eta = "", baseFair = "", fairPerKM = "", fairPerMin = "", packageName = "";

    private LocationFetcherBG locationFetcherBG;
    private LatLng latLng;


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        SplashNewActivity.initializeServerURL(this);

        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());

        locationFetcherBG = new LocationFetcherBG(this, 60000);
        //if(getIntent.hasExtra("package_name")){
        //packageName = intent.getStringExtra("package_name");

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

        /*convertView = inflater.inflate(R.layout.dialog_genie_layout, null);
        relativeLayoutInner = (RelativeLayout)convertView.findViewById(R.id.innerRl);*/



        Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(GenieService.this);
        if(!"".equalsIgnoreCase(pair.first)){
            accessToken = pair.first;
        }



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

        /* Initially Visiblity of stick is gone, will open
            when server response is in success */
        chatheadView.setVisibility(View.GONE);


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
                            //showAllJugnooApps();

//                            Intent intent = new Intent(GenieService.this, GenieActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);

                            showJeaniePopup();


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



        //checkRunningApps();

    }


    private void showJeaniePopup(){

        LinearLayout linearLayoutInner;
        ImageView imageViewClose;
        TextView textViewJugnoo, textViewETA, textViewBaseFair, textViewPerKM, textViewWait;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        relativeLayoutJeaniePopup = (RelativeLayout) inflater.inflate(R.layout.dialog_genie_layout, null);

        linearLayoutInner = (LinearLayout)relativeLayoutJeaniePopup.findViewById(R.id.innerRl);
        imageViewClose = (ImageView)relativeLayoutJeaniePopup.findViewById(R.id.close);
        textViewJugnoo = (TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewJugnoo);
        textViewETA = (TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewETA);
        textViewBaseFair = (TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewBaseFair);
        textViewPerKM = (TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewPerKM);
        textViewWait = (TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewWait);

        Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
        if(!"".equalsIgnoreCase(pair.first)){
            accessToken = pair.first;
        }

        String s;
        if(Integer.parseInt(eta) == 1){
            s = eta+" \nmin";
        }else{
            s = eta+" \nmins";
        }
        SpannableString ss=  new SpannableString(s);
        ss.setSpan(new RelativeSizeSpan(1.75f), 0, 2, 0); // set size
        //ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
        textViewETA.setText(ss);
        textViewBaseFair.setText(baseFair);
        textViewPerKM.setText(fairPerKM+"/");
        textViewWait.setText(fairPerMin+"/");

        textViewJugnoo.setTypeface(Fonts.latoRegular(this));
        textViewETA.setTypeface(Fonts.latoRegular(this));
        textViewBaseFair.setTypeface(Fonts.latoRegular(this));
        textViewPerKM.setTypeface(Fonts.latoRegular(this));
        textViewWait.setTypeface(Fonts.latoRegular(this));
        ((TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewKMTxt)).setTypeface(Fonts.latoLight(this));
        ((TextView)relativeLayoutJeaniePopup.findViewById(R.id.textViewMinTxt)).setTypeface(Fonts.latoLight(this));


        linearLayoutInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                windowManager.removeView(relativeLayoutJeaniePopup);
                stopSelf();
                Intent intent = new Intent(GenieService.this, SplashNewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("transfer_from_jeanie", 1);
                startActivity(intent);
				FlurryEventLogger.event(JUGNOO_STICKY_TRANSFER_TO_APP);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(relativeLayoutJeaniePopup);
                chatheadView.setVisibility(View.VISIBLE);
            }
        });

        final WindowManager.LayoutParams paramsRv = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsRv.gravity = Gravity.TOP | Gravity.LEFT;
        paramsRv.x = 0;
        paramsRv.y = 0;

        windowManager.addView(relativeLayoutJeaniePopup, paramsRv);
        ASSL.DoMagic(relativeLayoutJeaniePopup);
        chatheadView.setVisibility(View.GONE);

		FlurryEventLogger.event(JUGNOO_STICKY_EXPANDED);

    }

    private void getNearestDriver(){
        if (AppStatus.getInstance(GenieService.this).isOnline(GenieService.this)) {
            RequestParams params = new RequestParams();
            params.put("access_token", accessToken);
            params.put("lat", latLng.latitude);
            params.put("long", latLng.longitude);
            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/fare_estimate_for_jeanie", params,
                    new AsyncHttpResponseHandler() {
                        private JSONObject jObj;

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Log.e("on failure", "on failure");
                            chatheadView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            Log.i("Response find_a_driver", "response = " + response);
                            try {
                                JSONObject jObj = new JSONObject(response);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                eta = jObj.optString("eta");
                                baseFair = jObj.optString("base_fare");
                                fairPerKM = jObj.optString("fare_per_km");
                                fairPerMin = jObj.optString("fare_per_min");
                                if((ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag)
                                        && (Integer.parseInt(eta) != 0)){
                                    chatheadView.setVisibility(View.VISIBLE);
									FlurryEventLogger.event(JUGNOO_STICKY_OPENED);
                                }

                            }  catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }

                        /*@Override
                        public void onFailure(Throwable arg3) {
                            chatheadView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i("Response find_a_driver", "response = " + response);
                            try {
                                JSONObject jObj = new JSONObject(response);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                eta = jObj.optString("eta");
                                baseFair = jObj.optString("base_fare");
                                fairPerKM = jObj.optString("fare_per_km");
                                fairPerMin = jObj.optString("fare_per_min");
                                if((ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag)
                                        && (Integer.parseInt(eta) != 0)){
                                    chatheadView.setVisibility(View.VISIBLE);
                                }

                            }  catch (Exception exception) {
                                exception.printStackTrace();
                            }


                        }*/
                    });
        } else {

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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

        if(intent.hasExtra("package_name")) {
            packageName = intent.getStringExtra("package_name");
            Log.d("package name in service","--> "+packageName);
        }

        if(intent.hasExtra("latitude")){
            double latitude  = intent.getDoubleExtra("latitude", 0);
            double longitude  = intent.getDoubleExtra("longitude", 0);
            latLng = new LatLng(latitude, longitude);

            getNearestDriver();
            Log.v("sticky latlng", "--> " + latitude + ", " + longitude);
            //chatheadView.setVisibility(View.VISIBLE);
            locationFetcherBG.destroy();
        }
        else{
            if(Utils.compareDouble(LocationFetcher.getSavedLatFromSP(this), 0) != 0
                    && Utils.compareDouble(LocationFetcher.getSavedLngFromSP(this), 0) != 0 ){

                double latitude  = LocationFetcher.getSavedLatFromSP(this);
                double longitude  = LocationFetcher.getSavedLngFromSP(this);
                latLng = new LatLng(latitude, longitude);

                getNearestDriver();
                Log.v("sticky latlng", "--> " + latitude + ", " + longitude);
                //chatheadView.setVisibility(View.VISIBLE);
                locationFetcherBG.destroy();

            }
        }

        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        FlurryAgent.onEndSession(this);
        Prefs.with(this).save("remove_chat_head", packageName);
        Log.v("onDestroy service", "onDestroy service "+Prefs.with(this).getString("remove_chat_head", ""));
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

        try{
            locationFetcherBG.destroy();
        } catch(Exception e){}


        try{
            windowManager.removeView(relativeLayoutJeaniePopup);
        } catch(Exception e){
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*@Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v("Running", String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                event.getClassName(), event.getPackageName(),
                event.getEventTime()));
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {

    }*/

    /*String TAG = "IntroPage";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.e(TAG, "NOTIFICATION!!");
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.e(TAG, "AccessibilityService Connected");
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "myAccessibilityService onInterrupt Called");
    }*/
    public static Dialog dialog;
    public static void alertPopup(Context activity) {
        try {
            //dismissAlertPopup();
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            //dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_genie_layout);

            FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            /*if(dialogCancelListener != null) {
                dialog.setOnCancelListener(dialogCancelListener);
            }

            TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
            textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            TextView textMessage = (TextView) dialog
                    .findViewById(R.id.textMessage);
            textMessage.setTypeface(Fonts.latoRegular(activity));

            textMessage.setMovementMethod(new ScrollingMovementMethod());
            textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));

            textHead.setText(title);
            textMessage.setText(message);

            if(showTitle){
                textHead.setVisibility(View.VISIBLE);
            }
            else{
                textHead.setVisibility(View.GONE);
            }

            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
            if(!"".equalsIgnoreCase(okText)){
                btnOk.setText(okText);
            }

            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Fonts.latoRegular(activity));
            if(!"".equalsIgnoreCase(canceltext)){
                btnCancel.setText(canceltext);
            }

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listenerPositive.onClick(view);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listenerNegative.onClick(v);
                }
            });


            dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });


            dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(cancelable){
                        dismissAlertPopup();
                    }
                }
            });*/

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

