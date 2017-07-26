package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusCategoryCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<Item> subItems;
    private int categoryPos;
    private Category category;
    private Callback callback;
    private ArrayList<Category> categoriesSearched;


    private static final int SEARCHED_CATEGORY_ITEM = 3;

    public MenusCategoryCategoriesAdapter(Context context,ArrayList<Category> category,Callback callback) {
        this.context = context;
        this.callback = callback;
        this.categoriesSearched = category;
    }



    public void setList( ArrayList<Category> categoriesSearched){
        this.categoriesSearched = categoriesSearched;
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SEARCHED_CATEGORY_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_search_category, parent, false);
            return new MenusCategoryCategoriesAdapter.CategoryViewHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }




    @Override
    public int getItemViewType(int position) {
      return SEARCHED_CATEGORY_ITEM;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MenusCategoryCategoriesAdapter.CategoryViewHolder) {
            MenusCategoryCategoriesAdapter.CategoryViewHolder categoryViewHolder = ((MenusCategoryCategoriesAdapter.CategoryViewHolder) holder);
            categoryViewHolder.tvCategoryName.setText(categoriesSearched.get(position).getCategoryName().toUpperCase());
            categoryViewHolder.relative.setTag(position);
            categoryViewHolder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        callback.onCategoryClick(categoriesSearched.get(pos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            categoryViewHolder.borderBottom.setVisibility(position==categoriesSearched.size()-1?View.INVISIBLE:View.VISIBLE);
        }



	}


    @Override
    public int getItemCount() {
        return categoriesSearched == null ? 0 : categoriesSearched.size() ;
    }




    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout relative;
        public TextView tvCategoryName;
        public View borderBottom;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            borderBottom = itemView.findViewById(R.id.border_bottom);
        }
    }


    public interface Callback{

        void onCategoryClick(Category category);



    }









}
