package product.clicklabs.jugnoo;

import android.location.Location;

public interface LocationUpdate {
	public void locationChanged(Location location, int priority);
}
