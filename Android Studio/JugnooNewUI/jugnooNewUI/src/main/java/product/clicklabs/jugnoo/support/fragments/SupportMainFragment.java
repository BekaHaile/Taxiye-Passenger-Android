package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.SupportCategory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class SupportMainFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = SupportMainFragment.class.getSimpleName();

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private ImageView imageViewDriver;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotalValue;
	private TextView textViewDate, textViewStart, textViewEnd, textViewStartValue, textViewEndValue;

	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private SupportActivity activity;

	private int showPanelCalled = 0, getRideSummaryCalled = 0;
	private EndRideData endRideData;
	private GetRideSummaryResponse getRideSummaryResponse;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportMainFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_main, container, false);

        activity = (SupportActivity) getActivity();
		activity.setTitle(activity.getResources().getString(R.string.support_main_title));

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showPanelCalled = 0; getRideSummaryCalled = 0;

		linearLayoutRideShortInfo = (LinearLayout)rootView.findViewById(R.id.linearLayoutRideShortInfo);
		((TextView)rootView.findViewById(R.id.textViewIssueWithRide)).setTypeface(Fonts.mavenRegular(activity));
		textViewDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewDriverCarNumber = (TextView)rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewTripTotal)).setTypeface(Fonts.mavenLight(activity));
		textViewTripTotalValue = (TextView)rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		imageViewDriver = (ImageView) rootView.findViewById(R.id.imageViewDriver);
		textViewDate = (TextView)rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenRegular(activity));
		textViewStart = (TextView)rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.mavenRegular(activity));
		textViewEnd = (TextView)rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.mavenRegular(activity));
		textViewStartValue = (TextView)rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndValue = (TextView)rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenLight(activity));

		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						new TransactionUtils().openItemInFragment(activity, activity.getContainer(),
								-1, "", activity.getResources().getString(R.string.support_main_title), item, "");
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		linearLayoutRideShortInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (endRideData != null && getRideSummaryResponse != null) {
					new TransactionUtils().openRideIssuesFragment(activity,
							activity.getContainer(),
							Integer.parseInt(endRideData.engagementId), endRideData, getRideSummaryResponse);
				}
			}
		});

		linearLayoutRideShortInfo.setVisibility(View.GONE);
		recyclerViewSupportFaq.setVisibility(View.GONE);
		getRideSummaryAPI(activity);
		showPanel();


		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.setTitle(activity.getResources().getString(R.string.support_main_title));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
        System.gc();
	}


	private void showPanel() {
		String savedSupportVersion = Prefs.with(activity).getString(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION, "-1");
		if(savedSupportVersion.equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
			ArrayList<ShowPanelResponse.Item> menu = Database2.getInstance(activity)
					.getSupportDataItems(SupportCategory.MAIN_MENU.getOrdinal());
			showPanelSuccess(menu);
		}
		else {
			if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, "");

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

				RestClient.getApiServices().showPanel(params,
						new Callback<ShowPanelResponse>() {
							@Override
							public void success(ShowPanelResponse showPanelResponse, Response response) {
								DialogPopup.dismissLoadingDialog();
								try {
									Log.i(TAG, "showPanel reader" + new String(((TypedByteArray) response.getBody()).getBytes()));
									showPanelSuccess((ArrayList<ShowPanelResponse.Item>) showPanelResponse.getMenu());
									Database2.getInstance(activity)
											.insertUpdateSupportData(SupportCategory.MAIN_MENU.getOrdinal(),
													showPanelResponse.getMenu());
									Prefs.with(activity).save(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION,
											Data.userData.getInAppSupportPanelVersion());
								} catch (Exception exception) {
									exception.printStackTrace();
									retryDialog(DialogErrorType.SERVER_ERROR);
								}
							}

							@Override
							public void failure(RetrofitError error) {
								Log.e(TAG, "showPanel error=>" + error);
								DialogPopup.dismissLoadingDialog();
								recyclerViewSupportFaq.setVisibility(View.GONE);
								showPanelCalled = -1;
								retryDialog(DialogErrorType.CONNECTION_LOST);
							}
						});
			} else {
				retryDialog(DialogErrorType.NO_NET);
			}
		}
	}

	private void showPanelSuccess(ArrayList<ShowPanelResponse.Item> menu){
		recyclerViewSupportFaq.setVisibility(View.VISIBLE);
		showPanelCalled = 1;
		update(menu);
	}


	public void getRideSummaryAPI(final Activity activity) {
		new ApiGetRideSummary(activity, Data.userData.accessToken, -1, Data.fareStructure.fixedFare,
				new ApiGetRideSummary.Callback() {
					@Override
					public void onSuccess(EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
						SupportMainFragment.this.endRideData = endRideData;
						SupportMainFragment.this.getRideSummaryResponse = getRideSummaryResponse;
						setRideData();
						linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
						getRideSummaryCalled = 1;
					}

					@Override
					public boolean onActionFailed(String message) {
						linearLayoutRideShortInfo.setVisibility(View.GONE);
						getRideSummaryCalled = 1;
						return false;
					}

					@Override
					public void onFailure() {
						getRideSummaryCalled = -1;
						linearLayoutRideShortInfo.setVisibility(View.GONE);
					}

					@Override
					public void onRetry(View view) {
						hitRetry();
					}

					@Override
					public void onNoRetry(View view) {

					}
				}).getRideSummaryAPI();
	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						hitRetry();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {

					}
				});
	}

	private void hitRetry(){
		if(showPanelCalled != 1) {
			showPanel();
		}
		if(getRideSummaryCalled != 1){
			getRideSummaryAPI(activity);
		}
	}

	private void update(ArrayList<ShowPanelResponse.Item> menu){
		supportFAQItemsAdapter.setResults(menu);
	}

	private void setRideData(){
		try{
			if(endRideData != null){
				textViewDriverName.setText(endRideData.driverName);
				textViewDriverCarNumber.setText(endRideData.driverCarNumber);

				textViewStartValue.setText(endRideData.pickupAddress);
				textViewEndValue.setText(endRideData.dropAddress);

				textViewStart.append(" " + endRideData.pickupTime);
				textViewEnd.append(" " + endRideData.dropTime);

				if("".equalsIgnoreCase(endRideData.getTripTotal())){
					textViewTripTotalValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
							Utils.getMoneyDecimalFormat().format(endRideData.fare)));
				} else{
					textViewTripTotalValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
							endRideData.getTripTotal()));
				}

				if(!"".equalsIgnoreCase(endRideData.driverImage)){
					Picasso.with(activity).load(endRideData.driverImage).transform(new CircleTransform()).into(imageViewDriver);
				}

				textViewDate.setText(String.format(activity.getResources().getString(R.string.date_colon_format),
						endRideData.getRideDate()));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}



}
