package product.clicklabs.jugnoo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import java.io.File;

import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 05/06/17.
 */

public class DeleteCacheIntentService extends IntentService {

	private final String TAG = "DeleteCacheIntentService";

	public DeleteCacheIntentService(){
		super("DeleteCacheIntentService");
	}

	public DeleteCacheIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		try {
			long thresholdTime = 7L*Constants.DAY_MILLIS;
			long lastClearTime = Prefs.with(this).getLong(Constants.SP_CACHE_CLEAR_TIME,
					System.currentTimeMillis()-(8L*Constants.DAY_MILLIS));
			Log.v(TAG, "lastClearTimeDiff => "+((System.currentTimeMillis() - lastClearTime)/60000L));
			if(System.currentTimeMillis() - lastClearTime >= thresholdTime) {
				Log.v(TAG, "clearing started");
				deleteCache(this);
				Prefs.with(this).save(Constants.SP_CACHE_CLEAR_TIME, System.currentTimeMillis());
				Log.v(TAG, "clearing ended");
			} else {
				Log.v(TAG, "clearing not needed");
			}
		} catch (Exception|OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	private void deleteCache(Context context) {
		try {
			File dir = context.getCacheDir();
			boolean fileDeleted = deleteDir(dir);
			Log.i(TAG, "dir => "+dir+", fileDeleted => "+fileDeleted);
		} catch (Exception e) {}
	}

	private void getCacheSize(Context context) {
		try {
			File dir = context.getCacheDir();
			long size = dirSize(dir);
			Log.i(TAG, "getCacheSize => "+dir+", size => "+size);
		} catch (Exception e) {}
	}

	private boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			boolean fileDeleted = dir.delete();
			Log.i(TAG, "dir => "+dir+", fileDeleted => "+fileDeleted);
			return fileDeleted;
		} else if(dir!= null && dir.isFile()) {
			boolean fileDeleted = dir.delete();
			Log.i(TAG, "dir => "+dir+", fileDeleted => "+fileDeleted);
			return fileDeleted;
		} else {
			return false;
		}
	}

	private long dirSize(File dir) {

		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for(int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if(fileList[i].isDirectory()) {
					result += dirSize(fileList [i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}
		return 0;
	}

}
