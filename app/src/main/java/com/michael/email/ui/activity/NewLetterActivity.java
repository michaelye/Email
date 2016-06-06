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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.michael.email.R;
import com.michael.email.SendEmailTask;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.dialog.AlertDialogFragment;
import com.michael.email.dialog.DialogResultListener;
import com.michael.email.model.Email;
import com.michael.email.receiver.AlarmClockManager;
import com.michael.email.ui.component.AttachItem;
import com.michael.email.util.Consts;
import com.michael.email.util.EmailBus;
import com.michael.email.util.EmailFormatter;
import com.michael.email.util.L;
import com.michael.email.util.NetworkUtil;
import com.michael.email.util.SharedPreferenceUtils;
import com.michael.email.util.StatusThemeUtil;
import com.michael.email.util.TimeUtils;
import com.michael.email.util.Toaster;
import com.michael.email.util.UIUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    private LinearLayout llClock;
    private TextView tvClock;
    private ImageView ivClockDelete;

    /**
     * 定时发送的时间
     */
    private long pendingTime = 0;

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
        llClock = (LinearLayout) findViewById(R.id.llClock);
        tvClock = (TextView) findViewById(R.id.tvClock);
        ivClockDelete = (ImageView) findViewById(R.id.ivClockDelete);

        ivClockDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pendingTime = 0;
                llClock.setVisibility(View.GONE);
                tvClock.setText("");
            }
        });

        String emailTo = getIntent().getStringExtra("emailTo");
        if (emailTo != null && !emailTo.isEmpty())
        {
            etEmailTo.setText(emailTo);
            etEmailTo.setSelection(emailTo.length());
        }
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
            case R.id.action_clock:
                showDateAndTimePickerDialog();
                break;
            case R.id.action_attach:
                UIUtil.startFilePickerActivity(NewLetterActivity.this, REQUEST_DIRECTORY);
                break;
            case R.id.action_send:
                if (checkEmail())
                {
                    if (pendingTime > 0)//说明用户要定时发送
                    {
                        //TODO 保存数据到本地，状态为未发送，设置一个AlarmClock，AlarmClock触发的时候，调用Service得到本地的Email，然后发送邮件
                        if(pendingTime > System.currentTimeMillis())
                        {
                            saveToLocalAndPending(new String[]{etEmailTo.getText().toString()}, etSubject.getText().toString(), etContent.getText().toString(), attachPaths.toArray(new String[0]));
                            this.finish();
                        }
                        else
                        {
                            Toaster.show(getResources().getString(R.string.toast_email_choose_time_again));
                        }
                    } else
                    {
                        if (NetworkUtil.isConnected(NewLetterActivity.this))
                        {
                            final ProgressDialog pdLoading = new ProgressDialog(NewLetterActivity.this);
                            new SendEmailTask().send(NewLetterActivity.this, new String[]{etEmailTo.getText().toString()}, etSubject.getText().toString(), etContent.getText().toString(), attachPaths.toArray(new String[0])).setOnTaskStateListener(new SendEmailTask.OnTaskStateListener()
                            {
                                @Override
                                public void onTaskStarted()
                                {
                                    pdLoading.setMessage(getResources().getString(R.string.new_letter_activity_sending_dialog_tip));
                                    pdLoading.setCancelable(false);
                                    pdLoading.setCanceledOnTouchOutside(false);
                                    pdLoading.show();
                                }

                                @Override
                                public void onTaskSuccess()
                                {
                                    Toaster.show(getResources().getString(R.string.toast_email_send_success));
                                    NewLetterActivity.this.finish();
                                }

                                @Override
                                public void onTaskFail()
                                {
                                    Toaster.show(getResources().getString(R.string.toast_email_send_fail), true);
                                }

                                @Override
                                public void onTaskEnd()
                                {
                                    pdLoading.dismiss();
                                }
                            });
                        } else
                        {
                            Toaster.show(getResources().getString(R.string.toast_email_network_un_available));
                        }
                    }
                }
                break;
            case android.R.id.home:
                if (needConfirmDialog())
                {
                    showConfirmDialog();
                } else
                {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将邮件保存到本地
     * */
    private void saveToLocalAndPending(final String[] emailTo, final String subject, final String content, final String[] attachFileNames)
    {
        Email email = new Email();
        email.receiver = emailTo[0];
        email.subject = subject;
        email.content = content;
        email.attachPaths = new ArrayList<>(Arrays.asList(attachFileNames));
        email.isStar = false;
        email.sendTime = pendingTime;// 发送时间
        email.state = 0;
        email.sender = getUserEmail();
        long rowId = DBManagerEmail.getInstance().insertEmail(email);//邮件的id
        EmailBus.getInstance().post(new EmailBus.BusEvent(EmailBus.BUS_ID_REFRESH_PENDING));
        AlarmClockManager.setClock(this, rowId, pendingTime);
        Toaster.show("邮件将于"+TimeUtils.getFormatTime(pendingTime)+"发送");
    }

    /**
     * 用户设置的Email地址
     */
    private String getUserEmail()
    {
        return SharedPreferenceUtils.getString(this, Consts.USER_EMAIL, "");
    }

    /**
     * 日期时间选择器
     */
    private void showDateAndTimePickerDialog()
    {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .build()
                .show();
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener()
    {

        @Override
        public void onDateTimeSet(Date date)
        {
            if (date.getTime() < System.currentTimeMillis())
            {
                Toaster.show(getResources().getString(R.string.toast_email_choose_time_fail));
            } else
            {
                llClock.setVisibility(View.VISIBLE);
                tvClock.setText("发送时间：" + TimeUtils.getFormatTime(date.getTime()));
                pendingTime = date.getTime();
            }
        }

        @Override
        public void onDateTimeCancel()
        {

        }
    };

    /**
     * 是否需要一个对话框提醒用户
     */
    private boolean needConfirmDialog()
    {
        String emailTo = etEmailTo.getText().toString();
        String subject = etSubject.getText().toString();
        String content = etContent.getText().toString();
        if ((emailTo != null && !emailTo.isEmpty())
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
     */
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
            L.e(TAG, "addAttachPathAndRefresh()->" + uri.getPath());
            attachPaths.add(uri.getPath());
            refreshAttachList();
        }
    }

    /**
     * 移除附件地址
     */
    private void removeAttachPathAndRefresh(int position)
    {
        if (attachPaths != null && !attachPaths.isEmpty() && attachPaths.get(position) != null)
        {
            L.e(TAG, "removeAttachPathAndRefresh()->pos:" + position);
            attachPaths.remove(position);
        }
        refreshAttachList();
    }

    /**
     * 刷新附件UI
     */
    private void refreshAttachList()
    {
        L.e(TAG, "refreshAttachList()->attachPaths:" + attachPaths.size());
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
                    L.d(TAG, "addView:" + attachPath);
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

        if (pendingTime != 0 && (pendingTime < System.currentTimeMillis()))
        {
            Toaster.show(getResources().getString(R.string.toast_email_choose_time_again));
            llClock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            return false;
        }

        return true;
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


}
