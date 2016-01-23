package product.clicklabs.jugnoo.support.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.ViewType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SupportMainFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotalValue;
	private TextView textViewDate, textViewStart, textViewEnd, textViewStartValue, textViewEndValue;

	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;
	private ShowPanelResponse showPanelResponse;

	private View rootView;
    private SupportActivity activity;

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

		linearLayoutRideShortInfo = (LinearLayout)rootView.findViewById(R.id.linearLayoutRideShortInfo);
		((TextView)rootView.findViewById(R.id.textViewIssueWithRide)).setTypeface(Fonts.mavenRegular(activity));
		textViewDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewDriverCarNumber = (TextView)rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewTripTotal)).setTypeface(Fonts.mavenLight(activity));
		textViewTripTotalValue = (TextView)rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		textViewDate = (TextView)rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenRegular(activity));
		textViewStart = (TextView)rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.mavenRegular(activity));
		textViewEnd = (TextView)rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.mavenRegular(activity));
		textViewStartValue = (TextView)rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndValue = (TextView)rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenLight(activity));

		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						if(ViewType.TEXT_BOX.getOrdinal() == item.getViewType()
								|| ViewType.CALL_BUTTON.getOrdinal() == item.getViewType()
								|| ViewType.TEXT_ONLY.getOrdinal() == item.getViewType()) {
							activity.getSupportFragmentManager().beginTransaction()
									.add(activity.getLinearLayoutContainer().getId(),
											new SupportFAQItemFragment(item.getText(), item), SupportFAQItemFragment.class.getName())
									.addToBackStack(SupportFAQItemFragment.class.getName())
									.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
											.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
									.commitAllowingStateLoss();
						} else if(ViewType.LIST_VIEW.getOrdinal() == item.getViewType()) {
							activity.getSupportFragmentManager().beginTransaction()
									.add(activity.getLinearLayoutContainer().getId(),
											new SupportFAQItemsListFragment(item), SupportFAQItemsListFragment.class.getName())
									.addToBackStack(SupportFAQItemsListFragment.class.getName())
									.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
											.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
									.commitAllowingStateLoss();
						}
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		linearLayoutRideShortInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

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
		if(!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "");

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

			RestClient.getApiServices().showPanel(params,
					new Callback<ShowPanelResponse>() {
						@Override
						public void success(ShowPanelResponse showPanelResponse, Response response) {
							DialogPopup.dismissLoadingDialog();
							try {
								update(showPanelResponse);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}

						@Override
						public void failure(RetrofitError error) {
							DialogPopup.dismissLoadingDialog();
						}
					});
		} else{
			DialogPopup.dialogNoInternet(activity,
					activity.getResources().getString(R.string.no_net_title),
					activity.getResources().getString(R.string.no_net_text),
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick() {
							showPanel();
						}

						@Override
						public void neutralClick() {

						}

						@Override
						public void negativeClick() {

						}
					});
		}
	}

	private void update(ShowPanelResponse showPanelResponse){
		this.showPanelResponse = showPanelResponse;
		supportFAQItemsAdapter.setResults((ArrayList<ShowPanelResponse.Item>) this.showPanelResponse.getMenu());
	}



}
