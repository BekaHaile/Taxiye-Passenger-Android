package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fragments.DocumentUploadFragment;
import product.clicklabs.jugnoo.retrofit.model.DocumentData;
import product.clicklabs.jugnoo.utils.Fonts;

public class DocumentStatusAdapter extends RecyclerView.Adapter<DocumentStatusAdapter.ViewHolder> {

    private Activity activity;
    private OnDocumentClicked onDocumentClicked;
    List<DocumentData> documentDataList;
    public DocumentStatusAdapter(Activity activity,List<DocumentData> documentDataList,OnDocumentClicked onDocumentClicked) {
        this.activity = activity;
        this.onDocumentClicked = onDocumentClicked;
        this.documentDataList = documentDataList;
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
        holder.tvDocumentName.setText(documentDataList.get(position).getDocumentName());
        if(documentDataList.get(position).getStatus() == DocumentUploadFragment.DocStatus.NOT_UPLOADED.getI()) {
            holder.ivDocStatus.setImageResource(R.drawable.ic_upload_button);
        } else if(documentDataList.get(position).getStatus() == DocumentUploadFragment.DocStatus.VERIFIED.getI()) {
            holder.ivDocStatus.setImageResource(R.drawable.ic_checked);
        } else if(documentDataList.get(position).getStatus() == DocumentUploadFragment.DocStatus.APPROVAL_PENDING.getI()) {
            holder.ivDocStatus.setImageResource(R.drawable.ic_sand_glass);
        } else if(documentDataList.get(position).getStatus() == DocumentUploadFragment.DocStatus.REJECTED.getI()) {
            holder.ivDocStatus.setImageResource(R.drawable.ic_cancel_red);
        } else if(documentDataList.get(position).getStatus() == DocumentUploadFragment.DocStatus.UPLOADED.getI()) {
            holder.ivDocStatus.setImageResource(R.drawable.ic_info_yellow);
        }
    }

    public void updateList(List<DocumentData> documentDataList) {
        this.documentDataList = documentDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return documentDataList != null? documentDataList.size() : 0;
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
