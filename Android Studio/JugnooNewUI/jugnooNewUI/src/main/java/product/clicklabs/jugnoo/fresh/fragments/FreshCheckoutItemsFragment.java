package product.clicklabs.jugnoo.fresh.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.adapters.FreshCategoryItemsAdapter;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.utils.ASSL;


@SuppressLint("ValidFragment")
public class FreshCheckoutItemsFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;

	private View rootView;
    private FreshActivity activity;

	private ArrayList<SubItem> subItemsInCart;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshCheckoutItemsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);

		if(subItemsInCart == null) {
			subItemsInCart = new ArrayList<>();
		}
		for(Category category : activity.getProductsResponse().getCategories()){
			for(SubItem subItem : category.getSubItems()){
				if(subItem.getSubItemQuantitySelected() > 0){
					subItemsInCart.add(subItem);
				}
			}
		}


		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity,
				subItemsInCart,
				FreshCategoryItemsAdapter.OpenMode.CART,
				new FreshCategoryItemsAdapter.Callback() {
					@Override
					public void onPlusClicked(int position, SubItem subItem) {
						activity.updateCartValues();
					}

					@Override
					public void onMinusClicked(int position, SubItem subItem) {
						activity.updateCartValues();
						if(subItem.getSubItemQuantitySelected() == 0){
							subItemsInCart.remove(position);
							checkIfEmpty();
						}
					}

					@Override
					public void onDeleteClicked(int position, SubItem subItem) {
						activity.updateCartValues();
						if(subItem.getSubItemQuantitySelected() == 0){
							subItemsInCart.remove(position);
							checkIfEmpty();
						}
					}
				});
		recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);

		return rootView;
	}

	public void deleteCart(){
		for(SubItem subItem : subItemsInCart){
			subItem.setSubItemQuantitySelected(0);
		}
		activity.updateCartValues();
		subItemsInCart.clear();
		freshCategoryItemsAdapter.notifyDataSetChanged();
		checkIfEmpty();

	}

	private void checkIfEmpty(){
		if(subItemsInCart.size() == 0){
			activity.performBackPressed();
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
