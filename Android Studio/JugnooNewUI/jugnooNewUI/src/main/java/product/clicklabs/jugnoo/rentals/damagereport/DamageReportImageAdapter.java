package product.clicklabs.jugnoo.rentals.damagereport;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;

public class DamageReportImageAdapter extends RecyclerView.Adapter<DamageReportImageAdapter.ViewHolder> {

    private List<String> imageViewList = new ArrayList<>();
    private Context context;

    // Max Number of damaged images that rider can upload
    private final int MAX_NO_OF_IMAGES = 1;

    DamageReportImageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.damage_report_image_item_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        int position = viewHolder.getAdapterPosition();
        if (position == imageViewList.size()) {
            Glide.with(viewHolder.imageView.getContext())
                    .load(R.drawable.add_image)
                    .into(viewHolder.imageView);
        } else {
            Glide.with(viewHolder.imageView.getContext())
                    .load(imageViewList.get(position))
                    .into(viewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {

        if (imageViewList.size() == MAX_NO_OF_IMAGES) {
            return imageViewList.size();
        } else {
            return imageViewList.size() + 1;
        }
    }

    public interface DamageReportListener {
        void selectImage();
        void imageRemoved(ArrayList<String> imageList);
        void displayImage(int position);

    }

    public List<String> getImageList()
    {
        return imageViewList;

    }

//    void insertImageInList(int position , Uri damageImage) {
//        imageViewList.add(position,damageImage);
//        notifyItemInserted(imageViewList.size() - 1);
//    }

    void insertImageInList(ArrayList<String> damageImages) {

        imageViewList.addAll(damageImages);
        notifyDataSetChanged();
    }

    void clearList()
    {
        imageViewList.clear();
    }

    private void removeImageFromList(int index) {
        if (index > imageViewList.size()) {
            return;
        }
        imageViewList.remove(index);
        notifyItemRemoved(index);
    }

    int getMaxNoOfImages() {
        return MAX_NO_OF_IMAGES;
    }

    private int getListSize()
    {
        return imageViewList.size();
    }

    void checkIfListFull()
    {
        if(getListSize() == getMaxNoOfImages() - 1)
        {
            notifyItemRemoved(getMaxNoOfImages());
            Toast.makeText(context,"Can\'t upload more than "+getMaxNoOfImages()
                    + " images",Toast.LENGTH_SHORT).show();
        }
    }

    private void removeImage(final int position) {
        final CharSequence[] displayOptions = {"Delete", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove Image");
        builder.setItems(displayOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (displayOptions[which].equals("Delete")) {
                    removeImageFromList(position);
                    ((DamageReportListener) context).imageRemoved((ArrayList<String>) imageViewList);

                    if (getListSize() == getMaxNoOfImages()-1)
                    {
                        notifyItemInserted(getItemCount() - 1);
                    }

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (getAdapterPosition() == imageViewList.size()) {
                        return false;
                    }
                    removeImage(getAdapterPosition());
                    return true;

                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (imageViewList.size() == MAX_NO_OF_IMAGES) {
                        ((DamageReportListener) context).displayImage(getAdapterPosition());
                    } else {
                        if (getAdapterPosition() == getItemCount() - 1) {

                            ((DamageReportListener) context).selectImage();

                        } else {
                            ((DamageReportListener) context).displayImage(getAdapterPosition());
                        }
                    }
                }
            });
        }

    }
}
