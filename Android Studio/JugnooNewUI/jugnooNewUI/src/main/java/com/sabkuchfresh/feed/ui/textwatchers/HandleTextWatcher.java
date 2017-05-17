package com.sabkuchfresh.feed.ui.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Parminder Singh on 4/15/17.
 */

public abstract class HandleTextWatcher implements TextWatcher {





    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {


        if(s.length()>0 && Character.isDigit(s.charAt(0)))
            s.delete(0,1);



        /*if(StringUtils.isNumericSpace(s.toString())){
            enableSubmitButton(false);
            onShowError("Atleast one character is required");
        }
*/
        enableSubmitButton(s.length()>=3);
        afterTextChange(s);


    }

    public   abstract void enableSubmitButton(boolean isEnable);

    public  abstract void afterTextChange(Editable s);
}
