package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import product.clicklabs.jugnoo.R;

/**
 * Class for displaying custom info window for marker
 * @author shankar
 *
 */
public class CustomInfoWindow implements InfoWindowAdapter {
	View mymarkerview;
	String titleStr;
	String snippetStr;

	public CustomInfoWindow(Activity activity, String title, String snippet) {
		mymarkerview = activity.getLayoutInflater().inflate(
				R.layout.custom_info_window, null);
		this.titleStr = title;
		this.snippetStr = snippet;
	}

	@Override
	public View getInfoWindow(Marker marker) {

		render(marker, mymarkerview);
		return mymarkerview;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	void render(Marker marker, View view) {

		TextView title = (TextView) view.findViewById(R.id.textViewTitle);
		title.setText("" + titleStr);

		TextView snippet = (TextView) view.findViewById(R.id.snippet);
		snippet.setText("" + snippetStr);

	}
}


