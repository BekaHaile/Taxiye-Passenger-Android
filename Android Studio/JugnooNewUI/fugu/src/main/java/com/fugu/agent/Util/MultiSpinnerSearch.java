package com.fugu.agent.Util;

/**
 * Created by gurmail on 24/07/18.
 *
 * @author gurmail
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.R;
import com.fugu.agent.model.broadcastResponse.Tag;
import com.fugu.agent.model.broadcastResponse.User;

import java.util.List;

public class MultiSpinnerSearch extends AppCompatSpinner implements OnCancelListener {
    private static final String TAG = MultiSpinnerSearch.class.getSimpleName();
    //    private List<KeyPairBoolData> items;
    private int LIST_TYPE = 0;
    private List<Tag> items;
    private List<User> userArray;
    private String defaultText = "";
    private String spinnerTitle = "";
    private SpinnerListener listener;
    public static AlertDialog.Builder builder;
    public static AlertDialog ad;

    private ListAdapter teamAdapter;
    private FleetListAdapter fleetListAdapter;

    public MultiSpinnerSearch(Context context) {
        super(context);
    }

    public MultiSpinnerSearch(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MultiSpinnerSearch_hintText) {
                spinnerTitle = a.getString(attr);
                defaultText = spinnerTitle;
                //break;
            }
            if (attr == R.styleable.MultiSpinnerSearch_listType) {
                LIST_TYPE = a.getInt(attr, 0);
                break;
            }

        }

        a.recycle();
    }

    public MultiSpinnerSearch(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        if(LIST_TYPE == 0) {
            listener.onItemsSelected(items);
        } else {

        }
    }

    @Override
    public boolean performClick() {

        builder = new AlertDialog.Builder(getContext(), R.style.dialog);
        builder.setTitle(spinnerTitle);

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.fugu_alert_dialog_listview, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.alertSearchListView);
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);
        recyclerView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        recyclerView.requestFocus();

        if(LIST_TYPE == 0) {
            teamAdapter = new ListAdapter(getContext(), items);
            recyclerView.setAdapter(teamAdapter);
        } else {
            fleetListAdapter = new FleetListAdapter(getContext(), userArray);
            recyclerView.setAdapter(fleetListAdapter);
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setOnCancelListener(this);
        ad = builder.show();
        return true;
    }

    public void setItems(List<Tag> items, SpinnerListener listener) {
        LIST_TYPE = 0;
        this.items = items;
        this.listener = listener;
    }

    public void setFleetItems(List<User> userArray, SpinnerListener listener) {
        LIST_TYPE = 1;
        this.userArray = userArray;
        this.listener = listener;
    }



    public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Tag> arrayList;
        private LayoutInflater inflater;

        public ListAdapter(Context context, List<Tag> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.hippo_textview_for_spinner, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Tag data = arrayList.get(position);
            holder.textView.setText(data.getTagName());
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.checkBox.setChecked(data.isSelected());
        }

        @Override
        public int getItemCount() {
            return arrayList == null ? 0 : arrayList.size();
        }

    }


    public class FleetListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<User> arrayList;
        private LayoutInflater inflater;

        public FleetListAdapter(Context context, List<User> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.hippo_textview_for_spinner, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User data = arrayList.get(position);
            holder.textView.setText(data.getFullName());
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.checkBox.setChecked(data.isSelected());
        }

        @Override
        public int getItemCount() {
            return arrayList == null ? 0 : arrayList.size();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        RelativeLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.alertTextView);
            checkBox = itemView.findViewById(R.id.alertCheckbox);
            mainLayout = itemView.findViewById(R.id.main_layout);

            mainLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if(LIST_TYPE == 0) {
                        if (pos == 0) {
                            for (int i = 0; i < items.size(); i++) {
                                items.get(i).setSelected(!items.get(i).isSelected());
                            }
                        } else {
                            items.get(pos).setSelected(!items.get(pos).isSelected());
                            if (!items.get(pos).isSelected()) {
                                items.get(0).setSelected(false);
                            } else {
                                for (int i = 1; i < items.size(); i++) {
                                    if (!items.get(i).isSelected()) {
                                        items.get(0).setSelected(false);
                                        break;
                                    } else {
                                        items.get(0).setSelected(true);
                                    }
                                }
                            }
                        }
                        teamAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            });
        }
    }
}
