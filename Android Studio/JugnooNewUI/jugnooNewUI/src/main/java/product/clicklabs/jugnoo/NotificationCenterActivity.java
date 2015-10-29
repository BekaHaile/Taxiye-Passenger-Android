package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.NotificationAdapter;
import product.clicklabs.jugnoo.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.EventsHolder;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView textViewTitle, textViewInfo;
    private ImageView imageViewBack;
    private RecyclerView recyclerViewNotification;
    private NotificationAdapter myNotificationAdapter;
    private ArrayList<NotificationData> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView)findViewById(R.id.imageViewBack);

		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
		textViewInfo.setVisibility(View.GONE);

        recyclerViewNotification = (RecyclerView) findViewById(R.id.my_request_recycler);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationCenterActivity.this));
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
		recyclerViewNotification.setHasFixedSize(false);

		notificationList = new ArrayList<>();
		myNotificationAdapter = new NotificationAdapter(notificationList, NotificationCenterActivity.this, R.layout.notification_list_item);
		recyclerViewNotification.setAdapter(myNotificationAdapter);


        imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		new GetNotificationsAsync().execute();

    }



	class GetNotificationsAsync extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogPopup.showLoadingDialog(NotificationCenterActivity.this, "Loading...");
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				notificationList.clear();
				notificationList.addAll(Database2.getInstance(NotificationCenterActivity.this).getAllNotification());
				Prefs.with(NotificationCenterActivity.this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			DialogPopup.dismissLoadingDialog();
			if(notificationList.size() > 0){
				textViewInfo.setVisibility(View.GONE);
			} else{
				textViewInfo.setVisibility(View.VISIBLE);
			}
			myNotificationAdapter.notifyDataSetChanged();
		}
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
	public void onDisplayMessagePushReceived(JSONObject jsonObject) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new GetNotificationsAsync().execute();
			}
		});
	}

}
