package product.clicklabs.jugnoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.picker.image.util.Util;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import product.clicklabs.jugnoo.adapters.MultipleDestAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.AppInterruptHandler;
import product.clicklabs.jugnoo.promotion.models.Promo;
import product.clicklabs.jugnoo.utils.Fonts;

import static product.clicklabs.jugnoo.Constants.KEY_SEARCH_FIELD_TEXT;
import static product.clicklabs.jugnoo.Constants.STOP_PENDING;

public class MultiStopsActivity extends BaseAppCompatActivity implements SearchListAdapter.SearchListActionsHandler, AppInterruptHandler {

    private RecyclerView rvMultiDest;
    private RelativeLayout rlPlaceSearchList,rlDropParent;
    private TextView tvPickup, textViewPaymentModeValueConfirm, textViewOffersConfirm, textVieGetFareEstimateConfirm, textViewTotalFare;
    private MultipleDestAdapter multipleDestAdapter;
    private EditText editTextBidValue;
    private TextView btnConfirmRequestMultiStops,tvDrop;
    private ArrayList<AutoData.MultiDestData> localMultipleStops=new ArrayList<>();
    private PassengerScreenMode passengerScreenMode;
    private ImageView imageViewBack;
    public static AppInterruptHandler appInterruptHandler;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_stops);
        updateList();
