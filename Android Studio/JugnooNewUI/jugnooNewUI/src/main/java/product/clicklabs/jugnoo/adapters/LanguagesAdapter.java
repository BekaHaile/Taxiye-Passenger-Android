package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.FetchActiveLocaleResponse;
import product.clicklabs.jugnoo.utils.LocaleHelper;


public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.MyViewHolder> implements ItemListener{

	private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<FetchActiveLocaleResponse.Locale> locales;
    private RecyclerView recyclerView;
    private Callback callback;

    public LanguagesAdapter(Context context, RecyclerView recyclerView, Callback callback) {
    	this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.callback = callback;
    }

    public void setList(ArrayList<FetchActiveLocaleResponse.Locale> locales){
    	this.locales = locales;
    	notifyDataSetChanged();
	}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.textview_language,parent,false);
        return new MyViewHolder(convertView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    	FetchActiveLocaleResponse.Locale locale = locales.get(position);
        holder.textView.setText(locale.getName());
        holder.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
				LocaleHelper.getLanguage(context).equals(locale.getLocale()) ? R.drawable.ic_fresh_sort_tick_svg : 0, 0);
    }

    @Override
    public int getItemCount() {
        return locales ==null?0: locales.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        if(viewClicked.getId()==R.id.textView){
            int position = recyclerView.getChildAdapterPosition(parentView);
            if(position != RecyclerView.NO_POSITION){
				callback.onLanguageClick(locales.get(position));
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        public MyViewHolder(final View itemView, final ItemListener itemListener) {
            super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(textView, itemView);
				}
			});
        }
    }

    public interface Callback{
		void onLanguageClick(FetchActiveLocaleResponse.Locale locale);
	}

}
