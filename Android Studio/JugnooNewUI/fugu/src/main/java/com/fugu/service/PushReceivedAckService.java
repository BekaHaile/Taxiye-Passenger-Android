package com.fugu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.CommonResponse;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;

/**
 * Created by gurmail on 22/06/18.
 *
 * @author gurmail
 */

public class PushReceivedAckService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FuguLog.d("ClearFromRecentService", "Service Started");
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void sendAck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommonParams commonParams = new CommonParams.Builder()
                            .add("", "")
                            .build();

                    RestClient.getApiInterface().sendAckToServer(commonParams.getMap())
                            .enqueue(new ResponseResolver<CommonResponse>() {
                                @Override
                                public void success(CommonResponse commonResponse) {

                                }

                                @Override
                                public void failure(APIError error) {

                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
