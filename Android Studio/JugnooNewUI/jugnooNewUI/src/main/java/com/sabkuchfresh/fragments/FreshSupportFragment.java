package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.MyApplication;
import com.sabkuchfresh.R;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.config.Config;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.DialogErrorType;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Prefs;
import com.sabkuchfresh.utils.Utils;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FreshSupportFragment extends Fragment implements FlurryEventNames {

	private final String TAG = FreshSupportFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;
	private RelativeLayout relativeLayoutCallSupport;
    private Button buttonSubmit;
	private View rootView;
    private SupportActivity activity;
    private EditText editText;
	public FreshSupportFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_support, container, false);

        editText = (EditText) rootView.findViewById(R.id.edittext_view);
        editText.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);

        buttonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);
        buttonSubmit.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);

        activity = (SupportActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewJustContact)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewCallSupport)).setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutCallSupport = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCallSupport);

		relativeLayoutCallSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                String supportContact = Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_support_contact), "");
				if(!TextUtils.isEmpty(supportContact)){
					Utils.openCallIntent(activity, supportContact);
				} else{
					Utils.openCallIntent(activity, Config.getSupportNumber(activity));
				}

			}
		});

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(Support_SCREEN, SUPPORT_QUERY_TICKET, SUPPORT_QUERY_TICKET);
                if(!TextUtils.isEmpty(editText.getText().toString().trim())) {
                    sendQuery(editText.getText().toString().trim());
                } else {
                    Toast.makeText(activity, "Please enter some text!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FlurryEventLogger.event(Support_SCREEN);

		return rootView;
	}

    private void sendQuery(String text) {
        try {
            DialogPopup.showLoadingDialog(activity, "Loading...");
            if (AppStatus.getInstance(MyApplication.getInstance()).isOnline(MyApplication.getInstance())) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.TEXT, text);

                RestClient.getFreshApiService().supportQuery(params, new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {

                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                editText.setText("");
                                Toast.makeText(activity, ""+notificationInboxResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                activity.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                new Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View view) {
                                        sendQuery(editText.getText().toString().trim());
                                    }

                                    @Override
                                    public void neutralClick(View view) {

                                    }

                                    @Override
                                    public void negativeClick(View view) {

                                    }
                                });
                    }
                });
            } else {
                DialogPopup.dialogNoInternet(activity,
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                        new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                sendQuery(editText.getText().toString().trim());
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
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden){
			activity.fragmentUISetup(this);
		}
	}



    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
