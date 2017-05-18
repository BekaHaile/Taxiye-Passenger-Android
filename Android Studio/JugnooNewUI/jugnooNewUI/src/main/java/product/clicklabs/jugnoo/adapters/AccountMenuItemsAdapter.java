package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Parminder Singh on 4/27/17.
 */

public class AccountMenuItemsAdapter extends RecyclerView.Adapter<AccountMenuItemsAdapter.MenuItemViewHolder> implements ItemListener {

    private final LayoutInflater layoutInflater;
    private ArrayList<MenuInfo> menuList;
    private AccountMenuItemsCallback accountMenuItemsCallback;
    private RecyclerView recyclerView;
    private Activity activity;

    public AccountMenuItemsAdapter(ArrayList<MenuInfo> menuList,RecyclerView recyclerView,AccountMenuItemsCallback accountMenuItemsCallback, Activity activity){
        this.menuList = menuList;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.accountMenuItemsCallback = accountMenuItemsCallback;
        this.recyclerView=recyclerView;
        this.activity = activity;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView =  layoutInflater.inflate(R.layout.item_menu_in_account,parent,false);

        ASSL.DoMagic(convertView);

        return new MenuItemViewHolder(convertView,this,activity);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        try {


            if(menuList.get(position)!=null) {

                holder.tvValue.setVisibility(View.GONE);

                holder.tvJugnooStar.setText(menuList.get(position).getName());
                if(menuList.get(position).getTag().equalsIgnoreCase(MenuInfoTags.WALLET.getTag())) {
                    holder.tvValue.setVisibility(View.VISIBLE);
                    holder.tvValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormatWithoutFloat().format(Data.userData.getTotalWalletBalance())));
                }else if(menuList.get(position).getTag().equalsIgnoreCase(MenuInfoTags.INBOX.getTag())){
                    int unreadNotificationsCount = Prefs.with(activity).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
                    if(unreadNotificationsCount > 0){
                        holder.tvValue.setVisibility(View.VISIBLE);
                        holder.tvValue.setText(String.valueOf(unreadNotificationsCount));

                    }
                }
            }
            else
                holder.tvJugnooStar.setText(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return menuList==null?0:menuList.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {

        int pos = recyclerView.getChildAdapterPosition(parentView);
        if(pos!=RecyclerView.NO_POSITION) {
            accountMenuItemsCallback.onMenuItemClick(menuList.get(pos));
        }

    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder{


        @Bind(R.id.tvJugnooStar)
        TextView tvJugnooStar;
        @Bind(R.id.textViewValue)
        TextView tvValue;
        @Bind(R.id.rlJugnooStar)
        RelativeLayout rlRoot;

        public MenuItemViewHolder(final View itemView, final ItemListener itemListener, Activity activity) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvJugnooStar.setTypeface(Fonts.mavenMedium(activity));
            rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(rlRoot,itemView);
                }
            });
        }
    }


    public interface AccountMenuItemsCallback {
        void onMenuItemClick(MenuInfo menuInfo);
    }
}
