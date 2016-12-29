package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.MenusCustomizeItemAdapter;
import com.sabkuchfresh.adapters.MenusCustomizeOptionAdapter;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItem;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.CustomizeOption;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;

/**
 * Created by shankar on 3/4/16.
 */
public class MenusCustomizeOptionDialog {

	private final String TAG = MenusCustomizeOptionDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog;
	private MenusCustomizeOptionAdapter menusCustomizeOptionAdapter;
	private CustomizeItemSelected customizeItemSelected;
	private MenusCustomizeItemAdapter menusCustomizeItemAdapter;

	public MenusCustomizeOptionDialog(Activity activity, Callback callback,
									  MenusCustomizeItemAdapter menusCustomizeItemAdapter) {
		this.activity = activity;
		this.callback = callback;
		this.menusCustomizeItemAdapter = menusCustomizeItemAdapter;
	}

	public Dialog show(CustomizeItem customizeItem, CustomizeItemSelected customizeItemSelected) {
		try {
			this.customizeItemSelected = customizeItemSelected;
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_menus_customize_option);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);


			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			LinearLayout linearLayoutCustomizeOption = (LinearLayout) dialog.findViewById(R.id.linearLayoutCustomizeOption);

			TextView textViewCustomizeOption = (TextView) dialog.findViewById(R.id.textViewCustomizeOption);
			textViewCustomizeOption.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewCutomizeOptionItemValue = (TextView) dialog.findViewById(R.id.textViewCutomizeOptionItemValue);
			textViewCutomizeOptionItemValue.setTypeface(Fonts.mavenRegular(activity));

			RecyclerView recyclerViewCustomizeOption = (RecyclerView) dialog.findViewById(R.id.recyclerViewCustomizeOption);
			recyclerViewCustomizeOption.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
			recyclerViewCustomizeOption.setItemAnimator(new DefaultItemAnimator());
			recyclerViewCustomizeOption.setHasFixedSize(false);

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));

			textViewCustomizeOption.setText(customizeItem.getCustomizeItemName());

			textViewCutomizeOptionItemValue.setText((customizeItem.getIsCheckBox() == 1) ? activity.getString(R.string.customize_choose_item_value,
					String.valueOf(customizeItem.getCustomizeOptions().size())) : activity.getString(R.string.customize_choose_option_value));

			menusCustomizeOptionAdapter = new MenusCustomizeOptionAdapter(activity,
					(ArrayList<CustomizeOption>) customizeItem.getCustomizeOptions(),
					customizeItem.getIsCheckBox(),
					new MenusCustomizeOptionAdapter.Callback() {
						@Override
						public CustomizeItemSelected getCustomizeItemSelected() {
							return MenusCustomizeOptionDialog.this.customizeItemSelected;
						}
					});

			recyclerViewCustomizeOption.setAdapter(menusCustomizeOptionAdapter);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();

				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callback.onDismiss();
					menusCustomizeItemAdapter.notifyDataSetChanged();
				}
			});

			dialog.show();

			menusCustomizeOptionAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback {
		void onDismiss();
	}

}
