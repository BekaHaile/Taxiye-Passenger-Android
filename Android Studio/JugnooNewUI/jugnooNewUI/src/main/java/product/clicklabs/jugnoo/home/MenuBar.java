package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;

/**
 * Created by shankar on 4/8/16.
 */
public class MenuBar {

	Activity activity;
	DrawerLayout drawerLayout;

	public LinearLayout menuLayout;

	private RecyclerView recyclerViewMenu;
	public MenuAdapter menuAdapter;

	public MenuBar(Activity activity, DrawerLayout rootView){
		this.activity = activity;
		this.drawerLayout = rootView;
		initComponents();
	}


	private void initComponents(){
		menuLayout = (LinearLayout) drawerLayout.findViewById(R.id.menuLayout);

		recyclerViewMenu = (RecyclerView) drawerLayout.findViewById(R.id.recyclerViewMenu);
		recyclerViewMenu.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
		recyclerViewMenu.setHasFixedSize(false);

		menuAdapter = new MenuAdapter(Data.menuInfoList, activity);
		recyclerViewMenu.setAdapter(menuAdapter);

		menuLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		setupFreshUI();
	}

	public void setUserData(){
		try {
			menuAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setupFreshUI(){
		try {
			menuAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
