package product.clicklabs.jugnoo.rentals;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import product.clicklabs.jugnoo.R;

public class InstructionDialogAdapter extends PagerAdapter {

    private List<InstructionDialogModel> dialogList;
    private LayoutInflater inflater;

    InstructionDialogAdapter(Activity activity, List<InstructionDialogModel> dialogList) {
        this.dialogList = dialogList;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return dialogList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        final InstructionDialogModel dialogModel = dialogList.get(position);
        final View view = inflater.inflate(R.layout.instruction_dialog_list_item, container, false);

        TextView textViewTitle = view.findViewById(R.id.text_view_info_title);
        ImageView imageView = view.findViewById(R.id.image_view_info);
        TextView textViewDescription = view.findViewById(R.id.text_view_info_desc);

        textViewTitle.setText(dialogModel.getTitle());
        textViewDescription.setText(dialogModel.getDescription());

        Glide.with(container.getContext())
                .load(dialogModel.getImage())
                .into(imageView);

        view.setTag(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return dialogList.get(position).getTitle();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }
}
