package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.SplashNewActivity;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.ReferralResponse;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.JSONParser;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.ReferralActions;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ReferralsFragment extends Fragment implements Constants, FlurryEventNames {

    private RelativeLayout relativeLayoutRoot;

    private ImageView imageViewLogo, imageViewMore, imageViewFbMessanger, imageViewWhatsapp, imageViewMessage, imageViewEmail;
    private TextView textViewCode, textViewDesc, textViewMoreInfo;
    private Button buttonInvite;

    private View rootView;
    private SupportActivity activity;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_referrals, container, false);


        activity = (SupportActivity) getActivity();

        relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
        try {
            if (relativeLayoutRoot != null) {
                new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
//				FlurryEventLogger.eventGA(Constants.REFERRAL, "free rides", "Earn");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewLogo = (ImageView) rootView.findViewById(R.id.imageViewLogo);
        ((TextView) rootView.findViewById(R.id.textViewShare)).setTypeface(Fonts.mavenRegular(activity));
        textViewDesc = (TextView) rootView.findViewById(R.id.textViewDesc);
        textViewDesc.setTypeface(Fonts.mavenRegular(activity));
        textViewMoreInfo = (TextView) rootView.findViewById(R.id.textViewMoreInfo);
        textViewMoreInfo.setTypeface(Fonts.mavenRegular(activity));

        textViewCode = (TextView) rootView.findViewById(R.id.textViewCode);
        textViewCode.setTypeface(Fonts.latoRegular(activity));
        buttonInvite = (Button) rootView.findViewById(R.id.buttonInvite);
        buttonInvite.setTypeface(Fonts.mavenRegular(activity));

        imageViewMore = (ImageView) rootView.findViewById(R.id.imageViewMore);
        imageViewFbMessanger = (ImageView) rootView.findViewById(R.id.imageViewFbMessanger);
        imageViewWhatsapp = (ImageView) rootView.findViewById(R.id.imageViewWhatsapp);
        imageViewMessage = (ImageView) rootView.findViewById(R.id.imageViewMessage);
        imageViewEmail = (ImageView) rootView.findViewById(R.id.imageViewEmail);

        try {
            if(Data.userData.getInviteFriendButton() == 1){
                buttonInvite.setVisibility(View.VISIBLE);
            } else{
                buttonInvite.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
					FlurryEventLogger.event(REFER_SCREEN, INVITE, "Details");
                    DialogPopup.alertPopupWithListener(activity, "", ""+Data.userData.referralDescription, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (AppStatus.getInstance(activity).isOnline(activity)) {
                    FlurryEventLogger.event(REFER_SCREEN, INVITE, INVITE);
                    shareApplication();
                } else {
                    DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
                }*/

                if(AppStatus.getInstance(activity).isOnline(activity)) {
                    FlurryEventLogger.event(REFER_SCREEN, INVITE, INVITE);
                    ReferralActions.openGenericShareIntent(activity, activity.getCallbackManager());

                } else{
                    DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
                }
            }
        });

        imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInvite.performClick();
                FlurryEventLogger.event(REFER_SCREEN, INVITE, "Others");
            }
        });

        imageViewFbMessanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferralActions.shareToFacebook(activity, true, activity.getCallbackManager());
                FlurryEventLogger.event(REFER_SCREEN, INVITE, "Facebook");
            }
        });

        imageViewWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferralActions.shareToWhatsapp(activity);
                FlurryEventLogger.event(REFER_SCREEN, INVITE, "WhatsApp");
            }
        });

        imageViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferralActions.sendSMSIntent(activity);
                FlurryEventLogger.event(REFER_SCREEN, INVITE, "Message");
            }
        });

        imageViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferralActions.openMailIntent(activity);
                FlurryEventLogger.event(REFER_SCREEN, INVITE, "Email");
            }
        });


        try {
            textViewCode.setText(Data.userData.referralCode);
            //textViewDesc.setText(Data.userData.referralShortDesc);
            textViewDesc.setText(Data.userData.referralShortDesc+ "Details");

            SpannableString ss = new SpannableString(Data.userData.referralShortDesc+" Details");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    textViewMoreInfo.performClick();
                }
            };
            ss.setSpan(clickableSpan, (textViewDesc.getText().length() - 6), textViewDesc.getText().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textViewDesc.setText(ss);
            textViewDesc.setMovementMethod(LinkMovementMethod.getInstance());

            if (!"".equalsIgnoreCase(Data.userData.referralBanner)) {
                Picasso.with(activity).load(Data.userData.referralBanner)
                        .placeholder(R.drawable.ic_promotions_friend_refer)
                        .error(R.drawable.ic_promotions_friend_refer)
                        .into(imageViewLogo);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        FlurryEventLogger.event(REFER_SCREEN);
//        getReferralCall();

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
        System.gc();
    }

    /*private void shareApplication() {
        DialogPopup.showLoadingDialog(activity, "Loading...");
        BranchMetricsUtils branchMetricsUtils = new BranchMetricsUtils(getActivity(), new BranchMetricsUtils.BranchMetricsEventHandler() {
            @Override
            public void onBranchLinkCreated(String link) {
                DialogPopup.dismissLoadingDialog();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Data.userData.referralShareTitle);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Data.userData.referralShareText+"\n\n"+link);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_fragment)));
//                startActivity(sendIntent);
            }

            @Override
            public void onBranchError(String error) {
                DialogPopup.dismissLoadingDialog();

            }

            @Override
            public void onCompleteDialog() {
                DialogPopup.dismissLoadingDialog();
            }
        });

        HashMap<String, String> hashMap  = new HashMap<>(2);
        hashMap.put(BRANCH_USER_ID, Data.userData.getUserId());
        if(BuildConfig.DEBUG_MODE) {
            hashMap.put(KEY_REFERRAL_CODE, "GURMAIL" + Data.userData.referralCode);
        } else {
            hashMap.put(KEY_REFERRAL_CODE, Data.userData.referralCode);
        }

        String title = "Title";//getActivity().getResources().getString(R.string.share_title);
        String message = "Message";//getActivity().getResources().getString(R.string.share_message);

        branchMetricsUtils.getBranchLinkForChannel("Generic", hashMap, "", title, message);
    }*/

    public void getReferralCall() {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {
//                if(Data.userData.getReferralLeaderboardEnabled() == 1) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                    RestClient.getFreshApiService().referServerCall(params,
                            new Callback<ReferralResponse>() {
                                @Override
                                public void success(ReferralResponse leaderboardResponse, Response response) {
                                    DialogPopup.dismissLoadingDialog();
                                    try {
                                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                        JSONObject jObj;
                                        jObj = new JSONObject(jsonString);
                                        int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                                        String message = JSONParser.getServerMessage(jObj);
                                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                                Log.v("success at", "leaderboeard");
//                                                ShareActivity.this.leaderboardResponse = leaderboardResponse;
//                                                updateFragment(2);
                                            } else {
                                                retryLeaderboardDialog(message);
                                            }
                                        }
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                        retryLeaderboardDialog(Data.SERVER_ERROR_MSG);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    DialogPopup.dismissLoadingDialog();
                                    retryLeaderboardDialog(Data.SERVER_NOT_RESOPNDING_MSG);
                                }
                            });
//                }
            } else {
                //retryLeaderboardDialog(Data.CHECK_INTERNET_MSG);
                DialogPopup.dialogNoInternet(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View v) {
                        getReferralCall();
                    }

                    @Override
                    public void neutralClick(View v) {

                    }

                    @Override
                    public void negativeClick(View v) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retryLeaderboardDialog(String message){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                getResources().getString(R.string.retry),
                getResources().getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getReferralCall();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                }, true, false);
    }

}
