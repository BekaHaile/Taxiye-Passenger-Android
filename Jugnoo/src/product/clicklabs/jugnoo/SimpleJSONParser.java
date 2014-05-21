package product.clicklabs.jugnoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.Environment;

public class SimpleJSONParser {

    static InputStream inputStream = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public SimpleJSONParser() {
    }
    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();           

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
            		inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            json = stringBuilder.toString();
            inputStream.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }
    
    
    public void writeJSONToFile(String response, String filePrefix){
    	try {
    		 String fileName =
    		 Environment.getExternalStorageDirectory() +
    		 "/"+filePrefix+".txt";
    		
    		 File ff = new File(fileName);
    		 ff.delete();
    		
    		 File gpxfile = new File(fileName);
    		 FileWriter writer = new FileWriter(gpxfile);
    		 writer.append(""+response);
    		 writer.flush();
    		 writer.close();
    		 } catch (Exception e1) {
    		 Log.e("file exception", "=-="+e1.toString());
    		 e1.printStackTrace();
    		 }
    }
    
}
