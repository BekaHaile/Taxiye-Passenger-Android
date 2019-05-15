package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;

public class DocumentStatusAdapter extends RecyclerView.Adapter<DocumentStatusAdapter.ViewHolder> {

    private Activity activity;
    private OnDocumentClicked onDocumentClicked;
    public DocumentStatusAdapter(Activity activity,OnDocumentClicked onDocumentClicked) {
        this.activity = activity;
        this.onDocumentClicked = onDocumentClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.item_verify_details,parent,false);
        ViewHolder viewHolder = new ViewHolder(activity,rootView,onDocumentClicked);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDocumentName.setText("James Andrew");
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDocStatus;
        TextView tvDocumentName;

        public ViewHolder(Activity activity,View itemView,OnDocumentClicked onDocumentClicked) {
            super(itemView);
            ivDocStatus = itemView.findViewById(R.id.ivDocStatus);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            tvDocumentName.setTypeface(Fonts.mavenMedium(activity));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDocumentClicked.onDocClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnDocumentClicked {
        public void onDocClick(int position);
    }
}
