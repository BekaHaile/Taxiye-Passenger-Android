package com.sabkuchfresh.pros.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 15/06/17.
 */

public class ProsProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

    private Context context;
    private List<ProsProductData.ProsProductDatum> prosProductData;
    private Callback callback;
    private RecyclerView recyclerView;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;


    public ProsProductsAdapter(Context context, Callback callback, RecyclerView recyclerView) {
        this.context = context;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }

    public synchronized void setResults(List<ProsProductData.ProsProductDatum> prosProductData){
        this.prosProductData = prosProductData;
        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pros_product, parent, false);
            return new MainViewHolder(v, this);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            return new ViewTitleHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if(position == prosProductData.size()) {
            return BLANK_ITEM;
        }
        return MAIN_ITEM;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            MainViewHolder mHolder = ((MainViewHolder) holder);
            ProsProductData.ProsProductDatum prosProductDatum = prosProductData.get(position);
            mHolder.tvItemName.setText(prosProductDatum.getName());
            try {
                if (!TextUtils.isEmpty(prosProductDatum.getImageUrl())) {
                    Picasso.with(context).load(prosProductDatum.getImageUrl())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(mHolder.ivItemImage);
                } else {
                    mHolder.ivItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(holder instanceof ViewTitleHolder) {
            ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        }
	}

    @Override
    public int getItemCount() {
        return prosProductData == null ? 0 : prosProductData.size()+1;
    }


    private class MainViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        private ImageView ivItemImage;
        public TextView tvItemName;
        public View vSep;
        MainViewHolder(final View itemView, final ItemListener itemListener) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            ivItemImage = (ImageView) itemView.findViewById(R.id.ivItemImage);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            vSep = itemView.findViewById(R.id.vSep);
            relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(relative, itemView);
                }
            });
        }
    }

    private class ViewTitleHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        ViewTitleHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    public interface Callback{
        void onProductClick(ProsProductData.ProsProductDatum prosProductDatum);
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if(pos != RecyclerView.NO_POSITION){
            switch(viewClicked.getId()){
                case R.id.relative:
                    callback.onProductClick(prosProductData.get(pos));
                    break;
            }
        }
    }
}
