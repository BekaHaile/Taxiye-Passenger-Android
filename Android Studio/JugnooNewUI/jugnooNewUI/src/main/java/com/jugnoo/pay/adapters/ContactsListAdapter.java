package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        holder.mobileTxt.setText(num+"  Mobile");

        // Set image if exists
        try {

            if (selectUsersList.get(position).getThumb() != null) {
                /*if ( android.os.Build.VERSION.SDK_INT >= 19 && !selectUsersList.get(position).getThumb().isPremultiplied() )
                {
                    selectUsersList.get(position).getThumb().setPremultiplied( true );
                }*/
                //holder.contactImage.setImageBitmap(selectUsersList.get(position).getThumb());
                Picasso.with(activity).load(selectUsersList.get(position).getThumb()).into(holder.contactImage);
            } else {
                holder.contactImage.setImageResource(R.drawable.icon_user);
            }
            // Seting round image
//            Bitmap bm = BitmapFactory.decodeResource(holder.contactImage.getResources(), R.drawable.icon_logo); // Load default image
//            roundedImage = new RoundImage(bm);
//            v.imageView.setImageDrawable(roundedImage);
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
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    selectUsersList.add(wp);
                }

            }

            if(selectUsersList.size()==0)
            {
                    SelectUser selectUser = new SelectUser();
                    charText = charText.replace(" ", "");
                    selectUser.setPhone(CommonMethods.extractNumber(charText));
                    if(selectUser.getPhone().length()==0)
                    {
                        selectUser.setPhone(charText);
                        selectUser.setName("VPA Address");
                    }
                    else
                    selectUser.setName("Unknown");
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
              contactNameTxt = (TextView) view.findViewById(R.id.contact_name_txt);
              mobileTxt = (TextView) view.findViewById(R.id.mobile_txt);
              contactImage = (ImageView) view.findViewById(R.id.contact_image);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.recyclerViewListClicked(view,getAdapterPosition());
        }
    }



}
