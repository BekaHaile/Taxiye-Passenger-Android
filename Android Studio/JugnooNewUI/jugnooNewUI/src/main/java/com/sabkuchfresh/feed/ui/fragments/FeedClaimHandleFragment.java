package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.models.HandleSuggestionsResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.ui.textwatchers.HandleTextWatcher;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public final class FeedClaimHandleFragment extends FeedBaseFragment implements GAAction {

    @BindView(R.id.edt_claim_handle)
    EditText edtClaimHandle;
    @BindView(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.tvClaimHandle)
    TextView tvClaimHandle;
    @BindView(R.id.sv)
    ScrollView sv;
    @BindView(R.id.llMain)
    LinearLayout llMain;
    @BindView(R.id.layout_suggestions)
    RelativeLayout layoutHandleSuggestions;
    @BindView(R.id.tv_suggestion_1)
    TextView tvSuggestion1;
    @BindView(R.id.tv_suggestion_2)
    TextView tvSuggestion2;
    @BindView(R.id.tv_suggestion_3)
    TextView tvSuggestion3;
    @BindView(R.id.label_suggestions)
    TextView labelSuggestions;
    @BindView(R.id.iv_refresh_suggestions)
    ImageView ivRefreshSuggestions;
    @BindView(R.id.sv_suggestions)
    HorizontalScrollView scrollViewSuggestions;
    private ApiCommon<HandleSuggestionsResponse> handleSuggestionsAPI;
    private RotateAnimation rotateAnimation;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;

    Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        btnReserveSpot.setEnabled(false);
        scrollViewSuggestions.setHorizontalScrollBarEnabled(false);
        ivRefreshSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!handleSuggestionsAPI.isInProgress()){
                    if (rotateAnimation==null) {
                        rotateAnimation = new RotateAnimation(0f, 360f,v.getWidth()/2,v.getHeight()/2);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setRepeatCount(Animation.INFINITE);
                        rotateAnimation.setDuration(500);
                        rotateAnimation.setFillAfter(true);
                    }

                    ivRefreshSuggestions.startAnimation(rotateAnimation);
                    getHandleSuggestions(false);
                }

            }
        });
        edtClaimHandle.addTextChangedListener(new HandleTextWatcher() {
            @Override
            public void enableSubmitButton(boolean isEnable) {
                btnReserveSpot.setEnabled(isEnable);
            }

            @Override
            public void afterTextChange(Editable s) {
                if (edtClaimHandle.isSelected()) {
                    edtClaimHandle.setSelected(false);
                    tvError.setVisibility(View.INVISIBLE);
                }

                resetSuggestionsState(s);


            }
        });


        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                if (getView()!=null) {
                    sv.fullScroll(View.FOCUS_DOWN);

                }
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            }

            @Override
            public void keyBoardClosed() {
                activity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);


            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);


        GAUtils.trackScreenView(FEED + HANDLE_INPUT);
        tvSuggestion1.setOnClickListener(suggestionListener);
        tvSuggestion2.setOnClickListener(suggestionListener);
        tvSuggestion3.setOnClickListener(suggestionListener);
        getHandleSuggestions(true);
        return rootView;

    }

    private void resetSuggestionsState(Editable s) {
        if (s.toString().trim().length() > 0) {
            tvSuggestion1.setSelected(s.toString().equals(tvSuggestion1.getText().toString()));
            tvSuggestion2.setSelected(s.toString().equals(tvSuggestion2.getText().toString()));
            tvSuggestion3.setSelected(s.toString().equals(tvSuggestion3.getText().toString()));
        }
        else{
            tvSuggestion1.setSelected(false);
            tvSuggestion2.setSelected(false);
            tvSuggestion3.setSelected(false);
        }
    }

    private HashMap<String,String> handleSuggestionParams = new HashMap<>();
    private void getHandleSuggestions(final boolean isFirstTime) {

        if (handleSuggestionsAPI == null) {
            handleSuggestionsAPI = new ApiCommon<HandleSuggestionsResponse>(activity).putAccessToken(true).putDefaultParams(true).showLoader(false);
        }

        if (!handleSuggestionsAPI.isInProgress()) {
            handleSuggestionParams.put(Constants.ALL_CRAZY,String.valueOf(isFirstTime?0:1));
            handleSuggestionsAPI.execute(handleSuggestionParams, ApiName.GET_HANLDE_SUGGESTIONS, new APICommonCallback<HandleSuggestionsResponse>() {
                @Override
                public boolean onNotConnected() {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();
                    ivRefreshSuggestions.clearAnimation();
                    return true;
                }

                @Override
                public boolean onException(Exception e) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();
                    ivRefreshSuggestions.clearAnimation();
                    return true;
                }

                @Override
                public void onSuccess(HandleSuggestionsResponse handleSuggestionsResponse, String message, int flag) {

                    if (FeedClaimHandleFragment.this.getView() != null ) {

                        if(ivRefreshSuggestions.getAnimation()!=null) {
                            ivRefreshSuggestions.getAnimation().setRepeatCount(0);
                        }
                        if (handleSuggestionsResponse.getHandleSuggestions() != null) {
                            if (layoutHandleSuggestions.getVisibility() != View.VISIBLE) {
                                layoutHandleSuggestions.setVisibility(View.VISIBLE);
                            }
                            edtClaimHandle.setText(null);
                            for (int suggestionIndex = 0; suggestionIndex < handleSuggestionsResponse.getHandleSuggestions().size() ; suggestionIndex++) {


                                TextView viewToSet = null;
                                switch (suggestionIndex) {
                                    case 0:
                                        edtClaimHandle.setText(handleSuggestionsResponse.getHandleSuggestions().get(suggestionIndex));
                                        edtClaimHandle.setSelection(edtClaimHandle.getText().toString().length());

                                        break;
                                    case 1:
                                        viewToSet = tvSuggestion1;
                                        break;
                                    case 2:
                                        viewToSet = tvSuggestion2;
                                        break;
                                    case 3:
                                        viewToSet = tvSuggestion3;
                                        break;
                                }

                                if (viewToSet != null) {
                                    if (viewToSet.getVisibility() != View.VISIBLE) {
                                        viewToSet.setVisibility(View.VISIBLE);
                                    }
                                    viewToSet.setText(handleSuggestionsResponse.getHandleSuggestions().get(suggestionIndex).trim());
                                }


                                //To Refresh Suggestion State
                                resetSuggestionsState(edtClaimHandle.getText());
                                sv.fullScroll(View.FOCUS_DOWN);



                                 /*
                                   Using Reflection
                                   try {
                                        Field field = FeedClaimHandleFragment.this.getClass().getDeclaredField("tvSuggestion" + suggestionIndex+1);
                                        TextView textView = (TextView) field.get(FeedClaimHandleFragment.this);
                                        textView.setVisibility(View.VISIBLE);
                                        textView.setText(handleSuggestionsResponse.getHandleSuggestions().get(suggestionIndex));
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
    */


                            }
                        }
                    }
                }

                @Override
                public boolean onError(HandleSuggestionsResponse handleSuggestionsResponse, String message, int flag) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();
                    ivRefreshSuggestions.clearAnimation();
                    return true;
                }

                @Override
                public boolean onFailure(Exception error) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();
                    ivRefreshSuggestions.clearAnimation();
                    return true;
                }

                @Override
                public void onNegativeClick() {

                }
            });
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handleSuggestionsAPI != null) {
            handleSuggestionsAPI.setCancelled(true);
        }
        unbinder.unbind();
    }

    @OnClick(R.id.btn_reserve_spot)
    public void onClick() {

        if (edtClaimHandle.getText().toString().trim().length() <= 0) {
            Toast.makeText(activity, R.string.error_enter_handle, Toast.LENGTH_SHORT).show();
            return;
        }


        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_HANDLE, edtClaimHandle.getText().toString().trim());
         new ApiCommon<>(activity).putAccessToken(true).execute(params, ApiName.SET_HANDLE_API, new APICommonCallback<FeedCommonResponse>() {
            @Override
            public boolean onNotConnected() {
                return false;
            }

            @Override
            public boolean onException(Exception e) {
                return false;
            }

            @Override
            public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {
                ivRefreshSuggestions.clearAnimation();
                ivRefreshSuggestions.setVisibility(View.GONE);
                Utils.hideKeyboard(activity);
                Data.getFeedData().setHasHandle(1);
                edtClaimHandle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_green_vector, 0);
                edtClaimHandle.setActivated(true);
                tvError.setVisibility(View.INVISIBLE);
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.performSuperBackPress();
                        activity.addFeedFragment();

                    }
                }, 1000);

            }

            @Override
            public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                tvError.setText(message);
                tvError.setVisibility(View.VISIBLE);
                edtClaimHandle.setSelected(true);
                return true;
            }

            @Override
            public boolean onFailure(Exception error) {
                return false;
            }

            @Override
            public void onNegativeClick() {

            }
        });

    }

    View.OnClickListener suggestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                if (textView.getText().toString().trim().length() > 0) {
                    edtClaimHandle.setText(((TextView) v).getText());
                    edtClaimHandle.setSelection(edtClaimHandle.getText().toString().length());
                }
            }

        }
    };


}
