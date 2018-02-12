package com.sabkuchfresh.datastructure;

/**
 * Created by gurmail on 23/06/16.
 */
public class PopupData {
    public int popup_id;
    public String title_text="";
    public String desc_text="";
    public String image_url="";
    public String cancel_title="";
    private String ok_title;
    private Integer is_cancellable;
    public int deep_index ;
    public String ext_url="";


    public String getOk_title() {
        return ok_title==null?"OK":ok_title;
    }

    public int getIs_cancellable(){
        return is_cancellable==null?1:0;
    }
}
