package com.sabkuchfresh.bus;

/**
 * Created by Gurmail S. Kang on 5/6/16.
 */
public class AddressAdded {

    public boolean flag;
    public boolean dontRefresh = false;

    public AddressAdded(boolean flag) {
        this.flag = flag;
        this.dontRefresh = false;
    }

    public AddressAdded(boolean flag, boolean dontRefresh){
        this.flag = flag;
        this.dontRefresh = dontRefresh;
    }
}
