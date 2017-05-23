package com.ansen.logcat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends PreferenceActivity implements
        OnPreferenceChangeListener, OnSharedPreferenceChangeListener {
    private final static String START = "setprop service.adb.tcp.port 5555\nstop adbd\nstart adbd\n";
    private final static String STOP = "setprop service.adb.tcp.port 5555\nstop adbd\n";
    private static final String TAG = null;
    private CheckBoxPreference autosave;
    //	private CheckBoxPreference netdebug;
    private ListPreference level;
    private ListPreference format;
    private ListPreference buffer;
    private PreferenceEngine mPreferenceEngine;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);
        mPreferenceEngine = new PreferenceEngine(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        init();

    }

    private void init() {
        autosave = (CheckBoxPreference) findPreference(PreferenceEngine.AUTOSAVE_KEY);
//		netdebug = (CheckBoxPreference) findPreference(Prefs.NETDEBUG_KEY);
        level = (ListPreference) findPreference(PreferenceEngine.LEVEL_KEY);
        format = (ListPreference) findPreference(PreferenceEngine.FORMAT_KEY);
        buffer = (ListPreference) findPreference(PreferenceEngine.BUFFER_KEY);
        autosave.setOnPreferenceChangeListener(this);
//		netdebug.setOnPreferenceChangeListener(this);
        level.setOnPreferenceChangeListener(this);
        format.setOnPreferenceChangeListener(this);
        buffer.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference pf, Object o) {
        /*if (netdebug == pf) {
			Boolean dev = (Boolean) o;
			if (dev) {
				Comm.shell(START);
			} else {
				Comm.shell(STOP);
			}
		} else */
        if (autosave == pf) {
            Boolean auto = (Boolean) o;
            Log.i(Comm.TAG, "auto=" + auto);
            if (mIntent == null)
                mIntent = new Intent(this, LogSaveService.class);
            if (auto) {
                startService(mIntent);
            } else {
                stopService(mIntent);
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNetdebug();
        setLevelTitle();
        setFormatTitle();
        setBufferTitle();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "key:  " + key + "  value: ");
        if (key.equals(PreferenceEngine.LEVEL_KEY)) {
            setLevelTitle();
        } else if (key.equals(PreferenceEngine.FORMAT_KEY)) {
            setFormatTitle();
        } else if (key.equals(PreferenceEngine.BUFFER_KEY)) {
            setBufferTitle();
        }
    }

    private void setNetdebug() {
        NetCfg nc = Comm.netcfg();
        if (nc != null) {
//			netdebug.setSummary(nc.getIp() + "," + nc.getMac());
        }
    }

    private void setLevelTitle() {
        level.setTitle(getRes(R.string.level)
                + mPreferenceEngine.getLevel().getTitle(this));
    }

    private void setFormatTitle() {
        format.setTitle(getRes(R.string.format)
                + mPreferenceEngine.getFormat().getTitle(this));
    }

    private void setBufferTitle() {
        buffer.setTitle(getRes(R.string.buffer)
                + mPreferenceEngine.getBuffer().getTitle(this));
    }

    private String getRes(int id) {
        return getResources().getString(id);
    }
}