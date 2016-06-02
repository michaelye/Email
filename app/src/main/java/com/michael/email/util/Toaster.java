package com.michael.email.util;

import android.widget.Toast;

import com.michael.email.EmailApp;

/**
 * @author michael
 */
public class Toaster
{

    public static void show(String tip, boolean isLongTime)
    {

        Toast.makeText(EmailApp.applicationContext, tip, isLongTime == true ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void show(String tip)
    {
        Toaster.show(tip, false);
    }
}
