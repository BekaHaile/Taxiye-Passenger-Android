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

    public String getToastMessage() {
        return toastMessage;
    }

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
        return parseStatus(getIsMenus(),status);




    }

    public static IciciPaymentOrderStatus parseStatus(boolean isMenus,int status){
        if(isMenus){
            if(status==-8)
                return IciciPaymentOrderStatus.PENDING;
            else if(status ==-7)
                return IciciPaymentOrderStatus.FAILURE;
            else if (status==3)
                return IciciPaymentOrderStatus.CANCELLED;
            else if(status==1||status==0)
                return IciciPaymentOrderStatus.SUCCESSFUL;
            else
                return IciciPaymentOrderStatus.PROCESSED;

        }else{
            if(status==10)
                return IciciPaymentOrderStatus.PENDING;
            else if (status==3)
                return IciciPaymentOrderStatus.CANCELLED;
            else if(status <0)
                return IciciPaymentOrderStatus.FAILURE;
            else if (status>=0)
                return IciciPaymentOrderStatus.SUCCESSFUL;
            else
                return IciciPaymentOrderStatus.PENDING;
        }
    }

    public String getError() {
        return error;
    }

}
