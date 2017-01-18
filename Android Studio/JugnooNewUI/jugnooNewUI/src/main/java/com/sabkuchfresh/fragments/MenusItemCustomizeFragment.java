package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.MenusItemCustomizeAdapter;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


@SuppressLint("ValidFragment")
public class MenusItemCustomizeFragment extends Fragment {

	private RelativeLayout rlRoot;

	private RecyclerView rvCustomizeItem;
	private MenusItemCustomizeAdapter menusItemCustomizeAdapter;
	private RelativeLayout rlAddToCart;
	private TextView tvItemTotalValue;

	private View rootView;
	private FreshActivity activity;


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
				if (item != null) {
					rlAddToCart = (RelativeLayout) rootView.findViewById(R.id.rlAddToCart);
					((TextView) rootView.findViewById(R.id.tvAddToCart)).setTypeface(Fonts.mavenMedium(activity));
					tvItemTotalValue = (TextView) rootView.findViewById(R.id.tvItemTotalValue);
					tvItemTotalValue.setTypeface(Fonts.mavenMedium(activity));

					menusItemCustomizeAdapter = new MenusItemCustomizeAdapter(activity, item,
							new MenusItemCustomizeAdapter.Callback() {
								@Override
								public void updateItemTotalPrice(ItemSelected itemSelected) {
									tvItemTotalValue.setText(activity.getString(R.string.rupees_value_format,
											Utils.getMoneyDecimalFormat().format(itemSelected.getTotalPriceWithQuantity())));
								}
							});
					rvCustomizeItem.setAdapter(menusItemCustomizeAdapter);

					rlAddToCart.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
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
							activity.performBackPressed();
							activity.getVendorMenuFragment().onUpdateListEvent(new UpdateMainList(true));
						}
					});

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return rootView;
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ASSL.closeActivity(rlRoot);
		System.gc();
	}

}
