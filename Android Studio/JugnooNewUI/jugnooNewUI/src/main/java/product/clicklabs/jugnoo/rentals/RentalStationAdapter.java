package product.clicklabs.jugnoo.rentals;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabkuchfresh.utils.Utils;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.models.Region;

public class RentalStationAdapter extends RecyclerView.Adapter<RentalStationAdapter.ViewHolder> {


    private List<Region.Stations> list;
    private Context context;


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
        final double latitude = list.get(i).getLatitude();
        final double longitude = list.get(i).getLongitude();
        final double distance = list.get(i).getDistance();

        if (i == list.size() - 1) {
            viewHolder.imageViewSeparator.setVisibility(View.INVISIBLE);
        }

        viewHolder.textViewName.setText(name);
        viewHolder.textViewAddress.setText(address);
        viewHolder.textViewDistance.setText(Utils.getDecimalFormat2Decimal().format(distance / 1000.0)+" Km");
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
        TextView textViewDistance;
        ImageView imageViewSeparator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSeparator = itemView.findViewById(R.id.imageViewSep);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewName = itemView.findViewById(R.id.textViewSearchName);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
        }
    }
}
