package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class SavedPlacesAdapter extends BaseAdapter{

    private class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress, textViewAddressUsed;
        ImageView imageViewType, imageViewSep;
        RelativeLayout relative;
        int id;
    }

    private Context context;
    private LayoutInflater mInflater;
    private ViewHolderSearchItem holder;
    private Callback callback;
    private boolean separatorOnTop, addSepMargins;

    private ArrayList<SearchResult> searchResults;

    public SavedPlacesAdapter(Context context, ArrayList<SearchResult> searchResults, Callback callback,
                              boolean separatorOnTop, boolean addSepMargins)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResults = searchResults;
            this.callback = callback;
            this.separatorOnTop = separatorOnTop;
            this.addSepMargins = addSepMargins;
        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderSearchItem();
            convertView = mInflater.inflate(R.layout.list_item_saved_place, null);

            holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName);
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenMedium(context));
            holder.textViewAddressUsed = (TextView) convertView.findViewById(R.id.textViewAddressUsed);
            holder.textViewAddressUsed.setTypeface(Fonts.mavenRegular(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            SearchResult searchResult = searchResults.get(position);
            holder.textViewSearchName.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(searchResult.getName())){
                holder.textViewSearchName.setVisibility(View.VISIBLE);
                holder.textViewSearchName.setText(searchResult.getName());
            }
            if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_HOME)){
                holder.textViewSearchName.setText(R.string.home);
            } else if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)){
                holder.textViewSearchName.setText(R.string.work);
            }

            holder.textViewSearchAddress.setText(searchResult.getAddress());

            holder.textViewAddressUsed.setVisibility(View.GONE);
            if(searchResult.getFreq() > 0) {
                if(searchResults.get(position).getFreq() <= 1){
                    holder.textViewAddressUsed.setText(context.getString(R.string.address_used_one_time_format,
                            String.valueOf(searchResults.get(position).getFreq())));
                } else{
                    holder.textViewAddressUsed.setText(context.getString(R.string.address_used_multiple_time_format,
                            String.valueOf(searchResults.get(position).getFreq())));
                }

                holder.textViewAddressUsed.setVisibility(View.VISIBLE);
            }

			holder.imageViewType.setVisibility(View.VISIBLE);
            if(searchResult.getType() == SearchResult.Type.RECENT){
                holder.imageViewType.setImageResource(R.drawable.ic_recent_loc);
            } else {
                if(Constants.TYPE_HOME.equalsIgnoreCase(searchResult.getName())){
                    holder.imageViewType.setImageResource(R.drawable.ic_home);
                } else if(Constants.TYPE_WORK.equalsIgnoreCase(searchResult.getName())){
                    holder.imageViewType.setImageResource(R.drawable.ic_work);
                } else {
                    holder.imageViewType.setImageResource(R.drawable.ic_loc_other);
                }
            }

            holder.imageViewSep.setVisibility(View.VISIBLE);

            if(separatorOnTop){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imageViewSep.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, 0);
                int margin = addSepMargins ? (int)(ASSL.Xscale() * 35f) : 0;
                params.setMargins(margin, 0, margin, 0);
                holder.imageViewSep.setLayoutParams(params);
                holder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
            } else {
                if(position == getCount()-1){
                    holder.imageViewSep.setVisibility(View.GONE);
                }
            }

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
					try {
						holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        callback.onItemClick(autoCompleteSearchResult);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
                  });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public interface Callback{
        void onItemClick(SearchResult searchResult);
    }

}
