package com.sabkuchfresh.retrofit.model;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class SortResponseModel {

    public int id;
    public String name;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean check;

    public SortResponseModel(int id, String name, boolean check) {
        this.id = id;
        this.name = name;
        this.check = check;
    }


}
