package com.michael.email.receiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class AlarmClockManager
{

	/**
	 * 设置闹钟，启动任务
	 * 
	 * http://www.miui.com/thread-1241523-1-1.html
	 * http://lmbj.net/blog/xiaomi-alarmmanager-failure-problem/
	 * */
	public static void setClock(Context context, long id, long triggerAtTime)
	{
		Intent intent=new Intent(context, AlarmReceiver.class);
		intent.putExtra("id", id);
		PendingIntent pi = PendingIntent.getBroadcast(context, (int)id , intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);//设置闹铃，在小米上面有问题
		am.set(AlarmManager.RTC, triggerAtTime, pi);//设置闹铃，但是不具有唤醒功能 TODO
	}
	
	/**
	 * 取消闹钟
	 * */
	public static boolean cancelClock(Context context, int id)
	{
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		am.cancel(pi);//取消闹钟 
		return true;
	}
	
}
