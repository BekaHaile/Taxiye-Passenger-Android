package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.RideInfo;
import product.clicklabs.jugnoo.datastructure.UpdateRideTransaction;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideTransactionsActivity extends BaseActivity implements UpdateRideTransaction, FlurryEventNames {

    private final String TAG = RideTransactionsActivity.class.getSimpleName();

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	ListView listViewRideTransactions;
	TextView textViewInfo;
	Button buttonGetRide;
	
	RideTransactionAdapter rideTransactionAdapter;
	
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;

    FutureSchedule futureSchedule = null;
    ArrayList<RideInfo> rideInfosList = new ArrayList<RideInfo>();
    int totalRides = 0;

	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDec = new DecimalFormat("#");

    public static UpdateRideTransaction updateRideTransaction;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
        try {
            rideTransactionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);

        updateRideTransaction = this;

        futureSchedule = null;
        rideInfosList = new ArrayList<RideInfo>();
        totalRides = 0;

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		listViewRideTransactions = (ListView) findViewById(R.id.listViewRideTransactions);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
		buttonGetRide = (Button) findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.latoRegular(this));
		textViewInfo.setVisibility(View.GONE);
		buttonGetRide.setVisibility(View.GONE);
		
		
		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_show_more, null);
		listViewRideTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(viewF);
		
		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewShowMore.setText("Show More");

		rideTransactionAdapter = new RideTransactionAdapter(this);
		listViewRideTransactions.setAdapter(rideTransactionAdapter);
		rideTransactionAdapter.notifyDataSetChanged();

        relativeLayoutShowMore.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonGetRide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRecentRidesAPI(RideTransactionsActivity.this, true);
			}
		});
		
		
		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRecentRidesAPI(RideTransactionsActivity.this, false);
			}
		});


        getRecentRidesAPI(RideTransactionsActivity.this, true);

	}

	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
	public void getRecentRidesAPI(final Activity activity, final boolean refresh) {
        DialogPopup.dismissLoadingDialog();
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			if(refresh){
				rideInfosList.clear();
				futureSchedule = null;
			}
			
            DialogPopup.showLoadingDialog(activity, "Loading...");
            textViewInfo.setVisibility(View.GONE);
			
			HashMap<String, String> params = new HashMap<>();
		
			params.put("access_token", Data.userData.accessToken);
            params.put("start_from", "" + rideInfosList.size());

            RestClient.getApiServices().getRecentRides(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "getRecentRides response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {

                                totalRides = jObj.getInt("num_rides");

                                if (jObj.has("schedule")) {
                                    JSONObject jSchedule = jObj.getJSONObject("schedule");
                                    futureSchedule = new FutureSchedule(jSchedule.getString("pickup_id"),
                                            jSchedule.getString("address"),
                                            jSchedule.getString("pickup_date"),
                                            jSchedule.getString("pickup_time"),
                                            new LatLng(jSchedule.getDouble("latitude"), jSchedule.getDouble("longitude")),
                                            jSchedule.getInt("modifiable"),
                                            jSchedule.getInt("status"));
                                    totalRides = totalRides + 1;
                                } else {
                                    if (rideInfosList.size() == 0) {
                                        futureSchedule = null;
                                    }
                                }


                                if (jObj.has("rides")) {
                                    JSONArray jRidesArr = jObj.getJSONArray("rides");
                                    for (int i = 0; i < jRidesArr.length(); i++) {
                                        JSONObject jRide = jRidesArr.getJSONObject(i);
                                        int isRatedBefore = 1;
                                        if (jRide.has("is_rated_before")) {
                                            isRatedBefore = jRide.getInt("is_rated_before");
                                        }

                                        int driverId = 0;
                                        if (jRide.has("driver_id")) {
                                            driverId = jRide.getInt("driver_id");
                                        }

                                        int engagementId = 0;
                                        if (jRide.has("engagement_id")) {
                                            engagementId = jRide.getInt("engagement_id");
                                        }

                                        double waitTime = -1;
                                        if (jRide.has("wait_time")) {
                                            waitTime = jRide.getDouble("wait_time");
                                        }

                                        int isCancelledRide = 0;
                                        if (jRide.has("is_cancelled_ride")) {
                                            isCancelledRide = jRide.getInt("is_cancelled_ride");
                                        }

                                        rideInfosList.add(new RideInfo(jRide.getString("pickup_address"),
                                                jRide.getString("drop_address"),
                                                jRide.getDouble("amount"),
                                                jRide.getDouble("distance"),
                                                jRide.getDouble("ride_time"), waitTime,
                                                jRide.getString("date"), isRatedBefore, driverId, engagementId, isCancelledRide));
                                    }
                                }

                                updateListData("You haven't tried Jugnoo yet.", false);

                            } else {
                                updateListData("Some error occurred, tap to retry", true);
                            }
                        } else {
                            updateListData("Some error occurred, tap to retry", true);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        updateListData("Some error occurred, tap to retry", true);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "getRecentRides error="+error.toString());
                    updateListData("Some error occurred, tap to retry", true);
                    DialogPopup.dismissLoadingDialog();
                }
            });
		}
		else {
			//updateListData("No internet connection, tap to retry", true);
            DialogPopup.dialogNoInternet(RideTransactionsActivity.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG, new Utils.AlertCallBackWithButtonsInterface() {
                @Override
                public void positiveClick(View v) {
                    getRecentRidesAPI(RideTransactionsActivity.this, true);
                }

                @Override
                public void neutralClick(View v) {
                }

                @Override
                public void negativeClick(View v) {
                }
            });
		}
	}
	
	public void updateListData(String message, boolean errorOccurred){
		
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			buttonGetRide.setVisibility(View.GONE);
			
			rideInfosList.clear();
			rideTransactionAdapter.notifyDataSetChanged();
		}
		else{
			if(rideInfosList.size() == 0 && futureSchedule == null){
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
				buttonGetRide.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
				buttonGetRide.setVisibility(View.GONE);
			}
			rideTransactionAdapter.notifyDataSetChanged();
		}
	}




    class ViewHolderRideTransaction {
        TextView textViewPickupAt, textViewIdValue, textViewFrom, textViewFromValue, textViewTo,
            textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewCancel,
				textViewRideCancelled;
        ImageView imageViewDiv;
        RelativeLayout relativeLayoutTo;
		LinearLayout linearLayoutCancel;
		LinearLayout linearLayoutRideReceipt, linearLayoutMain;
        RelativeLayout relative;
        int id;
    }

    class RideTransactionAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ViewHolderRideTransaction holder;
        Context context;
        public RideTransactionAdapter(Context context) {
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }

        @Override
        public int getCount() {
            if(futureSchedule == null){
                return rideInfosList.size();
            }
            else{
                return rideInfosList.size()+1;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolderRideTransaction();
                convertView = mInflater.inflate(R.layout.list_item_ride_transaction, null);

                holder.textViewPickupAt = (TextView) convertView.findViewById(R.id.textViewPickupAt); holder.textViewPickupAt.setTypeface(Fonts.mavenLight(context));
                ((TextView)convertView.findViewById(R.id.textViewId)).setTypeface(Fonts.mavenLight(context));
                holder.linearLayoutMain = (LinearLayout)convertView.findViewById(R.id.linearLayoutMain);
                holder.textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue); holder.textViewIdValue.setTypeface(Fonts.mavenLight(context));
                holder.textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom); holder.textViewFrom.setTypeface(Fonts.mavenLight(context));
                holder.textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue); holder.textViewFromValue.setTypeface(Fonts.mavenLight(context));
                holder.textViewTo = (TextView) convertView.findViewById(R.id.textViewTo); holder.textViewTo.setTypeface(Fonts.mavenLight(context));
                holder.textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue); holder.textViewToValue.setTypeface(Fonts.mavenLight(context));
                holder.textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails); holder.textViewDetails.setTypeface(Fonts.mavenLight(context));
                holder.textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue); holder.textViewDetailsValue.setTypeface(Fonts.mavenLight(context));
                holder.textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount); holder.textViewAmount.setTypeface(Fonts.mavenLight(context));
                holder.textViewCancel = (TextView) convertView.findViewById(R.id.textViewCancel); holder.textViewCancel.setTypeface(Fonts.mavenLight(context));
				holder.textViewRideCancelled = (TextView) convertView.findViewById(R.id.textViewRideCancelled);
				holder.textViewRideCancelled.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);


                holder.imageViewDiv = (ImageView) convertView.findViewById(R.id.imageViewDiv);

                holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
                holder.linearLayoutCancel = (LinearLayout) convertView.findViewById(R.id.linearLayoutCancel);
                holder.relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
				holder.linearLayoutRideReceipt = (LinearLayout) convertView.findViewById(R.id.linearLayoutRideReceipt);

                holder.relative.setTag(holder);
                holder.linearLayoutCancel.setTag(holder);
                holder.linearLayoutMain.setTag(holder);
				holder.linearLayoutRideReceipt.setTag(holder);

                holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
                ASSL.DoMagic(holder.relative);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderRideTransaction) convertView.getTag();
            }



            holder.id = position;

            if(futureSchedule != null){
                if(position == 0){
                    holder.textViewPickupAt.setVisibility(View.VISIBLE);
                    holder.relativeLayoutTo.setVisibility(View.GONE);
					holder.linearLayoutRideReceipt.setVisibility(View.GONE);
                    holder.imageViewDiv.setVisibility(View.VISIBLE);
					holder.textViewRideCancelled.setVisibility(View.GONE);

                    holder.textViewAmount.setVisibility(View.GONE);

                    holder.textViewIdValue.setText(""+futureSchedule.pickupId);
                    holder.textViewFromValue.setText(futureSchedule.pickupAddress);
                    holder.textViewDetails.setText("Date: ");
                    holder.textViewDetailsValue.setText(futureSchedule.pickupDate + ", " + futureSchedule.pickupTime);

                    if(futureSchedule.modifiable == 1){
                        holder.linearLayoutCancel.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.linearLayoutCancel.setVisibility(View.GONE);
                    }
                }
                else{
                    RideInfo rideInfo = rideInfosList.get(position-1);

                    holder.textViewPickupAt.setVisibility(View.GONE);
                    holder.textViewAmount.setVisibility(View.VISIBLE);
                    holder.linearLayoutCancel.setVisibility(View.GONE);

                    holder.textViewIdValue.setText(""+rideInfo.engagementId);
                    holder.textViewFromValue.setText(rideInfo.pickupAddress);
                    holder.textViewToValue.setText(rideInfo.dropAddress);
                    holder.textViewDetails.setText("Details: ");


					if(0 == rideInfo.isCancelledRide) {
						if (rideInfo.rideTime == 1) {
							holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
									+ decimalFormatNoDec.format(rideInfo.rideTime) + " minute, " + rideInfo.date);
						} else {
							holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
									+ decimalFormatNoDec.format(rideInfo.rideTime) + " minutes, " + rideInfo.date);
						}
						holder.textViewAmount.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(rideInfo.amount)));

						if (1 != rideInfo.isRatedBefore) {
							holder.imageViewDiv.setVisibility(View.VISIBLE);
						} else {
							holder.imageViewDiv.setVisibility(View.VISIBLE);
						}
						holder.linearLayoutRideReceipt.setVisibility(View.GONE);
						holder.textViewRideCancelled.setVisibility(View.GONE);
						holder.relativeLayoutTo.setVisibility(View.VISIBLE);
					}
					else{
						holder.textViewDetailsValue.setText(rideInfo.date+",");
						holder.textViewAmount.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(rideInfo.amount)));
						holder.imageViewDiv.setVisibility(View.VISIBLE);
						holder.linearLayoutRideReceipt.setVisibility(View.GONE);
						holder.textViewRideCancelled.setVisibility(View.VISIBLE);
						holder.relativeLayoutTo.setVisibility(View.GONE);
					}

                }
            }
            else{
                RideInfo rideInfo = rideInfosList.get(position);

                holder.textViewPickupAt.setVisibility(View.GONE);
                holder.textViewAmount.setVisibility(View.VISIBLE);
                holder.linearLayoutCancel.setVisibility(View.GONE);

                holder.textViewIdValue.setText(""+rideInfo.engagementId);
                holder.textViewFromValue.setText(rideInfo.pickupAddress);
                holder.textViewToValue.setText(rideInfo.dropAddress);
                holder.textViewDetails.setText("Details: ");

				if(0 == rideInfo.isCancelledRide) {
					if (rideInfo.rideTime == 1) {
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
								+ decimalFormatNoDec.format(rideInfo.rideTime) + " minute, " + rideInfo.date);
					} else {
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
								+ decimalFormatNoDec.format(rideInfo.rideTime) + " minutes, " + rideInfo.date);
					}
					holder.textViewAmount.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(rideInfo.amount)));

					if (1 != rideInfo.isRatedBefore) {
						holder.imageViewDiv.setVisibility(View.VISIBLE);
					} else {
						holder.imageViewDiv.setVisibility(View.VISIBLE);
					}
					holder.linearLayoutRideReceipt.setVisibility(View.GONE);
					holder.textViewRideCancelled.setVisibility(View.GONE);
					holder.relativeLayoutTo.setVisibility(View.VISIBLE);
				}
				else{
					holder.textViewDetailsValue.setText(rideInfo.date+",");
					holder.textViewAmount.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(rideInfo.amount)));
					holder.imageViewDiv.setVisibility(View.VISIBLE);
					holder.linearLayoutRideReceipt.setVisibility(View.GONE);
					holder.textViewRideCancelled.setVisibility(View.VISIBLE);
					holder.relativeLayoutTo.setVisibility(View.GONE);
				}
            }


			holder.linearLayoutMain.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
                        holder = (ViewHolderRideTransaction) v.getTag();
                        RideInfo rideInfo = null;
                        if (futureSchedule != null) {
                            rideInfo = rideInfosList.get(holder.id - 1);
                        } else {
                            rideInfo = rideInfosList.get(holder.id);
                        }

                        if(0 == rideInfo.isCancelledRide) {
                            if (AppStatus.getInstance(context).isOnline(context)) {

                                Intent intent = new Intent(RideTransactionsActivity.this, RideSummaryActivity.class);
                                if (futureSchedule != null) {
                                    intent.putExtra("engagement_id", rideInfo.engagementId);
                                } else {
                                    intent.putExtra("engagement_id", rideInfo.engagementId);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            } else {
                                DialogPopup.alertPopup(RideTransactionsActivity.this, "", Data.CHECK_INTERNET_MSG);
                            }
                            FlurryEventLogger.event(RIDE_SUMMARY_CHECKED_LATER);
                        }

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


            return convertView;
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if(totalRides > getCount()){
                relativeLayoutShowMore.setVisibility(View.VISIBLE);
            }
            else{
                relativeLayoutShowMore.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void updateRideTransaction(int position) {
        try {
            if (rideInfosList.size() > 0) {
                rideInfosList.get(position).isRatedBefore = 1;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
