package com.ansen.logcat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.ansen.logcat.utils.PreferenceEngine;


public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "LogCatcher";
    private PreferenceEngine mPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
//		mPrefs = new PreferenceEngine(context);
//		if (mPrefs.isNetdebug()) {
//			Comm.shell("setprop service.adb.tcp.port 5555\nstop adbd\nstart adbd\n");
//		}
        String type = Build.TYPE;
        Log.d(TAG, "BootReceiver --> Build.TYPE: " + type);
        if (!"user".equals(type)) {    //  非user版本才启动离线log服务
            Intent svcIntent = new Intent(context, LogSaveService.class);
            context.startService(svcIntent);
        }
    }
}
