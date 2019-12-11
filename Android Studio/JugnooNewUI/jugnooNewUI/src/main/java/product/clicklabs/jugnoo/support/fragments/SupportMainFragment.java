package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.ParseUtils;
import product.clicklabs.jugnoo.support.RideOrderShortView;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.SupportCategory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class SupportMainFragment extends Fragment implements  Constants, GAAction, GACategory {

	private final String TAG = SupportMainFragment.class.getSimpleName();

	private LinearLayout root;

	private CardView cardViewRideShortInfo;
	private RideOrderShortView rideOrderShortView;

	private CardView cardViewRecycler;
	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private SupportActivity activity;

	private int showPanelCalled = 0, getRideSummaryCalled = 0;

    @Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//        FlurryAgent.onEvent(SupportMainFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_main, container, false);

        activity = (SupportActivity) getActivity();
		activity.setTitle(MyApplication.getInstance().ACTIVITY_NAME_SUPPORT);

		Data.isSupportRideIssueUpdated = false;

		GAUtils.trackScreenView(SUPPORT);

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showPanelCalled = 0; getRideSummaryCalled = 0;

		cardViewRideShortInfo = (CardView) rootView.findViewById(R.id.cardViewRideShortInfo);
		rideOrderShortView = new RideOrderShortView(activity, rootView, true);

		cardViewRecycler = (CardView) root.findViewById(R.id.cvRoot);
		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						new TransactionUtils().openItemInFragment(activity, activity.getContainer(),
								-1, "", activity.getResources().getString(R.string.support_main_title), item, "", -1, "", "", ProductType.NOT_SURE.getOrdinal());

						GAUtils.event(SIDE_MENU, SUPPORT, item.getText());
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		cardViewRideShortInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openSupportRideIssuesFragment(EngagementStatus.ENDED.getOrdinal());
			}
		});

		cardViewRideShortInfo.setVisibility(View.GONE);
		cardViewRecycler.setVisibility(View.GONE);
		showPanel();
		activity.getRideSummaryAPI(activity, ProductType.NOT_SURE, EngagementStatus.ENDED.getOrdinal());



		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.setTitle(MyApplication.getInstance().ACTIVITY_NAME_SUPPORT);
			if(Data.isSupportRideIssueUpdated) {
				Data.isSupportRideIssueUpdated = false;
				activity.getRideSummaryAPI(activity, ProductType.NOT_SURE, EngagementStatus.ENDED.getOrdinal());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
        System.gc();
	}


	private void showPanel() {
		try {
			String savedSupportVersion = Prefs.with(activity).getString(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION, "-1");
			if(savedSupportVersion.equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
				ArrayList<ShowPanelResponse.Item> menu = MyApplication.getInstance().getDatabase2()
						.getSupportDataItems(SupportCategory.MAIN_MENU.getOrdinal());
				showPanelSuccess(menu);
			}
			else {
				if (!HomeActivity.checkIfUserDataNull(activity) && MyApplication.getInstance().isOnline()) {
					DialogPopup.showLoadingDialog(activity, "");

					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

					new HomeUtil().putDefaultParams(params);
					RestClient.getApiService().showPanel(params,
							new Callback<ShowPanelResponse>() {
								@Override
								public void success(ShowPanelResponse showPanelResponse, Response response) {
									DialogPopup.dismissLoadingDialog();
									try {
										Log.i(TAG, "showPanel reader" + new String(((TypedByteArray) response.getBody()).getBytes()));
										ArrayList<ShowPanelResponse.Item> itemsMain = new ParseUtils()
												.saveAndParseAllMenu(activity, showPanelResponse, SupportCategory.MAIN_MENU.getOrdinal());
										if(itemsMain != null) {
											showPanelSuccess(itemsMain);
										}
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
									cardViewRecycler.setVisibility(View.GONE);
									showPanelCalled = -1;
									retryDialog(DialogErrorType.CONNECTION_LOST);
								}
							});
				} else {
					retryDialog(DialogErrorType.NO_NET);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showPanelSuccess(ArrayList<ShowPanelResponse.Item> menu){
		showPanelCalled = 1;
		if(menu != null && menu.size() > 0){
			cardViewRecycler.setVisibility(View.VISIBLE);
		} else{
			cardViewRecycler.setVisibility(View.GONE);
		}
		update(menu);
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
			activity.getRideSummaryAPI(activity, ProductType.NOT_SURE, EngagementStatus.ENDED.getOrdinal());
		}
	}

	private void update(ArrayList<ShowPanelResponse.Item> menu){
		supportFAQItemsAdapter.setResults(menu);
	}

	private void setRideData(){
		try{
			rideOrderShortView.updateData(activity.getEndRideData(), activity.getDatum());
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void updateSuccess(){
		setRideData();
		cardViewRideShortInfo.setVisibility(View.VISIBLE);
		getRideSummaryCalled = 1;
	}

	public void updateFail(){
		cardViewRideShortInfo.setVisibility(View.GONE);
		getRideSummaryCalled = 1;
	}

}
