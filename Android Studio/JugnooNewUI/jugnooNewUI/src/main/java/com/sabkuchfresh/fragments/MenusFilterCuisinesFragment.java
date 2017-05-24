package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sabkuchfresh.adapters.MenusFilterCuisinesAdapter;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Shankar on 15/11/16.
 */
public class MenusFilterCuisinesFragment extends Fragment{
    private final String TAG = MenusFilterCuisinesFragment.class.getSimpleName();

    private LinearLayout linearLayoutRoot;
    private EditText editTextCuisine;
    private RecyclerView recyclerViewCuisines;
    private Button buttonDone;
    private MenusFilterCuisinesAdapter menusFilterCuisinesAdapter;

    private View rootView;
    private FreshActivity activity;

    public MenusFilterCuisinesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menus_filter_cuisines, container, false);

        activity = (FreshActivity) getActivity();

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        editTextCuisine = (EditText) rootView.findViewById(R.id.editTextCuisine); editTextCuisine.setTypeface(Fonts.mavenMedium(activity));
        recyclerViewCuisines = (RecyclerView) rootView.findViewById(R.id.recyclerViewCuisines);
        recyclerViewCuisines.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCuisines.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCuisines.setHasFixedSize(false);
        buttonDone = (Button) rootView.findViewById(R.id.buttonDone); buttonDone.setTypeface(Fonts.mavenMedium(activity));

        menusFilterCuisinesAdapter = new MenusFilterCuisinesAdapter(activity, activity.getFilterCuisinesLocal(), editTextCuisine, null);
        recyclerViewCuisines.setAdapter(menusFilterCuisinesAdapter);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getFilterCuisinesLocal().clear();
                for(int i=0; i<menusFilterCuisinesAdapter.getCuisines().size(); i++){
                    activity.getFilterCuisinesLocal().add(menusFilterCuisinesAdapter.getCuisines().get(i));
                }
                activity.performBackPressed(false);
            }
        });

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }

}
