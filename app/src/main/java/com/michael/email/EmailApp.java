package com.michael.email;

import android.app.Application;

/**
 * Created by michael on 16/6/1.
 */
public class EmailApp extends Application
{

    public static EmailApp applicationContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        applicationContext = this;
    }
}
