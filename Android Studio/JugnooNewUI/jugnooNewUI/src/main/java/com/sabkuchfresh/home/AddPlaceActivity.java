//package com.sabkuchfresh.home;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.maps.model.LatLng;
//import com.sabkuchfresh.MyApplication;
//import product.clicklabs.jugnoo.R;
//import com.sabkuchfresh.adapters.SearchListAdapter;
//import com.sabkuchfresh.datastructure.ApiResponseFlags;
//import com.sabkuchfresh.datastructure.AutoCompleteSearchResult;
//import com.sabkuchfresh.datastructure.SPLabels;
//import com.sabkuchfresh.datastructure.SearchResult;
//import com.sabkuchfresh.retrofit.RestClient;
//import com.sabkuchfresh.retrofit.model.SettleUserDebt;
//import com.sabkuchfresh.utils.ASSL;
//import com.sabkuchfresh.utils.AppStatus;
//import com.sabkuchfresh.utils.Data;
//import com.sabkuchfresh.utils.DialogPopup;
//import com.sabkuchfresh.utils.Fonts;
//import com.sabkuchfresh.utils.JSONParser;
//import com.sabkuchfresh.utils.LocalGson;
//import com.sabkuchfresh.utils.Log;
//import com.sabkuchfresh.utils.Prefs;
//import com.sabkuchfresh.widgets.NonScrollListView;
//import com.sabkuchfresh.widgets.ProgressWheel;
//import com.squareup.otto.Bus;
//
//import org.json.JSONObject;
//
//import java.util.HashMap;
//
//import retrofit.Callback;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//import retrofit.mime.TypedByteArray;
//
//
///**
// * Created by Ankit on 10/7/15.
// */
//public class AddPlaceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//
//    private final String TAG = AddPlaceActivity.class.getSimpleName();
//
//    private ImageView imageViewBack, imageViewSearchCross;
//    private LinearLayout root;
//    private EditText editTextSearch;
//    private ProgressWheel progressBarSearch;
//    private NonScrollListView listViewSearch;
//    private LinearLayout linearLayoutSearch, linearLayoutScrollSearch;
//    private TextView textViewScrollSearch, textViewTitle;
//    private Button buttonRemove;
//    private String placeName;
//
//	private GoogleApiClient mGoogleApiClient;
//
//    private final String TYPE_HOME = "home";
//    private final String TYPE_WORK = "work";
//
//    private Bus mBus;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_places);
//        root = (LinearLayout) findViewById(R.id.root);
//        new ASSL(this, root, 1134, 720, false);
//
//		mGoogleApiClient = new GoogleApiClient
//				.Builder(this)
//				.addApi(Places.GEO_DATA_API)
//				.addApi(Places.PLACE_DETECTION_API)
//				.addConnectionCallbacks(this)
//				.addOnConnectionFailedListener(this)
//				.build();
//
//        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.mavenRegular(this));
//        buttonRemove = (Button)findViewById(R.id.buttonRemove); buttonRemove.setTypeface(Fonts.mavenRegular(this));
//        editTextSearch = (EditText) findViewById(R.id.editTextSearch);editTextSearch.setTypeface(Fonts.latoRegular(this));
//
//
//        mBus = ((MyApplication) getApplication()).getBus();
//
//        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
//        progressBarSearch = (ProgressWheel) findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
//        listViewSearch = (NonScrollListView) findViewById(R.id.listViewSearch);
//        imageViewSearchCross = (ImageView) findViewById(R.id.imageViewSearchCross); imageViewSearchCross.setVisibility(View.GONE);
//        linearLayoutScrollSearch = (LinearLayout) findViewById(R.id.linearLayoutScrollSearch);
//        textViewScrollSearch = (TextView) findViewById(R.id.textViewScrollSearch);
//
//
//
//
//        buttonRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(placeName.equalsIgnoreCase(SPLabels.ADD_HOME)){
//                    addPlacesApi("", "", TYPE_HOME, "", false);
//                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_WORK)){
//                    addPlacesApi("", "", TYPE_WORK, "", false);
//                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_GYM)){
//                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_GYM, "");
//                }else if(placeName.equalsIgnoreCase(SPLabels.ADD_FRIEND)){
//                    Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_FRIEND, "");
//                }
//            }
//        });
//
//        imageViewBack.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                performBackPressed();
//            }
//        });
//
//        SearchListAdapter searchListAdapter = new SearchListAdapter(this, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
//                new SearchListAdapter.SearchListActionsHandler() {
//
//                    @Override
//                    public void onTextChange(String text) {
//                        if(text.length() > 0){
//                            imageViewSearchCross.setVisibility(View.VISIBLE);
//                        }
//                        else{
//                            imageViewSearchCross.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onSearchPre() {
//                        progressBarSearch.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onSearchPost() {
//                        progressBarSearch.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
//                        String strResult = new LocalGson().getJSONFromAutoCompleteSearchResult(autoCompleteSearchResult);
//
//                        if(placeName.equalsIgnoreCase("HOME")){
//                            String savedWorkStr = Prefs.with(AddPlaceActivity.this).getString(SPLabels.ADD_WORK, "");
//                            boolean removeOther = false;
//                            if(!"".equalsIgnoreCase(savedWorkStr)){
//                                AutoCompleteSearchResult savedWork = new LocalGson().getAutoCompleteSearchResultFromJSON(savedWorkStr);
//                                if(savedWork.getPlaceId().equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())){
//                                    removeOther = true;
//                                }
//                            }
//                            addPlacesApi(autoCompleteSearchResult.address, autoCompleteSearchResult.placeId, TYPE_HOME, strResult, removeOther);
//                        }
//                        else if(placeName.equalsIgnoreCase("WORK")){
//                            String savedHomeStr = Prefs.with(AddPlaceActivity.this).getString(SPLabels.ADD_HOME, "");
//                            boolean removeOther = false;
//                            if(!"".equalsIgnoreCase(savedHomeStr)){
//                                AutoCompleteSearchResult savedHome = new LocalGson().getAutoCompleteSearchResultFromJSON(savedHomeStr);
//                                if(savedHome.getPlaceId().equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())){
//                                    removeOther = true;
//                                }
//                            }
//                            addPlacesApi(autoCompleteSearchResult.address, autoCompleteSearchResult.placeId, TYPE_WORK, strResult, removeOther);
//                        }
//
//                        Log.v("onPlaceClick result ", "---> " + strResult);
//
//                        editTextSearch.setText(autoCompleteSearchResult.name + ", " + autoCompleteSearchResult.address);
//
//                    }
//
//                    @Override
//                    public void onPlaceSearchPre() {
//                        //progressBarInitialSearch.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onPlaceSearchPost(SearchResult searchResult) {
//                        Log.v("Search result ", "---> " + searchResult.getClass().getName());
//
//                    }
//
//                    @Override
//                    public void onPlaceSearchError() {
//                        //progressBarInitialSearch.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onPlaceSaved() {
//
//                    }
//                });
//        searchListAdapter.setShowSavedPlaces(false);
//
//        listViewSearch.setAdapter(searchListAdapter);
//
//        imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editTextSearch.setText("");
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mBus.register(this);
//        if(getIntent().hasExtra("requestCode")){
//            placeName = getIntent().getStringExtra("requestCode");
//            textViewTitle.setText("EDIT "+placeName);
//            buttonRemove.setText("REMOVE "+placeName);
//            editTextSearch.setHint("Enter " + placeName.toLowerCase() + " location");
//
//            if(!getIntent().getStringExtra("address").equalsIgnoreCase("")){
//                AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(getIntent().getStringExtra("address"));
//                editTextSearch.setText(searchResult.name);
//				editTextSearch.setSelection(editTextSearch.getText().length());
//                buttonRemove.setVisibility(View.VISIBLE);
//            }else {
//                buttonRemove.setVisibility(View.GONE);
//                textViewTitle.setText("ADD "+placeName);
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mBus.unregister(this);
//    }
//
//    @Override
//	public void onStart() {
//		super.onStart();
//		mGoogleApiClient.connect();
//	}
//
//	@Override
//	public void onStop() {
//		mGoogleApiClient.disconnect();
//		super.onStop();
//	}
//
//    public void performBackPressed(){
//        Intent intent=new Intent();
//        setResult(RESULT_CANCELED, intent);
//        finish();
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }
//
//	@Override
//	public void onBackPressed() {
//		performBackPressed();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		ASSL.closeActivity(root);
//		System.gc();
//	}
//	public void onConnected(Bundle bundle) {
//	}
//
//	@Override
//	public void onConnectionSuspended(int i) {
//	}
//
//	@Override
//	public void onConnectionFailed(ConnectionResult connectionResult) {
//	}
//
//    public void addPlacesApi(final String address, final String googlePlaceId, final String type,
//                             final String strResult, final boolean removeOther) {
//        try {
//            if(AppStatus.getInstance(AddPlaceActivity.this).isOnline(AddPlaceActivity.this)) {
//
//                HashMap<String, String> params = new HashMap<>();
//                if(removeOther){
//                    params.put("access_token", Data.userData.accessToken);
//                    params.put("address", "");
//                    params.put("google_place_id", "");
//
//                    if(type.equalsIgnoreCase(TYPE_HOME)){
//                        params.put("type", TYPE_WORK);
//                    } else if(type.equalsIgnoreCase(TYPE_WORK)){
//                        params.put("type", TYPE_HOME);
//                    }
//                }
//                else{
//                    params.put("access_token", Data.userData.accessToken);
//                    params.put("address", address);
//                    params.put("google_place_id", googlePlaceId);
//                    params.put("type", type);
//                }
//
//				DialogPopup.showLoadingDialog(AddPlaceActivity.this, "Updating...");
//
//                RestClient.getApiServices().addHomeAndWorkAddress(params, new Callback<SettleUserDebt>() {
//                    @Override
//                    public void success(SettleUserDebt settleUserDebt, Response response) {
//                        String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
//                        Log.i(TAG, "addHomeAndWorkAddress response = " + responseStr);
//                        DialogPopup.dismissLoadingDialog();
//                        try {
//                            JSONObject jObj = new JSONObject(responseStr);
//                            int flag = jObj.optInt("", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
//                            String message = JSONParser.getServerMessage(jObj);
//                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
//
//                                if(removeOther){
//                                    if(type.equalsIgnoreCase(TYPE_HOME)){
//                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_WORK, "");
//                                    } else if(type.equalsIgnoreCase(TYPE_WORK)){
//                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_HOME, "");
//                                    }
//                                    addPlacesApi(address, googlePlaceId, type, strResult, false);
//                                }
//                                else{
//                                    if(type.equalsIgnoreCase(TYPE_HOME)){
//                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_HOME, strResult);
//                                    } else if(type.equalsIgnoreCase(TYPE_WORK)){
//                                        Prefs.with(AddPlaceActivity.this).save(SPLabels.ADD_WORK, strResult);
//                                    }
//
//                                    Intent intent=new Intent();
//                                    intent.putExtra("PLACE", strResult);
//                                    if("".equalsIgnoreCase(strResult)) {
//                                        setResult(RESULT_CANCELED, intent);
//                                    } else{
//                                        setResult(RESULT_OK, intent);
//                                    }
//                                    finish();
//                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                                }
//
//                            } else{
//                                DialogPopup.alertPopup(AddPlaceActivity.this, "", message);
//                            }
//                        }  catch (Exception exception) {
//                            exception.printStackTrace();
//                            DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.SERVER_ERROR_MSG);
//                            DialogPopup.dismissLoadingDialog();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.e(TAG, "addHomeAndWorkAddress error="+error.toString());
//                        DialogPopup.dismissLoadingDialog();
//                        DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
//                    }
//                });
//			}
//			else {
//				DialogPopup.alertPopup(AddPlaceActivity.this, "", Data.CHECK_INTERNET_MSG);
//			}
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
