package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
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
public class AddPlaceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = AddPlaceActivity.class.getSimpleName();

    private ImageView imageViewBack, imageViewSearchCross;
    private LinearLayout root;
    private EditText editTextSearch;
    private ProgressWheel progressBarSearch;
    private NonScrollListView listViewSearch;
    private LinearLayout linearLayoutSearch, linearLayoutScrollSearch;
    private TextView textViewScrollSearch, textViewTitle;
    private Button buttonRemove;
    private int placeRequestCode;
    private SearchResult searchResult;
    private boolean editThisAddress = false;

    RelativeLayout relativeLayoutLabel;
    EditText editTextSearchLabel;
    ImageView imageViewSearchCrossLabel;

	private GoogleApiClient mGoogleApiClient;

    private Gson gson;



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
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);editTextSearch.setTypeface(Fonts.mavenMedium(this));



        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        progressBarSearch = (ProgressWheel) findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
        listViewSearch = (NonScrollListView) findViewById(R.id.listViewSearch);
        imageViewSearchCross = (ImageView) findViewById(R.id.imageViewSearchCross); imageViewSearchCross.setVisibility(View.GONE);
        linearLayoutScrollSearch = (LinearLayout) findViewById(R.id.linearLayoutScrollSearch);
        textViewScrollSearch = (TextView) findViewById(R.id.textViewScrollSearch);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        relativeLayoutLabel = (RelativeLayout) findViewById(R.id.relativeLayoutLabel);
        editTextSearchLabel = (EditText) findViewById(R.id.editTextSearchLabel); editTextSearchLabel.setTypeface(Fonts.mavenMedium(this));
        imageViewSearchCrossLabel = (ImageView) findViewById(R.id.imageViewSearchCrossLabel);
        relativeLayoutLabel.setVisibility(View.GONE);


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

        SearchListAdapter searchListAdapter = new SearchListAdapter(this, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient, 0,
                new SearchListAdapter.SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        if(text.length() > 0){
                            imageViewSearchCross.setVisibility(View.VISIBLE);
                        }
                        else{
                            imageViewSearchCross.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSearchPost() {
                        progressBarSearch.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPlaceClick(SearchResult autoCompleteSearchResult) {

                    }

                    @Override
                    public void onPlaceSearchPre() {
                        //progressBarInitialSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION
                                && editTextSearchLabel.getText().toString().trim().length() == 0){
                            editTextSearchLabel.requestFocus();
                            editTextSearchLabel.setError(getString(R.string.label_can_not_be_empty));
                        } else {
                            if (editThisAddress && AddPlaceActivity.this.searchResult != null) {
                                searchResult.setId(AddPlaceActivity.this.searchResult.getId());
                            }
                            if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                                searchResult.setName(editTextSearchLabel.getText().toString().trim());
                            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                                searchResult.setName(TYPE_HOME);
                            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                                searchResult.setName(TYPE_WORK);
                            }

                            if (placeRequestCode == Constants.REQUEST_CODE_ADD_HOME) {
                                String savedWorkStr = Prefs.with(AddPlaceActivity.this).getString(SPLabels.ADD_WORK, "");
                                int otherId = 0;
                                if (!"".equalsIgnoreCase(savedWorkStr)) {
                                    SearchResult savedWork = new Gson().fromJson(savedWorkStr, SearchResult.class);
                                    if (savedWork.getPlaceId().equalsIgnoreCase(searchResult.getPlaceId())) {
                                        otherId = savedWork.getId();
                                    }
                                }
                                addPlacesApi(searchResult, false, otherId);
                            } else if (placeRequestCode == Constants.REQUEST_CODE_ADD_WORK) {
                                String savedHomeStr = Prefs.with(AddPlaceActivity.this).getString(SPLabels.ADD_HOME, "");
                                int otherId = 0;
                                if (!"".equalsIgnoreCase(savedHomeStr)) {
                                    SearchResult savedHome = new Gson().fromJson(savedHomeStr, SearchResult.class);
                                    if (savedHome.getPlaceId().equalsIgnoreCase(searchResult.getPlaceId())) {
                                        otherId = savedHome.getId();
                                    }
                                }
                                addPlacesApi(searchResult, false, otherId);
                            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION) {
                                addPlacesApi(searchResult, false, 0);
                            }

                            editTextSearch.setText(searchResult.getAddress());
                        }
                    }

                    @Override
                    public void onPlaceSearchError() {
                        //progressBarInitialSearch.setVisibility(View.GONE);
                        Toast.makeText(AddPlaceActivity.this, R.string.could_not_find_address, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPlaceSaved() {

                    }
                }, false);

        listViewSearch.setAdapter(searchListAdapter);

        imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra(Constants.KEY_REQUEST_CODE)){
            placeRequestCode = getIntent().getIntExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
            String address = getIntent().getStringExtra(Constants.KEY_ADDRESS);
            if(TextUtils.isEmpty(address)){
                searchResult = null;
                editThisAddress = false;
                if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                    textViewTitle.setText("ADD Home");
                    editTextSearch.setHint("Enter Home location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    textViewTitle.setText("ADD Work");
                    editTextSearch.setHint("Enter Work location");
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                    textViewTitle.setText("ADD New Address");
                    editTextSearch.setHint("Enter location");
                    relativeLayoutLabel.setVisibility(View.VISIBLE);
                }
                buttonRemove.setVisibility(View.GONE);
            } else {
                searchResult = new Gson().fromJson(getIntent().getStringExtra(Constants.KEY_ADDRESS), SearchResult.class);
                editThisAddress = true;
                textViewTitle.setText("EDIT "+ searchResult.getName());
                buttonRemove.setText("REMOVE "+ searchResult.getName());
                editTextSearch.setHint("Enter " + searchResult.getName().toLowerCase() + " location");
                editTextSearch.setText(searchResult.getAddress());
                editTextSearch.setSelection(editTextSearch.getText().length());
                buttonRemove.setVisibility(View.VISIBLE);
                if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                    relativeLayoutLabel.setVisibility(View.VISIBLE);
                    editTextSearchLabel.setText(searchResult.getName());
                }
            }
        }
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
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
                    params.put(Constants.KEY_KEEP_DUPLICATE, "1");
                }
                else{
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_ADDRESS, searchResult.getAddress());
                    params.put(Constants.KEY_GOOGLE_PLACE_ID, searchResult.getPlaceId());
                    params.put(Constants.KEY_TYPE, searchResult.getName());

                    params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));
                    params.put(Constants.KEY_KEEP_DUPLICATE, "1");

                    if(editThisAddress){
                        params.put(Constants.KEY_ADDRESS_ID, String.valueOf(searchResult.getId()));
                        if(deleteAddress) {
                            params.put(Constants.KEY_DELETE_FLAG, "1");
                        }
                    }
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
                                    searchResult.setId(jObj.optInt(KEY_ID, 0));

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

}
