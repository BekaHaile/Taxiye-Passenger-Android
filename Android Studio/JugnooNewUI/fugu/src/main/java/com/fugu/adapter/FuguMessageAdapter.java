package com.fugu.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.fugu.FuguFontConfig;
import com.fugu.R;
import com.fugu.activity.FuguChatActivity;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.CustomAction;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguFileDetails;
import com.fugu.model.Message;
import com.fugu.utils.DateUtils;
import com.fugu.utils.GridDividerItemDecoration;
import com.fugu.utils.MyCustomEditTextListener;
import com.fugu.utils.RoundedCornersTransformation;
import com.fugu.utils.zoomview.ZoomageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bhavya Rattan on 02/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant, QRCallback  {

    private final int FUGU_TYPE_HEADER = 0;
    private final int FUGU_ITEM_TYPE_OTHER = 1;
    private final int FUGU_ITEM_TYPE_SELF = 2;
    private final int FUGU_RATING_VIEW = 3;
    private final int FUGU_GALLERY_VIEW = -990; // TODO: 09/05/18 Update with the correct value
    private final int FUGU_TEXT_VIEW = 15;
    private final int FUGU_QUICK_REPLY_VIEW = 16;
    private final int FUGU_FORUM_VIEW = 17;
    private final int FUGU_VIDEO_CALL_VIEW = 18;
    private final int FUGU_OTHER_VIDEO_CALL_VIEW = 19;
    private DateUtils fuguDateUtil = DateUtils.getInstance();
    private Long fuguLabelId;
    private OnRetryListener mOnRetry;
    private onVideoCall onVideoCall;
    private FuguColorConfig fuguColorConfig;
    private Activity activity;
    private FuguConversation fuguConversation;
    private FuguChatActivity fuguChatActivity;
    private Configuration config;
    //for bot
    private QuickReplyAdapaterActivityCallback qrCallback;
    private OnRatingListener onRatingListener;
    private FragmentManager fragmentManager;
    private FuguFontConfig fuguFontConfig;

    private String agentName = "";
    private boolean isVideoCallEnabled = false;
    private boolean isAudioCallEnabled = false;
    String callType = "video";
    @NonNull
    private List<ListItem> fuguItems = Collections.emptyList();

    /*public FuguMessageAdapter(Activity activity, @NonNull List<ListItem> fuguItems, Long fuguLabelId, FuguConversation fuguConversation) {
        this.fuguItems = fuguItems;
        this.activity = activity;
        this.fuguLabelId = fuguLabelId;
        this.fuguConversation = fuguConversation;
        removeDefaultMsgTime();
        fuguColorConfig = CommonData.getColorConfig();
        config = activity.getResources().getConfiguration();
    }*/

    public FuguMessageAdapter(Activity activity, @NonNull List<ListItem> fuguItems, Long fuguLabelId,
                              FuguConversation fuguConversation, OnRatingListener onRatingListener,
                              QuickReplyAdapaterActivityCallback callback, FragmentManager fragmentManager) {
        this.fuguItems = fuguItems;
        this.activity = activity;
        this.fuguLabelId = fuguLabelId;
        this.fuguConversation = fuguConversation;
        removeDefaultMsgTime();
        fuguColorConfig = CommonData.getColorConfig();
        config = activity.getResources().getConfiguration();
        this.onRatingListener = onRatingListener;
        this.qrCallback = callback;
        this.fragmentManager = fragmentManager;
		fuguFontConfig = CommonData.getFontConfig();
    }

    public void setOnRetryListener(OnRetryListener OnRetryListener) {
        mOnRetry = OnRetryListener;
    }

    public void setOnVideoCallListener(onVideoCall onVideoCall) {
        this.onVideoCall = onVideoCall;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void isVideoCallEnabled(boolean isVideoCallEnabled) {
        this.isVideoCallEnabled = isVideoCallEnabled;
    }

    public void isAudioCallEnabled(boolean isAudioCallEnabled) {
        this.isAudioCallEnabled = isAudioCallEnabled;
    }

    @Override
    public void onFormClickListener(int id, Message currentFormMsg) {
        onRatingListener.onFormDataCallback(currentFormMsg);
    }

    @Override
    public void DataFormCallback() {

    }

    @Override
    public void onClickListener(Message message, int pos, QuickReplyViewHolder viewHolder) {
        viewHolder.list_qr.setVisibility(View.GONE);
        qrCallback.QuickReplyListener(message, pos);
    }

    public interface OnRetryListener {
        void onRetry(String file, final int messageIndex, int messageType, FuguFileDetails fileDetails, String uuid);
        void onMessageRetry(String muid, int position);
        void onMessageCancel(String muid, int position);
    }

    public void updateList(@NonNull List<ListItem> items) {
        updateList(items, true);
    }
    public void updateList(@NonNull List<ListItem> items, boolean flag) {
        this.fuguItems = items;
        if(flag)
            removeDefaultMsgTime();
    }

    private void removeDefaultMsgTime() {
        if (fuguItems.size() > 0) {
            for (int i = 0; i < 2; i++) {
                if (i >= fuguItems.size()) {
                    break;
                }

                if (fuguItems.get(i).getType() == FUGU_ITEM_TYPE_OTHER && fuguLabelId.compareTo(-1L) != 0) {
                    ((EventItem) fuguItems.get(i)).getEvent().setSentAtUtc("");
                    break;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (viewType == FUGU_TYPE_HEADER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_date, parent, false);
            return new DateViewHolder(normalView);
        } else if (viewType == FUGU_ITEM_TYPE_OTHER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_left, parent, false);
            return new OtherMessageViewHolder(normalView);
        } else if (viewType == FUGU_ITEM_TYPE_SELF) {
            View headerRow = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_right, parent, false);
            return new SelfMessageViewHolder(headerRow);
        }*/

        if (viewType == FUGU_TYPE_HEADER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_date, parent, false);
            return new DateViewHolder(normalView);
        } else if (viewType == FUGU_ITEM_TYPE_OTHER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_left, parent, false);
            return new OtherMessageViewHolder(normalView);
        } else if (viewType == FUGU_FORUM_VIEW) {
            View forumView = LayoutInflater.from(activity).inflate(R.layout.fugu_data_fourm, parent, false);
            return new ForumViewHolder(forumView);
        } else if (viewType == FUGU_TEXT_VIEW) {
            View forumView = LayoutInflater.from(activity).inflate(R.layout.fugu_text_item, parent, false);
            return new SimpleTextView(forumView);
        } else if (viewType == FUGU_QUICK_REPLY_VIEW) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.hippo_item_quick_replay, parent, false);
            return new QuickReplyViewHolder(normalView);
        } else if (viewType == FUGU_GALLERY_VIEW) {
            View galleryView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_gallery, parent, false);
            return new GalleryViewHolder(galleryView);
        } else if (viewType == FUGU_ITEM_TYPE_SELF) {
            View headerRow = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_right, parent, false);
            return new SelfMessageViewHolder(headerRow);
        } else if (viewType == FUGU_RATING_VIEW) {
            View ratingView = LayoutInflater.from(activity).inflate(R.layout.hippo_feedback_dialog, parent, false);
            return new RatingViewHolder(ratingView, new MyCustomEditTextListener());
        } else if(viewType == FUGU_VIDEO_CALL_VIEW) {
            View videoCallView = LayoutInflater.from(activity).inflate(R.layout.hippo_video_self_side, parent, false);
            return new SelfVideoViewHolder(videoCallView);
        } else if(viewType == FUGU_OTHER_VIDEO_CALL_VIEW) {
            View otherVideoCallView = LayoutInflater.from(activity).inflate(R.layout.hippo_video_other_side, parent, false);
            return new VideoViewHolder(otherVideoCallView);
        }

        return null;
    }

    private int pxToDp(int dpParam) {
        float d = activity.getResources().getDisplayMetrics().density;
        return (int) (dpParam * d); // margin in pixels
    }

    private Long downloadFile(String url, String fileName, String ext) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ext);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            return manager.enqueue(request);
        } else
            return null;
    }

    private void setImageClick(int item, ImageView... imageViews) {
        int width = (int) convertDpToPixel(40);
        int height = (int) convertDpToPixel(40);
        /*for(ImageView imageView : imageViews) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            imageView.setLayoutParams(layoutParams);
        }*/

        for (int i = 1; i <= imageViews.length; i++) {
            if (i == item) {
                int length = (int) convertDpToPixel(60);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(length, length);
                imageViews[i - 1].setLayoutParams(layoutParams);
                switch (item) {
                    case 1:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_terrible_image_selected);
                        break;
                    case 2:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_bad_image_selected);
                        break;
                    case 3:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_okay_image_selected);
                        break;
                    case 4:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_good_image_selected);
                        break;
                    case 5:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_great_image_selected);
                        break;
                    default:

                        break;
                }
            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                imageViews[i - 1].setLayoutParams(layoutParams);
                switch (i) {
                    case 1:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_terrible_image);
                        break;
                    case 2:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_bad_image);
                        break;
                    case 3:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_okay_image);
                        break;
                    case 4:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_good_image);
                        break;
                    case 5:
                        imageViews[i - 1].setBackgroundResource(R.drawable.hippo_ic_great_image);
                        break;
                    default:

                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final int itemType = getItemViewType(position);
        fuguChatActivity = (FuguChatActivity) activity;

        boolean isRightToLeft = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isRightToLeft = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
            } else {
                isRightToLeft = TextUtilsCompat.getLayoutDirectionFromLocale(Locale
                        .getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
            }
        } catch (Exception e) {

        }

        switch (itemType) {
            case FUGU_VIDEO_CALL_VIEW:
                final SelfVideoViewHolder videoViewHolder = (SelfVideoViewHolder) holder;
                Message videoMessage = ((EventItem) fuguItems.get(position)).getEvent();
                callType = "video";
                if(!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                    callType = "voice";
                }
                if(videoMessage.getMessageState() != null && videoMessage.getMessageState().intValue() == 2) {
                    videoViewHolder.tvMsg.setText(agentName+" missed a "+ callType +" call with you");
                } else {
                    videoViewHolder.tvMsg.setText("The "+ callType +" call ended");
                }
                if (videoMessage.getSentAtUtc().isEmpty()) {
                    videoViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    videoViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(videoMessage.getSentAtUtc())));
                    videoViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
                if(videoMessage.getVideoCallDuration()>0) {
                    videoViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                    videoViewHolder.tvDuration.setVisibility(View.VISIBLE);
                    videoViewHolder.tvDuration.setText(convertSeconds(videoMessage.getVideoCallDuration())+" at ");
//                    videoViewHolder.tvDuration.setText(videoMessage.getVideoCallDuration()+"sec at ");

                } else {
                    videoViewHolder.ivCallIcon.setVisibility(View.GONE);
                    videoViewHolder.tvDuration.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                    if(!CommonData.getAudioCallStatus() || !isAudioCallEnabled) {
                        videoViewHolder.callAgain.setVisibility(View.GONE);
                    }
                } else {
                    if(!CommonData.getVideoCallStatus() || !isVideoCallEnabled) {
                        videoViewHolder.callAgain.setVisibility(View.GONE);
                    }
                }

                videoViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(onVideoCall != null) {
                            int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                            if (!TextUtils.isEmpty(videoMessage.getCallType()) && videoMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                                callType = FuguAppConstant.AUDIO_CALL_VIEW;
                            }
                            onVideoCall.onVideoCallClicked(callType);
                        }*/
                    }
                });
                break;
            case FUGU_OTHER_VIDEO_CALL_VIEW:
                final VideoViewHolder videoOtherViewHolder = (VideoViewHolder) holder;
                Message videoOtherMessage = ((EventItem) fuguItems.get(position)).getEvent();
                callType = "video";
                if(!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                    callType = "voice";
                }
                if(videoOtherMessage.getMessageState() != null && videoOtherMessage.getMessageState().intValue() == 2) {
                    videoOtherViewHolder.tvMsg.setText("You missed a "+ callType +" call with "+videoOtherMessage.getfromName());
                } else {
                    videoOtherViewHolder.tvMsg.setText("The "+ callType +" call ended");
                }
                if (videoOtherMessage.getSentAtUtc().isEmpty()) {
                    videoOtherViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    videoOtherViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(videoOtherMessage.getSentAtUtc())));
                    videoOtherViewHolder.tvTime.setVisibility(View.VISIBLE);
                }

                if(videoOtherMessage.getVideoCallDuration()>0) {
                    videoOtherViewHolder.ivCallIcon.setVisibility(View.VISIBLE);
                    videoOtherViewHolder.tvDuration.setVisibility(View.VISIBLE);
                    videoOtherViewHolder.tvDuration.setText(convertSeconds(videoOtherMessage.getVideoCallDuration())+" at ");
//                    videoOtherViewHolder.tvDuration.setText(videoOtherMessage.getVideoCallDuration()+"sec at ");
                } else {
                    videoOtherViewHolder.ivCallIcon.setVisibility(View.GONE);
                    videoOtherViewHolder.tvDuration.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                    if(!CommonData.getAudioCallStatus() || !isAudioCallEnabled) {
                        videoOtherViewHolder.callAgain.setVisibility(View.GONE);
                    }
                } else {
                    if(!CommonData.getVideoCallStatus() || !isVideoCallEnabled) {
                        videoOtherViewHolder.callAgain.setVisibility(View.GONE);
                    }
                }

                videoOtherViewHolder.callAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(onVideoCall != null) {
                            int callType = FuguAppConstant.VIDEO_CALL_VIEW;
                            if (!TextUtils.isEmpty(videoOtherMessage.getCallType()) && videoOtherMessage.getCallType().equalsIgnoreCase(FuguAppConstant.CallType.AUDIO.toString())) {
                                callType = FuguAppConstant.AUDIO_CALL_VIEW;
                            }
                            onVideoCall.onVideoCallClicked(callType);
                        }*/
                    }
                });

                break;
            case FUGU_GALLERY_VIEW:
                int count = 4;
                final GalleryViewHolder viewHolder1 = (GalleryViewHolder) holder;
                setView(viewHolder1.llGalleryButtonLayout, count);
                break;
            case FUGU_TEXT_VIEW:

                final SimpleTextView textView = (SimpleTextView) holder;
                Message msg = ((EventItem) fuguItems.get(position)).getEvent();
                textView.tvText.setText(msg.getMessage());
                break;
            case FUGU_RATING_VIEW:


                final RatingViewHolder viewHolder = (RatingViewHolder) holder;
                final Message currentMessage = ((EventItem) fuguItems.get(position)).getEvent();
                if (currentMessage.isRatingGiven()) {
                    viewHolder.askRateLayout.setVisibility(View.GONE);
                    viewHolder.ratedLayout.setVisibility(View.VISIBLE);

                    String title = currentMessage.getLineAfterFeedback_1() + "  ";
                    SpannableStringBuilder ssb = new SpannableStringBuilder(title);
                    //Bitmap smiley = BitmapFactory.decodeResource(activity.getResources(), R.drawable.photo_icon );

                    Drawable android = activity.getResources().getDrawable(R.drawable.hippo_ic_okay_image_selected);
                    switch (currentMessage.getRatingGiven()) {
                        case 1:
                            android = activity.getResources().getDrawable(R.drawable.hippo_ic_terrible_image_selected);
                            break;
                        case 2:
                            android = activity.getResources().getDrawable(R.drawable.hippo_ic_bad_image_selected);
                            break;
                        case 3:
                            android = activity.getResources().getDrawable(R.drawable.hippo_ic_okay_image_selected);
                            break;
                        case 4:
                            android = activity.getResources().getDrawable(R.drawable.hippo_ic_good_image_selected);
                            break;
                        case 5:
                            android = activity.getResources().getDrawable(R.drawable.hippo_ic_great_image_selected);
                            break;
                        default:

                            break;
                    }
                    int size = (int) convertDpToPixel(34);
                    android.setBounds(0, 0, size, size);
                    ImageSpan image = new ImageSpan(android, ImageSpan.ALIGN_BOTTOM);

                    ssb.setSpan(image, title.length() - 1, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    viewHolder.ratedTitle.setText(ssb, TextView.BufferType.SPANNABLE);

                    //viewHolder.ratedTitle.setText(""+activity.getString(R.string.hippo_rating_title));
                    viewHolder.ratedSubTitle.setText(currentMessage.getLineAfterFeedback_2());
                    if (!TextUtils.isEmpty(currentMessage.getComment())) {
                        viewHolder.ratedMessage.setText("" + currentMessage.getComment());
                        viewHolder.messageLayout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.messageLayout.setVisibility(View.GONE);
                    }


                } else {
                    viewHolder.askRateLayout.setVisibility(View.VISIBLE);
                    viewHolder.ratedLayout.setVisibility(View.GONE);

                    viewHolder.titleTxt.setText(currentMessage.getLineBeforeFeedback());
                    //FuguLog.e("MESSAGE", " = "+currentMessage.getComment());

                    viewHolder.myCustomEditTextListener.updatePosition(currentMessage);
                    viewHolder.editText.setText(currentMessage.getComment());

                    if (currentMessage.getRatingGiven() > 0) {
                        setImageClick(currentMessage.getRatingGiven(), viewHolder.terribleImage, viewHolder.badImage,
                                viewHolder.okayImage, viewHolder.goodImage, viewHolder.greatImage);
                    } else {
                        setImageClick(3, viewHolder.terribleImage, viewHolder.badImage,
                                viewHolder.okayImage, viewHolder.goodImage, viewHolder.greatImage);
                        currentMessage.setRatingGiven(3);
                    }
                }

                viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRatingListener.onSubmitRating(viewHolder.editText.getText().toString(), currentMessage, position);
                        viewHolder.editText.setText("");
                    }
                });

                viewHolder.terribleImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        setImageClick(1, viewHolder.terribleImage, viewHolder.badImage, viewHolder.okayImage,
                                viewHolder.goodImage, viewHolder.greatImage);

                        onRatingListener.onRatingSelected(1, currentMessage);
