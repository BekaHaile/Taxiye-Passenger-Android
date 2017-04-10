package com.sabkuchfresh.feed.ui.fragments;


import android.app.ProgressDialog;
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
import com.sabkuchfresh.feed.ui.dialogs.EditPostPopup;
import com.sabkuchfresh.feed.utils.BadgeDrawable;
import com.sabkuchfresh.fragments.FreshHomeFragment;
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
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FeedHomeFragment extends Fragment implements GACategory, GAAction, DeletePostDialog.DeleteDialogCallback,EditPostPopup.EditPostDialogCallback{


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


    private final long UPDATE_NOTIFICATION_COUNT_INTERVAL = 15000;

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
        GAUtils.trackScreenView(FEED+HOME);
        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        feedHomeAdapter = new FeedHomeAdapter(getActivity(), getAdapterList(false,null,null,null), recyclerView, new FeedHomeAdapter.FeedPostCallback() {
            @Override
            public void onLikeClick(FeedDetail feedDetail, final int position) {
                if (likeFeed == null)
                        likeFeed = new LikeFeed(new LikeFeed.LikeUnLikeCallbackResponse() {
                        @Override
                        public void onSuccess(boolean isLiked,int position) {
                            feedHomeAdapter.notifyOnLike(position,isLiked);
                        }
                    });

                likeFeed.likeFeed(feedDetail.getPostId(), getActivity(), !feedDetail.isLiked(),position);
                GAUtils.event(FEED, HOME, LIKE+CLICKED);
            }

            @Override
            public void onCommentClick(final FeedDetail feedDetail, int positionInOriginalList) {


                activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail,positionInOriginalList,true);
                GAUtils.event(FEED, HOME, COMMENT+CLICKED);

            }


            @Override
            public void onRestaurantClick(int restaurantId) {
                if(restaurantId > 0){
                    activity.fetchRestaurantMenuAPI(restaurantId);
                }
            }

            @Override
            public String getEditTextString() {
                return null;
            }

            @Override
            public void onMoreClick(final FeedDetail feedDetail, int positionInOriginalList, View moreItemView){

               getEditPostDialog().show(feedDetail,moreItemView,positionInOriginalList);
//                getEditPostDialog().show(feedDetail,moreItemView,positionInOriginalList);



            }

            @Override
            public void onDeleteComment(FeedComment feedComment, int position, View viewClicked) {

            }

            @Override
            public void onFeedLayoutClick(FeedDetail feedDetail, int positionInOriginalList) {
                activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail, positionInOriginalList,false);

            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);;
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
            tvFeedEmpty.setText(Data.getFeedName(activity) + " is empty");
            SpannableStringBuilder ssb = new SpannableStringBuilder(activity.getString(R.string.be_first_one_to_add));
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
                    if(postIdToOpen != -1){
						activity.openFeedDetailsFragmentWithPostId(postIdToOpen);
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 50);


        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(updateFeedData){
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


    private ProgressDialog finalProgressDialog;
    private void dismissFeedLoadingDialog(){
        if(finalProgressDialog != null) {
            finalProgressDialog.dismiss();
            finalProgressDialog = null;
        }
    }

    public void fetchFeedsApi(boolean loader, final boolean scrollToTop) {

        try {
            if (MyApplication.getInstance().isOnline()) {

                if(loader) {
                    finalProgressDialog = DialogPopup.showLoadingDialogNewInstance(getActivity(), getActivity().getResources().getString(R.string.loading));
                }

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().generateFeed(params, new retrofit.Callback<FeedListResponse>() {
                    @Override
                    public void success(FeedListResponse feedbackResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        dismissFeedLoadingDialog();
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                relativeLayoutNotAvailable.setVisibility(View.GONE);
                                if(feedbackResponse.getFlag() == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                    relativeLayoutNotAvailable.setVisibility(View.VISIBLE);
                                    textViewNothingFound.setText(!TextUtils.isEmpty(feedbackResponse.getMessage()) ? feedbackResponse.getMessage() : activity.getString(R.string.nothing_found_near_you));
                                    feedHomeAdapter.setList(getAdapterList(false,feedbackResponse.getFeeds(),null,feedbackResponse.getCity()));
                                    activity.getFeedHomeAddPostView().setVisibility(View.GONE);

                                } else if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                                    feedHomeAdapter.setList(getAdapterList(true,feedbackResponse.getFeeds(),feedbackResponse.getAddPostText(),feedbackResponse.getCity()));
                                    rlNoReviews.setVisibility(feedbackResponse.getFeeds()==null||feedbackResponse.getFeeds().size()==0 ? View.VISIBLE : View.GONE);
                                    if(activity.getTopFragment() instanceof FreshHomeFragment) {
                                        activity.getFeedHomeAddPostView().setVisibility(View.VISIBLE);
                                    }
                                    if(activity.getTvAddPost() != null) {
                                        activity.getTvAddPost().setText(feedbackResponse.getAddPostText());
                                    }
                                    if(Data.getFeedData() != null){
                                        Data.getFeedData().setHandleName(feedbackResponse.getHandleName());
                                    }
                                    setNotificationCount(feedbackResponse.getCountNotification());

                                    RelativeLayout.LayoutParams paramsIvNoFeeds = (RelativeLayout.LayoutParams) ivNoFeeds.getLayoutParams();
                                    paramsIvNoFeeds.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_minus_40));
                                    ivNoFeeds.setLayoutParams(paramsIvNoFeeds);

                                    if(scrollToTop && feedHomeAdapter.getItemCount() > 0){
                                        recyclerView.scrollToPosition(0);
                                    }
                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dismissFeedLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private ArrayList<Object> adapterList;
    private ArrayList<Object> getAdapterList(boolean showAddPostText,List<FeedDetail> feedDetailList,String addPostText,String cityName) {
        if(adapterList ==null) {
            adapterList = new ArrayList<>();
        }

        adapterList.clear();

      /*  //Add Location Type
        String location =!TextUtils.isEmpty(cityName)?cityName:activity.getSelectedAddress();
        adapterList.add(new FeedHomeAdapter.SelectedLocation(location,!TextUtils.isEmpty(cityName)));*/

      /*

        //Add AddPostText
        if(showAddPostText)
        {
            String imageUrl =null;
            if(Data.userData!=null && !TextUtils.isEmpty(Data.userData.userImage)) {
                imageUrl = Data.userData.userImage;
            }

            adapterList.add(new FeedHomeAdapter.AddPostData(addPostText,imageUrl));
        }
*/

        if(feedDetailList!=null && feedDetailList.size()>0){

            adapterList.addAll(feedDetailList);
            adapterList.add(FeedHomeAdapter.ITEM_FOOTER_BLANK);


        }

        return adapterList;

    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        fetchFeedsApi(true, false);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean uploaded = intent.getBooleanExtra(Constants.KEY_UPLOADED, false);
            Log.i("FeedHomeFrag onReceive", "uploaded="+uploaded);
            if(uploaded){
                fetchFeedsApi(false, false);
            }
        }
    };


    public void refreshFeedInHomeFragment(int positionItemChangedInCommentsFragment) {
        if(positionItemChangedInCommentsFragment == -1){
            updateFeedData = true;
        } else {
            if (feedHomeAdapter != null && adapterList != null && adapterList.size() > positionItemChangedInCommentsFragment)
                feedHomeAdapter.notifyFeedListItem(positionItemChangedInCommentsFragment);
        }
    }




   private DeletePostDialog deletePostDialog;

    public DeletePostDialog getDeletePostDialog(){
        if(deletePostDialog==null)
            deletePostDialog=new DeletePostDialog(this,R.style.Feed_Popup_Theme,activity);

        return deletePostDialog;
    }

    private EditPostPopup editPostDialog;

    public EditPostPopup getEditPostDialog(){

            editPostDialog=new EditPostPopup(this,R.style.Feed_Popup_Theme,activity);

        return editPostDialog;
    }


    private DeleteFeed deleteFeed;
    @Override
    public void onDelete(FeedDetail feedDetail,int pos) {
        if(deleteFeed==null)
            deleteFeed=new DeleteFeed(new DeleteFeed.DeleteApiCallback() {
                @Override
                public void onSuccess(int posInOriginalList) {
                    notifyOnDelete(posInOriginalList);
                }
            });
        deleteFeed.delete(feedDetail.getPostId(),activity,pos);
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
    public void onMoreDelete(FeedDetail feedDetail, int positionInList) {
        getDeletePostDialog().show(feedDetail,positionInList);
    }

    @Override
    public void onMoreEdit(FeedDetail feedDetail, int positionInList) {
        onEdit(feedDetail);

    }


    private MenuItem itemCart;
    private long currentNotificationCount = 0;
    private boolean isNotificationsViewed;

    public void setNotificationsViewed(boolean notificationsViewed) {
        isNotificationsViewed = notificationsViewed;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed_home_menu, menu);
        itemCart = menu.findItem(R.id.item_notification);
        setNotificationCount(currentNotificationCount);
        super.onCreateOptionsMenu(menu, inflater);
    }




    private void setNotificationCount(long count){
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        BadgeDrawable.setBadgeCount(activity, icon, String.valueOf(!isNotificationsViewed || count!=currentNotificationCount?count:0));
        activity.collapsingToolbar.invalidate();
        currentNotificationCount = count;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.item_location:
              activity.getTransactionUtils().openChangeFeedCityFragment(activity, activity.getRelativeLayoutContainer());
                GAUtils.event(FEED, HOME, LOCATION+CLICKED);
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
            if(runnableNotificationCount != null) {
                activity.getHandler().postDelayed(runnableNotificationCount, UPDATE_NOTIFICATION_COUNT_INTERVAL);
            }
        }
    };

    private void updateFeedNotificationCountAPI(){
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

}
