package com.sabkuchfresh.feed.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.CountNotificationResponse;
import com.sabkuchfresh.feed.ui.adapters.FeedHomeAdapter;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.ui.api.DeleteFeed;
import com.sabkuchfresh.feed.ui.api.LikeFeed;
import com.sabkuchfresh.feed.ui.dialogs.DeletePostDialog;
import com.sabkuchfresh.feed.ui.dialogs.DialogPopupTwoButtonCapsule;
import com.sabkuchfresh.feed.ui.dialogs.EditPostPopup;
import com.sabkuchfresh.feed.utils.BadgeDrawable;
import com.sabkuchfresh.home.FeedContactsUploadService;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.RetrofitError;


public class FeedHomeFragment extends Fragment implements GACategory, GAAction, DeletePostDialog.DeleteDialogCallback, EditPostPopup.EditPostDialogCallback {


    private FeedHomeAdapter feedHomeAdapter;
    private LikeFeed likeFeed;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayoutNotAvailable;
    private TextView textViewNothingFound;
    private RelativeLayout rlNoReviews;
    private TextView tvFeedEmpty;
    private View viewDisabledEditPostPopUp;
    private boolean updateFeedData;
    private ImageView ivNoFeeds;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressWheel pBarPagination;

    //Pagination variables
    private ApiCommon<FeedListResponse> feedPagingApi;
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageCount, countRecords, maxPageCount;

    public FeedHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FreshActivity) {
            activity = (FreshActivity) context;
            activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_CONTACTS_UPLOADED));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activity != null) {
            activity.unregisterReceiver(broadcastReceiver);
            activity.getHandler().removeCallbacks(runnableNotificationCount);
        }
    }

    private FreshActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity.fragmentUISetup(this);
        setHasOptionsMenu(true);
        GAUtils.trackScreenView(FEED + HOME);
        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);
