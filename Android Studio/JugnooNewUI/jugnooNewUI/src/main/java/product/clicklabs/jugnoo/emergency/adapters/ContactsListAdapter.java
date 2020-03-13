package product.clicklabs.jugnoo.emergency.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by shankar on 7/27/15.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private final String TAG = ContactsListAdapter.class.getSimpleName();
    private Activity activity;
    private int rowLayout;
    public ArrayList<ContactBean> contactBeans = new ArrayList<>();
    private int selectedCount;
    private Callback callback;
    private ListMode listMode;

    public ContactsListAdapter(ArrayList<ContactBean> contactBeans, Activity activity, int rowLayout,
                               Callback callback, ListMode listMode) {
        this.contactBeans = contactBeans;
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.selectedCount = 0;
        this.callback = callback;
        this.listMode = listMode;
    }

    public synchronized void setList(ArrayList<ContactBean> contactBeans) {
        this.contactBeans = contactBeans;
        notifyDataSetChanged();
    }

    public synchronized void setCountAndNotify() {
        selectedCount = 0;
        for (ContactBean contactBean : contactBeans) {
            if (contactBean.isSelected()) {
                selectedCount++;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(640, 120);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }


    @Override
    public void onBindViewHolder(ContactsListAdapter.ViewHolder holder, int position) {
        ContactBean contactBean = contactBeans.get(position);

        holder.textViewContactName.setText(contactBean.getName());
        holder.textViewContactNumberType.setText(contactBean.getPhoneNo() + " " + contactBean.getType());

        if (ListMode.ADD_CONTACTS == getListMode()
                || ListMode.SEND_RIDE_STATUS == getListMode()) {
            if(ListMode.ADD_CONTACTS == getListMode()) {
                holder.imageViewOption.setVisibility(View.GONE);
            }
            if (contactBean.isSelected()) {
                holder.imageViewOption.setImageResource(R.drawable.checkbox_signup_checked);
            } else {
                holder.imageViewOption.setImageResource(R.drawable.checkbox_signup_unchecked);
            }
        } else if (ListMode.EMERGENCY_CONTACTS == getListMode()) {
            holder.imageViewOption.setVisibility(View.GONE);
        } else if (ListMode.DELETE_CONTACTS == getListMode()) {
            holder.imageViewOption.setVisibility(View.VISIBLE);
            holder.imageViewOption.setImageResource(R.drawable.ic_cross_grey);
        } else if (ListMode.CALL_CONTACTS == getListMode()) {
            holder.imageViewOption.setVisibility(View.VISIBLE);
            holder.imageViewOption.setImageResource(R.drawable.ic_phone_green);
        }

        holder.relative.setTag(position);

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (ListMode.ADD_CONTACTS == getListMode()) {
                    if (contactBeans.get(position).isSelected()) {
                        contactBeans.get(position).setSelected(false);
                        selectedCount--;
                        callback.contactClicked(position, contactBeans.get(position));
                    } else if (selectedCount < EmergencyActivity.EMERGENCY_CONTACTS_ALLOWED_TO_ADD) {
                        contactBeans.get(position).setSelected(true);
                        selectedCount++;
                        callback.contactClicked(position, contactBeans.get(position));
                    } else {
                        Utils.showToast(activity, activity.getString(R.string.you_can_add_only_three_contacts));
                    }
                    notifyDataSetChanged();
                } else if (ListMode.EMERGENCY_CONTACTS == getListMode()) {
                    callback.contactClicked(position, contactBeans.get(position));
                } else if (ListMode.DELETE_CONTACTS == getListMode()) {
                    callback.contactClicked(position, contactBeans.get(position));
                } else if (ListMode.CALL_CONTACTS == getListMode()) {
                    callback.contactClicked(position, contactBeans.get(position));
                } else if (ListMode.SEND_RIDE_STATUS == getListMode()) {
                    if (contactBeans.get(position).isSelected()) {
                        contactBeans.get(position).setSelected(false);
                        callback.contactClicked(position, contactBeans.get(position));
                    } else {
                        contactBeans.get(position).setSelected(true);
                        callback.contactClicked(position, contactBeans.get(position));
                    }
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactBeans == null ? 0 : contactBeans.size();
    }

    public ListMode getListMode() {
        return listMode;
    }

    public void setListMode(ListMode listMode) {
        this.listMode = listMode;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewOption;
        public TextView textViewContactName, textViewContactNumberType;

        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewOption = (ImageView) itemView.findViewById(R.id.imageViewOption);
            textViewContactName = (TextView) itemView.findViewById(R.id.textViewContactName);
            textViewContactName.setTypeface(Fonts.mavenLight(activity));
            textViewContactNumberType = (TextView) itemView.findViewById(R.id.textViewContactNumberType);
            textViewContactNumberType.setTypeface(Fonts.mavenLight(activity));
        }
    }

    public interface Callback {
        void contactClicked(int position, ContactBean contactBean);
    }


    public enum ListMode {
        ADD_CONTACTS(0),
        EMERGENCY_CONTACTS(1),
        DELETE_CONTACTS(2),
        CALL_CONTACTS(3),
        SEND_RIDE_STATUS(4);

        private int ordinal;

        ListMode(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }
    }
}