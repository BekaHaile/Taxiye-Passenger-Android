package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by gurmail on 04/08/16.
 */
public class FileUtils {

    private Context context;

    public FileUtils(Context context){
        this.context = context;
    }

    public File getAppDataFolder(){
        try {
            String strFolder = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/data";
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

    public File getFile(String fileName){
        try {
            fileName = getAppDataFolder() + "/" + fileName + ".txt";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getAbsoluteFile(String relativePath, Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(context.getExternalFilesDir(null), relativePath);
        } else {
            return new File(context.getFilesDir(), relativePath);
        }
    }


    public void writeToFile(File file, String data) {
        try {
            if (file != null) {
                FileWriter writer = new FileWriter(file, false);
                writer.write(data);
                writer.flush();
                writer.close();
            }
        } catch (Exception e1) {
            Log.w("e1", "="+e1);
        }
    }


    public String readFromFile(File file) {
        String data = "";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
            data = stringBuilder.toString();
        }catch (Exception e) {
        } finally{
            try {
                in.close();
            } catch (Exception e) {
                Log.w("e1", "=" + e);
            }
        }
        return data;
    }



}
