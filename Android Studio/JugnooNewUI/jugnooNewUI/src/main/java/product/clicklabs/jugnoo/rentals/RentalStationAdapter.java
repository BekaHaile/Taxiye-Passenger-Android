package product.clicklabs.jugnoo.rentals;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.models.Region;

public class RentalStationAdapter extends RecyclerView.Adapter<RentalStationAdapter.ViewHolder> {


    private List<Region.Stations> list;
    Context context;


    public RentalStationAdapter(Context context, List<Region.Stations> list) {
        this.list = list;
        this.context = context;
    }

    public interface RentalStationAdapterOnClickHandler {
        void onStationClick(SearchResult autoCompleteSearchResult);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_rental_destination, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.imageViewIcon.setImageResource(R.drawable.ic_loc_other);

        final String name = list.get(i).getName();
        final String address = list.get(i).getAddress();
        final Double latitude = list.get(i).getLatitude();
        final Double longitude = list.get(i).getLongitude();

        viewHolder.textViewName.setText(name);
        viewHolder.textViewAddress.setText(address);
        final SearchResult autoCompleteSearchResult = new SearchResult(name, address, "", latitude, longitude, 0, 1, 0);

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RentalStationAdapterOnClickHandler) context).onStationClick(autoCompleteSearchResult);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewAddress;
        ImageView imageViewIcon;
        ConstraintLayout constraintLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewName = itemView.findViewById(R.id.textViewSearchName);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
        }
    }
}
