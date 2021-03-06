package com.picker.image.util;

import android.app.Activity;

import java.io.File;

/**
 * Encapsulate cwac-cam2 integration. This way the camera function can be optional, only if
 * present on the classpath.
 *
 *
 *
 */
public final class CameraSupport {

    private static Boolean enabled = null;

    public static boolean isEnabled() {
   /*     if (enabled == null) {
            try {
                Class.forName("com.commonsware.cwac.cam2.CameraActivity");
                enabled = true;
            } catch (ClassNotFoundException e) {
                enabled = false;
            }
        }*/
        return true;
    }

    public static void startPhotoCaptureActivity(Activity parent, File file, int requestCode) {
      /*  final Intent captureIntent = new com.commonsware.cwac.cam2.CameraActivity.IntentBuilder(parent)
                .skipConfirm()
                .debug()
                .to(file)
                .build();

        parent.startActivityForResult(captureIntent, requestCode);*/
    }

    public static void startVideoCaptureActivity(Activity parent, File file, int videoLengthLimit, int requestCode) {
      /*  final Intent captureIntent = new com.commonsware.cwac.cam2.VideoRecorderActivity.IntentBuilder(parent)
                .durationLimit(videoLengthLimit)
                .debug()
                .to(file)
                .build();

        parent.startActivityForResult(captureIntent, requestCode);*/
    }

}
