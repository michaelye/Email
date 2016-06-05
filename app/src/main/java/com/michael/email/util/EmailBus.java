package com.michael.email.util;

import android.os.Bundle;

import de.greenrobot.event.EventBus;

/**
 * Created by michael on 16/5/4.
 */
public class EmailBus
{
    public static EventBus getInstance()
    {
        return EventBus.getDefault();
    }

    private EmailBus()
    {

    }

    public static class BusEvent
    {
        public int eventId;
        public Bundle data;

        public BusEvent(int eventId, Bundle data)
        {
            this.eventId = eventId;
            this.data = data;
        }

        public BusEvent(int eventId)
        {
            this.eventId = eventId;
            this.data = null;
        }
    }

    /**
     * 用户修改了个人信息，需要刷新主界面
     */
    public static final int BUS_ID_REFRESH_USER_INFO = 001;
    /**
     * 刷新邮件
     * */
    public static final int BUS_ID_REFRESH_EMAIL = 002;
    /**
     * 刷新联系人
     * */
    public static final int BUS_ID_REFRESH_CONTACT = 003;
}
