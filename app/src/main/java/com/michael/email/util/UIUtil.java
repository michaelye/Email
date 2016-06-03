package com.michael.email.util;

import android.content.Context;
import android.content.Intent;

import com.michael.email.ui.activity.MainActivity;
import com.michael.email.ui.activity.NewLetterActivity;
import com.michael.email.ui.activity.UserInfoSettingActivity;

/**
 * Created by michael on 16/6/2.
 */
public class UIUtil
{
    /**
     * 程序主界面
     * */
    public static void startMainActivity(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     *
     * @param isModify 是否是要修改，如果是修改，则Actionbar上面有返回按钮
     * */
    public static void startUserInfoSettingActivity(Context context, boolean isModify)
    {
        Intent intent = new Intent(context, UserInfoSettingActivity.class);
        intent.putExtra("isModify", isModify);
        context.startActivity(intent);
    }

    /**
     * 新邮件
     * */
    public static void startNewLetterActivity(Context context)
    {
        Intent intent = new Intent(context, NewLetterActivity.class);
        context.startActivity(intent);
    }
}
