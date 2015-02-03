package product.clicklabs.jugnoo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.os.Environment;

public class AuthKeySaver {
	
	public static File getAuthFolder(){
		try {
			String strFolder = Environment.getExternalStorageDirectory() + "/Android/data/.jugnoo_auth";
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
	
	public static File getAuthFile(){
		try {
			String fileName = getAuthFolder() + "/auth";
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
	
	
	public static void writeAuthToFile(final String authKey) {
		try {
			File gpxfile = getAuthFile();
			if (gpxfile != null) {
				FileWriter writer = new FileWriter(gpxfile, false);
				writer.write(authKey);
				writer.flush();
				writer.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public static String readAuthFromFile() {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getAuthFile()));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
        }catch (Exception e) {
        } finally{
        	try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return stringBuilder.toString();
    }
	
}
