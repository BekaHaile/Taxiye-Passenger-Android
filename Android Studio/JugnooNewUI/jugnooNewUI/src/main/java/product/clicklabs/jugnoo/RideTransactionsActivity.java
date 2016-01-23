package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.datastructure.UpdateRideTransaction;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class RideTransactionsActivity extends BaseFragmentActivity implements UpdateRideTransaction, FlurryEventNames {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;

    RelativeLayout relativeLayoutContainer;
//	ListView listViewRideTransactions;
//	TextView textViewInfo;
//	Button buttonGetRide;
//
//	RideTransactionAdapter rideTransactionAdapter;
//
//	RelativeLayout relativeLayoutShowMore;
//	TextView textViewShowMore;
//
//    FutureSchedule futureSchedule = null;
//    ArrayList<RideInfo> rideInfosList = new ArrayList<>();
//    int totalRides = 0;
//
//	DecimalFormat decimalFormat = new DecimalFormat("#.#");
//	DecimalFormat decimalFormatNoDec = new DecimalFormat("#");

    public static UpdateRideTransaction updateRideTransaction;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
        try {
//            rideTransactionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);

        updateRideTransaction = this;

//        futureSchedule = null;
//        rideInfosList = new ArrayList<>();
//        totalRides = 0;

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
//		listViewRideTransactions = (ListView) findViewById(R.id.listViewRideTransactions);
//		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
//		buttonGetRide = (Button) findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.latoRegular(this));
//		textViewInfo.setVisibility(View.GONE);
//		buttonGetRide.setVisibility(View.GONE);
//        listViewRideTransactions.setVisibility(View.GONE);
		
		
//		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_show_more, null);
//		listViewRideTransactions.addFooterView(viewF);
//		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
//		ASSL.DoMagic(viewF);
//
//		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
//		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
//		textViewShowMore.setText("Show More");
//
//		rideTransactionAdapter = new RideTransactionAdapter(this);
//		listViewRideTransactions.setAdapter(rideTransactionAdapter);
//		rideTransactionAdapter.notifyDataSetChanged();
//
//        relativeLayoutShowMore.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

//		buttonGetRide.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				performBackPressed();
//			}
//		});
//
//		textViewInfo.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				getRecentRidesAPI(RideTransactionsActivity.this, true);
//			}
//		});
//
//
//		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				getRecentRidesAPI(RideTransactionsActivity.this, false);
//			}
//		});


//        getRecentRidesAPI(RideTransactionsActivity.this, true);

        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new RideTransactionsFragment(), RideTransactionsFragment.class.getName())
                .addToBackStack(RideTransactionsFragment.class.getName())
                .commitAllowingStateLoss();

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
	
	

    @Override
    public void updateRideTransaction(int position) {
        try {
//            if (rideInfosList.size() > 0) {
//                rideInfosList.get(position).isRatedBefore = 1;
//            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
