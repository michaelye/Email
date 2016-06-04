package com.michael.email.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 */
public class NetworkUtil
{

    public String TAG = this.getClass().getName();


    /**
     * 是否已经连接上网络了，无聊是WiFi还是mobile
     */
    public static boolean isConnected(Context context)
    {
        NetworkInfo info = getActiveNetWorkInfo(context);
        if (info != null && info.isConnected())
        {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE)
            {
                return true;
            }
        }
        return false;
    }

    private static NetworkInfo getActiveNetWorkInfo(Context context)
    {
        if (context == null)
        {
            return null;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return null;
        }
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isWifiConnected(Context context)
    {
        NetworkInfo info = getActiveNetWorkInfo(context);
        return (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected());
    }

    public static boolean isMobileConnected(Context context)
    {
        NetworkInfo info = getActiveNetWorkInfo(context);
        return (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE && info.isConnected());
    }
}
