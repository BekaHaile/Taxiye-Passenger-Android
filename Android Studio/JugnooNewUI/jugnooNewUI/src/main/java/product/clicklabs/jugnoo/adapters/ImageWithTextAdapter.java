package product.clicklabs.jugnoo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;

public class ImageWithTextAdapter extends RecyclerView.Adapter<ImageWithTextAdapter.ImageWithTextVH> {

    private ArrayList<HistoryResponse.OrderImages> imageList;
    private OnItemClickListener mOnItemClickListener;


    public ImageWithTextAdapter(final ArrayList<HistoryResponse.OrderImages> imageList, final OnItemClickListener onItemClickListener) {
        this.imageList = imageList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ImageWithTextVH onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_with_text, parent,false);
        return new ImageWithTextVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageWithTextVH holder, final int p) {

        int pos = holder.getAdapterPosition();

        if (pos >= 0 && pos < imageList.size()) {

            HistoryResponse.OrderImages orderImages = imageList.get(pos);

            Picasso.with(holder.ivImage.getContext()).load(orderImages.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.ivImage);

            if (orderImages.getDelivery_id() == 0) {
                holder.tvTitle.setVisibility(View.GONE);
            } else {
                String text = holder.ivImage.getContext().getString(R.string.delivery)
                        + " " + String.valueOf(orderImages.getDeliveryNo());
                holder.tvTitle.setText(text);
                holder.tvTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    class ImageWithTextVH extends RecyclerView.ViewHolder {

        AppCompatTextView tvTitle;
        AppCompatImageView ivImage;

        ImageWithTextVH(final View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (imageList != null && imageList.size() > 0) {
                        int pos = getAdapterPosition();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(imageList.get(pos), pos);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(final HistoryResponse.OrderImages image, final int pos);
    }
}
