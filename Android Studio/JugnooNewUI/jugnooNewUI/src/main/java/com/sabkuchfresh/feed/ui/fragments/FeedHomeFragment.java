package com.sabkuchfresh.feed.ui.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import product.clicklabs.jugnoo.utils.PermissionCommon;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class FeedHomeFragment extends Fragment implements GACategory, GAAction, DeletePostDialog.DeleteDialogCallback
        , EditPostPopup.EditPostDialogCallback, PermissionCommon.PermissionListener {


    private static final int SHOW_ADDPOST_ON_IDLE_DELAY_MILLIS = 2000;
    private FeedHomeAdapter feedHomeAdapter;
    private LikeFeed likeFeed;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayoutNotAvailable;
    private TextView textViewNothingFound;
    private RelativeLayout rlNoReviews;

    private boolean updateFeedData;
    private ImageView ivNoFeeds;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FreshActivity activity;

    //Pagination variables
    private ApiCommon<FeedListResponse> feedPagingApi;
    int pastVisiblesItems, visibleItemCount, totalItemCount, pageCount, countRecords;
    private boolean hasMorePages;

    private MenuItem itemCart;
    private long notificationsSeenCount = 0;
    private int UPDATE_NOTIFICATION_COUNT_INTERVAL = 15 * 1000;
    private boolean isResumed;
    private PermissionCommon mPermissionCommon;
    private final static int REQ_CODE_PERMISSION_CONTACT = 1000;


    public FeedHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(Data.getFeedData()!=null) UPDATE_NOTIFICATION_COUNT_INTERVAL =  Data.getFeedData().getCountNotificationPollingInterval();
        if (context instanceof FreshActivity) {
            activity = (FreshActivity) context;
            activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_CONTACTS_UPLOADED));
            mPermissionCommon = new PermissionCommon(this);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activity != null) {
            try {
                activity.getHandler().removeCallbacks(runnableNotificationCount);
                activity.unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity.fragmentUISetup(this);
        setHasOptionsMenu(true);
        GAUtils.trackScreenView(FEED + HOME);

        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        ivNoFeeds = (ImageView) rootView.findViewById(R.id.ivNoFeeds);
        relativeLayoutNotAvailable = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        textViewNothingFound = (TextView) rootView.findViewById(R.id.textViewNothingFound);
        TextView tvFeedEmpty = (TextView) rootView.findViewById(R.id.tvFeedEmpty);
        rlNoReviews = (RelativeLayout) rootView.findViewById(R.id.rlNoReviews);
        rlNoReviews.setVisibility(View.GONE);
        relativeLayoutNotAvailable.setVisibility(View.GONE);



        //SetUpRecyclerView
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        feedHomeAdapter = new FeedHomeAdapter(getActivity(), getAdapterList(null), recyclerView, new FeedHomeAdapter.FeedPostCallback() {
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
                    activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, positionInOriginalList, true, -1);
                    GAUtils.event(FEED, HOME, COMMENT + CLICKED);

                }

            }


            @Override
            public void onRestaurantClick(int restaurantId) {
                if (restaurantId > 0) {
                    activity.fetchRestaurantMenuAPI(restaurantId, false, null, null, -1, null);
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


            }

            @Override
            public void onDeleteComment(FeedComment feedComment, int position, View viewClicked) {

            }

            @Override
            public void onFeedLayoutClick(FeedDetail feedDetail, int positionInOriginalList) {
                if(!swipeRefreshLayout.isRefreshing()) {
                    activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, positionInOriginalList, false, -1);
                }

            }
        });
        recyclerView.setAdapter(feedHomeAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //To implement Pagination
                if (dy > 0) {

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isPagingApiInProgress && hasMorePages) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            fetchNextPage();
                        }
                    }
                }
            }



            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //To implement automatic show addPostLayout if scroll stays idle for SHOW_ADDPOST_ON_IDLE_DELAY_MILLIS secs.
                if(newState==SCROLL_STATE_IDLE){


                    if(activity.getFeedHomeAddPostView().getTranslationY()!=0 && (adapterList==null || adapterList.size()<=0 ||  layoutManager.findLastVisibleItemPosition()!=adapterList.size()-1 )  ){
                        activity.getHandler().postDelayed(showAddPostOnIdleStateOfScroll, SHOW_ADDPOST_ON_IDLE_DELAY_MILLIS);
                    }

                } else {


                    activity.getHandler().removeCallbacks(showAddPostOnIdleStateOfScroll);
                }
            }
        });
        setUpSwipeRefreshLayout(rootView);



        //Layout In Case of feed Empty
        tvFeedEmpty.setText(R.string. label_feed_empty);
        SpannableStringBuilder ssb = new SpannableStringBuilder(activity.getString(R.string.be_first_one_to_add_a_post));
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFeedEmpty.append("\n");
        tvFeedEmpty.append(ssb);





        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);

        startContactSync();

        // To check if user has clicked on some post id's push notification from sp_post_id_to_open
        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int postIdToOpen = Prefs.with(activity).getInt(Constants.SP_POST_ID_TO_OPEN, -1);
                    int postNotificationId = Prefs.with(activity).getInt(Constants.SP_POST_NOTIFICATION_ID_TO_OPEN, -1);
                    if (postIdToOpen != -1) {
                        activity.openFeedDetailsFragmentWithPostId(postIdToOpen, postNotificationId);
                    }
                    Prefs.with(activity).save(Constants.SP_POST_ID_TO_OPEN, -1);
                    Prefs.with(activity).save(Constants.SP_POST_NOTIFICATION_ID_TO_OPEN, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 50);




        return rootView;
    }

    private void startContactSync() {
        try {

            if(mPermissionCommon.isGranted(Manifest.permission.READ_CONTACTS)){

                if (Data.getFeedData() != null && Data.getFeedData().getContactsSynced() != null && Data.getFeedData().getContactsSynced() == 0) {
                    Intent syncContactsIntent = new Intent(activity, FeedContactsUploadService.class);
                    syncContactsIntent.putExtra(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    activity.startService(syncContactsIntent);
                    Data.getFeedData().setContactsSynced(1);
                }
            }
            else {
                mPermissionCommon.getPermission(REQ_CODE_PERMISSION_CONTACT,false, true, Manifest.permission.READ_CONTACTS);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void setUpSwipeRefreshLayout(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.white);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeedsApi(false, false, false);

            }
        });
    }


    private Runnable showAddPostOnIdleStateOfScroll = new Runnable() {
        @Override
        public void run() {

        activity.getFeedHomeAddPostView().animate().translationY(0).start();
        }
    };


    private boolean isFragmentHidden;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            isFragmentHidden = true;
            activity.fragmentUISetup(this);
            activity.getFeedHomeAddPostView().setVisibility(relativeLayoutNotAvailable.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFragmentHidden = false;
                    if (updateFeedData) {
                        fetchFeedsApi(true, false, false);
                    }
                    updateFeedData = false;
                }
            }, 200);
            setUpNotificationCountAPI(1000);
        } else {
            activity.getTvAddPost().clearAnimAndSetText();
            activity.getHandler().removeCallbacks(runnableNotificationCount);
            activity.getHandler().removeCallbacks(showAddPostOnIdleStateOfScroll);
            isFragmentHidden=true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        setUpNotificationCountAPI(1000);
    }

    private void setUpNotificationCountAPI(long startAfter) {
        activity.getHandler().removeCallbacks(runnableNotificationCount);
        activity.getHandler().postDelayed(runnableNotificationCount, startAfter);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
        activity.getHandler().removeCallbacks(runnableNotificationCount);
        activity.getTvAddPost().clearAnimAndSetText();
        activity.getHandler().removeCallbacks(showAddPostOnIdleStateOfScroll);
    }


    public void fetchFeedsApi(boolean loader, final boolean scrollToTop,final boolean animateAddPostText) {
        countRecords = 0;
        pageCount = 1;
        hasMorePages=false;
        if (feedPagingApi != null) feedPagingApi.setCancelled(true);
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


                //setHandleName
                if(feedbackResponse.getHandleName()!=null)
                {if(Data.getFeedData()!=null){
                        Data.getFeedData().setHandleName(feedbackResponse.getHandleName());
                    }
                }

                //set Variables for pagination
                hasMorePages = pageCount != feedbackResponse.getPageCount();
                pageCount = feedbackResponse.getPageCount();
                countRecords = feedbackResponse.getCountRecords();

                //Hide feedNot available view
                relativeLayoutNotAvailable.setVisibility(View.GONE);


                //Show No Feed as yet Image for that city
                rlNoReviews.setVisibility(feedbackResponse.getFeeds() == null || feedbackResponse.getFeeds().size() == 0 ? View.VISIBLE : View.GONE);
                RelativeLayout.LayoutParams paramsIvNoFeeds = (RelativeLayout.LayoutParams) ivNoFeeds.getLayoutParams();
                paramsIvNoFeeds.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_minus_40));
                paramsIvNoFeeds.setMarginStart(0);
                paramsIvNoFeeds.setMarginEnd(0);
                ivNoFeeds.setLayoutParams(paramsIvNoFeeds);


                //In case of successful result returned for that city
                feedHomeAdapter.setList(getAdapterList(feedbackResponse.getFeeds()));


                //Set notification Count
                setNotificationCount(feedbackResponse.getCountNotification());


                //Set Data for Add Post View
                if(animateAddPostText && isResumed && activity.getTopFragment() instanceof FeedHomeFragment){
                    activity.getTvAddPost().animateText(feedbackResponse.getAddPostText());

                }else{
                    activity.getTvAddPost().setmText(feedbackResponse.getAddPostText());

                }



                if (scrollToTop && feedHomeAdapter.getItemCount() > 0) {
                    recyclerView.scrollToPosition(0);
                }


                //Since view belongs to activity it should not be made visible in  other fragments happens in case when api is hit from any other fragment
                if (activity.getTopFragment() instanceof FeedHomeFragment) {
                    activity.getFeedHomeAddPostView().setVisibility(View.VISIBLE);
                } else {
                    activity.getFeedHomeAddPostView().setVisibility(View.GONE);

                }

                //SetUp Notification Count API
                setUpNotificationCountAPI(UPDATE_NOTIFICATION_COUNT_INTERVAL);
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
                    feedHomeAdapter.setList(getAdapterList(feedbackResponse.getFeeds()));


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
                hasMorePages = pageCount != feedbackResponse.getPageCount();
                pageCount = feedbackResponse.getPageCount();
                countRecords = feedbackResponse.getCountRecords();


                //Append data to existing list
                if (feedHomeAdapter != null && feedbackResponse.getFeeds() != null && feedbackResponse.getFeeds().size() > 0) {
                    int previousIndex = adapterList.size();
                    adapterList.addAll(feedbackResponse.getFeeds());
                    feedHomeAdapter.notifyItemRangeChanged(previousIndex-1,feedbackResponse.getFeeds().size()-1);

                }

                //Show AddPost Layout in case recycler view is in idle state
                if(recyclerView.getScrollState()==SCROLL_STATE_IDLE){
                    activity.getHandler().postDelayed(showAddPostOnIdleStateOfScroll,2000);
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

    private ArrayList<Object> getAdapterList(List<FeedDetail> feedDetailList) {
        if (adapterList == null) {
            adapterList = new ArrayList<>();
        }
        adapterList.clear();

        if (feedDetailList != null && feedDetailList.size() > 0) {
            adapterList.addAll(feedDetailList);

//            adapterList.add(FeedHomeAdapter.ITEM_FOOTER_BLANK);
        }


        return adapterList;



    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean uploaded = intent.getBooleanExtra(Constants.KEY_UPLOADED, false);

            if (uploaded) {
                fetchFeedsApi(false, false, false);
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
            public void onLeftClick() {
                onDelete(feedDetail, pos);
            }

            @Override
            public void onRightClick() {
            }
        }, R.style.Feed_Popup_Theme, activity, message).show();
    }


    public EditPostPopup getEditPostDialog() {

        return new EditPostPopup(this, R.style.Feed_Popup_Theme, activity);
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




    public void setNotificationsSeenCount(long notificationsSeenCount) {
        this.notificationsSeenCount = notificationsSeenCount;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.feed_home_menu, menu);
        itemCart = menu.findItem(R.id.item_notification);
        setNotificationCount(notificationsSeenCount);
        super.onCreateOptionsMenu(menu, inflater);
    }

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
                activity.getHandler().postDelayed(runnableNotificationCount,UPDATE_NOTIFICATION_COUNT_INTERVAL);
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

   private FeedHomeAdapter.ProgressBarItem progressBarItem;

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



    public boolean shouldTranslateFeedHomeAddPost() {
        return recyclerView.getScrollState()!=SCROLL_STATE_IDLE || isFragmentHidden;
    }

    @Override
    public void permissionGranted(final int requestCode) {
        if(requestCode == REQ_CODE_PERMISSION_CONTACT){
            startContactSync();
        }
    }

    @Override
    public void permissionDenied(final int requestCode) {

    }
}
