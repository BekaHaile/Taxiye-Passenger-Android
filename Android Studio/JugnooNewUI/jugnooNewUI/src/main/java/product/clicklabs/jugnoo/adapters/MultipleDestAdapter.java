package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.ASSL;

import static product.clicklabs.jugnoo.Constants.STOP_PENDING;
import static product.clicklabs.jugnoo.Constants.STOP_REACHED;

public class MultipleDestAdapter extends RecyclerView.Adapter<MultipleDestAdapter.viewholder> {

    private LayoutInflater layoutInflater;

    private ArrayList<AutoData.MultiDestData> multiDestDataArrayList;
    private MultiDestClickListener multiDestClickListener;
    private int size;
    private Activity activity;

    public MultipleDestAdapter(Activity activity, MultiDestClickListener multiDestClickListener, ArrayList<AutoData.MultiDestData> multiDestDataArrayList) {
        layoutInflater = activity.getLayoutInflater();
        this.multiDestClickListener = multiDestClickListener;
        this.multiDestDataArrayList = multiDestDataArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.multidest_viewholder, parent, false);
        return new viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        size = multiDestDataArrayList.size();
        if (position == size - 1) {
            holder.ivLineLower.setVisibility(View.INVISIBLE);
            if (multiDestDataArrayList.get(position) == null) {
                holder.imageViewDropAdd.setVisibility(View.GONE);
                holder.imagViewDropCross.setVisibility(View.GONE);
            } else {
                if (size >= 4) {
                    holder.imageViewDropAdd.setVisibility(View.GONE);
                    if(singlePendingStopRemaining())
                        holder.imagViewDropCross.setVisibility(View.GONE);
                    else
                        holder.imagViewDropCross.setVisibility(View.VISIBLE);
                } else {
                    holder.imageViewDropAdd.setVisibility(View.VISIBLE);
                    holder.imagViewDropCross.setVisibility(View.GONE);
                }
            }

        } else {
            holder.ivLineLower.setVisibility(View.VISIBLE);
            holder.imageViewDropAdd.setVisibility(View.GONE);
            holder.imagViewDropCross.setVisibility(View.VISIBLE);
        }

        if (multiDestDataArrayList.get(position) != null) {
            SearchResult searchResult = null;
            if (multiDestDataArrayList.get(position).getLatlng() != null) {
                searchResult = HomeUtil.getNearBySavedAddress(activity, multiDestDataArrayList.get(position).getLatlng(), false);
            }
            if (searchResult != null)
                holder.textViewDestSearch.setText(searchResult.getName());
            else
                holder.textViewDestSearch.setText(multiDestDataArrayList.get(position).getDropAddress());

            if (HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING
                    ||multiDestDataArrayList.get(position).getStopReachStatus()==STOP_REACHED) {
                holder.itemView.setEnabled(false);
                holder.textViewDestSearch.setTextColor(activity.getResources().getColor(R.color.red));
                holder.imagViewDropCross.setVisibility(View.GONE);
            }
            else{
                holder.itemView.setEnabled(true);
                holder.textViewDestSearch.setTextColor(activity.getResources().getColor(R.color.text_color));

            }
        } else
            holder.textViewDestSearch.setText("Enter Destination");


    }
    private boolean singlePendingStopRemaining(){
        if(multiDestDataArrayList.size()==4)
            for(int i=0;i<3;i++){
                if(multiDestDataArrayList.get(i).getStopReachStatus()==STOP_PENDING)
                    return false;
            }
        return true;
    }

    public void itemRemoved(int position) {
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {

        return multiDestDataArrayList.size();

    }

    public interface MultiDestClickListener {
        void onClick(int position);

        void crossClicked(int position);

        void addClicked();
    }

    class viewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewDestSearch)
        TextView textViewDestSearch;
        @BindView(R.id.imageViewDropCross)
        View imagViewDropCross;
        @BindView(R.id.imageViewDropAdd)
        View imageViewDropAdd;
        @BindView(R.id.ivLineLower)
        View ivLineLower;
        View itemView;

        public viewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (multiDestClickListener != null) {
                        multiDestClickListener.onClick(getAdapterPosition());
                    }
                }
            });
            imagViewDropCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (multiDestClickListener != null) {
                        multiDestDataArrayList.remove(getAdapterPosition());
                        MultipleDestAdapter.this.notifyItemRemoved(getAdapterPosition());
                        MultipleDestAdapter.this.notifyItemRangeChanged(0, getItemCount());
                        multiDestClickListener.crossClicked(getAdapterPosition());
                    }
                }
            });
            imageViewDropAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    multiDestDataArrayList.add(null);
                    MultipleDestAdapter.this.notifyItemInserted(multiDestDataArrayList.size() - 1);
                    MultipleDestAdapter.this.notifyItemRangeChanged(0, getItemCount());
                }
            });
        }
    }
}
