package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SortResponseModel;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class FreshSortingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<SortResponseModel> sortArray;
    private Callback callback;

    public FreshSortingAdapter(FreshActivity activity, ArrayList<SortResponseModel> sortArray, Callback callback) {
        this.activity = activity;
        this.sortArray = sortArray;
        this.callback = callback;
    }

    public void setList(ArrayList<SortResponseModel> sortArray){
        this.sortArray = sortArray;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_sort, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            SortResponseModel slot = sortArray.get(position);

                ((ViewHolderSlot)holder).textViewSlotTime.setText(slot.name);
                if(!(slot.check)){
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(0);
                } else{
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(R.drawable.ic_tick);
                }

            if(position == sortArray.size()-1){
                ((ViewHolderSlot)holder).viewDivider.setVisibility(View.GONE);
            } else{
                ((ViewHolderSlot)holder).viewDivider.setVisibility(View.VISIBLE);
            }
//                if(slot.check){
//                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(1.0f);
//                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(1.0f);
//                } else{
//                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(0.4f);
//                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(0.4f);
//                }
//                ((ViewHolderSlot)holder).linear.setEnabled(slot.check);
                ((ViewHolderSlot)holder).linear.setTag(position);
                ((ViewHolderSlot)holder).linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            callback.onSlotSelected(pos, sortArray.get(pos));
                            for(int i=0;i<sortArray.size();i++) {
                                if(i==pos) {
                                    sortArray.get(i).setCheck(true);
                                } else {
                                    sortArray.get(i).setCheck(false);
                                }
                            }
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return sortArray == null ? 0 : sortArray.size();
    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        private ImageView imageViewRadio;
        public TextView textViewSlotTime;
        public View viewDivider;
        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
            textViewSlotTime = (TextView)itemView.findViewById(R.id.textViewSlotTime); textViewSlotTime.setTypeface(Fonts.mavenRegular(context));
            viewDivider = (View) itemView.findViewById(R.id.viewDivider);
        }
    }


    public interface Callback {
        void onSlotSelected(int position, SortResponseModel slot);
    }

}
