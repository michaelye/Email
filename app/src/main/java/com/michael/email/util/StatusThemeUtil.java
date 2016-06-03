package com.michael.email.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 当状态栏为白色的时候，设置主题为灰色
 * <p/>
 * http://blog.isming.me/2016/01/09/chang-android-statusbar-text-color/
 * <p/>
 * Created by michael on 16/6/4.
 */
public class StatusThemeUtil
{

    /**
     * 设置小米
     * */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode)
    {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try
        {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置魅族
     * */
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark)
    {
        boolean result = false;
        if (activity != null)
        {
            try
            {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark)
                {
                    value |= bit;
                } else
                {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e)
            {
            }
        }
        return result;
    }
}
