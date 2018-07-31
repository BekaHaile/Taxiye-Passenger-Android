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
import com.sabkuchfresh.commoncalls.ApiFeedNotifications;
import com.sabkuchfresh.feed.models.FeedNotificationsResponse;
import com.sabkuchfresh.feed.models.NotificationDatum;
import com.sabkuchfresh.feed.ui.adapters.FeedNotificationsAdapter;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.R;


public class FeedNotificationsFragment extends Fragment implements GACategory, GAAction {


	@BindView(R.id.rvNotifications)
	RecyclerView rvNotifications;
	FeedNotificationsAdapter notificationsAdapter;

	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R.id.llNoNotifications)
	LinearLayout llNoNotifications;

	FreshActivity activity;
	private ArrayList<NotificationDatum> notificationData;

	Unbinder unbinder;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feed_notifications, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		notificationData = new ArrayList<>();


		swipeRefreshLayout.setColorSchemeResources(R.color.white);
		swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
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
				int postNotificationId;
				if(!notificationDatum.isRead()) {
					notificationDatum.setIsRead(1);
					postNotificationId = notificationDatum.getNotificationId();
				} else {
					postNotificationId = -1;
				}
				activity.openFeedDetailsFragmentWithPostId(notificationDatum.getPostId(), postNotificationId);
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
			notificationsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
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

					activity.getFeedHomeFragment().setNotificationsSeenCount(0);
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

}
