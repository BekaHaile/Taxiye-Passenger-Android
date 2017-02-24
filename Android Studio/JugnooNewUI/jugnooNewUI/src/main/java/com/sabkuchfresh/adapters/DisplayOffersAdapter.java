package com.sabkuchfresh.adapters;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class DisplayOffersAdapter extends RecyclerView.Adapter<DisplayOffersAdapter.ViewHolderReviewImage> {

    private FreshActivity activity;
    private ArrayList<String> data;
    private boolean displayOnlyTerms;


    public DisplayOffersAdapter(FreshActivity activity,boolean displayOnlyTerms) {
        this.activity = activity;
        this.displayOnlyTerms=displayOnlyTerms;

    }



    @Override
    public DisplayOffersAdapter.ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_menu_offer_heading, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderReviewImage(v);
    }

    @Override
    public void onBindViewHolder(DisplayOffersAdapter.ViewHolderReviewImage holder, int position) {
        try {


            holder.tvTerms.setText("1. 20% cashback on your first Menus order.\n2. Minimum order size should be Rs 500.\n3. Cashback will be credited to your Jugnoo wallet within 24 hours.");




        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    private void setUpOfferTitle(String heading, String subHeading,TextView textView) {
        if(TextUtils.isEmpty(heading)){
            textView.setText(null);
            return ;
        }


        textView.setTypeface(Fonts.mavenMedium(activity), Typeface.NORMAL);
        SpannableString spannableContent =null;

        if(subHeading==null || subHeading.trim().length()<1)
            new SpannableString(heading);
        else
            new SpannableString(heading + "\n" + subHeading);

        spannableContent.setSpan(new StyleSpan(Typeface.BOLD),0,heading.length(),0);
        spannableContent.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.color_orange_menus)), 0, heading.length(), 0);
        if(subHeading!=null && subHeading.trim().length()>0)
            spannableContent.setSpan(new RelativeSizeSpan(0.6f), spannableContent.length()-subHeading.length(),spannableContent.length(), 0);
        textView.setText(spannableContent);

    }
    @Override
    public int getItemCount() {
//        return data == null ? 0 : data.size();
        return 1;
    }

    class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageButton btnDown;
        public TextView tvTerms;
        public View divider;
        public ViewHolderReviewImage(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_offer_title);
            tvTerms = (TextView) itemView.findViewById(R.id.tv_terms_and_conditions);
            btnDown = (ImageButton) itemView.findViewById(R.id.ib_arrow);
            divider =  itemView.findViewById(R.id.divider);
            if(displayOnlyTerms) {
                tvTitle.setVisibility(View.GONE);
                btnDown.setVisibility(View.GONE);
                tvTerms.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
            }
            else{
              tvTerms.setVisibility(View.VISIBLE);
               divider.setVisibility(View.VISIBLE);
            }
        }

    }



}
