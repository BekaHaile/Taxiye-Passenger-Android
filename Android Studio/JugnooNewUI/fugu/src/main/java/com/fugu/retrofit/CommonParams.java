package com.fugu.retrofit;

import com.fugu.BuildConfig;

import java.util.HashMap;

import static com.fugu.constant.FuguAppConstant.ANDROID_USER;
import static com.fugu.constant.FuguAppConstant.APP_SOURCE_TYPE;
import static com.fugu.constant.FuguAppConstant.APP_VERSION;
import static com.fugu.constant.FuguAppConstant.DEVICE_TYPE;

/**
 * Created by cl-macmini-33 on 27/09/16.
 */

public class CommonParams {
    HashMap<String, Object> map = new HashMap<>();

    private CommonParams(Builder builder) {
        builder.map.put(APP_SOURCE_TYPE, String.valueOf(1));
        builder.map.put(APP_VERSION, BuildConfig.VERSION_CODE);
        builder.map.put(DEVICE_TYPE, ANDROID_USER);
        this.map = builder.map;

    }

    public HashMap<String, Object> getMap() {
        return map;
    }


    public static class Builder {
        HashMap<String, Object> map = new HashMap<>();

        public Builder() {
        }

        public Builder add(String key, Object value) {
            map.put(key, String.valueOf(value));
            return this;
        }

        /**
         * Temp fix for future use of common key inside CommonParams constr.
         * @param map
         * @return
         */
        public Builder putMap(HashMap<String, Object> map) {
            this.map = map;
            return this;
        }

        public CommonParams build() {
            return new CommonParams(this);
        }
    }
}


