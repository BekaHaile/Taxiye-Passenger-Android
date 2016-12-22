package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;

import com.jugnoo.pay.models.FetchPaymentAddressResponse;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cl-macmini-38 on 06/06/16.
 */
public class PaymentAddressAdapter extends RecyclerView.Adapter<PaymentAddressAdapter.MyViewHolder> {
    public ArrayList<FetchPaymentAddressResponse.VpaList> savedUsersList;
    public  int selectedPosition;
    private Activity activity;
    private ArrayList<FetchPaymentAddressResponse.VpaList> arraylist;
    private Callback clickListener;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_payment_address, parent, false);
        return new MyViewHolder(itemView);
    }

    public PaymentAddressAdapter(Activity activity, ArrayList<FetchPaymentAddressResponse.VpaList> savedUsersList, Callback clickListener)
    {
        this.savedUsersList = savedUsersList;
        this.activity = activity;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(savedUsersList);
        this.clickListener = clickListener;
    }


    public void setList(ArrayList<FetchPaymentAddressResponse.VpaList> savedUsersList){
        arraylist.clear();
        arraylist.addAll(savedUsersList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.contactNameTxt.setText(savedUsersList.get(position).getName());
        holder.mobileTxt.setText(savedUsersList.get(position).getVpa());

        try {
                Picasso.with(activity).load(R.drawable.icon_user)
                        .transform(new CircleTransform())
                        .into(holder.contactImage);
        } catch (Exception e) {
            Picasso.with(activity).load(R.drawable.icon_user)
                    .transform(new CircleTransform())
                    .into(holder.contactImage);
            e.printStackTrace();
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.recyclerViewListClicked(position);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onDelete(savedUsersList.get(position).getVpa());
            }
        });
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        savedUsersList.clear();
        if (charText.length() == 0) {
            savedUsersList.addAll(arraylist);
        } else {
            for (FetchPaymentAddressResponse.VpaList wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    savedUsersList.add(wp);
                }

            }

            /*if(selectUsersList.size()==0)
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

            }*/
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return savedUsersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout container;
        public TextView contactNameTxt,mobileTxt;
        public ImageView contactImage, ivDelete;
        public MyViewHolder(View view) {
            super(view);
              container = (LinearLayout) view.findViewById(R.id.container);
              contactNameTxt = (TextView) view.findViewById(R.id.contact_name_txt); contactNameTxt.setTypeface(Fonts.mavenRegular(activity));
              mobileTxt = (TextView) view.findViewById(R.id.mobile_txt); mobileTxt.setTypeface(Fonts.mavenRegular(activity));
              contactImage = (ImageView) view.findViewById(R.id.contact_image);
              ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        }

    }

    public interface Callback
    {
        void recyclerViewListClicked(int position);
        void onDelete(String vpa);
    }



}
