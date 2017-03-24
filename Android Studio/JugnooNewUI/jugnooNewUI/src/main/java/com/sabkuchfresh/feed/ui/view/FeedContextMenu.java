package com.sabkuchfresh.feed.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sabkuchfresh.feed.utils.Utils;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;


/**
 * Created by froger_mcs on 15.12.14.
 */
public class FeedContextMenu extends LinearLayout {
    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(240);

    private FeedDetail feedItem =null;
    private int position;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
//        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(FeedDetail feedItem,int position) {
        this.feedItem = feedItem;
        this.position=position;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }

    @OnClick(R.id.btnReport)
    public void onReportClick() {
        if (onItemClickListener != null) {
            dismiss();
            FeedContextMenuManager.getInstance().toggleActivityState(false);
            onItemClickListener.onEditClick(feedItem,position);
        }
    }



    @OnClick(R.id.btnCancel)
    public void onCancelClick() {
        if (onItemClickListener != null) {
            dismiss();
            FeedContextMenuManager.getInstance().toggleActivityState(false);
            onItemClickListener.onDeleteClick(feedItem,position);
        }
    }

    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onEditClick(FeedDetail feedItem, int position);

        public void onDeleteClick(FeedDetail feedItem, int position);
    }
}