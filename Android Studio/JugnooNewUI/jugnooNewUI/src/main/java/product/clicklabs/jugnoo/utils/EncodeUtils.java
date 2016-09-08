package product.clicklabs.jugnoo.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by shankar on 8/16/16.
 */
public class EncodeUtils {

	public static String jsonToUrlEncode(String json) {
		StringBuilder sb = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			Iterator<String> keys = jsonObject.keys();

			sb = new StringBuilder();
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				sb.append(key);
				sb.append("=");
				sb.append(jsonObject.get(key));
				sb.append("&");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static byte[] jsonToUrlEncodeBytes(String json, String encoding) {
		StringBuilder sb = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			Iterator<String> keys = jsonObject.keys();

			sb = new StringBuilder();
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				sb.append(URLEncoder.encode(key, encoding));
				sb.append("=");
				sb.append(URLEncoder.encode(jsonObject.getString(key), encoding));
				sb.append("&");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		android.util.Log.i("colib","form data is "+sb);

		return sb.toString().getBytes();
	}

}