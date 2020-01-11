package com.sabkuchfresh.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 13/06/17.
 */

public class CheckoutRequestCancellationAdapter extends RecyclerView.Adapter<CheckoutRequestCancellationAdapter.MyViewHolder> implements ItemListener{


    private LayoutInflater layoutInflater;
    private ArrayList<String> cancellationReason;
    private int recentSelectedPosition = RecyclerView.NO_POSITION;
    private RecyclerView recyclerView;
    private Button submitButton;
    private Button backButton;
    public CheckoutRequestCancellationAdapter(Activity activity, ArrayList<String> cancellationReason, RecyclerView recyclerView, Button submitButton, Button backButton) {
        layoutInflater = LayoutInflater.from(activity);
        this.cancellationReason = cancellationReason;
        this.recyclerView = recyclerView;
        this.submitButton  = submitButton;
        this.backButton  = backButton;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(layoutInflater.inflate(R.layout.item_checkout_request_payment_cancel_reason,parent,false),this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.radioButton.setText(cancellationReason.get(position));

    }

    @Override
    public int getItemCount() {
        return cancellationReason==null?0:cancellationReason.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        if(viewClicked.getId()==R.id.rb_reason){
            int position = recyclerView.getChildAdapterPosition(parentView);
            if(position!= RecyclerView.NO_POSITION){
                togglePosition(false,recentSelectedPosition);
                togglePosition(true,position);
                recentSelectedPosition = position;
                submitButton.setEnabled(true);
                backButton.setSelected(submitButton.isEnabled());

            }

        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RadioButton radioButton;
        private CompoundButton.OnCheckedChangeListener checkedChangeListener ;
        public MyViewHolder(final View itemView, final ItemListener itemListener) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.rb_reason);
            checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    itemListener.onClickItem(radioButton,itemView);
                }
            };
            radioButton.setOnCheckedChangeListener(checkedChangeListener);
        }
    }

    public String  getSelectedItem(){
        return recentSelectedPosition >=0 && recentSelectedPosition <cancellationReason.size()?cancellationReason.get(recentSelectedPosition):null;
    }

    public void setItemPosition(int posSelected){
        if(recentSelectedPosition!=posSelected) {
            togglePosition(false, recentSelectedPosition);
            togglePosition(true, posSelected);
            recentSelectedPosition = posSelected;
        }

    }

    private  void togglePosition(boolean check,int position) {
        if(position!=RecyclerView.NO_POSITION){
            MyViewHolder viewHolder= (MyViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            viewHolder.radioButton.setOnCheckedChangeListener(null);
            viewHolder.radioButton.setChecked(check);
            viewHolder.radioButton.setOnCheckedChangeListener(viewHolder.checkedChangeListener);

        }
    }


}
