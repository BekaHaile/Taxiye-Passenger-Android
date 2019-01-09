package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.MenusItemCustomizeAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Utils;


@SuppressLint("ValidFragment")
public class MenusItemCustomizeFragment extends Fragment implements GAAction {

	private RelativeLayout rlRoot;

	private RecyclerView rvCustomizeItem;
	private MenusItemCustomizeAdapter menusItemCustomizeAdapter;

	private View rootView;
	private FreshActivity activity;
	private String currencyCode, currency;


	public static MenusItemCustomizeFragment newInstance(int categoryPos, int subCategoryPos, int itemPos) {
		MenusItemCustomizeFragment frag = new MenusItemCustomizeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_CATEGORY_POSITION, categoryPos);
		bundle.putInt(Constants.KEY_SUBCATEGORY_POSITION, subCategoryPos);
		bundle.putInt(Constants.KEY_ITEM_POSITION, itemPos);
		frag.setArguments(bundle);
		return frag;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_menus_customize_item, container, false);

		try {
			Bundle bundle = getArguments();
			if (bundle.containsKey(Constants.KEY_CATEGORY_POSITION)
					&& bundle.containsKey(Constants.KEY_SUBCATEGORY_POSITION)
					&& bundle.containsKey(Constants.KEY_ITEM_POSITION)) {
				int categoryPos = bundle.getInt(Constants.KEY_CATEGORY_POSITION);
				int subCategoryPos = bundle.getInt(Constants.KEY_SUBCATEGORY_POSITION);
				int itemPos = bundle.getInt(Constants.KEY_ITEM_POSITION);

				activity = (FreshActivity) getActivity();
				activity.fragmentUISetup(this);
				rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
				try {
					if (rlRoot != null) {
						new ASSL(activity, rlRoot, 1134, 720, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				rvCustomizeItem = (RecyclerView) rootView.findViewById(R.id.rvCustomizeItem);
				rvCustomizeItem.setLayoutManager(new LinearLayoutManager(activity));
				rvCustomizeItem.setItemAnimator(new DefaultItemAnimator());
				rvCustomizeItem.setHasFixedSize(false);

				Item item = null;
				if (subCategoryPos > -1) {
					item = activity.getMenuProductsResponse().getCategories().get(categoryPos).getSubcategories().get(subCategoryPos).getItems().get(itemPos);
				} else {
					item = activity.getMenuProductsResponse().getCategories().get(categoryPos).getItems().get(itemPos);
				}
				currencyCode = activity.getMenuProductsResponse().getCurrencyCode();
				currency = activity.getMenuProductsResponse().getCurrency();
				if (item != null) {
					menusItemCustomizeAdapter = new MenusItemCustomizeAdapter(activity, item,
							new MenusItemCustomizeAdapter.Callback() {
								@Override
								public void updateItemTotalPrice(ItemSelected itemSelected) {

									activity.tvItemTotalValue.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(itemSelected.getTotalPriceWithQuantity(), currencyCode, currency));
								}

								@Override
								public void onItemMinusClick(boolean allItemsFinished) {
									if(allItemsFinished)
									activity.performBackPressed(false);

										GAUtils.event(activity.getGaCategory(), GAAction.CUSTOMIZE_ITEM, GAAction.ITEM + GAAction.DECREASED);

								}

								@Override
								public void onItemPlusClick(){
										GAUtils.event(activity.getGaCategory(), GAAction.CUSTOMIZE_ITEM, GAAction.ITEM + GAAction.INCREASED);
								}
							}, currencyCode, currency,true);
					rvCustomizeItem.setAdapter(menusItemCustomizeAdapter);

					activity.rlAddToCart.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(activity.getTotalPrice() <= 0){
								activity.saveDeliveryAddressModel();
							}
							for(int i=0; i<menusItemCustomizeAdapter.getItemSelected().getCustomizeItemSelectedList().size(); i++){
								CustomizeItemSelected customizeItemSelected = menusItemCustomizeAdapter.getItemSelected().getCustomizeItemSelectedList().get(i);
								if(customizeItemSelected.getCustomizeOptions(false) == null || customizeItemSelected.getCustomizeOptions(false).size() == 0){
									menusItemCustomizeAdapter.getItemSelected().getCustomizeItemSelectedList().remove(i);
									i--;
								}
							}

							int index = menusItemCustomizeAdapter.getItem().getItemSelectedList().indexOf(menusItemCustomizeAdapter.getItemSelected());
							if(index > -1){
								menusItemCustomizeAdapter.getItem().getItemSelectedList().get(index).setQuantity(
										menusItemCustomizeAdapter.getItem().getItemSelectedList().get(index).getQuantity()
												+ menusItemCustomizeAdapter.getItemSelected().getQuantity());
							} else {
								menusItemCustomizeAdapter.getItem().getItemSelectedList().add(menusItemCustomizeAdapter.getItemSelected());
							}
								GAUtils.event(activity.getGaCategory(), GAAction.CUSTOMIZE_ITEM, GAAction.ADD_TO_CART + GAAction.CLICKED);
							activity.performBackPressed(false);
							activity.getVendorMenuFragment().onUpdateListEvent(new UpdateMainList(true));
						}
					});

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GAUtils.trackScreenView(activity.getGaCategory()+CUSTOMIZE_ITEM);
		activity.getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					rlRoot.setPaddingRelative(0, 0, 0, activity.rlAddToCart.getMeasuredHeight());
					Utils.hideSoftKeyboard(activity, activity.getTopBar().etSearch);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 200);




		return rootView;
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ASSL.closeActivity(rlRoot);
		System.gc();
	}

}
