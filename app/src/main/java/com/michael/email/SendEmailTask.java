package com.michael.email;

import android.content.Context;
import android.os.Bundle;

import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskBuilder;
import com.michael.email.db.DBManagerContact;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.mail.MailSenderInfo;
import com.michael.email.mail.SimpleMainSender;
import com.michael.email.model.Contact;
import com.michael.email.model.Email;
import com.michael.email.util.Consts;
import com.michael.email.util.EmailBus;
import com.michael.email.util.L;
import com.michael.email.util.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by michael on 16/6/6.
 */
public class SendEmailTask
{
    private String TAG = this.getClass().getName();

    private Context context;

    private String[] emailTo;
    private String subject;
    private String content;
    private String[] attachFileNames;

    /**
     * 立即发送邮件
     * */
    public SendEmailTask send(Context context, final String[] emailTo, final String subject, final String content, final String[] attachFileNames)
    {
        return send(context, emailTo, subject, content, attachFileNames, false, "-1");
    }

    /**
     * pending 的邮件调用这个方法发送
     *
     * 发送邮件，需要在线程里面操作
     */
    public SendEmailTask send(Context context, final String[] emailTo, final String subject, final String content, final String[] attachFileNames, final boolean isStar, final String emailId)
    {
        this.context = context;
        this.emailTo = emailTo;
        this.subject = subject;
        this.content = content;
        this.attachFileNames = attachFileNames;
        TaskBuilder.create(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return sendEmailBy163WithAttach(emailTo, subject, content, attachFileNames);
            }
        }, new SimpleTaskCallback<Boolean>()
        {

//            ProgressDialog pdLoading = new ProgressDialog(context);

            @Override
            public void onTaskStarted(String name, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskStarted");
                super.onTaskStarted(name, extras);
//                pdLoading.setMessage(getResources().getString(R.string.new_letter_activity_sending_dialog_tip));
//                pdLoading.setCancelable(false);
//                pdLoading.setCanceledOnTouchOutside(false);
//                pdLoading.show();
                if(onTaskStateListener != null)
                {
                    onTaskStateListener.onTaskStarted();
                }
            }

            @Override
            public void onTaskFailure(Throwable ex, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskFailure");
                super.onTaskFailure(ex, extras);
            }

            @Override
            public void onTaskSuccess(Boolean success, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskSuccess");
                super.onTaskSuccess(success, extras);
                if(success)
                {
//                    Toaster.show(getResources().getString(R.string.toast_email_send_success));
                    insertEmailToDBAndNotify(true, System.currentTimeMillis(), isStar, emailId);
                    insertContactToDBAndNotify();
//                    NewLetterActivity.this.finish();
                    if(onTaskStateListener != null)
                    {
                        onTaskStateListener.onTaskSuccess();
                    }
                }
                else
                {
//                    Toaster.show(getResources().getString(R.string.toast_email_send_fail), true);
                    if(onTaskStateListener != null)
                    {
                        onTaskStateListener.onTaskFail();
                    }
                }
            }

            @Override
            public void onTaskFinished(String name, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskFinished");
                super.onTaskFinished(name, extras);
//                pdLoading.dismiss();
                if(onTaskStateListener != null)
                {
                    onTaskStateListener.onTaskEnd();
                }
            }
        }, TAG).start();
        return this;
    }

    /**
     * 将邮件写入数据库
     * */
    private void insertEmailToDBAndNotify(boolean sendNow, long sendTime, boolean isStar, String emailId)
    {
        Email email = new Email();
        email.receiver = this.emailTo[0];
        email.subject = this.subject;
        email.content = this.content;
        email.attachPaths = new ArrayList<>(Arrays.asList(this.attachFileNames));
        email.isStar = isStar;
        email.sendTime = sendTime;// 发送时间
        email.state = sendNow ? 1 : 0;
        email.sender = getUserEmail();
        if(emailId.equals("-1"))//说明是新邮件
        {
            DBManagerEmail.getInstance().insertEmail(email);
        }
        else
        {
            DBManagerEmail.getInstance().updateEmail(emailId, email);
        }
        EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_EMAIL));
        EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_PENDING));
    }

    /**
     * 将联系人写入数据库
     * */
    private void insertContactToDBAndNotify()
    {
        Contact contact = new Contact();
        contact.emailAddress = this.emailTo[0];
        if(!DBManagerContact.getInstance().isContactExist(contact.emailAddress))
        {
            L.d(TAG, "insertContactToDBAndNotify（）->联系人不存在，执行插入数据的操作");
            DBManagerContact.getInstance().insertContact(contact);
            EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_CONTACT));
        }
        else
        {
            L.d(TAG, "insertContactToDBAndNotify（）->联系人存在，不执行插入数据的操作");
        }
    }

    /**
     * 通过163的账号来发送邮件，支持附件
     */
    private boolean sendEmailBy163WithAttach(String[] emailTo, String subject, String content, String[] attachFileNames)
    {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.163.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName(getUserEmail());//"michael_ye_36@163.com" TODO
        mailInfo.setPassword(getPassword());// "Donkey@1988" TODO
        mailInfo.setFromAddress(getUserEmail());//"michael_ye_36@163.com"
//        String[] to = {"34795251@qq.com"};
        mailInfo.setToAddress(emailTo);

//        String[] toCC = {"michaelye"};
//        mailInfo.setToCarbonCopyAddress(toCC);
//        String[] toBCC = {"*******@sina.com"};
//        mailInfo.setToBlindCarbonCopyAddress(toBCC);

//        if(new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator + Consts.AVATAR_NAME).exists())
//        {
//
//            String[] attachFileNames = {Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator + Consts.AVATAR_NAME};
//            L.e(TAG, "图片存在:"+attachFileNames[0]);
//            mailInfo.setAttachFileNames(attachFileNames);
//        }
//        else
//        {
//            L.e(TAG, "图片不存在");
//        }

        mailInfo.setSubject(subject);
        mailInfo.setContent(content);
        mailInfo.setAttachFileNames(attachFileNames);
        boolean success = SimpleMainSender.sendHtmlMail(mailInfo);
        return success;
    }

    /**
     * 用户设置的Email地址
     */
    private String getUserEmail()
    {
        return SharedPreferenceUtils.getString(context, Consts.USER_EMAIL, "");
    }

    /**
     * 用户设置的密码
     */
    private String getPassword()
    {
        return SharedPreferenceUtils.getString(context, Consts.PASSWORD, "");
    }

    private OnTaskStateListener onTaskStateListener;

    public void setOnTaskStateListener(OnTaskStateListener onTaskStateListener)
    {
        this.onTaskStateListener = onTaskStateListener;
    }

    public interface OnTaskStateListener
    {
        public void onTaskStarted();
        public void onTaskSuccess();
        public void onTaskFail();
        public void onTaskEnd();
    }

}
