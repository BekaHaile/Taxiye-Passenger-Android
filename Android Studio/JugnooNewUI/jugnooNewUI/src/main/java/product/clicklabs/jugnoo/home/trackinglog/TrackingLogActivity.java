package product.clicklabs.jugnoo.home.trackinglog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class TrackingLogActivity extends BaseActivity implements FlurryEventNames, FirebaseEvents {

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    EditText editTextEngagementId;
    Button buttonSubmit;
    RecyclerView recyclerViewLogs;


    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_log);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        editTextEngagementId = (EditText) findViewById(R.id.editTextEngagementId); editTextEngagementId.setTypeface(Fonts.mavenMedium(this));
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit); buttonSubmit.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

        recyclerViewLogs = (RecyclerView) findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLogs.setHasFixedSize(false);


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
