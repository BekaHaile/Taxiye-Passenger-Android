package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.EventsHolder;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationData> notificationList;
	private LinearLayout linearLayoutNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);

		linearLayoutNoNotifications = (LinearLayout) findViewById(R.id.linearLayoutNoNotifications);
		linearLayoutNoNotifications.setVisibility(View.GONE);
		((TextView)findViewById(R.id.textViewNoNotifications)).setTypeface(Fonts.mavenLight(this));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this,
				R.layout.list_item_notification);
		recyclerViewNotification.setAdapter(myNotificationAdapter);


        imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		loadListFromDB();

		FlurryEventLogger.event(this, FlurryEventNames.WHO_VISITED_THE_NOTIFICATION_SCREEN);
    }



	private void loadListFromDB(){
		notificationList.clear();
		notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
		Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
		if(notificationList.size() > 0){
			linearLayoutNoNotifications.setVisibility(View.GONE);
		} else{
			linearLayoutNoNotifications.setVisibility(View.VISIBLE);
		}
		myNotificationAdapter.notifyDataSetChanged();
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
		ASSL.closeActivity(root);
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadListFromDB();
			}
		});
	}

}