//        passengerScreenMode.setOrdinal(getIntent().getIntExtra("passenger_screen_mode",0));
        MultiStopsActivity.appInterruptHandler=this;
        init();
    }

    private void init() {
        rvMultiDest = findViewById(R.id.rvStops);
        imageViewBack = findViewById(R.id.imageViewBack);
        rlPlaceSearchList = findViewById(R.id.rlPlaceSearchList);
        rlDropParent = findViewById(R.id.rlDropParent);
        tvPickup = findViewById(R.id.tvPickup);
        tvPickup.setText(Data.autoData.getPickupSearchResult().getNameForText(this));
        tvPickup.setTypeface(Fonts.mavenRegular(this));
        tvDrop = findViewById(R.id.tvDrop);
        tvDrop.setText(Data.autoData.getDropAddress());
        tvDrop.setTypeface(Fonts.mavenRegular(this));
        editTextBidValue = findViewById(R.id.editTextBidValue);
        editTextBidValue.setTypeface(Fonts.mavenRegular(this));
        textViewPaymentModeValueConfirm = findViewById(R.id.textViewPaymentModeValueConfirm);
        textViewPaymentModeValueConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewOffersConfirm = findViewById(R.id.textViewOffersConfirm);
        textViewOffersConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textVieGetFareEstimateConfirm = findViewById(R.id.textVieGetFareEstimateConfirm);
        textVieGetFareEstimateConfirm.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        textViewTotalFare = findViewById(R.id.textViewTotalFare);
        textViewTotalFare.setTypeface(Fonts.avenirNext(this), Typeface.BOLD);
        btnConfirmRequestMultiStops = findViewById(R.id.btnConfirmRequestMultiStops);
        btnConfirmRequestMultiStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(singleNullStopAdded()){
                    Utils.showToast(MultiStopsActivity.this,getString(R.string.select_atlease_one_top));
                    return;
                }
                removeNullsFromMultiList();

                /* update the final destination and remove the last destination from multiDestList */
                Data.autoData.setDropLatLng(localMultipleStops.get(localMultipleStops.size() - 1).getLatlng());
                Data.autoData.setDropAddress(localMultipleStops.get(localMultipleStops.size() - 1).getDropAddress());
                Data.autoData.setDropAddressId(localMultipleStops.get(localMultipleStops.size() - 1).getDropAddressId());
                localMultipleStops.remove(localMultipleStops.size() - 1);
                Data.autoData.getMultiDestList().clear();
                Data.autoData.getMultiDestList().addAll(localMultipleStops);
                setResult(RESULT_OK);
                finish();
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        multipleDestAdapter = new MultipleDestAdapter(this, new MultipleDestAdapter.MultiDestClickListener() {
            @Override
            public void onClick(int position) {
                openPlaceSearchListFrag(position);
            }

            @Override
            public void crossClicked(int position) {

            }

            @Override
            public void addClicked() {

            }
        }, localMultipleStops);
        rvMultiDest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMultiDest.setAdapter(multipleDestAdapter);
    }

    private void removeNullsFromMultiList() {
        int pos = 0;
        for (Object o : localMultipleStops) {
            if (o == null)
                localMultipleStops.remove(pos);
            pos++;
        }
    }

    private void openPlaceSearchListFrag(int position) {
        rlPlaceSearchList.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
        bundle.putInt(Constants.STOP_PRESSED_POSITION, position);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(rlPlaceSearchList.getId(), PlaceSearchListFragment.newInstance(bundle), PlaceSearchListFragment.class.getSimpleName() + PassengerScreenMode.P_SEARCH)
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }
    public void updateList(){
        localMultipleStops.clear();
        localMultipleStops.addAll(Data.autoData.getMultiDestList());
        localMultipleStops.add(Data.autoData.new MultiDestData(Data.autoData.getDropLatLng(),Data.autoData.getDropAddress(),Data.autoData.getDropAddressId(),STOP_PENDING));
        if(multipleDestAdapter!=null)
            multipleDestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTextChange(String text) {

    }

    @Override
    public void onSearchPre() {

    }

    @Override
    public void onSearchPost() {

    }

    @Override
    public void onPlaceClick(SearchResult autoCompleteSearchResult) {

    }

    @Override
    public void onPlaceSearchPre() {

    }

    @Override
    public void onPlaceSearchPostForStop(SearchResult searchResult, int position) {
        localMultipleStops.set(position,Data.autoData.new MultiDestData(searchResult.getLatLng(),searchResult.getAddress(),searchResult.getId(),STOP_PENDING));
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult, PlaceSearchListFragment.PlaceSearchMode placeSearchMode) {
        getSupportFragmentManager().popBackStackImmediate();
        multipleDestAdapter.notifyDataSetChanged();
//        rlPlaceSearchList.setVisibility(View.GONE);
    }

    @Override
    public void onPlaceSearchError() {

    }

    @Override
    public void onPlaceSaved() {

    }

    @Override
    public void onNotifyDataSetChanged(int count) {

    }

    @Override
    public void onSetLocationOnMapClicked() {

    }
    private boolean singleNullStopAdded(){
        return localMultipleStops.size() == 1 && localMultipleStops.get(0) == null;
    }

    @Override
    public void refreshDriverLocations() {

    }

    @Override
    public void onChangeStatePushReceived() {

    }

    @Override
    public void onCancelCompleted() {

    }

    @Override
    public void rideRequestAcceptedInterrupt(JSONObject jObj) {

    }

    @Override
    public void onNoDriversAvailablePushRecieved(String logMessage, int requestType) {

    }

    @Override
    public void startRideForCustomer(int flag, String message, PlaceOrderResponse.ReferralPopupContent popupContent) {

    }

    @Override
    public void customerEndRideInterrupt(String engagementId, Promo promo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onAfterRideFeedbackSubmitted(int givenRating) {

    }

    @Override
    public void onJugnooCashAddedByDriver(double jugnooBalance, String message) {

    }

    @Override
    public void onDriverArrived(String logMessage) {

    }

    @Override
    public void refreshOnPendingCallsDone() {

    }

    @Override
    public void onEmergencyContactVerified(int emergencyContactId) {

    }

    @Override
    public void showDialog(String message) {

    }

    @Override
    public void onShowDialogPush() {

    }

    @Override
    public void onDisplayMessagePushReceived() {

    }

    @Override
    public void onUpdatePoolRideStatus(JSONObject jsonObject) {

    }

    @Override
    public void updateGpsLockStatus(int gpsLockStatus) {

    }

    @Override
    public void onNoDriverHelpPushReceived(JSONObject jsonObject) {

    }

    @Override
    public void onStopUpdated() {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               updateList();
           }
       });
    }

    @Override
    public void onBackPressed() {
//        btnConfirmRequestMultiStops.performClick();
        super.onBackPressed();
    }
}
