package sabkuchfresh.bus;

import com.squareup.otto.Bus;

/**
 * Created by gurmail on 20/05/16.
 */
public class DataBus {
    private static Bus instance = null;

    private DataBus() {
        instance = new Bus();
    }

    public static Bus getInstance() {
        if (instance == null) {
            instance = new Bus();
        }
        return instance;
    }
}
