package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class SavedPlacesAdapter extends BaseAdapter{

    private class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress;
        ImageView imageViewType, imageViewSep, imageViewEdit;
        RelativeLayout relative;
        int id;
    }

    private Context context;
    private LayoutInflater mInflater;
    private ViewHolderSearchItem holder;
    private Callback callback;
    private boolean showEditIcon, separatorOnTop;

    private ArrayList<SearchResult> searchResults;

    public SavedPlacesAdapter(Context context, ArrayList<SearchResult> searchResults, Callback callback,
                              boolean showEditIcon, boolean separatorOnTop)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResults = searchResults;
            this.callback = callback;
            this.showEditIcon = showEditIcon;
            this.separatorOnTop = separatorOnTop;
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
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);
            holder.imageViewEdit = (ImageView) convertView.findViewById(R.id.imageViewEdit);

            holder.relative.setTag(holder);
            holder.imageViewEdit.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            holder.textViewSearchName.setText(searchResults.get(position).getName());
            holder.textViewSearchAddress.setText(searchResults.get(position).getAddress());
			holder.imageViewType.setVisibility(View.VISIBLE);
			holder.imageViewType.setImageResource(R.drawable.ic_loc_other);

            holder.imageViewSep.setVisibility(View.VISIBLE);

            holder.imageViewEdit.setVisibility(showEditIcon ? View.VISIBLE : View.GONE);
            if(separatorOnTop){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imageViewSep.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                params.setMargins(0, 0, 0, 0);
                holder.imageViewSep.setLayoutParams(params);
                holder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
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

            holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        callback.onEditClick(autoCompleteSearchResult);
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
        void onEditClick(SearchResult searchResult);
    }

}
