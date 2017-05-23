package com.sabkuchfresh.bus;

/**
 * Created by Gurmail S. Kang on 5/5/16.
 */
public class UpdateMainList {

    public boolean flag;
    public boolean isVegToggle;

    public UpdateMainList(boolean flag) {
        this.flag = flag;
        isVegToggle = false;
    }
    public UpdateMainList(boolean flag, boolean isVegToggle) {
        this.flag = flag;
        this.isVegToggle = isVegToggle;
    }

}