//                        int height = (int) convertDpToPixel(60);
//                        viewHolder.terribleImage.getLayoutParams().width = height;
//                        viewHolder.terribleImage.getLayoutParams().height = height;
//                        viewHolder.terribleImage.requestLayout();
                    }
                });
                viewHolder.badImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImageClick(2, viewHolder.terribleImage, viewHolder.badImage, viewHolder.okayImage,
                                viewHolder.goodImage, viewHolder.greatImage);

                        onRatingListener.onRatingSelected(2, currentMessage);
                        /*int height = (int) convertDpToPixel(60);
                        viewHolder.badImage.getLayoutParams().width = height;
                        viewHolder.badImage.getLayoutParams().height = height;
                        viewHolder.badImage.requestLayout();*/
                    }
                });
                viewHolder.okayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImageClick(3, viewHolder.terribleImage, viewHolder.badImage, viewHolder.okayImage,
                                viewHolder.goodImage, viewHolder.greatImage);
                        onRatingListener.onRatingSelected(3, currentMessage);
//                        int height = (int) convertDpToPixel(60);
//                        viewHolder.okayImage.getLayoutParams().width = height;
//                        viewHolder.okayImage.getLayoutParams().height = height;
//                        viewHolder.okayImage.requestLayout();
                    }
                });
                viewHolder.goodImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImageClick(4, viewHolder.terribleImage, viewHolder.badImage,
                                viewHolder.okayImage, viewHolder.goodImage, viewHolder.greatImage);

                        onRatingListener.onRatingSelected(4, currentMessage);
