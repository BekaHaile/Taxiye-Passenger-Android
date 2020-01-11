package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.datastructure.FatafatTutorialData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 12/5/17.
 * Fatafat tutorial adapter
 */

public class FatafatTutorialAdapter extends RecyclerView.Adapter<FatafatTutorialAdapter.TutorialViewHolder> {

    private Context mContext;
    private ArrayList<FatafatTutorialData> mFatafatTutorialData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor
     *
     * @param context             calling context
     * @param fatafatTutorialData the fatafat tutorial data
     */
    public FatafatTutorialAdapter(Context context, ArrayList<FatafatTutorialData> fatafatTutorialData) {
        mContext = context;
        mFatafatTutorialData = fatafatTutorialData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public FatafatTutorialAdapter.TutorialViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View main = mLayoutInflater.inflate(R.layout.list_item_fatafat_tutorial, parent, false);
        return new TutorialViewHolder(main);
    }

    @Override
    public void onBindViewHolder(final FatafatTutorialAdapter.TutorialViewHolder holder, final int position) {
        final int mPosition = holder.getAdapterPosition();
        FatafatTutorialData fatafatTutorialData = mFatafatTutorialData.get(mPosition);

        // set data
        holder.mTvPointNo.setText(String.valueOf(mPosition + 1));
        if (fatafatTutorialData.getPointHeader() != null) {
            holder.mTvPointHeader.setText(fatafatTutorialData.getPointHeader());
        }
        if (fatafatTutorialData.getPointContent() != null) {
            holder.mTvPointContent.setText(fatafatTutorialData.getPointContent());
        }
        if (fatafatTutorialData.getImageUrl() != null && !TextUtils.isEmpty(fatafatTutorialData.getImageUrl())) {
            Picasso.with(mContext).load(fatafatTutorialData.getImageUrl())
                    .placeholder(R.drawable.ic_fresh_item_placeholder)
                    .error(R.drawable.ic_fresh_item_placeholder)
                    .into(holder.mImgVwCatIcon);
        } else {
            Picasso.with(mContext).cancelRequest(holder.mImgVwCatIcon);
        }

        // hide the timeline from the last item
        if (mPosition == mFatafatTutorialData.size() - 1) {
            holder.mRlTimeLine.setVisibility(View.INVISIBLE);
        } else {
            holder.mRlTimeLine.setVisibility(View.VISIBLE);
        }

        // hide the top view in case of first position
        if (mPosition == 0) {
            holder.mVwTopLine.setVisibility(View.INVISIBLE);
        } else {
            holder.mVwTopLine.setVisibility(View.VISIBLE);
        }

        holder.mllContent.post(new Runnable() {
            @Override
            public void run() {

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)holder.mllPoints.getLayoutParams();
                layoutParams.height=holder.mllContent.getMeasuredHeight();
                holder.mllPoints.requestLayout();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFatafatTutorialData.size();
    }

    /**
     * Tutorial ViewHolder
     */
    class TutorialViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvPointNo, mTvPointHeader, mTvPointContent;
        private ImageView mImgVwCatIcon;
        private RelativeLayout mRlTimeLine;
        private View mVwTopLine;
        private LinearLayout mllPoints,mllContent;

        TutorialViewHolder(final View itemView) {
            super(itemView);
            mTvPointNo = (TextView) itemView.findViewById(R.id.tvPointNo);
            mTvPointNo.setTypeface(mTvPointNo.getTypeface(), Typeface.BOLD);
            mTvPointHeader = (TextView) itemView.findViewById(R.id.tvHeader);
            mTvPointContent = (TextView) itemView.findViewById(R.id.tvContent);
            mImgVwCatIcon = (ImageView) itemView.findViewById(R.id.imgVwCatIcon);
            mRlTimeLine = (RelativeLayout) itemView.findViewById(R.id.rlTimeLine);
            mVwTopLine = itemView.findViewById(R.id.vwTopLine);
            mllPoints=(LinearLayout)itemView.findViewById(R.id.llPoints);
            mllContent=(LinearLayout)itemView.findViewById(R.id.llContent);
        }
    }
}
