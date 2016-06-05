package com.michael.email.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskBuilder;
import com.michael.email.R;
import com.michael.email.db.DBManagerContact;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.dialog.AlertDialogFragment;
import com.michael.email.dialog.DialogResultListener;
import com.michael.email.mail.MailSenderInfo;
import com.michael.email.mail.SimpleMainSender;
import com.michael.email.model.Contact;
import com.michael.email.model.Email;
import com.michael.email.ui.component.AttachItem;
import com.michael.email.util.Consts;
import com.michael.email.util.EmailFormatter;
import com.michael.email.util.L;
import com.michael.email.util.NetworkUtil;
import com.michael.email.util.SharedPreferenceUtils;
import com.michael.email.util.StatusThemeUtil;
import com.michael.email.util.Toaster;
import com.michael.email.util.UIUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 写邮件界面
 * <p/>
 * Created by michael on 16/6/3.
 */
public class NewLetterActivity extends AppCompatActivity implements DialogResultListener
{

    private String TAG = this.getClass().getName();

    private EditText etEmailTo;
    private EditText etSubject;
    private EditText etContent;

    private RelativeLayout rlAttach;
    private LinearLayout llAttach;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StatusThemeUtil.setMeizuStatusBarDarkIcon(this, true);
        StatusThemeUtil.setMiuiStatusBarDarkMode(this, true);
        setContentView(R.layout.activity_new_letter);
        iniComponent();
    }

    private void iniComponent()
    {
        attachPaths = new ArrayList<>();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_gray);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rlAttach = (RelativeLayout) findViewById(R.id.rlAttach);
        llAttach = (LinearLayout) findViewById(R.id.llAttach);
        etEmailTo = (EditText) findViewById(R.id.etEmailTo);
        etSubject = (EditText) findViewById(R.id.etSubject);
        etContent = (EditText) findViewById(R.id.etContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_letter_actionbar, menu);
        return true;
    }

    private static final int REQUEST_DIRECTORY = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_attach:
                UIUtil.startFilePickerActivity(NewLetterActivity.this, REQUEST_DIRECTORY);
                break;
            case R.id.action_send:
                if (checkEmail())
                {
                    if(NetworkUtil.isConnected(NewLetterActivity.this))
                    {
                        sendEmailTask(new String[]{etEmailTo.getText().toString()}, etSubject.getText().toString(), etContent.getText().toString(), attachPaths.toArray(new String[0]));
                    }
                    else
                    {
                        Toaster.show(getResources().getString(R.string.toast_email_network_un_available));
                    }
                }
                break;
            case android.R.id.home:
                if(needConfirmDialog())
                {
                    showConfirmDialog();
                }
                else
                {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 是否需要一个对话框提醒用户
     * */
    private boolean needConfirmDialog()
    {
        String emailTo = etEmailTo.getText().toString();
        String subject = etSubject.getText().toString();
        String content = etContent.getText().toString();
        if((emailTo != null && !emailTo.isEmpty())
                || (subject != null && !subject.isEmpty())
                || (content != null && !content.isEmpty())
                || attachPaths.size() > 0)
        {
            return true;
        }
        return false;
    }

    /**
     * 放弃编写邮件的提醒对话框
     * */
    private int REQUEST_GIVE_UP_EMAIL = 0;

    private void showConfirmDialog()
    {
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(this)
                .setRequestCode(REQUEST_GIVE_UP_EMAIL)
                .setMessage(R.string.dialog_cancel_email_tip)
                .setHasCancelOk(true)
                .setShowTitle(false)
                .setCancel(R.string.cancel)
                .setOk(R.string.ok)
                .setCancelable(false)
                .setListener(this);
        builder.create().show(this);
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        if (requestCode == REQUEST_GIVE_UP_EMAIL)
        {
            if (resultCode == RESULT_OK)
            {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY && resultCode == Activity.RESULT_OK)
        {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false))
            {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    ClipData clip = data.getClipData();

                    if (clip != null)
                    {
                        for (int i = 0; i < clip.getItemCount(); i++)
                        {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
//                            Toaster.show("more111:"+uri);
                            addAttachPathAndRefresh(uri);
                        }
                    }
                    // For Ice Cream Sandwich
                } else
                {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerActivity.EXTRA_PATHS);
                    if (paths != null)
                    {
                        for (String path : paths)
                        {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
//                            Toaster.show("more222:"+uri);
                            addAttachPathAndRefresh(uri);
                        }
                    }
                }

            } else
            {
                Uri uri = data.getData();
                // Do something with the URI
//                Toaster.show("one:" + uri);
                addAttachPathAndRefresh(uri);
            }
        }
    }

    private List<String> attachPaths;

    /**
     * 添加附件地址
     */
    private void addAttachPathAndRefresh(Uri uri)
    {
        if (uri != null)
        {
            L.e(TAG, "addAttachPathAndRefresh()->"+uri.getPath());
            attachPaths.add(uri.getPath());
            refreshAttachList();
        }
    }

    /**
     * 移除附件地址
     * */
    private void removeAttachPathAndRefresh(int position)
    {
        if(attachPaths != null && !attachPaths.isEmpty() && attachPaths.get(position) != null)
        {
            L.e(TAG, "removeAttachPathAndRefresh()->pos:"+position);
            attachPaths.remove(position);
        }
        refreshAttachList();
    }

    /**
     * 刷新附件UI
     */
    private void refreshAttachList()
    {
        L.e(TAG, "refreshAttachList()->attachPaths:"+attachPaths.size());
        llAttach.removeAllViews();
        if (attachPaths == null || attachPaths.isEmpty())
        {
            L.e(TAG, "refreshAttachList()->GONE");
            rlAttach.setVisibility(View.GONE);
        } else
        {
            L.e(TAG, "refreshAttachList()->VISIBLE");
            rlAttach.setVisibility(View.VISIBLE);
            int size = attachPaths.size();
            for (int i = 0; i < size; i++)
            {
                String attachPath = attachPaths.get(i);
                if (attachPath != null)
                {
                    AttachItem attachItem = new AttachItem(NewLetterActivity.this);
                    attachItem.setAttach(i, attachPath);
                    attachItem.setOnAttachDeleteListener(new AttachItem.OnAttachDeleteListener()
                    {
                        @Override
                        public void onDelete(int position)
                        {
                            removeAttachPathAndRefresh(position);
                        }
                    });
                    L.d(TAG, "addView:"+attachPath);
                    llAttach.addView(attachItem);
                    llAttach.invalidate();
                }
            }
        }
    }

    /**
     * 是否符合Email的规范
     */
    private boolean checkEmail()
    {
        //检查邮箱
        String email = etEmailTo.getText().toString();
        if (email == null || email.isEmpty())
        {
            Toaster.show(getResources().getString(R.string.toast_email_empty));
            etEmailTo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return false;
        } else if (!EmailFormatter.isEmailFormat(email))
        {
            Toaster.show(getResources().getString(R.string.toast_email_format_invalid));
            etEmailTo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return false;
        }

        //检查Subject
        String subject = etSubject.getText().toString();
        if (subject == null || subject.isEmpty())
        {
            Toaster.show(getResources().getString(R.string.toast_email_empty_subject));
            etSubject.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return false;
        }

        return true;
    }

    /**
     * 发送邮件，需要在线程里面操作
     */
    private void sendEmailTask(final String[] emailTo, final String subject, final String content, final String[] attachFileNames)
    {
        TaskBuilder.create(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return sendEmailBy163WithAttach(emailTo, subject, content, attachFileNames);
            }
        }, new SimpleTaskCallback<Boolean>()
        {

            ProgressDialog pdLoading = new ProgressDialog(NewLetterActivity.this);

            @Override
            public void onTaskStarted(String name, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskStarted");
                super.onTaskStarted(name, extras);
                pdLoading.setMessage(getResources().getString(R.string.new_letter_activity_sending_dialog_tip));
                pdLoading.setCancelable(false);
                pdLoading.setCanceledOnTouchOutside(false);
                pdLoading.show();
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
                    Toaster.show(getResources().getString(R.string.toast_email_send_success));
                    insertEmailToDB(true, System.currentTimeMillis());
                    insertContactToDB();
                    NewLetterActivity.this.finish();
                }
                else
                {
                    Toaster.show(getResources().getString(R.string.toast_email_send_fail), true);
                }
            }

            @Override
            public void onTaskFinished(String name, Bundle extras)
            {
                L.e(TAG, "SimpleTaskCallback()->onTaskFinished");
                super.onTaskFinished(name, extras);
                pdLoading.dismiss();
            }
        }, TAG).start();
    }

    /**
     * 将邮件写入数据库
     * */
    private void insertEmailToDB(boolean sendNow, long sendTime)
    {
        Email email = new Email();
        email.receiver = etEmailTo.getText().toString();
        email.subject = etSubject.getText().toString();
        email.content = etContent.getText().toString();
        email.attachPaths = attachPaths;
        email.isStar = false;
        email.sendTime = sendTime;// 让用户选时间
        email.state = sendNow ? 1 : 0;
        email.sender = getUserEmail();
        DBManagerEmail.getInstance().insertEmail(email);
    }

    /**
     * 将联系人写入数据库
     * */
    private void insertContactToDB()
    {
        Contact contact = new Contact();
        contact.emailAddress = etEmailTo.getText().toString();
        if(!DBManagerContact.getInstance().isContactExist(contact.emailAddress))
        {
            L.d(TAG, "insertContactToDB（）->联系人不存在，执行插入数据的操作");
            DBManagerContact.getInstance().insertContact(contact);
        }
        else
        {
            L.d(TAG, "insertContactToDB（）->联系人存在，不执行插入数据的操作");
        }
    }

