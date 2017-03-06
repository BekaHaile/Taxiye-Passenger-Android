package com.picker.image.util;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;


public class LoadingAlbumsRequest extends SpiceRequest<ArrayList> {
    private final Context mContext;
    private final Picker mPickerOptions;

    public LoadingAlbumsRequest(final Context context, final Picker pickerOptions) {
        super(ArrayList.class);
        mContext = context;
        mPickerOptions = pickerOptions;
    }

    @Override
    public ArrayList loadDataFromNetwork() throws Exception {
        return Util.getAlbums(mContext, mPickerOptions);
    }


}
