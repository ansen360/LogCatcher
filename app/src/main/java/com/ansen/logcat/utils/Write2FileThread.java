package com.ansen.logcat.utils;

import android.util.Log;

import com.ansen.logcat.comm.Comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Write2FileThread extends Thread {

	private InputStream is;
	private BufferedWriter bw;
	private File file;
	private boolean isStop;
	private OnLogListener mOnLogListener;

	public Write2FileThread(InputStream is, File file) {
		this.is = is;
		this.file = file;
	}

	public void setOnReadLineListener(OnLogListener logListener) {
		mOnLogListener = logListener;
	}

	@Override
	public void run() {
		super.run();
		try {
			bw = new BufferedWriter(new FileWriter(file) ,1024);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				bw.write(line + "\n");
				if (mOnLogListener != null)
					mOnLogListener.readLine(line);
				if(isStop){
					Log.d(Comm.TAG, "break this thread");
					bw.flush();
					bw.close();
					br.close();
					is.close();
					break;
				}
			}
		} catch (IOException e) {
			Log.e(Comm.TAG, "readLine error", e);
		}
	}

	public void stopSelf(boolean isStop){
		this.isStop = isStop;
	}
	public interface OnLogListener {
		 void readLine(String line);
	}
}
