package product.clicklabs.jugnoo.emergency.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;


/**
 * Created by shankar on 7/27/15.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private final String TAG = ContactsListAdapter.class.getSimpleName();
    private Activity activity;
    private int rowLayout;
    private ArrayList<ContactBean> contactBeans = new ArrayList<>();

    public ContactsListAdapter(ArrayList<ContactBean> contactBeans, Activity activity, int rowLayout) {
        this.contactBeans = contactBeans;
        this.activity = activity;
        this.rowLayout = rowLayout;
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
                Log.i(TAG, "onBindViewHolder onClick position="+position);
                if(contactBeans.get(position).isSelected()){
                    contactBeans.get(position).setSelected(false);
                } else{
                    contactBeans.get(position).setSelected(true);
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
        public LinearLayout relative;
        public ImageView imageViewContact, imageViewOption;
        public TextView textViewContactName, textViewContactNumberType;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            imageViewContact = (ImageView) itemView.findViewById(R.id.imageViewContact);
            imageViewOption = (ImageView)itemView.findViewById(R.id.imageViewOption);
            textViewContactName = (TextView)itemView.findViewById(R.id.textViewContactName);
            textViewContactName.setTypeface(Fonts.mavenLight(activity));
            textViewContactNumberType = (TextView)itemView.findViewById(R.id.textViewContactNumberType);
            textViewContactNumberType.setTypeface(Fonts.mavenLight(activity));
        }
    }
}