package product.clicklabs.jugnoo;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JSONParser {

	public JSONParser(){
		
	}
	
	
	public void parseLoginData(Context context, String response) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"));
		
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
		editor.commit();
		
	}
	
	
	public void parseAccessTokenLoginData(String response, String accessToken) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(accessToken, userData.getString("user_name"), 
				userData.getString("user_image"));
	}
	
}
