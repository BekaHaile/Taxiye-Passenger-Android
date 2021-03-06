package product.clicklabs.jugnoo.driver.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;


public class Utils {
	
	/**
	 * Compares two double values with epsilon precision
	 * @param d1 double value 1
	 * @param d2 double value 2
	 * @return 1 if d1 > d2, 
	 * -1 if d1 < d2 & 
	 * 0 if d1 == d2
	 */
	public static int compareDouble(double d1, double d2){
		if(d1 == d2){
			return 0;
		}
		else{
			double epsilon = 0.0000001;
			if((d1 - d2) > epsilon){
				return 1;
			}
			else if((d1 - d2) < epsilon){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
	
	/**
	 * Expands ListView for fixed height of item inside a ScrollView
	 */
	public static void expandListForFixedHeight(ListView list) {
		try {
			if (list.getCount() > 0) {
				ListAdapter listAdap = list.getAdapter();
				int totalHeight = 0;
				
				View listItem = listAdap.getView(0, null, list);
				listItem.measure(0, 0);
				int singleHeight = listItem.getMeasuredHeight();
				totalHeight = singleHeight * list.getCount();
				
//				for (int i = 0; i < listAdap.getCount(); i++) {
//					View listItem = listAdap.getView(i, null, list);
//					listItem.measure(0, 0);
//					totalHeight += listItem.getMeasuredHeight();
//				}
				ViewGroup.LayoutParams params = list.getLayoutParams();
				params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
				list.setLayoutParams(params);
				list.requestLayout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Expands ListView for variable height of item inside a ScrollView
	 */
	public static void expandListForVariableHeight(ListView list) {
		try {
			if (list.getCount() > 0) {
				ListAdapter listAdap = list.getAdapter();
				int totalHeight = 0;
				
				for (int i = 0; i < listAdap.getCount(); i++) {
					View listItem = listAdap.getView(i, null, list);
					listItem.measure(0, 0);
					totalHeight += listItem.getMeasuredHeight();
				}
				ViewGroup.LayoutParams params = list.getLayoutParams();
				params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
				list.setLayoutParams(params);
				list.requestLayout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Hides keyboard
	 * 
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity, View searchET) {
		try {
			InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public static ArrayList<NameValuePair> convertQueryToNameValuePairArr(String query) throws UnsupportedEncodingException {
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        nameValuePairs.add(new BasicNameValuePair(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8")));
	    }
	    return nameValuePairs;
	}
	
	
	
	public static String[] splitStringInParts(String s, int partLength) {
	    int len = s.length();

	    // Number of parts
	    int nparts = (len + partLength - 1) / partLength;
	    String parts[] = new String[nparts];

	    // Break into parts
	    int offset= 0;
	    int i = 0;
	    while (i < nparts) {
	        parts[i] = s.substring(offset, Math.min(offset + partLength, len));
	        offset += partLength;
	        i++;
	    }

	    return parts;
	}
	
}
