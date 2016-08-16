package sabkuchfresh.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import sabkuchfresh.home.FreshActivity;
import sabkuchfresh.retrofit.model.DeliveryAddress;

/**
 * Created by Gurmail S. Kang on 5/13/16.
 */
public class FreshAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private List<DeliveryAddress> slots;
    private Callback callback;

    private static final int MAIN_VIEW = 0;
    private static final int ADD_VIEW = 1;


    public FreshAddressAdapter(FreshActivity activity, List<DeliveryAddress> slots, Callback callback) {
        this.activity = activity;
        this.slots = slots;
        this.callback = callback;
    }

    public void setList(ArrayList<DeliveryAddress> slots){
        this.slots = slots;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == MAIN_VIEW){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_address, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);

        } else if(viewType == ADD_VIEW){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_add_address, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderButton(v);

        } else {
            // should not happened
            throw new IllegalStateException(new Exception());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if(holder instanceof ViewHolderButton){
                ((ViewHolderButton)holder).linear.setTag(position);
                ((ViewHolderButton)holder).addButton.setTag(position);
                ((ViewHolderButton)holder).addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onAddAddress();
                    }
                });
            } else if(holder instanceof ViewHolderSlot){
            DeliveryAddress slot = slots.get(position);

                ((ViewHolderSlot)holder).textViewLast.setText(slot.getLastAddress());
//                ((ViewHolderSlot)holder).textViewLast.setTypeface(Fonts.mavenRegular(activity));
                String selectedLat = Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_lati), "");
                String selectedLongi = Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_longi), "");;
                String selectedAddress = activity.getSelectedAddress();

                if(selectedAddress.equalsIgnoreCase(slot.getLastAddress()) && selectedLat.equalsIgnoreCase(slot.getDeliveryLatitude()) &&
                        selectedLongi.equalsIgnoreCase(slot.getDeliveryLongitude())) {
                    ((ViewHolderSlot)holder).imageViewRadio.setBackgroundResource(R.drawable.radio_selected_icon);
                } else {
                    ((ViewHolderSlot)holder).imageViewRadio.setBackgroundResource(R.drawable.radio_unselected_icon);
                }

                ((ViewHolderSlot)holder).linear.setTag(position);
                ((ViewHolderSlot)holder).linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            callback.onSlotSelected(pos, slots.get(pos));
                            //notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return slots == null ? 0 : slots.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == slots.size()) {
            return ADD_VIEW;
        }
        return MAIN_VIEW;
    }

    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public RelativeLayout linear;
        private ImageView imageViewRadio;
        public TextView textViewLast;
        public ViewHolderSlot(View itemView, Activity context) {
            super(itemView);
            linear = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutLast);
            imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewLast);
            textViewLast = (TextView)itemView.findViewById(R.id.textViewLast);
            textViewLast.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderButton extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        private Button addButton;
        public ViewHolderButton(View itemView) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            addButton = (Button) itemView.findViewById(R.id.add_button);
        }
    }

    public interface Callback{
        void onSlotSelected(int position, DeliveryAddress slot);
        void onAddAddress();
    }



}