//    private void sendEmailAsyncTask(final String[] emailTo, final String subject, final String content, final String[] attachFileNames)
//    {
//        this.emailTo = emailTo;
//        this.subject = subject;
//        this.content = content;
//        this.attachFileNames = attachFileNames;
//        new AsyncCaller().execute();
//    }

//    String[] emailTo;
//    String subject;
//    String content;
//    String[] attachFileNames;
//
//    private class AsyncCaller extends AsyncTask<Void, Void, Void>
//    {
//        ProgressDialog pdLoading = new ProgressDialog(NewLetterActivity.this);
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            //this method will be running on UI thread
//            pdLoading.setMessage("\tLoading...");
//            pdLoading.show();
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            sendEmailBy163WithAttach(emailTo, subject, content, attachFileNames);
//            //this method will be running on background thread so don't update UI frome here
//            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            L.e(TAG, "onResult:"+result);
//            Toaster.show(""+result);
//            //this method will be running on UI thread
//
//            pdLoading.dismiss();
//        }
//    }

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
        return SharedPreferenceUtils.getString(this, Consts.USER_EMAIL, "");
    }

    /**
     * 用户设置的密码
     */
    private String getPassword()
    {
        return SharedPreferenceUtils.getString(this, Consts.PASSWORD, "");
    }

}
