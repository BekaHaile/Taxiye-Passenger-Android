package com.sabkuchfresh.feed.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;


public class FeedSpotReservedSharingFragment extends Fragment implements GACategory, GAAction, View.OnClickListener {


	@Bind(R.id.tvRankDescription)
	TextView tvRankDescription;
	@Bind(R.id.tvWantEarlyAccessMessage)
	TextView tvWantEarlyAccessMessage;
	@Bind(R.id.tvShareFB)
	TextView tvShareFB;
	@Bind(R.id.tvShareTweet)
	TextView tvShareTweet;
	@Bind(R.id.tvShareEmail)
	TextView tvShareEmail;
	@Bind(R.id.tvShareWhatsapp)
	TextView tvShareWhatsapp;
	@Bind(R.id.tvMoreSharingOptions)
	TextView tvMoreSharingOptions;
	@Bind(R.id.tvWantEarlyAccess)
	TextView tvWantEarlyAccess;

	FreshActivity activity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feed_spot_reserved_sharing, container, false);
		ButterKnife.bind(this, rootView);
		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		long feedRank = 0;
		if(Data.getFeedData() != null && Data.getFeedData().getFeedRank() != null){
			feedRank = Data.getFeedData().getFeedRank() - 1;
		}
		if(feedRank > 0) {
			SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(feedRank));
			ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			tvRankDescription.setText(ssb);
			tvRankDescription.append(" ");
			tvRankDescription.append(activity.getString(R.string.people_ahead_of_you_in_queue));
		} else {
			tvRankDescription.setText(activity.getString(R.string.you_are_first_in_queue));
		}

		if(Data.getFeedData() != null && !TextUtils.isEmpty(Data.getFeedData().getEarlyAccessText())) {
			tvWantEarlyAccessMessage.setText(Utils.trimHTML(Utils.fromHtml(Data.getFeedData().getEarlyAccessText())));
		}

		Utils.setTextUnderline(tvMoreSharingOptions, activity.getString(R.string.view_more_sharing_options));

		tvShareFB.setOnClickListener(this);
		tvShareTweet.setOnClickListener(this);
		tvShareEmail.setOnClickListener(this);
		tvShareWhatsapp.setOnClickListener(this);
		tvMoreSharingOptions.setOnClickListener(this);
		tvWantEarlyAccess.setTypeface(tvWantEarlyAccess.getTypeface(), Typeface.BOLD);

		GAUtils.trackScreenView(FEED + HOME + WAITLIST + SHARING);

		return rootView;
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onClick(final View v) {
		// for sharing actions only, don't add other onCLicks in this switch
		if (MyApplication.getInstance().isOnline()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constants.KEY_DEEPINDEX, String.valueOf(AppLinkIndex.FEED_PAGE.getOrdinal()));
			BranchMetricsUtils.getBranchLink(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
						@Override
						public void onBranchLinkCreated(String link) {
							String appType = "";
							switch (v.getId()) {
								case R.id.tvShareFB:
									ReferralActions.shareToFacebookDialog(activity,
											Data.getFeedData().getEarlyAccessShareTitle(),
											Data.getFeedData().getEarlyAccessShareDesc(), link,
											Data.getFeedData().getEarlyAccessShareImage());
									GAUtils.event(FEED, WAITLIST+SHARING, FACEBOOK+CLICKED);
									return;

								case R.id.tvShareTweet:
									appType = ReferralActions.TWITTER;
									GAUtils.event(FEED, WAITLIST+SHARING, TWITTER+CLICKED);
									break;

								case R.id.tvShareEmail:
									appType = ReferralActions.EMAIL;
									GAUtils.event(FEED, WAITLIST+SHARING, EMAIL+CLICKED);
									break;

								case R.id.tvShareWhatsapp:
									appType = ReferralActions.WHATSAPP;
									GAUtils.event(FEED, WAITLIST+SHARING, WHATSAPP+CLICKED);
									break;

								case R.id.tvMoreSharingOptions:
									GAUtils.event(FEED, WAITLIST+SHARING, OTHER_SHARING_OPTIONS+CLICKED);
									break;
							}
							ReferralActions.shareIntent(activity,
									Data.getFeedData().getEarlyAccessShareTitle(),
									Data.getFeedData().getEarlyAccessShareDesc()+" "+link, appType);
						}

						@Override
						public void onBranchError(String error) {
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
						}
					},
					Data.getFeedData().getEarlyAccessShareTitle(),
					Data.getFeedData().getEarlyAccessShareDesc(),
					Data.getFeedData().getEarlyAccessShareImage(),
					BranchMetricsUtils.BRANCH_CHANNEL_FEED_EARLY_ACCESS,
					map);
		} else {
			DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
		}
	}

}
