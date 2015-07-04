package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class FareEstimateActivity extends Activity {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    RelativeLayout relativeLayoutDropLocationBar;
    EditText editTextDropLocation;
    ProgressBar progressBarDropLocation;
    ListView listViewDropLocationSearch;

    LinearLayout linearLayoutFareEstimateDetails;
    GoogleMap mapLite;
    TextView textViewPickupLocation, textViewDropLocation, textViewEstimateTime, textViewEstimateDistance, textViewEstimateFare, textViewEstimateFareNote;
    Button buttonOk;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        relativeLayoutDropLocationBar = (RelativeLayout) findViewById(R.id.relativeLayoutDropLocationBar);
        editTextDropLocation = (EditText) findViewById(R.id.editTextDropLocation); editTextDropLocation.setTypeface(Fonts.latoRegular(this));
        progressBarDropLocation = (ProgressBar) findViewById(R.id.progressBarDropLocation);
        listViewDropLocationSearch = (ListView) findViewById(R.id.listViewDropLocationSearch);


        linearLayoutFareEstimateDetails = (LinearLayout) findViewById(R.id.linearLayoutFareEstimateDetails);






        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
    }


    public void performBackPressed() {
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

}
