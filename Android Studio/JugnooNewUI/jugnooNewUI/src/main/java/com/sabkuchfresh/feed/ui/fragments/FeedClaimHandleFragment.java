package com.sabkuchfresh.feed.ui.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public final class FeedClaimHandleFragment extends FeedBaseFragment implements GAAction {

    @Bind(R.id.edt_claim_handle)
    EditText edtClaimHandle;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.tvClaimHandle)
    TextView tvClaimHandle;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.llMain)
    LinearLayout llMain;
    @Bind(R.id.layout_suggestions)
    RelativeLayout layoutHandleSuggestions;
    @Bind(R.id.tv_suggestion_1)
    TextView tvSuggestion1;
    @Bind(R.id.tv_suggestion_2)
    TextView tvSuggestion2;
    @Bind(R.id.tv_suggestion_3)
    TextView tvSuggestion3;
    @Bind(R.id.label_suggestions)
    TextView labelSuggestions;
    private ApiCommon<HandleSuggestionsResponse> handleSuggestionsAPI;
    private Runnable  scrollDownRunnable = new Runnable() {
        @Override
        public void run() {
            if(sv!=null){
                sv.fullScroll(View.FOCUS_DOWN);
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);
        ButterKnife.bind(this, rootView);
        btnReserveSpot.setEnabled(false);

        edtClaimHandle.setOnTouchListener(new View.OnTouchListener() {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_RIGHT = 2;


            if (event.getAction() == MotionEvent.ACTION_UP) {


                if (event.getRawX() >= (edtClaimHandle.getRight() - (edtClaimHandle.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() + 75))) {

                       getHandleSuggestions();
                       return true;
                }


                return false;


            }
            return false;


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



        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(llMain, null, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                sv.fullScroll(View.FOCUS_DOWN);
                activity.getFabViewTest().relativeLayoutFABTest.setVisibility(View.GONE);
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            }

            @Override
            public void keyBoardClosed() {
                activity.getFabViewTest().relativeLayoutFABTest.setVisibility(View.VISIBLE);
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);


            }
        });
        llMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        GAUtils.trackScreenView(FEED + HANDLE_INPUT);
        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FeedClaimHandleFragment.this.getView() != null) {
                    Utils.showSoftKeyboard(activity, edtClaimHandle);
                }
            }
        }, 200);

        tvSuggestion1.setOnClickListener(suggestionListener);
        tvSuggestion2.setOnClickListener(suggestionListener);
        tvSuggestion3.setOnClickListener(suggestionListener);
        getHandleSuggestions();


        return rootView;

    }

    private void resetSuggestionsState(Editable s) {
        if(s.toString().trim().length()>0){
            tvSuggestion1.setSelected(s.toString().equals(tvSuggestion1.getText().toString()));
            tvSuggestion2.setSelected(s.toString().equals(tvSuggestion2.getText().toString()));
            tvSuggestion3.setSelected(s.toString().equals(tvSuggestion3.getText().toString()));
        }
    }

    private void getHandleSuggestions() {

        if(handleSuggestionsAPI==null) {
            handleSuggestionsAPI = new ApiCommon<HandleSuggestionsResponse>(activity).putAccessToken(true).putDefaultParams(true).showLoader(false);
        }
        if(!handleSuggestionsAPI.isInProgress()){
            handleSuggestionsAPI.execute(null, ApiName.GET_HANLDE_SUGGESTIONS, new APICommonCallback<HandleSuggestionsResponse>() {
                @Override
                public boolean onNotConnected() {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onException(Exception e) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();

                    return true;
                }

                @Override
                public void onSuccess(HandleSuggestionsResponse handleSuggestionsResponse, String message, int flag) {

                    if (FeedClaimHandleFragment.this.getView()!=null && handleSuggestionsResponse.getHandleSuggestions()!=null) {

                        if(layoutHandleSuggestions.getVisibility()!=View.VISIBLE) {
                            layoutHandleSuggestions.setVisibility(View.VISIBLE);
                        }
                        for(int suggestionIndex = 0;suggestionIndex<handleSuggestionsResponse.getHandleSuggestions().size()-1;suggestionIndex++){


                            TextView viewToSet = null;
                            switch (suggestionIndex){
                                case 0:
                                    viewToSet = tvSuggestion1;
                                    break;
                                case 1:
                                    viewToSet = tvSuggestion2;
                                    break;
                                case 2:
                                    viewToSet = tvSuggestion3;
                                    break;
                            }

                            if(viewToSet!=null) {
                                if(viewToSet.getVisibility()!=View.VISIBLE) {
                                    viewToSet.setVisibility(View.VISIBLE);
                                }
                                viewToSet.setText(handleSuggestionsResponse.getHandleSuggestions().get(suggestionIndex).trim());
                            }


                            //To Refresh Suggestion State
                            resetSuggestionsState(edtClaimHandle.getText());

                            activity.getHandler().postDelayed(scrollDownRunnable,250);

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

                @Override
                public boolean onError(HandleSuggestionsResponse handleSuggestionsResponse, String message, int flag) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();

                    return true;
                }

                @Override
                public boolean onFailure(RetrofitError error) {
                    Toast.makeText(activity, R.string.error_get_handle_suggestions, Toast.LENGTH_SHORT).show();

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
        if(handleSuggestionsAPI!=null){
            handleSuggestionsAPI.setCancelled(true);
        }
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_reserve_spot)
    public void onClick() {

        if (edtClaimHandle.getText().toString().trim().length() <= 0) {
            Toast.makeText(activity, R.string.error_enter_handle, Toast.LENGTH_SHORT).show();
            return;
        }


        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_HANDLE, edtClaimHandle.getText().toString().trim());
        new ApiCommon<FeedCommonResponse>(activity).putAccessToken(true).execute(params, ApiName.SET_HANDLE_API, new APICommonCallback<FeedCommonResponse>() {
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
            public boolean onFailure(RetrofitError error) {
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
            if(v instanceof TextView){
                TextView textView = (TextView) v;
                if(textView.getText().toString().trim().length()>0) {
                    edtClaimHandle.setText(((TextView) v).getText());
                    edtClaimHandle.setSelection(edtClaimHandle.getText().toString().length());
                }
            }

        }
    };


}
