package product.clicklabs.jugnoo.home.trackinglog;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

/**
 * Created by shankar on 9/19/16.
 */
public class TrackingLogHelper {

	private final String TAG = TrackingLogHelper.class.getSimpleName();
	private Context context;

	public TrackingLogHelper(Context context){
		this.context = context;
	}

	//79152766

	private void generateTrackLogFile(String engagementId){
		try {
			JSONArray driverLocations = MyApplication.getInstance().getDatabase2().getDriverLocations(Integer.parseInt(engagementId));
			JSONArray trackingLogs = MyApplication.getInstance().getDatabase2().getTrackingLogs(Integer.parseInt(engagementId));
			if(trackingLogs.length() > 0 && driverLocations.length() > 0) {
				JSONObject jsonObjectTrackingLog = new JSONObject();
				jsonObjectTrackingLog.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
				jsonObjectTrackingLog.put(Constants.KEY_DRIVER_LOCATIONS, driverLocations);
				jsonObjectTrackingLog.put(Constants.KEY_TRACKING_LOGS, trackingLogs);
				writeTrackingLogToFile(engagementId, jsonObjectTrackingLog.toString());
				MyApplication.getInstance().getDatabase2().deleteDriverLocations(Integer.parseInt(engagementId));
				MyApplication.getInstance().getDatabase2().deleteTrackingLogs(Integer.parseInt(engagementId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadAllTrackLogs(){
		try{
			ArrayList<Integer> engagementIds = MyApplication.getInstance().getDatabase2().getDistinctEngagementIdsFromDriverLocations();
			for (Integer engagementId : engagementIds) {
				generateTrackLogFile(String.valueOf(engagementId));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void writeTrackingLogToFile(final String filePrefix, final String response) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File gpxfile = getTrackingLogFile(filePrefix);
					if(gpxfile != null){
						FileWriter writer = new FileWriter(gpxfile, true);
						writer.append(response);
						writer.flush();
						writer.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}).start();
	}

	private File getTrackingLogFolder(){
		try {
			String strFolder = Environment.getExternalStorageDirectory() + "/Android/data/." + BuildConfig.APP_DB_ID + "_auth/tracking_log";
			File folder = new File(strFolder);
			if(!folder.exists()){
				folder.mkdirs();
			}
			return folder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private File getTrackingLogFile(final String filePrefix){
		try {
			String fileName = getTrackingLogFolder() + "/" + filePrefix + ".txt";
			File gpxfile = new File(fileName);
			if (!gpxfile.exists()) {
				gpxfile.createNewFile();
			}
			return gpxfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	void checkAndUploadRemainingFiles(String accessToken){
		try{
			File[] files = getTrackingLogFolder().listFiles();
			if(files.length == 0){
				return;
			}
			for (File file : files) {
				try {
					Log.d(TAG, "FileName:" + file.getName());
					String filePrefix = file.getName().split("\\.")[0];
					if(Utils.checkIfOnlyDigits(filePrefix)){
						TypedFile typedFile = new TypedFile("application/octet-stream", file);
						HashMap<String, String> map = new HashMap<>();
						map.put(Constants.KEY_ACCESS_TOKEN, accessToken);
						map.put(Constants.KEY_ENGAGEMENT_ID, filePrefix);
						map.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());

						new HomeUtil().putDefaultParams(map);
						Response response = RestClient.getApiService().customerUploadRideLog(typedFile, map);
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						JSONObject jObject1 = new JSONObject(responseStr);
						int flag = jObject1.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
						if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
							file.delete();
						}
						Thread.sleep(2000);
					}
				} catch (Exception|OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			files = getTrackingLogFolder().listFiles();
			int filesLeft = 0;
			for (File file : files) {
				String filePrefix = file.getName().split("\\.")[0];
				if(Utils.checkIfOnlyDigits(filePrefix)){
					filesLeft++;
				}
			}
			if(filesLeft > 0){
				Thread.sleep(2000);
				checkAndUploadRemainingFiles(accessToken);
			}
		} catch (Exception|OutOfMemoryError e){
			e.printStackTrace();
		}
	}


	public void startSyncService(PassengerScreenMode mode, String accessToken){
		try{
			if (mode != PassengerScreenMode.P_REQUEST_FINAL
					&& mode != PassengerScreenMode.P_DRIVER_ARRIVED) {
				File[] files = getTrackingLogFolder().listFiles();
				if (files.length == 0) {
					return;
				}
				Intent intent = new Intent(context, TrackingLogSyncIntentService.class);
				intent.putExtra(Constants.KEY_ACCESS_TOKEN, accessToken);
				context.startService(intent);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
