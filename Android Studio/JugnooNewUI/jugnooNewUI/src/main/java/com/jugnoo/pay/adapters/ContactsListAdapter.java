package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by cl-macmini-38 on 06/06/16.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.MyViewHolder> {
    List<SelectUser> selectUsersList;
    public  int selectedPosition;
    private Activity activity;
    private ArrayList<SelectUser> arraylist;
    private RecyclerViewClickListener clickListener;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_recycler_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public ContactsListAdapter(Activity activity, List<SelectUser> selectUsers, RecyclerViewClickListener clickListener)
    {
        this.selectUsersList = selectUsers;
        this.activity = activity;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(selectUsers);
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.contactNameTxt.setText(selectUsersList.get(position).getName());
        String num = selectUsersList.get(position).getPhone().replace(" ","").trim();
        holder.mobileTxt.setText(activity.getString(R.string.mobile_number_format, num));

        // Set image if exists
        try {

            if (selectUsersList.get(position).getThumb() != null) {
                Picasso.with(activity).load(selectUsersList.get(position).getThumb())
                        .transform(new CircleTransform())
                        .into(holder.contactImage);

            } else {
                Picasso.with(activity).load(R.drawable.icon_user)
                        .transform(new CircleTransform())
                        .into(holder.contactImage);
            }
        } catch (Exception e) {
            // Add default picture
            holder.contactImage.setImageResource(R.drawable.icon_user);
            e.printStackTrace();
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        selectUsersList.clear();
        if (charText.length() == 0) {
            selectUsersList.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)
                        || wp.getPhone().toLowerCase(Locale.getDefault()).contains(charText)) {
                    selectUsersList.add(wp);
                }

            }

            if (selectUsersList.size() == 0) {
                SelectUser selectUser = new SelectUser();
                charText = charText.replace(" ", "");
                selectUser.setPhone(CommonMethods.extractNumber(charText));
                if (selectUser.getPhone().length() == 0) {
                    selectUser.setPhone(charText);
                    selectUser.setName(activity.getString(R.string.vpa_address));
                } else
                    selectUser.setName(activity.getString(R.string.unknown));
                selectUsersList.add(selectUser);

            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        return this.selectUsersList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView contactNameTxt,mobileTxt;
         public ImageView contactImage;
          public MyViewHolder(View view) {
            super(view);
              contactNameTxt = (TextView) view.findViewById(R.id.contact_name_txt); contactNameTxt.setTypeface(Fonts.mavenRegular(activity));
              mobileTxt = (TextView) view.findViewById(R.id.mobile_txt); mobileTxt.setTypeface(Fonts.mavenRegular(activity));
              contactImage = (ImageView) view.findViewById(R.id.contact_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.recyclerViewListClicked(view,getAdapterPosition());
        }
    }



}