//                        int height = (int) convertDpToPixel(60);
//                        viewHolder.goodImage.getLayoutParams().width = height;
//                        viewHolder.goodImage.getLayoutParams().height = height;
//                        viewHolder.goodImage.requestLayout();
                    }
                });

                viewHolder.greatImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImageClick(5, viewHolder.terribleImage, viewHolder.badImage,
                                viewHolder.okayImage, viewHolder.goodImage, viewHolder.greatImage);

                        onRatingListener.onRatingSelected(5, currentMessage);
//                        int height = (int) convertDpToPixel(60);
//                        viewHolder.greatImage.getLayoutParams().width = height;
//                        viewHolder.greatImage.getLayoutParams().height = height;
//                        viewHolder.greatImage.requestLayout();
                    }
                });

                //*******************          //QUICK REPLIES//--------*********************

                break;

            case FUGU_QUICK_REPLY_VIEW:

                final QuickReplyViewHolder qrViewHolder = (QuickReplyViewHolder) holder;
                Message currentFormMsg = ((EventItem) fuguItems.get(position)).getEvent();
//                qrViewHolder.title_view_text.setText(currentFormMsg.getMessage());
//                if (fuguItems.get(position + 1) != null) {
//                    qrCallback.sendActionId(((EventItem) fuguItems.get(position)).getEvent(),position);
//                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                qrViewHolder.list_qr.setLayoutManager(layoutManager);
                HippoQuickReplayAdapter replayAdapter = new HippoQuickReplayAdapter(currentFormMsg, this, qrViewHolder);
                qrViewHolder.list_qr.setAdapter(replayAdapter);


                break;
            case FUGU_FORUM_VIEW:

                Message currentFormDataMsg = ((EventItem) fuguItems.get(position)).getEvent();
                final ForumViewHolder forumViewHolder = (ForumViewHolder) holder;
                LinearLayoutManager mlayoutManager = new LinearLayoutManager(activity);
                forumViewHolder.rvDataForm.setLayoutManager(mlayoutManager);
                DataFormAdapter dataFormAdapter = new DataFormAdapter(currentFormDataMsg, this, fragmentManager);
                forumViewHolder.rvDataForm.setNestedScrollingEnabled(true);
                forumViewHolder.rvDataForm.setAdapter(dataFormAdapter);


                break;
            case FUGU_TYPE_HEADER:
                final DateViewHolder dateViewHolder = (DateViewHolder) holder;
                HeaderItem headerItem = (HeaderItem) fuguItems.get(position);
                if (headerItem.getDate().isEmpty()) {
                    dateViewHolder.tvDate.setVisibility(View.GONE);
                } else {
                    String date = DateUtils.getInstance().getDate(headerItem.getDate());
                    dateViewHolder.tvDate.setText(date);
                    dateViewHolder.tvDate.setVisibility(View.VISIBLE);
                }

                GradientDrawable drawable = (GradientDrawable) dateViewHolder.tvDate.getBackground();
                drawable.setStroke((int) activity.getResources().getDimension(R.dimen.fugu_border_width), fuguColorConfig.getFuguBorderColor()); // set stroke width and stroke color

                dateViewHolder.tvDate.setTextColor(fuguColorConfig.getFuguChatDateText());
                break;
            case FUGU_ITEM_TYPE_OTHER:
                final OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
                final Message currentOrderItem = ((EventItem) fuguItems.get(position)).getEvent();
                otherMessageViewHolder.tvUserName.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgFromName());
                otherMessageViewHolder.tvMsg.setTextColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
                otherMessageViewHolder.tvMsg.setLinkTextColor(fuguColorConfig.getFuguPrimaryTextMsgFrom());
                otherMessageViewHolder.tvMsg.setAutoLinkMask(Linkify.ALL);
                otherMessageViewHolder.tvTime.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgFrom());

                if (currentOrderItem.getMessage().isEmpty()) {
                    otherMessageViewHolder.tvMsg.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    otherMessageViewHolder.rlMessages.setLayoutParams(params2);
                    otherMessageViewHolder.tvMsg.setTextSize(pxToDp(3));
                } else {
                    otherMessageViewHolder.tvMsg.setText(currentOrderItem.getMessage());
                    otherMessageViewHolder.tvMsg.setTextSize(17);
                    otherMessageViewHolder.tvMsg.setVisibility(View.VISIBLE);
                }


                String userNameText = "";
                String[] userNameSplitArray;
                int userNameStringCount = 1;
                if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                    if (currentOrderItem.getfromName().split(" ").length > 0) {
                        userNameStringCount = currentOrderItem.getfromName().trim().split(" ").length;
                    }
                    userNameSplitArray = currentOrderItem.getfromName().trim().split(" ");
                    for (int i = 0; i < userNameStringCount; i++) {
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(0, 1).toUpperCase());
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(1).toLowerCase());
                        userNameText = userNameText.concat(" ");
                    }
                } else {
                    userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : activity.getString(R.string.fugu_support);

                }

                int dp_1 = pxToDp(1);
                int dp_2 = pxToDp(2);
                int dp_4 = pxToDp(4);
                int dp_5 = pxToDp(5);
                int dp_7 = pxToDp(7);
                int dp_8 = pxToDp(8);
                int dp_10 = pxToDp(10);
                int dp_15 = pxToDp(15);
                int dp_17 = pxToDp(17);

                if (position != 0 && getItemViewType(position - 1) == FUGU_ITEM_TYPE_OTHER) {
                    Message lastOrderItem = ((EventItem) fuguItems.get(position - 1)).getEvent();
                    if (currentOrderItem.getUserId().compareTo(lastOrderItem.getUserId()) != 0) {
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);
                        otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                        otherMessageViewHolder.tvUserName.setText(userNameText);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            otherMessageViewHolder.llRoot.setPaddingRelative(dp_10, dp_8, 0, dp_1);
                            otherMessageViewHolder.tvUserName.setPaddingRelative(dp_15, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPaddingRelative(dp_15, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPaddingRelative(0, dp_5, 0, 0);
                        } else {
                            otherMessageViewHolder.llRoot.setPadding(dp_10, dp_8, 0, dp_1);
                            otherMessageViewHolder.tvUserName.setPadding(dp_15, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPadding(dp_15, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPadding(0, dp_5, 0, 0);
                        }

                    } else if (position + 1 != fuguItems.size() && getItemViewType(position + 1) != FUGU_ITEM_TYPE_OTHER) {
                        otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_left_normal);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            otherMessageViewHolder.llRoot.setPaddingRelative(dp_17, dp_1, 0, dp_4);
                            otherMessageViewHolder.tvUserName.setPaddingRelative(dp_8, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPaddingRelative(dp_8, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPaddingRelative(0, dp_5, 0, 0);
                        } else {
                            otherMessageViewHolder.llRoot.setPadding(dp_17, dp_1, 0, dp_4);
                            otherMessageViewHolder.tvUserName.setPadding(dp_8, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPadding(dp_8, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPadding(0, dp_5, 0, 0);
                        }
                    } else {
                        otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_left_normal);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            otherMessageViewHolder.llRoot.setPaddingRelative(dp_17, dp_1, 0, dp_1);
                            otherMessageViewHolder.tvUserName.setPaddingRelative(dp_8, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPaddingRelative(dp_8, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPaddingRelative(0, dp_5, 0, 0);
                        } else {
                            otherMessageViewHolder.llRoot.setPadding(dp_17, dp_1, 0, dp_1);
                            otherMessageViewHolder.tvUserName.setPadding(dp_8, dp_7, dp_10, 0);
                            otherMessageViewHolder.tvMsg.setPadding(dp_8, dp_7, dp_2, dp_7);
                            otherMessageViewHolder.ivMsgImage.setPadding(0, dp_5, 0, 0);
                        }
                    }
                } else if (position != 0 && (position + 1 != fuguItems.size()) && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_OTHER)) {
                    int tvMsgTopPadding;
                    if (currentOrderItem.getMessageType() == TEXT_MESSAGE) {
                        tvMsgTopPadding = 0;
                    } else if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                        tvMsgTopPadding = pxToDp(10);
                    } else {
                        tvMsgTopPadding = pxToDp(5);
                    }
                    otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvUserName.setText(userNameText);
                    otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        otherMessageViewHolder.llRoot.setPaddingRelative(pxToDp(10), pxToDp(4), pxToDp(0), pxToDp(1));
                        otherMessageViewHolder.tvUserName.setPaddingRelative(pxToDp(15), pxToDp(7), pxToDp(10), pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPaddingRelative(pxToDp(15), pxToDp(tvMsgTopPadding), pxToDp(2), pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPaddingRelative(dp_5, 0, 0, 0);
                    } else {
                        otherMessageViewHolder.llRoot.setPadding(pxToDp(10), pxToDp(4), pxToDp(0), pxToDp(1));
                        otherMessageViewHolder.tvUserName.setPadding(pxToDp(15), pxToDp(7), pxToDp(10), pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPadding(pxToDp(15), pxToDp(tvMsgTopPadding), pxToDp(2), pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPadding(dp_5, 0, 0, 0);
                    }
                } else {
                    int tvMsgTopPadding;
                    if (currentOrderItem.getMessageType() == TEXT_MESSAGE) {
                        tvMsgTopPadding = 0;
                    } else if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                        tvMsgTopPadding = pxToDp(10);
                    } else {
                        tvMsgTopPadding = pxToDp(5);
                    }
                    otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvUserName.setText(userNameText);
                    otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_left);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        otherMessageViewHolder.llRoot.setPaddingRelative(pxToDp(10), pxToDp(4), 0, pxToDp(4));
                        otherMessageViewHolder.tvUserName.setPaddingRelative(pxToDp(15), pxToDp(7), pxToDp(10), pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPaddingRelative(pxToDp(15), pxToDp(tvMsgTopPadding), pxToDp(2), pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPaddingRelative(pxToDp(5), pxToDp(0), 0, pxToDp(0));
                    } else {
                        otherMessageViewHolder.llRoot.setPadding(pxToDp(10), pxToDp(4), 0, pxToDp(4));
                        otherMessageViewHolder.tvUserName.setPadding(pxToDp(15), pxToDp(7), pxToDp(10), pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPadding(pxToDp(15), pxToDp(tvMsgTopPadding), pxToDp(2), pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(5), pxToDp(0), 0, pxToDp(0));
                    }
                }
                if (currentOrderItem.getSentAtUtc().isEmpty()) {
                    otherMessageViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    otherMessageViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem.getSentAtUtc())));
                    otherMessageViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
                if (otherMessageViewHolder.tvUserName.length() > (otherMessageViewHolder.tvMsg.length() + otherMessageViewHolder.tvTime.length())
                        && otherMessageViewHolder.tvUserName.getVisibility() == View.VISIBLE) {
                    int length;
                    switch (otherMessageViewHolder.tvMsg.length()) {
                        case 1:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() + 2;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        case 2:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length();
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        case 3:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                        default:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.hippo_space));
                            }
                            break;
                    }

                }
                NinePatchDrawable drawable2 = (NinePatchDrawable) otherMessageViewHolder.llMessageBg.getBackground();
                drawable2.setColorFilter(fuguColorConfig.getFuguBgMessageFrom(), PorterDuff.Mode.MULTIPLY);
                if (!currentOrderItem.getThumbnailUrl().isEmpty()) {
                    new RequestOptions();
                    RequestOptions myOptions = RequestOptions
                            .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                            .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                    Glide.with(activity).load(currentOrderItem.getThumbnailUrl())
                            .apply(myOptions)
                            .into(otherMessageViewHolder.ivMsgImage);
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.GONE);
                }

                otherMessageViewHolder.rlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImageDialog(activity, currentOrderItem.getUrl());
                    }
                });

                if (currentOrderItem.getMessageType() == FILE_MESSAGE) {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileName.setText(currentOrderItem.getFileName());

                    otherMessageViewHolder.ivDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.GONE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.VISIBLE);

                            currentOrderItem.setDownloadId(downloadFile(currentOrderItem.getUrl(),
                                    currentOrderItem.getFileName(),
                                    currentOrderItem.getFileExtension()));
                        }
                    });

                    otherMessageViewHolder.rlStopDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.GONE);
                            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                            if (manager != null) {
                                manager.remove(currentOrderItem.getDownloadId());
                            }

                        }
                    });

                    otherMessageViewHolder.llFileDetails.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileSize.setText(currentOrderItem.getFileSize());
                    otherMessageViewHolder.tvExtension.setText(currentOrderItem.getFileExtension());
                } else {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.GONE);
                }


                if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) otherMessageViewHolder
                            .rlCustomAction.getLayoutParams();

                    // increase left margin if background is chat_bg_left
                    if (otherMessageViewHolder.llMessageBg.getBackground().getConstantState() == ContextCompat.getDrawable(activity, R.drawable.hippo_chat_bg_left).getConstantState()) {
                        layoutParams.setMargins(pxToDp(13), pxToDp(10), pxToDp(10), pxToDp(10));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            layoutParams.setMarginStart(pxToDp(13));
                            layoutParams.setMarginEnd(dp_10);
                        }
                    } else {
                        layoutParams.setMargins(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));

                    }

                    CustomAction customAction = currentOrderItem.getCustomAction();
                    if (customAction != null) {
                        // title
                        if (customAction.getTitle() != null && !TextUtils.isEmpty(customAction.getTitle())) {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitle.setText(customAction.getTitle());
                        } else {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.GONE);
                        }

                        // title description
                        if (customAction.getTitleDescription() != null && !TextUtils.isEmpty(customAction.getTitleDescription())) {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitleDescription.setText(customAction.getTitleDescription());
                        } else {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.GONE);
                        }

                        // image
                        if (customAction.getImageUrl() != null && !TextUtils.isEmpty(customAction.getImageUrl())) {
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_bottom);
                            otherMessageViewHolder.ivActionImage.setVisibility(View.VISIBLE);
                            RequestOptions myOptions = RequestOptions
                                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                            Glide.with(activity).load(customAction.getImageUrl())
                                    .apply(myOptions)
                                    .into(otherMessageViewHolder.ivActionImage);
                        } else {
                            otherMessageViewHolder.ivActionImage.setVisibility(View.GONE);
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_all_sides);
                        }
                        // description
                        if (customAction.getDescriptionObjects() != null && customAction.getDescriptionObjects().size() != 0) {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionDescription.setLayoutManager(new LinearLayoutManager(activity));
                            otherMessageViewHolder.rvActionDescription.setNestedScrollingEnabled(false);
                            otherMessageViewHolder.rvActionDescription.setAdapter(new CustomActionDescriptionAdapter(activity,
                                    customAction.getDescriptionObjects()));
                        } else {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.GONE);
                        }
                        // buttons
                        if (customAction.getActionButtons() != null && customAction.getActionButtons().size() != 0) {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setNestedScrollingEnabled(false);

                            // set span size of grid
                            int span = 2;
                            int size = customAction.getActionButtons().size();
                            if (size == 1) {
                                span = 1;
                            } else if (size % 3 == 0) {
                                span = 3;
                            } else {
                                span = 2;
                            }

                            otherMessageViewHolder.rvActionButtons.setLayoutManager(new GridLayoutManager(activity, span));
                            otherMessageViewHolder.rvActionButtons.addItemDecoration(new GridDividerItemDecoration(activity));
                            otherMessageViewHolder.rvActionButtons.setAdapter(new CustomActionButtonsAdapter(activity,
                                    customAction.getActionButtons()));
                        } else {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.GONE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.GONE);
                        }
                    }
                } else {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.GONE);
                }


                break;
            case FUGU_ITEM_TYPE_SELF:
                final SelfMessageViewHolder selfMessageViewHolder = (SelfMessageViewHolder) holder;
                final Message currentOrderItem2 = ((EventItem) fuguItems.get(position)).getEvent();
                int start = 0;
                int firstMsgStart = 0;
                int startImage = 0;
                int endImage = 0;
                int right = pxToDp(17);

                if(isRightToLeft) {
                    start = pxToDp(4);
                    firstMsgStart = pxToDp(10);
                    startImage = pxToDp(2);
                    endImage = pxToDp(14);
                    right = pxToDp(10);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                    if (position != 0
                            && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {

                        selfMessageViewHolder.fuguLlRoot.setPaddingRelative(pxToDp(0), pxToDp(1), right, pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));

                    } else if (position != 0
                            && ((getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) != FUGU_ITEM_TYPE_SELF))
                            || (!(position - 1 < 0) && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF))) {
                        if (position == fuguItems.size() - 1) {
                            selfMessageViewHolder.fuguLlRoot.setPaddingRelative(start, pxToDp(1), right, pxToDp(1));
                            selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                        } else {
                            selfMessageViewHolder.fuguLlRoot.setPaddingRelative(start, pxToDp(1), right, pxToDp(4));
                            selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                        }

                    } else if (position != 0
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {
                        selfMessageViewHolder.fuguLlRoot.setPaddingRelative(start, pxToDp(4), pxToDp(10), pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                    } else {
                        if (position == fuguItems.size() - 1) {
                            selfMessageViewHolder.fuguLlRoot.setPaddingRelative(start, pxToDp(4), pxToDp(10), pxToDp(1));
                            selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                        } else {
                            selfMessageViewHolder.fuguLlRoot.setPaddingRelative(start, pxToDp(4), pxToDp(10), pxToDp(4));
                            selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                        }
                    }
                    if (position != 0 && (getItemViewType(position - 1) != FUGU_ITEM_TYPE_SELF)) {
                        selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_right);
                        //selfMessageViewHolder.FuguLlMessageBg.setPaddingRelative(firstMsgStart, pxToDp(0), pxToDp(12), pxToDp(0));
                        selfMessageViewHolder.fuguRlMessages.setPaddingRelative(firstMsgStart, pxToDp(0), pxToDp(12), pxToDp(0));
                        selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                    } else {
                        selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_right_normal);
                        selfMessageViewHolder.fuguRlMessages.setPaddingRelative(start, pxToDp(0), pxToDp(4), pxToDp(0));
                        selfMessageViewHolder.fuguRlImageMessage.setPaddingRelative(startImage, pxToDp(4), endImage, pxToDp(4));
                    }
                } else {


                    if (position != 0
                            && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {

                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(1), pxToDp(17), pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));

                    } else if (position != 0
                            && ((getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) != FUGU_ITEM_TYPE_SELF))
                            || (!(position - 1 < 0) && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF))) {
                        if (position == fuguItems.size() - 1) {
                            selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(1), pxToDp(17), pxToDp(1));
                            selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));
                        } else {
                            selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(1), pxToDp(17), pxToDp(4));
                            selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));
                        }

                    } else if (position != 0
                            && (position + 1 != fuguItems.size())
                            && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {
                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(4), pxToDp(10), pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));
                    } else {
                        if (position == fuguItems.size() - 1) {
                            selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(4), pxToDp(10), pxToDp(1));
                            selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));
                        } else {
                            selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0), pxToDp(4), pxToDp(10), pxToDp(4));
                            selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(0), pxToDp(4));
                        }
                    }
                    if (position != 0 && (getItemViewType(position - 1) != FUGU_ITEM_TYPE_SELF)) {
                        selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_right);
                        selfMessageViewHolder.fuguRlMessages.setPadding(pxToDp(0), pxToDp(0), pxToDp(12), pxToDp(0));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), pxToDp(4), pxToDp(4));
                    } else {
                        selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.hippo_chat_bg_right_normal);
                        selfMessageViewHolder.fuguRlMessages.setPadding(pxToDp(0), pxToDp(0), pxToDp(4), pxToDp(0));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0), pxToDp(4), 0, pxToDp(4));
                    }


                }


                NinePatchDrawable drawable3 = (NinePatchDrawable) selfMessageViewHolder.FuguLlMessageBg.getBackground();
                drawable3.setColorFilter(fuguColorConfig.getFuguBgMessageYou(), PorterDuff.Mode.MULTIPLY);

                selfMessageViewHolder.fuguTvMsg.setTextColor(fuguColorConfig.getFuguPrimaryTextMsgYou());
                selfMessageViewHolder.fuguTvMsg.setLinkTextColor(fuguColorConfig.getFuguPrimaryTextMsgYou());
                selfMessageViewHolder.fuguTvMsg.setAutoLinkMask(Linkify.ALL);
                selfMessageViewHolder.fuguTvTime.setTextColor(fuguColorConfig.getFuguSecondaryTextMsgYou());


                if (currentOrderItem2.getMessage().isEmpty()) {
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params2);
                    selfMessageViewHolder.fuguTvMsg.setTextSize(pxToDp(3));
                } else {
                    selfMessageViewHolder.fuguTvMsg.setText(currentOrderItem2.getMessage());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params);
                    selfMessageViewHolder.fuguTvMsg.setTextSize(17);
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.VISIBLE);
                }

                if (currentOrderItem2.getSentAtUtc().isEmpty()) {
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguTvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem2.getSentAtUtc())));
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.VISIBLE);
                }
