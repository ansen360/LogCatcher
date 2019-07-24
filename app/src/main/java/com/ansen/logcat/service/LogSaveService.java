package com.ansen.logcat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LogSaveService extends Service {

    private ScheduledExecutorService service;
    private LogEngine mLogEngine;

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable runnable = new Runnable() {
            public void run() {
                if(mLogEngine == null){
                    mLogEngine = new LogEngine(LogSaveService.this);
                    mLogEngine.start();
                }else{
                    mLogEngine.stop();
                    mLogEngine = new LogEngine(LogSaveService.this);
                    mLogEngine.start();
                }

            }
        };
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 12, TimeUnit.HOURS);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.shutdown();     // 不可以再submit新的task,已经submit的将继续执行
    }
}