//        pBarPagination= (ProgressWheel) rootView.findViewById(R.id.pBar_pagination);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeedsApi(false, false);

            }
        });

        ivNoFeeds = (ImageView) rootView.findViewById(R.id.ivNoFeeds);

        feedHomeAdapter = new FeedHomeAdapter(getActivity(), getAdapterList(false, null, null, null, pageCount), recyclerView, new FeedHomeAdapter.FeedPostCallback() {
            @Override
            public void onLikeClick(FeedDetail feedDetail, final int position) {
                if (!swipeRefreshLayout.isRefreshing()) {

                    if (likeFeed == null)
                        likeFeed = new LikeFeed(new LikeFeed.LikeUnLikeCallbackResponse() {
                            @Override
                            public void onSuccess(boolean isLiked, int position, FeedDetail feedDetail) {
                                if (getView() != null && feedHomeAdapter != null) {

                                    feedHomeAdapter.notifyOnLike(position, isLiked);
                                }
                            }

                            @Override
                            public void onFailure(boolean isLiked, int posInOriginalList, FeedDetail feedDetail) {
                                if (feedDetail != null) {
                                    feedDetail.setIsLikeAPIInProgress(false);
                                }
                            }
                        });
                    likeFeed.likeFeed(feedDetail.getPostId(), getActivity(), !feedDetail.isLiked(), position, feedDetail);
                    GAUtils.event(FEED, HOME, LIKE + CLICKED);
                }

            }

            @Override
            public void onCommentClick(final FeedDetail feedDetail, int positionInOriginalList) {

                if (!swipeRefreshLayout.isRefreshing()) {
                    activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, positionInOriginalList, true);
                    GAUtils.event(FEED, HOME, COMMENT + CLICKED);

                }

            }


            @Override
            public void onRestaurantClick(int restaurantId) {
                if (restaurantId > 0) {
                    activity.fetchRestaurantMenuAPI(restaurantId);
                }
            }

            @Override
            public String getEditTextString() {
                return null;
            }

            @Override
            public void onMoreClick(final FeedDetail feedDetail, int positionInOriginalList, View moreItemView) {

                if(!swipeRefreshLayout.isRefreshing()) {
                    getEditPostDialog().show(feedDetail, moreItemView, positionInOriginalList);
                }
//                getEditPostDialog().show(feedDetail,moreItemView,positionInOriginalList);


            }

            @Override
            public void onDeleteComment(FeedComment feedComment, int position, View viewClicked) {

            }

            @Override
            public void onFeedLayoutClick(FeedDetail feedDetail, int positionInOriginalList) {
                if(!swipeRefreshLayout.isRefreshing()) {
                    activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, positionInOriginalList, false);
                }

            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        ;
        recyclerView.setAdapter(feedHomeAdapter);
        try {
            if (Data.getFeedData() != null && Data.getFeedData().getContactsSynced() != null && Data.getFeedData().getContactsSynced() == 0) {
                Intent syncContactsIntent = new Intent(activity, FeedContactsUploadService.class);
                syncContactsIntent.putExtra(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                activity.startService(syncContactsIntent);
                Data.getFeedData().setContactsSynced(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayoutNotAvailable = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        textViewNothingFound = (TextView) rootView.findViewById(R.id.textViewNothingFound);
        relativeLayoutNotAvailable.setVisibility(View.GONE);
        rlNoReviews = (RelativeLayout) rootView.findViewById(R.id.rlNoReviews);
        rlNoReviews.setVisibility(View.GONE);
        tvFeedEmpty = (TextView) rootView.findViewById(R.id.tvFeedEmpty);
        try {
            tvFeedEmpty.setText("Feed is empty!");
            SpannableStringBuilder ssb = new SpannableStringBuilder(activity.getString(R.string.be_first_one_to_add_a_post));
            ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvFeedEmpty.append("\n");
            tvFeedEmpty.append(ssb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);

        activity.getHandler().removeCallbacks(runnableNotificationCount);
        activity.getHandler().postDelayed(runnableNotificationCount, 1000);


        // To check if user has clicked on some post id's push notification from sp_post_id_to_open
        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int postIdToOpen = Prefs.with(activity).getInt(Constants.SP_POST_ID_TO_OPEN, -1);
                    if (postIdToOpen != -1) {
                        activity.openFeedDetailsFragmentWithPostId(postIdToOpen);
                    }
                    Prefs.with(activity).save(Constants.SP_POST_ID_TO_OPEN, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 50);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isPagingApiInProgress && pageCount < maxPageCount) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            fetchNextPage();
                            ;
                        }
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            activity.getFeedHomeAddPostView().setVisibility(relativeLayoutNotAvailable.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (updateFeedData) {
                        fetchFeedsApi(true, false);
                    }
                    updateFeedData = false;
                }
            }, 200);
            activity.getHandler().removeCallbacks(runnableNotificationCount);
            activity.getHandler().postDelayed(runnableNotificationCount, 1000);
        } else {
            activity.getHandler().removeCallbacks(runnableNotificationCount);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getHandler().removeCallbacks(runnableNotificationCount);
        activity.getHandler().postDelayed(runnableNotificationCount, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.getHandler().removeCallbacks(runnableNotificationCount);
    }


    public void fetchFeedsApi(boolean loader, final boolean scrollToTop) {
        countRecords = 0;
        pageCount = 1;
        if (feedPagingApi != null) {
            feedPagingApi.setCancelled(true);
        }
//        pBarPagination.setVisibility(View.GONE);
        toggleProgressBarVisibility(false);
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
        params.put(Constants.PAGE_COUNT, String.valueOf(pageCount));
        params.put(Constants.COUNT_RECORDS, String.valueOf(countRecords));
        new HomeUtil().putDefaultParams(params);

        new ApiCommon<FeedListResponse>(activity).showLoader(loader).execute(params, ApiName.GENERATE_FEED_API, new APICommonCallback<FeedListResponse>() {
            @Override
            public boolean onNotConnected() {
                swipeRefreshLayout.setRefreshing(false);
                return false;
            }

            @Override
            public boolean onException(Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                return false;
            }

            @Override
            public void onSuccess(FeedListResponse feedbackResponse, String message, int flag) {
                swipeRefreshLayout.setRefreshing(false);

                //set Variables for pagination
                pageCount = feedbackResponse.getPageCount();
                countRecords = feedbackResponse.getCountRecords();
                maxPageCount = feedbackResponse.getMaxPageCount();

                //Hide feedNot available view
                relativeLayoutNotAvailable.setVisibility(View.GONE);


                //Show No Feed as yet Image for that city
                rlNoReviews.setVisibility(feedbackResponse.getFeeds() == null || feedbackResponse.getFeeds().size() == 0 ? View.VISIBLE : View.GONE);
                RelativeLayout.LayoutParams paramsIvNoFeeds = (RelativeLayout.LayoutParams) ivNoFeeds.getLayoutParams();
                paramsIvNoFeeds.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_minus_40));
                ivNoFeeds.setLayoutParams(paramsIvNoFeeds);


                //In case of successful result returned for that city
                feedHomeAdapter.setList(getAdapterList(true, feedbackResponse.getFeeds(), feedbackResponse.getAddPostText(), feedbackResponse.getCity(), pageCount));


                //Set notification Count
                setNotificationCount(feedbackResponse.getCountNotification());


                //Set Data for Add Post View
                activity.getTvAddPost().setText(feedbackResponse.getAddPostText());


                if (scrollToTop && feedHomeAdapter.getItemCount() > 0) {
                    recyclerView.scrollToPosition(0);
                }


                //Since view belongs to activity it should not be made visible in  other fragments happens in case when api is hit from any other fragment
                if (activity.getTopFragment() instanceof FeedHomeFragment) {
                    activity.getFeedHomeAddPostView().setVisibility(View.VISIBLE);
                } else {
                    activity.getFeedHomeAddPostView().setVisibility(View.GONE);

                }

            }

            @Override
            public boolean onError(FeedListResponse feedbackResponse, String message, int flag) {
                swipeRefreshLayout.setRefreshing(false);
                activity.getFeedHomeAddPostView().setVisibility(View.GONE);
                //Set notification Count
                setNotificationCount(feedbackResponse.getCountNotification());

                if (feedbackResponse.getFlag() == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()) {

                    //In case feed in not available in the city
                    relativeLayoutNotAvailable.setVisibility(View.VISIBLE);
                    textViewNothingFound.setText(!TextUtils.isEmpty(feedbackResponse.getMessage()) ? feedbackResponse.getMessage() : activity.getString(R.string.nothing_found_near_you));
                    feedHomeAdapter.setList(getAdapterList(false, feedbackResponse.getFeeds(), null, feedbackResponse.getCity(), pageCount));


                } else {
                    DialogPopup.alertPopup(activity, "", message);
                }

                return true;
            }

            @Override
            public boolean onFailure(RetrofitError error) {
                swipeRefreshLayout.setRefreshing(false);
                return false;
            }

            @Override
            public void onNegativeClick() {


            }
        });


    }


    private boolean isPagingApiInProgress;

    public void fetchNextPage() {
        toggleProgressBarVisibility(true);
        isPagingApiInProgress = true;
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
        params.put(Constants.PAGE_COUNT, String.valueOf(pageCount));
        params.put(Constants.COUNT_RECORDS, String.valueOf(countRecords));
        feedPagingApi = new ApiCommon<FeedListResponse>(activity).showLoader(false);
        feedPagingApi.execute(params, ApiName.GENERATE_FEED_API, new APICommonCallback<FeedListResponse>() {
            @Override
            public boolean onNotConnected() {
                isPagingApiInProgress = false;
                toggleProgressBarVisibility(false);
                return true;
            }

            @Override
            public boolean onException(Exception e) {
                isPagingApiInProgress = false;
                toggleProgressBarVisibility(false);
                return false;
            }

            @Override
            public void onSuccess(FeedListResponse feedbackResponse, String message, int flag) {
                isPagingApiInProgress = false;
                toggleProgressBarVisibility(false);

                //set Variables for pagination
                pageCount = feedbackResponse.getPageCount();
                countRecords = feedbackResponse.getCountRecords();
                maxPageCount = feedbackResponse.getMaxPageCount();

                //Append data to existing list
                if (feedHomeAdapter != null && feedbackResponse.getFeeds() != null && feedbackResponse.getFeeds().size() > 0) {
                    int previousIndex = adapterList.size();
                    adapterList.addAll(feedbackResponse.getFeeds());
                    feedHomeAdapter.notifyItemRangeChanged(previousIndex-1,feedbackResponse.getFeeds().size()-1);

                }
            }

            @Override
            public boolean onError(FeedListResponse feedListResponse, String message, int flag) {
                isPagingApiInProgress = false;
                toggleProgressBarVisibility(false);
                return false;
            }

            @Override
            public boolean onFailure(RetrofitError error) {
                isPagingApiInProgress = false;
                toggleProgressBarVisibility(false);
                return false;
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    private ArrayList<Object> adapterList;

    private ArrayList<Object> getAdapterList(boolean showAddPostText, List<FeedDetail> feedDetailList, String addPostText, String cityName, int pageCount) {
        if (adapterList == null) {
            adapterList = new ArrayList<>();
        }
        adapterList.clear();

        if (feedDetailList != null && feedDetailList.size() > 0) {
            adapterList.addAll(feedDetailList);

//            adapterList.add(FeedHomeAdapter.ITEM_FOOTER_BLANK);
        }


        return adapterList;

           /*  Add Location Type
        String location =!TextUtils.isEmpty(cityName)?cityName:activity.getSelectedAddress();
        adapterList.add(new FeedHomeAdapter.SelectedLocation(location,!TextUtils.isEmpty(cityName)));*/

      /*Add AddPostText

        if(showAddPostText)
        {
            String imageUrl =null;
            if(Data.userData!=null && !TextUtils.isEmpty(Data.userData.userImage)) {
                imageUrl = Data.userData.userImage;
            }

            adapterList.add(new FeedHomeAdapter.AddPostData(addPostText,imageUrl));
        }
*/

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean uploaded = intent.getBooleanExtra(Constants.KEY_UPLOADED, false);
            Log.i("FeedHomeFrag onReceive", "uploaded=" + uploaded);
            if (uploaded) {
                fetchFeedsApi(false, false);
            }
        }
    };


    public void refreshFeedInHomeFragment(int positionItemChangedInCommentsFragment) {
        if (positionItemChangedInCommentsFragment == -1) {
            updateFeedData = true;
        } else {
            if (feedHomeAdapter != null && adapterList != null && adapterList.size() > positionItemChangedInCommentsFragment)
                feedHomeAdapter.notifyFeedListItem(positionItemChangedInCommentsFragment);
        }
    }


    private DeletePostDialog deletePostDialog;

    public DeletePostDialog getDeletePostDialog() {
        if (deletePostDialog == null)
            deletePostDialog = new DeletePostDialog(this, R.style.Feed_Popup_Theme, activity);

        return deletePostDialog;
    }

    public void showDialogPopupTwoButtonCapsule(String message, final FeedDetail feedDetail, final int pos) {
        new DialogPopupTwoButtonCapsule(new DialogPopupTwoButtonCapsule.DialogCallback() {
            @Override
            public void onPositiveClick() {
                onDelete(feedDetail, pos);
            }

            @Override
            public void onNegativeClick() {
            }
        }, R.style.Feed_Popup_Theme, activity, message).show();
    }


    private EditPostPopup editPostDialog;

    public EditPostPopup getEditPostDialog() {

        editPostDialog = new EditPostPopup(this, R.style.Feed_Popup_Theme, activity);

        return editPostDialog;
    }


    private DeleteFeed deleteFeed;

    @Override
    public void onDelete(FeedDetail feedDetail, int pos) {
        if (deleteFeed == null)
            deleteFeed = new DeleteFeed(new DeleteFeed.DeleteApiCallback() {
                @Override
                public void onSuccess(int posInOriginalList) {
                    notifyOnDelete(posInOriginalList);
                }
            });
        deleteFeed.delete(feedDetail.getPostId(), activity, pos);
    }

    @Override
    public void onEdit(FeedDetail feedDetail) {
        activity.openFeedAddPostFragment(feedDetail);
    }

    @Override
    public void onDismiss(FeedDetail feedDetail) {

    }

    public void notifyOnDelete(int positionInOriginalList) {
        if (feedHomeAdapter != null && adapterList != null && adapterList.size() > positionInOriginalList) {
            if (positionInOriginalList != -1) {
                adapterList.remove(positionInOriginalList);
                feedHomeAdapter.notifyItemRemoved(positionInOriginalList);
            } else {
                refreshFeedInHomeFragment(positionInOriginalList);
            }
        }
    }

    @Override
    public void onMoreDelete(final FeedDetail feedDetail, final int positionInList) {
//        getDeletePostDialog().show(feedDetail,positionInList);
        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialogPopupTwoButtonCapsule(activity.getString(R.string.delete_post_alert_message), feedDetail, positionInList);

            }
        }, 100);
    }

    @Override
    public void onMoreEdit(FeedDetail feedDetail, int positionInList) {
        onEdit(feedDetail);

    }


    private MenuItem itemCart;

    public void setNotificationsSeenCount(long notificationsSeenCount) {
        this.notificationsSeenCount = notificationsSeenCount;
    }

    private long notificationsSeenCount = 0;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_home_menu, menu);
        itemCart = menu.findItem(R.id.item_notification);
        setNotificationCount(notificationsSeenCount);
        super.onCreateOptionsMenu(menu, inflater);
    }


    /*
    Old logic to view notification count
    private boolean isNotificationsViewed;

      public void setNotificationsViewed(boolean notificationsViewed) {
          isNotificationsViewed = notificationsViewed;
      }
    private void setNotificationCount(long count){
          LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
          BadgeDrawable.setBadgeCount(activity, icon, String.valueOf(!isNotificationsViewed || count!=notificationsSeenCount?count:0));
          activity.collapsingToolbar.invalidate();
          if(count!=notificationsSeenCount){
              isNotificationsViewed=false;
          }
          notificationsSeenCount = count;
      }
      */
    private void setNotificationCount(long count) {
        this.notificationsSeenCount = count;
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        BadgeDrawable.setBadgeCount(activity, icon, String.valueOf(count));
        activity.collapsingToolbar.invalidate();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_location:
                activity.getTransactionUtils().openChangeFeedCityFragment(activity, activity.getRelativeLayoutContainer());
                GAUtils.event(FEED, HOME, LOCATION + CLICKED);
                break;

            case R.id.item_notification:
                activity.getTransactionUtils().openFeedNotificationsFragment(activity, activity.getRelativeLayoutContainer());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private Runnable runnableNotificationCount = new Runnable() {
        @Override
        public void run() {
            updateFeedNotificationCountAPI();
            if (runnableNotificationCount != null) {
                long UPDATE_NOTIFICATION_COUNT_INTERVAL = 15000;
                activity.getHandler().postDelayed(runnableNotificationCount,
                        Data.getFeedData() != null ? Data.getFeedData().getCountNotificationPollingInterval() :
                                UPDATE_NOTIFICATION_COUNT_INTERVAL);
            }
        }
    };

    private void updateFeedNotificationCountAPI() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

            new ApiCommon<CountNotificationResponse>(activity)
                    .putDefaultParams(true).showLoader(false)
                    .execute(params, ApiName.COUNT_NOTIFICATION, new APICommonCallback<CountNotificationResponse>() {
                        @Override
                        public boolean onNotConnected() {
                            return true;
                        }

                        @Override
                        public boolean onException(Exception e) {
                            return true;
                        }

                        @Override
                        public void onSuccess(CountNotificationResponse countNotificationResponse, String message, int flag) {
                            try {
                                setNotificationCount(countNotificationResponse.getCountNotification());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public boolean onError(CountNotificationResponse countNotificationResponse, String message, int flag) {
                            return true;
                        }

                        @Override
                        public boolean onFailure(RetrofitError error) {
                            return true;
                        }

                        @Override
                        public void onNegativeClick() {


                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FeedHomeAdapter.ProgressBarItem progressBarItem;

    public void toggleProgressBarVisibility(boolean isVisible) {
        if (isVisible) {
            if (progressBarItem == null) {
                progressBarItem = new FeedHomeAdapter.ProgressBarItem();
            }
            if (!adapterList.contains(progressBarItem)) {
                adapterList.add(progressBarItem);
                feedHomeAdapter.notifyItemInserted(adapterList.size() - 1);
            }
        } else {
            if (progressBarItem != null && adapterList.contains(progressBarItem)) {
                adapterList.remove(progressBarItem);
                feedHomeAdapter.notifyItemRemoved(adapterList.size() - 1);
            }
        }
    }

}
