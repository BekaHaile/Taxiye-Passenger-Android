package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity {

    private LinearLayout root;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationData> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());

//        Log.v("Date in timemillis first", "--> " + DateOperations.getCurrentTimeInUTC());
//        Log.v("Date in timemillis ", "--> " + DateOperations.getMilliseconds(DateOperations.getCurrentTimeInUTC()));

        getAllNotification();



        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database2.getInstance(NotificationCenterActivity.this).deleteNotificationTable();

            }
        });
    }

    public void getAllNotification(){

        notificationList = new ArrayList<>();
        notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());

        myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this, R.layout.notification_list_item);
        recyclerViewNotification.setAdapter(myNotificationAdapter);
    }

    public void performBackPressed(){
        Intent intent=new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
