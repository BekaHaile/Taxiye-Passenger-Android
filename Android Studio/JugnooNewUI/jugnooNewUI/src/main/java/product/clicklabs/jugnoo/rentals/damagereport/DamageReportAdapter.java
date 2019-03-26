package product.clicklabs.jugnoo.rentals.damagereport;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.R;

public class DamageReportAdapter extends RecyclerView.Adapter<DamageReportAdapter.ViewHolder> {

    private List<String> textViewDamageTypeList = new ArrayList<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> damagedItems = new HashMap<>();

    DamageReportAdapter() {
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.damage_report_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String items = textViewDamageTypeList.get(i);
        viewHolder.textView.setText(items);
    }


    void insertItemInList(String damageType) {
        textViewDamageTypeList.add(damageType);
        notifyItemChanged(getItemCount());
    }

    void removeItemFromList(int index) {
        if (index > getItemCount()) {
            return;
        }
        textViewDamageTypeList.remove(index);
        notifyItemChanged(index);
    }


    public List<String> getDamagItemsList()
    {
        return textViewDamageTypeList;
    }

    public void setDamagedItemsList(List<String> list)
    {
        textViewDamageTypeList.clear();
        textViewDamageTypeList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return textViewDamageTypeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.text_view_damage_list);
            linearLayout = itemView.findViewById(R.id.linear_layout_damage_report);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (damagedItems.containsKey(position)) {
                damagedItems.remove(position);
                textView.setTextColor(itemView.getResources().getColor(android.R.color.black));
                linearLayout.setBackground(itemView.getResources().getDrawable(R.drawable.custom_border_black));
            } else {
                damagedItems.put(position, textView.getText().toString());
                textView.setTextColor(itemView.getResources().getColor(android.R.color.white));
                linearLayout.setBackground(itemView.getResources().getDrawable(R.drawable.custom_border_white));
            }
        }

    }
}

