package com.ansen.logcat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class LogEngine {
	private static LogEngine mLogServer;
	private File path;
	private static final String TAG = "ansen";

	private PreferenceEngine mPrefs;
	BufferedWriter bw;
	Process proc;


	private LogEngine(Context context) {
		mPrefs = new PreferenceEngine(context);
		path = new File(Environment.getExternalStorageDirectory(), "AnsenLog");
		if (path != null && !path.exists()) {
			path.mkdir();
		}
	}

	public static LogEngine getInstance(Context context) {
		if (mLogServer == null) {
			mLogServer = new LogEngine(context);

		}
		return mLogServer;
	}

	public void start() {
		try {
			String[] cmd = { "logcat", "-v", mPrefs.getFormat().getValue(),"-b", mPrefs.getBuffer().getValue(),"*:" + mPrefs.getLevel() + "\n" };
			Log.d(TAG,"format: "+mPrefs.getFormat().getValue()+" buffer: "+mPrefs.getBuffer().getValue()+" level: "+mPrefs.getLevel().getValue());
			File file = new File(path + "/"+mPrefs.getFormat().getValue()+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ").format(new Date()) + ".txt");
			file.createNewFile();
			bw = new BufferedWriter(new FileWriter(file), 1024);
			proc = Runtime.getRuntime().exec(cmd);
			LogStream print = new LogStream(proc.getInputStream(), "OUT");
			print.setOnLogListener(new LogStream.OnLogListener() {
				@Override
				public void readLine(String line) {
					try {
						bw.write(line + "\n");
					} catch (IOException e) {
						Log.e(Comm.TAG, "error write log", e);
					}
				}
			});
			print.start();
		} catch (IOException e) {
			Log.e(Comm.TAG, "error create log file", e);
		}
	}

}
