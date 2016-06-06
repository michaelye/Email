package com.michael.email.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.michael.email.R;
import com.michael.email.SendEmailTask;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.model.Email;
import com.michael.email.ui.activity.MainActivity;
import com.michael.email.util.L;
import com.michael.email.util.NetworkUtil;


/**
 * 接收到BroadcastReceiver后启动闹钟界面
 */
public class AlarmReceiver extends BroadcastReceiver
{

    private String TAG = this.getClass().getName();

    @Override
    public void onReceive(final Context context, Intent intent)
    {
//		ToastUtil.show("onReceive");
        //判断下是短信还是电话
        long id = intent.getLongExtra("id", -1);
        L.e(TAG, "onReceive()->id:" + id);

        if (NetworkUtil.isConnected(context))
        {
            Email email = DBManagerEmail.getInstance().getEmail(id + "");
            if (email != null)
            {
                L.e(TAG, "onReceive()->get email:" + email);
                new SendEmailTask().send(context, new String[]{email.receiver}, email.subject, email.content, email.attachPaths.toArray(new String[0]), email.isStar, email.id).setOnTaskStateListener(new SendEmailTask.OnTaskStateListener()
                {
                    @Override
                    public void onTaskStarted()
                    {
                        L.e(TAG, "onTaskStarted()");
                    }

                    @Override
                    public void onTaskSuccess()
                    {
                        L.e(TAG, "onTaskSuccess()");
                        showNotification(context, context.getResources().getString(R.string.notification_tip_success));
                    }

                    @Override
                    public void onTaskFail()
                    {
                        L.e(TAG, "onTaskFail()");
                        showNotification(context, context.getResources().getString(R.string.notification_tip_fail));
                    }

                    @Override
                    public void onTaskEnd()
                    {
                        L.e(TAG, "onTaskEnd()");
                    }
                });
            } else
            {
                L.e(TAG, "onReceive()->邮件为空");
                showNotification(context, context.getResources().getString(R.string.notification_tip_fail_email_empty));
            }
        } else
        {
            L.e(TAG, "onReceive()->网络不可用");
            showNotification(context, context.getResources().getString(R.string.notification_tip_fail_not_net));
        }
    }

    /**
     * 显示通知栏
     * */
    private void showNotification(Context context, String tip)
    {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT < 16)
        {
            Notification n = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(tip)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).getNotification();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        } else
        {
            Notification n = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(tip)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
    }
}