package com.sabkuchfresh.home;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements FlurryEventNames {

	Activity activity;
	DrawerLayout drawerLayout;

	public RelativeLayout topRl;
	public ImageView imageViewMenu;
	public TextView title;
	public Button buttonCheckServer;
	public ImageView imageViewBack, ivDeliveryAddressCross, imageViewDelete;
	public EditText editTextDeliveryAddress;

	public RelativeLayout llSearchContainer;
	public EditText etSearch;
	public ImageView ivSearchCross, ivSearch, ivFilter;

	public RelativeLayout relativeLayoutLocality, llSearchCartContainer;
	public TextView textViewLocationValue, textViewReset, tvCartAmount;
	public LinearLayout llCartAmount, llCartContainer, llSearchCart;



	public TopBar(Activity activity, DrawerLayout drawerLayout){
		this.activity = activity;
		this.drawerLayout = drawerLayout;
		setupTopBar();
	}

	private void setupTopBar(){
		topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
		imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
		title = (TextView) drawerLayout.findViewById(R.id.title);title.setTypeface(Fonts.avenirNext(activity));

		buttonCheckServer = (Button) drawerLayout.findViewById(R.id.buttonCheckServer);
		llCartContainer = (LinearLayout) drawerLayout.findViewById(R.id.llCartContainer);
		llCartAmount = (LinearLayout) drawerLayout.findViewById(R.id.llCartAmount);
		tvCartAmount = (TextView) drawerLayout.findViewById(R.id.tvCartAmount); tvCartAmount.setTypeface(Fonts.mavenRegular(activity));
		llSearchCartContainer = (RelativeLayout) drawerLayout.findViewById(R.id.llSearchCartContainer);
		llSearchCart = (LinearLayout) drawerLayout.findViewById(R.id.llSearchCart);

		imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
		editTextDeliveryAddress = (EditText) drawerLayout.findViewById(R.id.editTextDeliveryAddress);
		editTextDeliveryAddress.setTypeface(Fonts.mavenLight(activity));
		ivDeliveryAddressCross = (ImageView) drawerLayout.findViewById(R.id.ivDeliveryAddressCross);
		imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);

		relativeLayoutLocality = (RelativeLayout) drawerLayout.findViewById(R.id.relativeLayoutLocality);
		((TextView)drawerLayout.findViewById(R.id.textViewLocation)).setTypeface(Fonts.mavenRegular(activity));
		textViewLocationValue = (TextView) drawerLayout.findViewById(R.id.textViewLocationValue); textViewLocationValue.setTypeface(Fonts.mavenMedium(activity));
		textViewReset = (TextView) drawerLayout.findViewById(R.id.textViewReset); textViewReset.setTypeface(Fonts.mavenMedium(activity));

		llSearchContainer = (RelativeLayout) drawerLayout.findViewById(R.id.llSearchContainer);
		etSearch = (EditText) drawerLayout.findViewById(R.id.etSearch); etSearch.setTypeface(Fonts.mavenMedium(activity));
		ivSearch = (ImageView) drawerLayout.findViewById(R.id.ivSearch);
		ivFilter = (ImageView) drawerLayout.findViewById(R.id.ivFilter);
		ivSearchCross = (ImageView) drawerLayout.findViewById(R.id.ivSearchCross);
		setSearchVisibility(View.GONE);


		topRl.setOnClickListener(topBarOnClickListener);
		imageViewMenu.setOnClickListener(topBarOnClickListener);
		buttonCheckServer.setOnClickListener(topBarOnClickListener);
		ivSearch.setOnClickListener(topBarOnClickListener);
		ivFilter.setOnClickListener(topBarOnClickListener);
		imageViewBack.setOnClickListener(topBarOnClickListener);

		buttonCheckServer.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(Data.AppType == com.sabkuchfresh.utils.AppConstant.ApplicationType.MENUS){
					Utils.showToast(activity, Config.getMenusServerUrlName());
				} else{
					Utils.showToast(activity, Config.getFreshServerUrlName());
				}
				return false;
			}
		});


		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0){
					ivSearchCross.setVisibility(View.VISIBLE);
				} else{
					ivSearchCross.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


	}

	public LinearLayout getLlSearchCart() {
		return llSearchCart;
	}

	public ImageView getIvSearch() {
		return ivSearch;
	}

	public LinearLayout getLlCartAmount() {
		return llCartAmount;
	}

	public TextView getTvCartAmount() {
		return tvCartAmount;
	}

	public RelativeLayout getLlSearchCartContainer() {
		return llSearchCartContainer;
	}

	public LinearLayout getLlCartContainer() {
		return llCartContainer;
	}

	public View.OnClickListener topBarOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.topRl:
					break;

				case R.id.imageViewMenu:

					drawerLayout.openDrawer(GravityCompat.START);
					break;

				case R.id.buttonCheckServer:
					break;

				case R.id.imageViewBack:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).performBackPressed();
					}
					break;

				case R.id.ivSearch:
					if(activity instanceof FreshActivity) {
						((FreshActivity)activity).searchItem();
					}
					break;

				case R.id.ivFilter:
					if(activity instanceof FreshActivity){
						((FreshActivity)activity).openMenusFilter();
					}
					break;

			}
		}
	};


	public void setSearchVisibility(int visibility){
		if(visibility == View.VISIBLE){
			llSearchContainer.setBackgroundResource(R.drawable.capsule_white_stroke);
			etSearch.setVisibility(View.VISIBLE);
		} else {
			llSearchContainer.setBackgroundResource(R.drawable.background_transparent);
			etSearch.setVisibility(View.GONE);
		}
	}

}
