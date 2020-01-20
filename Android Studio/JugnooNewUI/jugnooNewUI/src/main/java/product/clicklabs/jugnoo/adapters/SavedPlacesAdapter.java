package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        ImageView imageViewType, imageViewSep, ivDeleteAddress, ivAddAddress;
        RelativeLayout relative;
        int id;
    }

    private Context context;
    private LayoutInflater mInflater;
    private ViewHolderSearchItem holder;
    private Callback callback;
    private boolean separatorOnTop, addSepMargins, showDelete, showAdd;

    private ArrayList<SearchResult> searchResults;

    public SavedPlacesAdapter(Context context, ArrayList<SearchResult> searchResults, Callback callback,
                              boolean separatorOnTop, boolean addSepMargins, boolean showDelete, boolean showAdd)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResults = searchResults;
            this.callback = callback;
            this.separatorOnTop = separatorOnTop;
            this.addSepMargins = addSepMargins;
            this.showDelete = showDelete;
            this.showAdd = showAdd;
        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    public void setList(ArrayList<SearchResult> searchResults){
        this.searchResults = searchResults;
        notifyDataSetChanged();
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
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenMedium(context));
            holder.textViewAddressUsed = (TextView) convertView.findViewById(R.id.textViewAddressUsed);
            holder.textViewAddressUsed.setTypeface(Fonts.mavenRegular(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);
            holder.ivDeleteAddress = (ImageView) convertView.findViewById(R.id.ivDeleteAddress);
            holder.ivAddAddress = (ImageView) convertView.findViewById(R.id.ivAddAddress);

            holder.relative.setTag(holder);
            holder.ivDeleteAddress.setTag(holder);
            holder.ivAddAddress.setTag(holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            SearchResult searchResult = searchResults.get(position);
            if(!TextUtils.isEmpty(searchResult.getName())
					&& !searchResult.getAddress().contains(searchResult.getName())){
                holder.textViewSearchName.setVisibility(View.VISIBLE);
                holder.textViewSearchName.setText(searchResult.getName());

				if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_HOME)){
					holder.textViewSearchName.setText(R.string.home);
				} else if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)){
					holder.textViewSearchName.setText(R.string.work);
				}

				holder.textViewSearchAddress.setText(searchResult.getAddress());
            } else {
				String nameForDisp = (searchResult.getAddress().contains(",")) ?
						searchResult.getAddress().substring(0, searchResult.getAddress().indexOf(","))
						: searchResult.getAddress();

				holder.textViewSearchName.setVisibility(TextUtils.isEmpty(nameForDisp) ? View.GONE : View.VISIBLE);
				holder.textViewSearchName.setText(nameForDisp);

				holder.textViewSearchAddress.setText(TextUtils.isEmpty(nameForDisp) ? searchResult.getAddress()
						: searchResult.getAddress().replace(nameForDisp+", ", ""));
			}

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
                params.setMarginStart(margin);
                params.setMarginEnd(margin);
                holder.imageViewSep.setLayoutParams(params);
                holder.imageViewSep.setBackgroundColor(ContextCompat.getColor(context, R.color.stroke_light_grey_alpha));
            } else {
                if(position == getCount()-1){
                    holder.imageViewSep.setVisibility(View.GONE);
                }
            }

            //seperator hidden by setting color transparent
			holder.imageViewSep.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));

            holder.ivDeleteAddress.setVisibility(showDelete ? View.VISIBLE : View.GONE);
            holder.ivAddAddress.setVisibility(showAdd ? View.VISIBLE : View.GONE);

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
            holder.relative.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
					try {
						holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        callback.onItemLongClick(autoCompleteSearchResult);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
                  });

            holder.ivDeleteAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        callback.onDeleteClick(autoCompleteSearchResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.ivAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        callback.onAddClick(autoCompleteSearchResult);
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

    public static abstract class Callback{
        public abstract void onItemClick(SearchResult searchResult);
        public void onItemLongClick(SearchResult searchResult){}
        public void onAddClick(SearchResult searchResult){}
        public abstract void onDeleteClick(SearchResult searchResult);
    }

}
