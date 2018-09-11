package product.clicklabs.jugnoo.home.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.RideTransactionsAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.tutorials.NewUserReferralFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ScheduleRideFragment extends Fragment implements Constants {

	private final String TAG = ScheduleRideFragment.class.getSimpleName();

	TextView tvPickup, tvDestination,tvSelectDateTime;
	RecyclerView rvVehiclesList;
	Button btSchedule;

	private View rootView;
    private FragmentActivity activity;

	public ScheduleRideFragment(){
	}
	public static ScheduleRideFragment newInstance() {
		Bundle bundle = new Bundle();
		ScheduleRideFragment fragment = new ScheduleRideFragment();
		fragment.setArguments(bundle);
		return fragment;
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule_ride, container, false);


        activity = getActivity();

		rvVehiclesList = (RecyclerView) rootView.findViewById(R.id.rvVehiclesList);
		rvVehiclesList.setLayoutManager(new LinearLayoutManager(activity));
		rvVehiclesList.setItemAnimator(new DefaultItemAnimator());
		rvVehiclesList.setHasFixedSize(false);

		tvPickup = (TextView) rootView.findViewById(R.id.tvPickup);
		tvDestination = (TextView) rootView.findViewById(R.id.tvDestination);
		tvSelectDateTime = (TextView) rootView.findViewById(R.id.tvSelectDateTime);
		tvPickup.setTypeface(Fonts.mavenRegular(activity));
		tvDestination.setTypeface(Fonts.mavenRegular(activity));
		tvSelectDateTime.setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.tvPickupDateTime)).setTypeface(Fonts.mavenMedium(activity));
		btSchedule = (Button) rootView.findViewById(R.id.btSchedule);

		tvPickup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		tvDestination.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		tvSelectDateTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		return rootView;
	}


}
