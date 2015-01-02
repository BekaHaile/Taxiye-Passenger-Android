package product.clicklabs.jugnoo.driver;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.DriverType;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DriverTypeActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn, uncheckAll, checkAll;
	TextView title;
	ListView driverTypeList;
	
	
	DriverTypeListAdapter driverTypeListAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_type_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverTypeActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		uncheckAll = (Button) findViewById(R.id.uncheckAll); uncheckAll.setTypeface(Data.regularFont(getApplicationContext()));
		checkAll = (Button) findViewById(R.id.checkAll); checkAll.setTypeface(Data.regularFont(getApplicationContext()));
		
		driverTypeList = (ListView) findViewById(R.id.driverTypeList);
		driverTypeListAdapter = new DriverTypeListAdapter(this);
		
		driverTypeList.setAdapter(driverTypeListAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		uncheckAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i=0; i<Data.driverTypes.size(); i++){
					Data.driverTypes.get(i).selected = false;
				}
				driverTypeListAdapter.notifyDataSetChanged();
			}
			
		});
		
		
		checkAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i=0; i<Data.driverTypes.size(); i++){
					Data.driverTypes.get(i).selected = true;
				}
				driverTypeListAdapter.notifyDataSetChanged();
				
				onClickWhatsApp("Hello");
				
			}
			
		});
		
		
		
		if(Data.driverTypes == null){
			Data.driverTypes = new ArrayList<DriverType>();
		}
		Data.driverTypes.clear();
		Data.driverTypes.add(new DriverType("1", "Auto"));
		Data.driverTypes.add(new DriverType("2", "Taxi"));
		
		driverTypeListAdapter.notifyDataSetChanged();
		
	}
	
	
	public void onClickWhatsApp(String text) {
		try {
			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");
			waIntent.setPackage("com.whatsapp");
			if (waIntent != null) {
				waIntent.putExtra(Intent.EXTRA_TEXT, text);//
				startActivity(Intent.createChooser(waIntent, "Share with"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	
	
	
	class ViewHolderDriverType {
		TextView driverTypeName;
		ImageView tick;
		LinearLayout relative;
		int id;
	}

	class DriverTypeListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverType holder;
		Context context;

		public DriverTypeListAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			return Data.driverTypes.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {

			
			if (convertView == null) {
				
				holder = new ViewHolderDriverType();
				convertView = mInflater.inflate(R.layout.driver_type_list_item, null);
				
				holder.driverTypeName = (TextView) convertView.findViewById(R.id.driverTypeName); holder.driverTypeName.setTypeface(Data.regularFont(context));
				holder.tick = (ImageView) convertView.findViewById(R.id.tick);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 110));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolderDriverType) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.driverTypeName.setText(Data.driverTypes.get(position).driverTypeName);
			
			if(Data.driverTypes.get(position).selected){
				holder.tick.setImageResource(R.drawable.check_on);
			}
			else{
				holder.tick.setImageResource(R.drawable.check_off);
			}
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderDriverType) v.getTag();
					
					if(Data.driverTypes.get(holder.id).selected){
						Data.driverTypes.get(holder.id).selected = false;
					}
					else{
						Data.driverTypes.get(holder.id).selected = true;
					}
					
					notifyDataSetChanged();
				}
			});
			
			
			
			return convertView;
		}

	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
