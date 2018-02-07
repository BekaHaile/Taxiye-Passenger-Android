package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.sabkuchfresh.datastructure.UserContactObject;
import com.sabkuchfresh.fatafatchatpay.NewConversationActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 2/6/18.
 */

public class UserContactAdapter extends RecyclerView.Adapter<UserContactAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<UserContactObject> mContacts = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor
     *
     * @param context  calling context
     * @param contacts user contacts
     */
    public UserContactAdapter(Context context, ArrayList<UserContactObject> contacts) {
        mContext = context;
        mContacts = contacts;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.list_item_connections, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int pos = holder.getAdapterPosition();
        UserContactObject contactObject = mContacts.get(pos);

        if (contactObject.getUserName() != null) {
            holder.tvPersonName.setText(contactObject.getUserName());
        } else {
            holder.tvPersonName.setText("");
        }

        if (contactObject.getPhoneNumber() != null) {
            holder.tvPersonPhone.setText(contactObject.getPhoneNumber());
        } else {
            holder.tvPersonPhone.setText("");
        }

        if (contactObject.getUserImage() == null || contactObject.getUserImage().isEmpty() ||
                contactObject.getUserImage().equalsIgnoreCase(Constants.DEFAULT_IMAGE_URL)) {
            holder.ivPersonImage.setVisibility(View.GONE);
            holder.tvPersonInitial.setVisibility(View.VISIBLE);
            if (contactObject.getUserName() != null && !contactObject.getUserName().isEmpty()) {
                holder.tvPersonInitial.setText(contactObject.getUserName().substring(0, 1).toUpperCase());
            } else {
                holder.tvPersonInitial.setText("");
            }
            Glide.clear(holder.ivPersonImage);
        } else {
            holder.tvPersonInitial.setVisibility(View.GONE);
            holder.ivPersonImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(contactObject.getUserImage()).asBitmap()
                    .centerCrop()
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.placeholder_img))
                    .into(new BitmapImageViewTarget(holder.ivPersonImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivPersonImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Update contacts
     *
     * @param contactObjects contacts
     */
    public void updateContacts(ArrayList<UserContactObject> contactObjects) {
        this.mContacts = contactObjects;
        notifyDataSetChanged();
    }

    /**
     * @return Jugnoo contacts list
     */
    public ArrayList<UserContactObject> getJugnooContacts() {
        return mContacts;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPersonPhone, tvPersonName, tvPersonInitial;
        ImageView ivPersonImage;
        RelativeLayout rlMainContainer;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvPersonPhone = (TextView) itemView.findViewById(R.id.tvPersonPhone);
            tvPersonName = (TextView) itemView.findViewById(R.id.tvPersonName);
            tvPersonInitial = (TextView) itemView.findViewById(R.id.tvPersonInitial);
            ivPersonImage = (ImageView) itemView.findViewById(R.id.ivPersonImage);
            rlMainContainer =(RelativeLayout)itemView.findViewById(R.id.rlMainContainer);
            rlMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    ((NewConversationActivity)mContext).onContactSelected(getAdapterPosition());
                }
            });
        }
    }
}
