package com.sabkuchfresh.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;


/**
 * Created by Shankar on 3/12/17.
 */
public class RestaurantQuerySuggestionsAdapter extends RecyclerView.Adapter<RestaurantQuerySuggestionsAdapter.ViewHolderRestSuggestion> {

    private ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions;
    private Callback callback;

    public RestaurantQuerySuggestionsAdapter(ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions,
                                             Callback callback) {
        this.suggestions = suggestions;
        this.callback = callback;
    }

    public void setList(ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions){
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderRestSuggestion onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed_restaurant_suggestion, parent, false);
        return new ViewHolderRestSuggestion(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderRestSuggestion holder, int position) {
        try {
            SuggestRestaurantQueryResp.Suggestion suggestion = suggestions.get(position);
            holder.tvRestName.setText(suggestion.getName());
            holder.tvRestAddress.setText(suggestion.getAddress());

            RelativeLayout.LayoutParams paramsSep = (RelativeLayout.LayoutParams) holder.vSep.getLayoutParams();
            if(position == 0){
                paramsSep.setMargins(0, 0, 0, 0);
                paramsSep.setMarginStart(0);
                paramsSep.setMarginEnd(0);
            } else {
                int margin = holder.vSep.getContext().getResources().getDimensionPixelSize(R.dimen.dp_20);
                paramsSep.setMargins(margin, 0, margin, 0);
                paramsSep.setMarginStart(margin);
                paramsSep.setMarginEnd(margin);
            }
            holder.vSep.setLayoutParams(paramsSep);

            holder.relative.setTag(position);
            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        if(suggestions.get(pos).getId() > 0) {
                            callback.onSuggestionClick(pos, suggestions.get(pos));
                        }
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
        return suggestions == null ? 0 : suggestions.size();
    }


    class ViewHolderRestSuggestion extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public TextView tvRestName, tvRestAddress;
        public View vSep;
        public ViewHolderRestSuggestion(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            tvRestName = (TextView) itemView.findViewById(R.id.tvRestName);
            tvRestAddress = (TextView) itemView.findViewById(R.id.tvRestAddress);
            vSep = itemView.findViewById(R.id.vSep);
        }
    }

    public interface Callback{
        void onSuggestionClick(int position, SuggestRestaurantQueryResp.Suggestion suggestion);
    }
}
