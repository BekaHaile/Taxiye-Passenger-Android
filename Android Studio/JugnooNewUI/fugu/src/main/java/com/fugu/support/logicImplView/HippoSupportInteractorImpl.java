package com.fugu.support.logicImplView;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.fugu.apis.ApiPutUserDetails;
import com.fugu.database.CommonData;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.support.callback.HippoSupportInteractor;
import com.fugu.support.db.HippoDatabase;
import com.fugu.support.model.SupportDataList;
import com.fugu.support.model.SupportResponse;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Map;

import static com.fugu.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.fugu.constant.FuguAppConstant.APP_VERSION;
import static com.fugu.constant.FuguAppConstant.DEVICE_TYPE;
import static com.fugu.constant.FuguAppConstant.EN_USER_ID;
import static com.fugu.constant.FuguAppConstant.SUPPORT_IS_ACTIVE;

/**
 * Created by Gurmail S. Kang on 29/03/18.
 * @author gurmail
 */

public class HippoSupportInteractorImpl implements HippoSupportInteractor {

    private OnFinishedListener onFinishedListener;
    private HippoDatabase database;
    private SupportDataList dataList;
    private int hitCount = 0;


    @Override
    public void getSupportData(Activity activity, int serverDBVersion, String defaultCategory, OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
        database = HippoDatabase.getInstance(activity);
        getData(activity, serverDBVersion, defaultCategory);
    }

    private void getData(Activity activity, int version, String getCategoryData) {
        hasUserData(activity, version, getCategoryData, hitCount);
    }

    private void fetchLocalSupportData(Activity activity, int version, String mFAQSearchString) {
        SupportDataList dataList;
        if(!TextUtils.isEmpty(mFAQSearchString))
            dataList = database.getSupportDataItems(mFAQSearchString.toLowerCase().trim());
        else
            dataList = database.getSupportDataItems(0);

        if(dataList == null || dataList.getList() == null) {
            hasUserData(activity, version, mFAQSearchString, hitCount, true);
        } else {
            onFinishedListener.onSuccess(dataList);
        }
    }

    private void hasUserData(final Activity activity, final int version1, final String categoryId, final int hitCount) {
        hasUserData(activity, version1, categoryId, hitCount, false);
    }
    private void hasUserData(final Activity activity, final int version1, final String categoryId, final int hitCount, final boolean serverForceHit) {
        //final int localVersion = CommonData.getLocalVersion();
        if ((FuguConfig.getInstance().getUserData() == null) || (TextUtils.isEmpty(FuguConfig.getInstance().getUserData().getEnUserId())
                || (TextUtils.isEmpty(FuguConfig.getInstance().getAppKey())))) {
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    afterHasUserData(activity, version1, categoryId, hitCount, serverForceHit);
                }

                @Override
                public void onFailure() {
                    onFinishedListener.onFailure();
                }
            }).sendUserDetails(FuguConfig.getmResellerToken(), FuguConfig.getmReferenceId(), false);
        } else {
            afterHasUserData(activity, version1, categoryId, hitCount, serverForceHit);
        }
    }

    private void afterHasUserData(Activity activity, int version1, String categoryId, int hitCount, boolean serverForceHit) {
        if(!CommonData.getUserDetails().getData().isFAQEnabled()) {
            onFinishedListener.onSuccess(null);
            return;
        }
        int localVersion = CommonData.getLocalVersion();
        int version = CommonData.getUserDetails().getData().getInAppSupportVersion();
        if(serverForceHit) {
            fetchSupportData(activity, version, categoryId, hitCount);
        } else {
            if (version > localVersion)
                fetchSupportData(activity, version, categoryId, hitCount);
            else
                fetchLocalSupportData(activity, version, categoryId);
        }

        FuguLog.v("TAG", localVersion+" < localVersion versionCode >"+version);
    }


    private void fetchSupportData(Activity activity, final int version, final String categoryId, final int hitCount) {

        String enUserId = null;
        try {
            if(CommonData.getUserDetails() != null && CommonData.getUserDetails().getData() != null &&
                    CommonData.getUserDetails().getData().getEn_user_id() != null) {
                enUserId = CommonData.getUserDetails().getData().getEn_user_id();
            } else {
                enUserId = FuguConfig.getInstance().getUserData().getEnUserId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(enUserId) && hitCount < 2) {
            hasUserData(activity, version, categoryId, (hitCount+1));
            return;
        }

        CommonParams commonParams = new CommonParams.Builder()
                .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                .add(EN_USER_ID, enUserId)
                .add(APP_VERSION, BuildConfig.VERSION_NAME)
                .add(DEVICE_TYPE, 1)
                .add(SUPPORT_IS_ACTIVE, 1)
                .build();

        RestClient.getApiInterface().fetchSupportData(commonParams.getMap())
                .enqueue(new ResponseResolver<SupportResponse>(activity, false, true) {
                    @Override
                    public void success(SupportResponse response) {
                        try {

                            String defaultKey = categoryId == null ? response.getDefaultFaqName() : categoryId;
                            CommonData.setDefaultCategory(response.getDefaultFaqName());
                            if(!TextUtils.isEmpty(defaultKey)) {
                                Iterator it = response.getItemData().entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    SupportDataList dataList1 = (SupportDataList) pair.getValue();
                                    if(dataList1.getCategoryName().equalsIgnoreCase(defaultKey)) {
                                        dataList = dataList1;
                                        break;
                                    }
                                }
                            }
                            if(response.getItemData().isEmpty()) {
                                onFinishedListener.onSuccess(null);
                                return;
                            }

                            if(dataList == null)
                                dataList = response.getItemData().get(response.getItemData().keySet().iterator().next());// = response.getItemData().get(response.getItemData().keySet().iterator().next());
                            onFinishedListener.onSuccess(dataList);
                            new BackgroundInsert().execute(new Gson().toJson(response), String.valueOf(version));
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFinishedListener.onFailure();
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        onFinishedListener.onFailure();
                    }
                });
    }

    private class BackgroundInsert extends AsyncTask<String, String, Boolean> {
        int version = -1;
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SupportResponse response = new Gson().fromJson(strings[0], SupportResponse.class);
                Map<String, SupportDataList> itemData = response.getItemData();
                database.insertUpdateSupportData(itemData);

                try {
                    version = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            if(flag) {
                // current server version updated on local db
                CommonData.setCurrentVersion(version);
            }
            if(database != null)
                database.close();
        }
    }
}
