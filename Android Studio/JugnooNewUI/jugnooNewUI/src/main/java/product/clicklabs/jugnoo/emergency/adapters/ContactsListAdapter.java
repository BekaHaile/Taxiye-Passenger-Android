package product.clicklabs.jugnoo.emergency.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by shankar on 7/27/15.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private final String TAG = ContactsListAdapter.class.getSimpleName();
    private Activity activity;
    private int rowLayout;
    private ArrayList<ContactBean> contactBeans = new ArrayList<>();
    private int selectedCount;
    private Callback callback;

    public ContactsListAdapter(ArrayList<ContactBean> contactBeans, Activity activity, int rowLayout, Callback callback) {
        this.contactBeans = contactBeans;
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.selectedCount = 0;
        this.callback = callback;
    }

    public synchronized void setList(ArrayList<ContactBean> contactBeans){
        this.contactBeans = contactBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(640, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(ContactsListAdapter.ViewHolder holder, int position) {
        ContactBean contactBean = contactBeans.get(position);

        holder.textViewContactName.setText(contactBean.getName());
        holder.textViewContactNumberType.setText(contactBean.getPhoneNo() + " " + contactBean.getType());

        if(contactBean.isSelected()){
            holder.imageViewOption.setImageResource(R.drawable.checkbox_signup_checked);
        } else{
            holder.imageViewOption.setImageResource(R.drawable.checkbox_signup_unchecked);
        }

        holder.relative.setTag(position);

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if(contactBeans.get(position).isSelected()){
                    contactBeans.get(position).setSelected(false);
                    selectedCount--;
                    callback.contactSelected(false, contactBeans.get(position));
                } else if(selectedCount < Constants.MAX_EMERGENCY_CONTACTS_ALLOWED){
                    contactBeans.get(position).setSelected(true);
                    selectedCount++;
                    callback.contactSelected(true, contactBeans.get(position));
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactBeans == null ? 0 : contactBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewOption;
        public TextView textViewContactName, textViewContactNumberType;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewOption = (ImageView)itemView.findViewById(R.id.imageViewOption);
            textViewContactName = (TextView)itemView.findViewById(R.id.textViewContactName);
            textViewContactName.setTypeface(Fonts.mavenLight(activity));
            textViewContactNumberType = (TextView)itemView.findViewById(R.id.textViewContactNumberType);
            textViewContactNumberType.setTypeface(Fonts.mavenLight(activity));
        }
    }

    public interface Callback{
        void contactSelected(boolean selected, ContactBean contactBean);
    }

}