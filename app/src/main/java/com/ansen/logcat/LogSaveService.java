package com.ansen.logcat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class LogSaveService extends Service {

	@Override
	public void onCreate() {
		Log.d(Comm.TAG, "LogSaveService  >>  onCreate()");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogEngine log = LogEngine.getInstance(this);
		log.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(Comm.TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
