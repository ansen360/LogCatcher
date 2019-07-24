package com.ansen.logcat.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.ansen.logcat.comm.Comm;
import com.ansen.logcat.utils.Write2FileThread;
import com.ansen.logcat.utils.PreferenceEngine;

public class LogEngine {
	private static final String TAG = "ansen";

	private PreferenceEngine mPrefs;
	private SimpleDateFormat dataFormat= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss_");
	private static final File path;
	private Write2FileThread mWrite2FileThread;

	static {
		path = new File("/sdcard", "logcat");
		if (path != null && !path.exists()) {
			path.mkdir();
		}
	}

	public LogEngine(Context context) {
		mPrefs = new PreferenceEngine(context);
	}

	public void start() {
		try {
			Log.d(TAG,"LogEngine is start: format: "+mPrefs.getFormat().getValue()+"  buffer: "+mPrefs.getBuffer().getValue()+"  level: "+mPrefs.getLevel().getValue());

			String[] cmd = { "logcat", "-v", mPrefs.getFormat().getValue(),"-b", mPrefs.getBuffer().getValue(),"*:" + mPrefs.getLevel() + "\n" };

			File file = new File(path + "/"+ dataFormat.format(new Date()) +mPrefs.getFormat().getValue() + ".txt");
			file.createNewFile();

			Process proc = Runtime.getRuntime().exec(cmd);
			mWrite2FileThread = new Write2FileThread(proc.getInputStream(), file);
			mWrite2FileThread.stopSelf(false);
//			thread.setOnReadLineListener(new Write2FileThread.OnLogListener() {
//				@Override
//				public void readLine(String line) {
//						Log.d(Comm.TAG, "line: " + line);
//				}
//			});
			mWrite2FileThread.start();
		} catch (IOException e) {
			Log.e(Comm.TAG, "error create log file", e);
		}
	}
	public void stop() {
		mWrite2FileThread.stopSelf(true);
		Log.d(TAG,"LogEngine is stop");
	}

}
