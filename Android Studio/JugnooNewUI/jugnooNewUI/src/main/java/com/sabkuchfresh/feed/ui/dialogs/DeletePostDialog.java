package com.sabkuchfresh.feed.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
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

    public<T extends DeleteDialogCallback> DeletePostDialog(@NonNull T deleteDialogCall, @StyleRes int themeResId, Activity context) {
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
        wlp.gravity = Gravity.BOTTOM;
        wlp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width=WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations=R.style.dialog_anim_from_bottom;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black background during animation
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
