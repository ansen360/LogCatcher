package com.ansen.logcat.service;

import android.content.Context;
import android.util.Log;

import com.ansen.logcat.comm.Comm;
import com.ansen.logcat.utils.PreferenceEngine;
import com.ansen.logcat.utils.Write2FileThread;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogEngine {
    private static final String TAG = "LogCatcher";

    private PreferenceEngine mPrefs;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static final File sRootPath;
    private Write2FileThread mWrite2FileThread;

    static {
        sRootPath = new File("/sdcard", "logcat");
        if (sRootPath != null && !sRootPath.exists()) {
            sRootPath.mkdir();
        }
    }

    public LogEngine(Context context) {
        mPrefs = new PreferenceEngine(context);
    }

    public void start() {
        try {
            deleteFileIfNeed();

            Log.d(TAG, "LogEngine is start: format: " + mPrefs.getFormat().getValue() + "  buffer: " + mPrefs.getBuffer().getValue() + "  level: " + mPrefs.getLevel().getValue());
//			String[] cmd = { "logcat", "-v", mPrefs.getFormat().getValue(),"-b", mPrefs.getBuffer().getValue(),"*:" + mPrefs.getLevel() + "\n" };
            String[] cmd = {"logcat"};

            File file = new File(sRootPath + "/" + dataFormat.format(new Date()) + ".txt");
            file.createNewFile();

            Process proc = Runtime.getRuntime().exec(cmd);
            mWrite2FileThread = new Write2FileThread(proc.getInputStream(), file);
            mWrite2FileThread.stopSelf(false);
            mWrite2FileThread.start();
        } catch (IOException e) {
            Log.e(Comm.TAG, "error create log file", e);
        }
    }

    private void deleteFileIfNeed() {
        File[] files = sRootPath.listFiles();
        for (File f : files) {
            try {
                Calendar calendar = Calendar.getInstance();
                String name = f.getName();
                String fileName = f.getName().subSequence(0, f.getName().indexOf(".")).toString();
                calendar.setTime(dataFormat.parse(fileName));
                long timeInMillis1 = calendar.getTimeInMillis();    // 写文件的时间

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -10);
                long timeInMillis2 = cal.getTimeInMillis();        // 十天前的时间

                if ((timeInMillis2 - timeInMillis1) > 0) {      //  文件超过十天,删除文件
                    Log.d(TAG,"delete file: "+f.getName());
                    f.delete();

                }
                Log.d(TAG,"timeInMillis1: "+timeInMillis1+ "  timeInMillis2: "+timeInMillis2+"   "+((timeInMillis2 - timeInMillis1)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        mWrite2FileThread.stopSelf(true);
        Log.d(TAG, "LogEngine is stop");
    }

}
