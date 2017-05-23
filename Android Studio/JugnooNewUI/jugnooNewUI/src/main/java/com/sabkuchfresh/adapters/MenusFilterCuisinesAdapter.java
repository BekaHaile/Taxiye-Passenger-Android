package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.datastructure.FilterCuisine;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Shankar on 19/05/16.
 */
public class MenusFilterCuisinesAdapter extends RecyclerView.Adapter<MenusFilterCuisinesAdapter.ViewHolder> {

	private FreshActivity activity;
	private ArrayList<FilterCuisine> cuisines;
	private ArrayList<FilterCuisine> cuisinesToShow;
	private EditText editTextSearch;
	private Callback callback;

	public MenusFilterCuisinesAdapter(FreshActivity activity, ArrayList<FilterCuisine> cuisines, EditText editText, Callback callback) {
		this.activity = activity;
		this.cuisines = cuisines;
		this.cuisinesToShow = new ArrayList<>();
		this.cuisinesToShow.addAll(this.cuisines);
		this.editTextSearch = editText;

		this.editTextSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				editTextSearch.setError(searchVendors(s.toString()) == 0 ?
						MenusFilterCuisinesAdapter.this.activity.getString(R.string.no_cuisine_found) : null);
			}
		});
		this.callback = callback;
	}

	public ArrayList<FilterCuisine> getCuisines(){
		return cuisines;
	}

	private int searchVendors(String text){
		cuisinesToShow.clear();
		text = text.toLowerCase();
		if(TextUtils.isEmpty(text)){
			cuisinesToShow.addAll(cuisines);
		} else {
			for(FilterCuisine filterCuisine : cuisines){
				if(filterCuisine.getName().toLowerCase().contains(text)){
					cuisinesToShow.add(filterCuisine);
				}
			}
		}
		notifyDataSetChanged();
		return cuisinesToShow.size();
	}

	public void setList(ArrayList<FilterCuisine> cuisines) {
		this.cuisines = cuisines;
		this.cuisinesToShow.clear();
		this.cuisinesToShow.addAll(cuisines);
		notifyDataSetChanged();
	}

	@Override
	public MenusFilterCuisinesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_filter_cuisine, parent, false);
		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);
		ASSL.DoMagic(v);
		return new ViewHolder(v, activity);
	}

	@Override
	public void onBindViewHolder(MenusFilterCuisinesAdapter.ViewHolder holder, int position) {
		try {
			FilterCuisine filterCuisine = cuisinesToShow.get(position);
			holder.textViewCuisine.setText(filterCuisine.getName());
			holder.imageViewCheck.setImageResource(filterCuisine.getSelected() == 1 ? R.drawable.checkbox_signup_checked : R.drawable.check_box_unchecked);
			holder.imageViewSep.setVisibility(position == getItemCount()-1 ? View.GONE : View.VISIBLE);
			holder.relative.setTag(position);
			holder.relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int pos = (int) v.getTag();
						FilterCuisine filterCuisine = cuisinesToShow.get(pos);
						filterCuisine.setSelected(filterCuisine.getSelected() == 1 ? 0 : 1);
						if(callback != null) {
							callback.onCuisineClicked(filterCuisine);
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
		return cuisinesToShow == null ? 0 : cuisinesToShow.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		private ImageView imageViewCheck, imageViewSep;
		public TextView textViewCuisine;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			imageViewCheck = (ImageView) itemView.findViewById(R.id.imageViewCheck);
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
			textViewCuisine = (TextView) itemView.findViewById(R.id.textViewCuisine);
			textViewCuisine.setTypeface(Fonts.mavenMedium(context));
		}
	}


	public interface Callback{
		void onCuisineClicked(FilterCuisine filterCuisine);
	}

}
