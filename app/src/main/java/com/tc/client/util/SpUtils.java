package com.tc.client.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Set;


public final class SpUtils {

    private SharedPreferences sp;
    private static final String FILE_SP_NAME = "SpConfig";
    private static SpUtils mSPUtils;
    public SpUtils(Context context) {
        sp = context.getSharedPreferences(FILE_SP_NAME, Context.MODE_PRIVATE);
    }

    public static SpUtils getInstance(Context context) {
        if (mSPUtils == null) {
            synchronized (SpUtils.class) {
                if (mSPUtils == null) {
                    mSPUtils = new SpUtils(context);
                }
            }
        }
        return mSPUtils;
    }

    public void put(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void put(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void put(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return getLong(key, -1L);
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void put(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key) {
        return getFloat(key, -1f);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void put(String key, Set<String> values) {
        sp.edit().putStringSet(key, values).apply();
    }

    public Set<String> getStringSet(String key) {
        return getStringSet(key, Collections.<String>emptySet());
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    public boolean contains(String key) {
        return sp.contains(key);
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public void clear() {
        sp.edit().clear().apply();
    }
}