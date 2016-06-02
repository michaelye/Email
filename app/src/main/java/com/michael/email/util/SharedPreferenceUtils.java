package com.michael.email.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author michael
 */
public class SharedPreferenceUtils
{

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void putBoolean(Context context, final String key, final boolean value) {
        getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, final String key, boolean defaultValue) {
        return getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void putInt(Context context, final String key, final int i) {
        getDefaultSharedPreferences(context).edit().putInt(key, i).apply();
    }

    public static int getInt(Context context, final String key, final int defaultValue) {
        return getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    public static void putString(Context context, final String key, final String value) {
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, final String key, final String defaultValue) {
        return getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void putLong(Context context, final String key, final long i) {
        getDefaultSharedPreferences(context).edit().putLong(key, i).apply();
    }

    public static long getLong(Context context, final String key, final long defaultValue) {
        return getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    public static boolean contains(Context context, final String key) {
        return getDefaultSharedPreferences(context).contains(key);
    }
}
