package com.sabkuchfresh.feed.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 3/24/17.
 */

public class DeletePostDialog extends Dialog {

    private DeleteDialogCallback deleteDialogCallback;
    private FeedDetail feedDetail;
    private int positionInList;

    public<T extends DeleteDialogCallback>  DeletePostDialog(@NonNull T deleteDialogCall, @StyleRes int themeResId, Activity context) {
        super(context, themeResId);
        this.deleteDialogCallback=deleteDialogCall;
        setContentView(R.layout.dialog_feed_delete_post);
        TextView textViewCancel= (TextView) findViewById(R.id.tv_cancel);
        textViewCancel.setTypeface(textViewCancel.getTypeface(), Typeface.BOLD);
        findViewById(R.id.tv_edit_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                deleteDialogCallback.onEdit(feedDetail);
            }
        });
        findViewById(R.id.tv_delete_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                deleteDialogCallback.onDelete(feedDetail,positionInList);
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                deleteDialogCallback.onDismiss(feedDetail);
            }
        });
        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        setCanceledOnTouchOutside(true);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width=WindowManager.LayoutParams.MATCH_PARENT;
        wlp.dimAmount = 0.7f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);

    }


    public interface DeleteDialogCallback{
        void onDelete(FeedDetail feedDetail,int position);
        void onEdit(FeedDetail feedDetail);
        void onDismiss(FeedDetail feedDetail);
    }





    public void show(FeedDetail feedDetail,int postionInlist){
        this.feedDetail=feedDetail;
        this.positionInList =postionInlist;
        show();
    }
}
