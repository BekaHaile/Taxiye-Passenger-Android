package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiFeedNotificationUpdate;
import com.sabkuchfresh.commoncalls.ApiFeedNotifications;
import com.sabkuchfresh.feed.models.FeedNotificationsResponse;
import com.sabkuchfresh.feed.models.NotificationDatum;
import com.sabkuchfresh.feed.ui.adapters.FeedNotificationsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;


public class FeedNotificationsFragment extends Fragment implements GACategory, GAAction {


	@Bind(R.id.rvNotifications)
	RecyclerView rvNotifications;
	FeedNotificationsAdapter notificationsAdapter;

	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@Bind(R.id.llNoNotifications)
	LinearLayout llNoNotifications;

	FreshActivity activity;
	private ArrayList<NotificationDatum> notificationData;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feed_notifications, container, false);
		ButterKnife.bind(this, rootView);
		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		notificationData = new ArrayList<>();


		swipeRefreshLayout.setColorSchemeResources(R.color.white);
		swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				fetchFeedNotifications();
			}
		});

		rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvNotifications.setHasFixedSize(false);
		rvNotifications.setItemAnimator(new DefaultItemAnimator());
		notificationsAdapter = new FeedNotificationsAdapter(activity, notificationData, rvNotifications,
				new FeedNotificationsAdapter.Callback() {
			@Override
			public void onNotificationClick(int position, NotificationDatum notificationDatum) {
				if(!notificationDatum.isRead()) {
					updateFeedNotification(notificationDatum.getNotificationId());
				}
				// TODO: 03/04/17 open feed details fragment
				FeedDetail feedDetail = new FeedDetail();
				feedDetail.setPostId(notificationDatum.getPostId());
				activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, -1);
			}
		});
		rvNotifications.setAdapter(notificationsAdapter);

		fetchFeedNotifications();

		GAUtils.trackScreenView(FEED + NOTIFICATIONS);
		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}


	private ApiFeedNotifications apiFeedNotifications;
	public void fetchFeedNotifications(){
		if(apiFeedNotifications == null){
			apiFeedNotifications = new ApiFeedNotifications(activity, new ApiFeedNotifications.Callback() {
				@Override
				public void onSuccess(FeedNotificationsResponse feedNotificationsResponse) {
					notificationData.clear();
					notificationData.addAll(feedNotificationsResponse.getNotificationData());
					notificationsAdapter.notifyDataSetChanged();
					if(notificationData.size() == 0){
						llNoNotifications.setVisibility(View.VISIBLE);
						rvNotifications.setVisibility(View.GONE);
					} else {
						llNoNotifications.setVisibility(View.GONE);
						rvNotifications.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onFailure() {
				}

				@Override
				public void onRetry(View view) {
					fetchFeedNotifications();
				}

				@Override
				public void onNoRetry(View view) {

				}
			});
		}
		apiFeedNotifications.hit(swipeRefreshLayout);
	}



	private ApiFeedNotificationUpdate apiFeedNotificationUpdate;
	public void updateFeedNotification(int notificationId){
		if(apiFeedNotificationUpdate == null){
			apiFeedNotificationUpdate = new ApiFeedNotificationUpdate();
		}
		apiFeedNotificationUpdate.hit(notificationId);
	}

}