//                FuguLog.e("currentOrderItem.getThumbnailUrl()", "==" + currentOrderItem2.getThumbnailUrl());

                if (!TextUtils.isEmpty(currentOrderItem2.getThumbnailUrl())) {
                    if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                        RequestOptions myOptions = RequestOptions
                                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                        Glide.with(activity).load(currentOrderItem2.getThumbnailUrl())
                                .apply(myOptions)
                                .into(selfMessageViewHolder.fuguIvMsgImage);
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.VISIBLE);
                        } else {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                        }
                    } else {
                        RequestOptions myOptions = RequestOptions
                                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
                        Glide.with(activity).load(currentOrderItem2.getThumbnailUrl())
                                .apply(myOptions)
                                .into(selfMessageViewHolder.fuguIvMsgImage);
                        selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                        selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                    }
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.GONE);
                }
                selfMessageViewHolder.fuguRlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                            return;
                        }
                        showImageDialog(activity, currentOrderItem2.getUrl());
                    }
                });

                if(currentOrderItem2.getIsMessageExpired() == 1) {
                    selfMessageViewHolder.llRetry.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.tvTryAgain.setTag(position);
                    selfMessageViewHolder.tvCancel.setTag(position);
                    selfMessageViewHolder.tvTryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mOnRetry != null) {
                                String muid = currentOrderItem2.getMuid();
                                int pos = selfMessageViewHolder.getAdapterPosition();
                                //int pos = (int) view.getTag();
                                mOnRetry.onMessageRetry(muid, pos);
                                //selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                            }
                        }
                    });

                    selfMessageViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //int pos = (int) view.getTag();
                            int pos = selfMessageViewHolder.getAdapterPosition();
                            if(mOnRetry != null) {
                                String muid = currentOrderItem2.getMuid();
                                mOnRetry.onMessageCancel(muid, pos);
                            }
                        }
                    });
                } else {
                    selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                }

                switch (currentOrderItem2.getMessageStatus()) {
                    case MESSAGE_UNSENT:
                    case MESSAGE_IMAGE_RETRY:
                    case MESSAGE_FILE_RETRY:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        // TODO: 27/08/18
                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!currentOrderItem2.isSent()) {
                                    selfMessageViewHolder.tvTryAgain.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            fuguChatActivity.sendMessage(selfMessageViewHolder.getAdapterPosition());
                                        }
                                    });
                                    selfMessageViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            fuguChatActivity.cancelMessage(selfMessageViewHolder.getAdapterPosition());
                                        }
                                    });
                                    if (TextUtils.isEmpty(currentOrderItem2.getMessage())) {
                                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                        selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                                        selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                                    } else {
                                        selfMessageViewHolder.llRetry.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                }
                            }
                        }, 0);*/


                        break;
                    case MESSAGE_READ:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguMessageRead(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_SENT:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_DELIVERED:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        if (Build.VERSION.SDK_INT >= 21) {
                            selfMessageViewHolder.fuguIvMessageState.getDrawable().setTint(ContextCompat.getColor(activity, R.color.fugu_drawable_color));
                        }
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                    default:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(fuguColorConfig.getFuguSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                }

                selfMessageViewHolder.fuguBtnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnRetry != null) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                            mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                    IMAGE_MESSAGE, null, currentOrderItem2.getMuid());
                        }
                    }
                });

                if (currentOrderItem2.getMessageType() == FILE_MESSAGE) {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileName.setText(currentOrderItem2.getFileName());

                    selfMessageViewHolder.fuguIvUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mOnRetry != null) {

                                FuguFileDetails fileDetails = new FuguFileDetails();
                                fileDetails.setFilePath(currentOrderItem2.getFilePath());
                                fileDetails.setFileExtension(currentOrderItem2.getFileExtension());
                                fileDetails.setFileSize(currentOrderItem2.getFileSize());
                                fileDetails.setFileName(currentOrderItem2.getFileName());

                                selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                                selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                                mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                        FILE_MESSAGE, fileDetails, currentOrderItem2.getMuid());
                            }
                        }
                    });

                    selfMessageViewHolder.fuguLlFileDetails.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileSize.setText(currentOrderItem2.getFileSize());
                    selfMessageViewHolder.fuguTvExtension.setText(currentOrderItem2.getFileExtension());
                } else {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.GONE);
                }

                if (currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.VISIBLE);
                } else if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                }

                selfMessageViewHolder.fuguLlFileRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Check and ask for Permissions
                        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                                PERMISSION_READ_FILE)) return;

