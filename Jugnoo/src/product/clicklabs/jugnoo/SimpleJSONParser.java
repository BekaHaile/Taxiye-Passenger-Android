package product.clicklabs.jugnoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Environment;

public class SimpleJSONParser {

	static String SERVER_TIMEOUT = "SERVER_TIMEOUT";
	
    // constructor
    public SimpleJSONParser() {
    	SERVER_TIMEOUT = "SERVER_TIMEOUT";
    }
    public String getJSONFromUrl(String url) {
    	InputStream inputStream = null;
    	String json = "";
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
        	if(inputStream != null){
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	            		inputStream, "iso-8859-1"), 8);
	            StringBuilder stringBuilder = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                stringBuilder.append(line + "\n");
	            }
	
	            json = stringBuilder.toString();
	            inputStream.close();
        	}
        	else{
        		throw new Exception();
        	}
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            json = SERVER_TIMEOUT;
        }
        return json;

    }
    
    
    
    public String getJSONFromUrlParams(String url, ArrayList<NameValuePair> nameValuePairs) {
    	
    	InputStream inputStream = null;
    	String json = "";
    	
    	HttpParams httpParameters;
    	DefaultHttpClient httpClient;
    	HttpPost httpPost;
    	HttpResponse httpResponse;
    	HttpEntity httpEntity;
    	
        // Making HTTP request
        try {
        	
        	httpParameters = new BasicHttpParams();
        	int timeoutConnection = 30000;
        	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        	int timeoutSocket = 30000;
        	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        	
            httpClient = new DefaultHttpClient(httpParameters);
            httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();           

            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	httpParameters = null;
        	httpClient = null;
        	httpPost = null;
        	httpResponse = null;
        	httpEntity = null;
        }
        
        
        BufferedReader reader;
        StringBuilder stringBuilder;
        String line;
        
        try {
        	if(inputStream != null){
	            reader = new BufferedReader(new InputStreamReader(
	            		inputStream, "iso-8859-1"), 8);
	            stringBuilder = new StringBuilder();
	            line = null;
	            while ((line = reader.readLine()) != null) {
	                stringBuilder.append(line + "\n");
	            }
	
	            json = stringBuilder.toString();
        	}
        	else{
        		throw new Exception();
        	}
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            json = SERVER_TIMEOUT;
        } finally{
        	reader = null;
        	stringBuilder = null;
        	line = null;
        	try {
        		if(inputStream != null){
        			inputStream.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        	inputStream = null;
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
