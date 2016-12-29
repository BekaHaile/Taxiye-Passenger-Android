package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.adapters.MenusCustomizeItemAdapter;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItem;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;

/**
 * Created by shankar on 3/4/16.
 */
public class MenusCustomizeItemDialog {

	private final String TAG = MenusCustomizeItemDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;
	private MenusCustomizeItemAdapter menusCustomizeItemAdapter;
	private Item item;
	private ItemSelected itemSelected;
	private MenusCategoryItemsAdapter menusCategoryItemsAdapter;

	public MenusCustomizeItemDialog(Activity activity, Callback callback, MenusCategoryItemsAdapter menusCategoryItemsAdapter) {
		this.activity = activity;
		this.callback = callback;
		this.menusCategoryItemsAdapter = menusCategoryItemsAdapter;
	}

	public Dialog show(Item item) {
		try {
			this.item = item;
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_menus_customize_item);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			LinearLayout linearLayoutCustomizeItem = (LinearLayout) dialog.findViewById(R.id.linearLayoutCustomizeItem);
			ImageView imageViewFoodType = (ImageView) dialog.findViewById(R.id.imageViewFoodType);
			TextView textViewCustomizeItem = (TextView) dialog.findViewById(R.id.textViewCustomizeItem);textViewCustomizeItem.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewCustomizeItemName = (TextView) dialog.findViewById(R.id.textViewCustomizeItemName);textViewCustomizeItemName.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewCustomizeItemDesc = (TextView) dialog.findViewById(R.id.textViewCustomizeItemDesc);textViewCustomizeItemDesc.setTypeface(Fonts.mavenRegular(activity));
			RecyclerView recyclerViewCustomizeItemCategory = (RecyclerView) dialog.findViewById(R.id.recyclerViewCustomizeItemCategory);
			recyclerViewCustomizeItemCategory.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
			recyclerViewCustomizeItemCategory.setItemAnimator(new DefaultItemAnimator());
			recyclerViewCustomizeItemCategory.setHasFixedSize(false);

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);btnCancel.setTypeface(Fonts.mavenRegular(activity));
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);btnOk.setTypeface(Fonts.mavenRegular(activity));

			itemSelected = new ItemSelected();
			itemSelected.setRestaurantItemId(item.getRestaurantItemId());

			menusCustomizeItemAdapter = new MenusCustomizeItemAdapter(activity,
					(ArrayList<CustomizeItem>) item.getCustomizeItem(),
					new MenusCustomizeItemAdapter.Callback() {
						@Override
						public ItemSelected getItemSelected() {
							return itemSelected;
						}
					});


			recyclerViewCustomizeItemCategory.setAdapter(menusCustomizeItemAdapter);
			textViewCustomizeItem.setText(R.string.customize_item);

			textViewCustomizeItemName.setText(item.getItemName());
			textViewCustomizeItemDesc.setText(item.getItemDetails());
			imageViewFoodType.setImageResource(item.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);


			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MenusCustomizeItemDialog.this.item.getItemSelectedList().add(itemSelected);
					dialog.dismiss();
					menusCategoryItemsAdapter.notifyDataSetChanged();
				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.onDismiss();
				}
			});

			dialog.show();

			menusCustomizeItemAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback{
		void onDismiss();
	}

}
