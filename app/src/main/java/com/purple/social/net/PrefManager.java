package com.purple.social.net;

/**
 * Created by Recep on 30/09/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "status_app";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setBoolean(String PREF_NAME, Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }

    public void setString(String PREF_NAME, String VAL) {
        editor.putString(PREF_NAME, VAL);
        editor.commit();
    }

    public void setInt(String PREF_NAME, int VAL) {
        editor.putInt(PREF_NAME, VAL);
        editor.commit();
    }

    public boolean getBoolean(String PREF_NAME, boolean defVal) {
        return pref.getBoolean(PREF_NAME, defVal);
    }

    public void remove(String PREF_NAME) {
        if (pref.contains(PREF_NAME)) {
            editor.remove(PREF_NAME);
            editor.commit();
        }
    }

    public String getString(String PREF_NAME, String defVal) {
        if (pref.contains(PREF_NAME)) {
            return pref.getString(PREF_NAME, defVal);
        }
        return null;
    }

    public int getInt(String key, int defVal) {
        return pref.getInt(key, defVal);
    }
}
