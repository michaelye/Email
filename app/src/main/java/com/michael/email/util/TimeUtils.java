package com.michael.email.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 * <p/>
 * Created by michael on 15/5/23.
 */
public class TimeUtils
{

    /**
     * 将时间格式化显示
     * */
    public static String getFormatTime(long timeInMillSecond)
    {
        Date date=new Date(timeInMillSecond);
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm", java.util.Locale.getDefault());
        return sdf.format(date);
    }
}
