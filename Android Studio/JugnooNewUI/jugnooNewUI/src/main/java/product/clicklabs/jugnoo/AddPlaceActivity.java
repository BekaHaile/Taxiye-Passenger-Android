package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.home.TransactionUtils;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.Constants.KEY_ID;
import static product.clicklabs.jugnoo.Constants.TYPE_HOME;
import static product.clicklabs.jugnoo.Constants.TYPE_WORK;
import static product.clicklabs.jugnoo.Data.latitude;
import static product.clicklabs.jugnoo.Data.longitude;


/**
 * Created by Ankit on 10/7/15.
 */
public class AddPlaceActivity extends BaseFragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = AddPlaceActivity.class.getSimpleName();

    private ImageView imageViewBack;
    private LinearLayout root;
    private TextView textViewTitle;
    private Button buttonRemove;
    private int placeRequestCode;
    private SearchResult searchResult;
    private boolean editThisAddress = false;

    RelativeLayout relativeLayoutContainer;

	private GoogleApiClient mGoogleApiClient;

    private Gson gson;
    private EditText editTextDeliveryAddress;
    private RelativeLayout relativeLayoutSearch;
    private ImageView imageViewSearchCross;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        root = (LinearLayout) findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        gson = new Gson();

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.avenirNext(this));
        buttonRemove = (Button)findViewById(R.id.buttonRemove); buttonRemove.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        editTextDeliveryAddress = (EditText) findViewById(R.id.editTextDeliveryAddress);
        editTextDeliveryAddress.setTypeface(Fonts.mavenLight(AddPlaceActivity.this));
        relativeLayoutSearch = (RelativeLayout) findViewById(R.id.relativeLayoutSearch);
        imageViewSearchCross = (ImageView) findViewById(R.id.imageViewSearchCross);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlacesApi(searchResult, true, 0);
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDeliveryAddress.setText("");
            }
        });

        if(getIntent().hasExtra(Constants.KEY_REQUEST_CODE)){
            placeRequestCode = getIntent().getIntExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
            String address = getIntent().getStringExtra(Constants.KEY_ADDRESS);
            if(TextUtils.isEmpty(address)){
                searchResult = null;
                editThisAddress = false;
                if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                    textViewTitle.setText("ADD Home");
                    editTextDeliveryAddress.setHint("Enter Home location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    textViewTitle.setText("ADD Work");
                    editTextDeliveryAddress.setHint("Enter Work location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                    textViewTitle.setText("ADD New Address");
                    editTextDeliveryAddress.setHint("Enter location");
                }
                buttonRemove.setVisibility(View.GONE);
            } else {
                searchResult = new Gson().fromJson(getIntent().getStringExtra(Constants.KEY_ADDRESS), SearchResult.class);
                editThisAddress = true;
                textViewTitle.setText("EDIT "+ searchResult.getName());
                buttonRemove.setText("REMOVE "+ searchResult.getName());
                editTextDeliveryAddress.setHint("Enter " + searchResult.getName().toLowerCase() + " location");
                editTextDeliveryAddress.setText(searchResult.getAddress());
                editTextDeliveryAddress.setSelection(editTextDeliveryAddress.getText().length());
                buttonRemove.setVisibility(View.VISIBLE);
            }
        }


        getTransactionUtils().openDeliveryAddressFragment(this, relativeLayoutContainer);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

    public void performBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            Intent intent=new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
        }

    }

	@Override
	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
		System.gc();
	}
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

    public void addPlacesApi(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId) {
        try {
            if(AppStatus.getInstance(AddPlaceActivity.this).isOnline(AddPlaceActivity.this)) {

                HashMap<String, String> params = new HashMap<>();
                if(matchedWithOtherId > 0){
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_ADDRESS, "");
                    params.put(Constants.KEY_GOOGLE_PLACE_ID, "");

                    params.put(Constants.KEY_ADDRESS_ID, String.valueOf(matchedWithOtherId));
                    params.put(Constants.KEY_DELETE_FLAG, "1");
                    params.put(Constants.KEY_IS_CONFIRMED, "1");
                }
                else{
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_ADDRESS, searchResult.getAddress());
                    params.put(Constants.KEY_GOOGLE_PLACE_ID, searchResult.getPlaceId());
                    params.put(Constants.KEY_TYPE, searchResult.getName());

                    params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));

                    if(editThisAddress){
                        params.put(Constants.KEY_ADDRESS_ID, String.valueOf(searchResult.getId()));
                        if(deleteAddress) {
                            params.put(Constants.KEY_DELETE_FLAG, "1");
                        }
                    }
                    params.put(Constants.KEY_IS_CONFIRMED, "1");
                }

				DialogPopup.showLoadingDialog(AddPlaceActivity.this, "Updating...");

                RestClient.getApiServices().addHomeAndWorkAddress(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                        Log.i(TAG, "addHomeAndWorkAddress response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.optInt("", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                            String message = JSONParser.getServerMessage(jObj);
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){

                                if(matchedWithOtherId > 0){
                                    if(searchResult.getName().equalsIgnoreCase(TYPE_HOME)){
                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_WORK, "");
                                    } else if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)){
                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_HOME, "");
                                    }
                                    addPlacesApi(searchResult, deleteAddress, 0);
                                }
                                else{
                                    int id = 0;
                                    if(searchResult.getId() != null){
                                        id = searchResult.getId();
                                    }
                                    searchResult.setId(jObj.optInt(KEY_ID, id));

                                    String strResult = gson.toJson(searchResult, SearchResult.class);
                                    if(deleteAddress){
                                        strResult = "";
                                    }
                                    if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_HOME, strResult);
                                    } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_WORK, strResult);
                                    } else {
                                        if(deleteAddress){
                                            Data.userData.getSearchResults().remove(searchResult);
                                        } else {
                                            Data.userData.getSearchResults().add(searchResult);
                                        }
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra("PLACE", strResult);
                                    if("".equalsIgnoreCase(strResult)) {
                                        setResult(RESULT_CANCELED, intent);
                                    } else{
                                        setResult(RESULT_OK, intent);
                                    }
                                    finish();
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }

                            } else{
                                DialogPopup.alertPopup(AddPlaceActivity.this, "", message);
                            }
                        }  catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.SERVER_ERROR_MSG);
                            DialogPopup.dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "addHomeAndWorkAddress error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
			}
			else {
				DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public EditText getEditTextDeliveryAddress() {
        return editTextDeliveryAddress;
    }

    public void openMapAddress(Bundle bundle) {
        getTransactionUtils().openMapFragment(this, relativeLayoutContainer, bundle);
    }
    private TransactionUtils transactionUtils;

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public void openAddToAddressBook(Bundle bundle) {
        getTransactionUtils().openAddToAddressFragment(this, relativeLayoutContainer, bundle);
    }

    public DeliveryAddressesFragment getDeliveryAddressesFragment() {
        return (DeliveryAddressesFragment) getSupportFragmentManager().findFragmentByTag(DeliveryAddressesFragment.class.getName());
    }

    public int getPlaceRequestCode(){
        return placeRequestCode;
    }

    public SearchResult getSearchResult(){
        return searchResult;
    }

    public boolean isEditThisAddress() {
        return editThisAddress;
    }

    public Button getButtonRemove() {
        return buttonRemove;
    }

    public ImageView getImageViewSearchCross() {
        return imageViewSearchCross;
    }

    public RelativeLayout getRelativeLayoutSearch() {
        return relativeLayoutSearch;
    }
}
