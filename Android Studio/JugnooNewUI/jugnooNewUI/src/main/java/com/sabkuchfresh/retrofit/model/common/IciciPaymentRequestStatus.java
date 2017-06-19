package com.sabkuchfresh.retrofit.model.common;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;

/**
 * Created by Parminder Saini on 15/06/17.
 */

public class IciciPaymentRequestStatus {

    @SerializedName("flag")
    public int flag;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("error")
    private String error;
    @SerializedName("is_menus")
    private int isMenus;
    @SerializedName("toast_message")
    private String toastMessage;



    public boolean getIsMenus() {
        return isMenus==1;
    }

    public int getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public IciciPaymentOrderStatus getStatus() {
        if(getIsMenus()){
            if(status==-8)
                return IciciPaymentOrderStatus.PENDING;
            else if(status ==-7)
                return IciciPaymentOrderStatus.FAILURE;
            else if (status==3)
                return IciciPaymentOrderStatus.CANCELLED;
            else
                return IciciPaymentOrderStatus.PENDING;

        }else
            return IciciPaymentOrderStatus.CANCELLED;



    }

    public String getError() {
        return error;
    }

}
