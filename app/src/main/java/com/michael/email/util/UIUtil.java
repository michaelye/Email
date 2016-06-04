package com.michael.email.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.michael.email.ui.activity.MainActivity;
import com.michael.email.ui.activity.NewLetterActivity;
import com.michael.email.ui.activity.UserInfoSettingActivity;
import com.nononsenseapps.filepicker.FilePickerActivity;

/**
 * Created by michael on 16/6/2.
 */
public class UIUtil
{
    /**
     * 程序主界面
     */
    public static void startMainActivity(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param isModify 是否是要修改，如果是修改，则Actionbar上面有返回按钮
     */
    public static void startUserInfoSettingActivity(Context context, boolean isModify)
    {
        Intent intent = new Intent(context, UserInfoSettingActivity.class);
        intent.putExtra("isModify", isModify);
        context.startActivity(intent);
    }

    /**
     * 新邮件
     */
    public static void startNewLetterActivity(Context context)
    {
        Intent intent = new Intent(context, NewLetterActivity.class);
        context.startActivity(intent);
    }

    /**
     * 文件浏览器
     * */
    public static void startFilePickerActivity(Context context, int requestDirectoryCode)
    {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        ((Activity)context).startActivityForResult(i, requestDirectoryCode);
    }


}