//                        FuguLog.e("adapter file path", currentOrderItem2.getFilePath());

                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(currentOrderItem2.getFilePath());
                            photoPickerIntent.setData(Uri.fromFile(file));
                            activity.startActivity(photoPickerIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity.getApplicationContext(),
                                    activity.getString(R.string.fugu_file_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

        }
    }

    private void setView(LinearLayout llGalleryButtonLayout, int count) {
        llGalleryButtonLayout.removeAllViews();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Amit");
        strings.add("Gurmail");
        strings.add("Ankush");
        strings.add("Vishal");
        strings.add("Gagan");
        for (int i = 0; i < count; i++) {
            final int pos = i;
            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.hippo_layout_gallery_button, null);
            TextView textView = view.findViewById(R.id.tvButton);
            textView.setText(strings.get(i));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "Button" + String.valueOf(pos) + "clicked", Toast.LENGTH_SHORT).show();
                }
            });
            llGalleryButtonLayout.addView(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return fuguItems.get(position).getType();
    }

    private float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public int getItemCount() {
        return fuguItems.size();
    }

    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvMsg;
        private TextView tvTime;
        private RelativeLayout rlImageMessage;
        private ImageView ivMsgImage;
        private LinearLayout llMessageBg;
        private LinearLayout llRoot;
        private LinearLayout llFileRoot;
        private TextView tvFileName;
        private ImageView ivDownload;
        private RelativeLayout rlStopDownload, rlMessages;
        private LinearLayout llFileDetails;
        private TextView tvFileSize;
        private TextView tvExtension;
        // custom action components
        private RelativeLayout rlCustomAction;
        private ImageView ivActionImage;
        private TextView tvActionTitle;
        private TextView tvActionTitleDescription;
        private RecyclerView rvActionDescription;
        private RecyclerView rvActionButtons;
        private View vwActionButtonDivider;
        private LinearLayout llTextualContent;


        OtherMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llMessageBg = itemView.findViewById(R.id.llMessageBg);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            ivMsgImage = itemView.findViewById(R.id.ivMsgImage);
            llFileRoot = itemView.findViewById(R.id.llFileRoot);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            rlStopDownload = itemView.findViewById(R.id.rlStopDownload);
            llFileDetails = itemView.findViewById(R.id.llFileDetails);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvExtension = itemView.findViewById(R.id.tvExtension);
            rlMessages = itemView.findViewById(R.id.rlMessages);
            rlCustomAction = itemView.findViewById(R.id.layoutCustomAction);
            tvActionTitle = itemView.findViewById(R.id.tvActionTitle);
            rvActionDescription = itemView.findViewById(R.id.rvActionDescription);
            rvActionButtons = itemView.findViewById(R.id.rvActionButtons);
            ivActionImage = itemView.findViewById(R.id.ivActionImage);
            vwActionButtonDivider = itemView.findViewById(R.id.vwActionButtonDivider);
            tvActionTitleDescription = itemView.findViewById(R.id.tvActionDescription);
            llTextualContent = itemView.findViewById(R.id.llTextualContent);

            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            tvUserName.setTypeface(typeface); tvMsg.setTypeface(typeface); tvTime.setTypeface(typeface);
            tvFileName.setTypeface(typeface); tvExtension.setTypeface(typeface); tvActionTitle.setTypeface(typeface);
            tvActionTitleDescription.setTypeface(typeface);

        }
    }

    class SelfMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView fuguTvMsg, fuguTvTime;
        private RelativeLayout fuguRlImageMessage;
        private ImageView fuguIvMessageState, fuguIvMsgImage;
        private ProgressBar fuguPbLoading;
        private Button fuguBtnRetry;
        private LinearLayout FuguLlMessageBg;
        private LinearLayout fuguLlRoot;
        private TextView tvTryAgain;
        private TextView tvCancel;

        private LinearLayout fuguLlFileRoot;
        private TextView fuguTvFileName;
        private ImageView fuguIvUpload;
        private RelativeLayout fuguRlStopUpload, fuguRlMessages;
        private LinearLayout fuguLlFileDetails;
        private TextView fuguTvFileSize;
        private TextView fuguTvExtension;
        private LinearLayout llRetry;

        SelfMessageViewHolder(View itemView) {
            super(itemView);
            fuguLlRoot = itemView.findViewById(R.id.llRoot);
            FuguLlMessageBg = itemView.findViewById(R.id.llMessageBg);
            fuguTvMsg = itemView.findViewById(R.id.tvMsg);
            fuguTvTime = itemView.findViewById(R.id.tvTime);
            fuguRlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            fuguRlMessages = itemView.findViewById(R.id.rlMessages);
            fuguIvMessageState = itemView.findViewById(R.id.ivMessageState);
            fuguIvMsgImage = itemView.findViewById(R.id.ivMsgImage);
            fuguPbLoading = itemView.findViewById(R.id.pbLoading);
            fuguBtnRetry = itemView.findViewById(R.id.btnRetry);
            tvTryAgain = itemView.findViewById(R.id.tvTryAgain);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            llRetry = itemView.findViewById(R.id.llRetry);

            fuguLlFileRoot = itemView.findViewById(R.id.llFileRoot);
            fuguTvFileName = itemView.findViewById(R.id.tvFileName);
            fuguIvUpload = itemView.findViewById(R.id.ivUpload);
            fuguRlStopUpload = itemView.findViewById(R.id.rlStopUpload);
            fuguLlFileDetails = itemView.findViewById(R.id.llFileDetails);
            fuguTvFileSize = itemView.findViewById(R.id.tvFileSize);
            fuguTvExtension = itemView.findViewById(R.id.tvExtension);

            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            fuguTvMsg.setTypeface(typeface); fuguTvTime.setTypeface(typeface); tvCancel.setTypeface(typeface);
            tvTryAgain.setTypeface(typeface); fuguTvFileName.setTypeface(typeface); fuguTvFileSize.setTypeface(typeface);
            fuguTvExtension.setTypeface(typeface); fuguBtnRetry.setTypeface(typeface);
        }
    }

    class ForumViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvDataForm;

        ForumViewHolder(View itemView) {
            super(itemView);
            rvDataForm = itemView.findViewById(R.id.rvDataForm);
        }

    }

    class SimpleTextView extends RecyclerView.ViewHolder {

        private TextView tvText;

        public SimpleTextView(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);

        }
    }

    class QuickReplyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView list_qr;
        private TextView title_view_text;

        QuickReplyViewHolder(View itemView) {
            super(itemView);
            list_qr = itemView.findViewById(R.id.list_qr);
            title_view_text = itemView.findViewById(R.id.title_view_text);
        }

    }

    class SelfVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        public SelfVideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMsg, tvTime, callAgain, tvDuration;
        private ImageView ivCallIcon;
        public VideoViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            callAgain = itemView.findViewById(R.id.callAgain);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
        }
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {

        public MyCustomEditTextListener myCustomEditTextListener;
        private LinearLayout ratedLayout, askRateLayout, messageLayout;
        private TextView titleTxt, ratedTitle, ratedSubTitle, ratedMessage;
        private ImageView terribleImage, badImage, okayImage, goodImage, greatImage;
        private EditText editText;
        private Button sendBtn;
        private RelativeLayout sendFeedback;

        public RatingViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.title_view);
            ratedTitle = itemView.findViewById(R.id.rated_title);
            ratedSubTitle = itemView.findViewById(R.id.rated_sub_title);
            ratedMessage = itemView.findViewById(R.id.rated_message);

            editText = itemView.findViewById(R.id.ed_rating_txt);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);

            sendFeedback = itemView.findViewById(R.id.send_btn);
            sendBtn = itemView.findViewById(R.id.sendBtn);
            ratedLayout = itemView.findViewById(R.id.rated_layout);
            askRateLayout = itemView.findViewById(R.id.ask_rate_layout);
            messageLayout = itemView.findViewById(R.id.message_layout);

            terribleImage = itemView.findViewById(R.id.terrible_image);
            badImage = itemView.findViewById(R.id.bad_image);
            okayImage = itemView.findViewById(R.id.okay_image);
            goodImage = itemView.findViewById(R.id.good_image);
            greatImage = itemView.findViewById(R.id.great_image);
        }
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llGalleryButtonLayout;
        private TextView tvButton;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            llGalleryButtonLayout = itemView.findViewById(R.id.llGalleryButtonLayout);

        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;

        DateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            tvDate.setTypeface(typeface);
        }

    }

    private void showImageDialog(Context activity, String imgUrl) {

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog);
            @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 1.0f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            ZoomageView ivImage = dialog.findViewById(R.id.ivImage);
            RequestOptions myOptions = RequestOptions
                    .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(ContextCompat.getDrawable(activity, R.drawable.hippo_placeholder));
            Glide.with(activity).load(imgUrl)
                    .apply(myOptions)
                    .into(ivImage);
            TextView tvCross = dialog.findViewById(R.id.tvCross);
            tvCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnRatingListener {
        void onSubmitRating(String text, Message currentOrderItem, int position);

        void onRatingSelected(int rating, Message currentOrderItem);

        void onFormDataCallback(Message currentOrderItem);
    }

    public interface onVideoCall {
        void onVideoCallClicked(int callType);
    }

    private String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }
}