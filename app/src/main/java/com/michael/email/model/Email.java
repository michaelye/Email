package com.michael.email.model;

import java.util.List;

/**
 * Created by michael on 16/6/5.
 */
public class Email
{
    public String id;
    public String receiver;//接收者
    public String sender;//发送者
    public String subject;//主题
    public String content;//发送的内容
    public List<String> attachPaths;//附件地址
    public boolean isStar;//是否加星
    public int state;//状态 0未发送 1已发送
    public Long sendTime;//发送时间

    @Override
    public String toString()
    {
        return "id:"+id
                +"receiver:"+receiver
                +"sender:"+sender
                +"subject:"+subject
                +"content:"+content
                +"attachPaths:"+attachPaths
                +"isStar:"+isStar
                +"state:"+state
                +"sendTime:"+sendTime
                ;
    }
}
