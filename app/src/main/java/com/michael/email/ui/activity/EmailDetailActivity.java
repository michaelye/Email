package com.michael.email.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michael.email.R;
import com.michael.email.db.DBManagerEmail;
import com.michael.email.dialog.AlertDialogFragment;
import com.michael.email.dialog.DialogResultListener;
import com.michael.email.model.Email;
import com.michael.email.ui.component.AttachItem;
import com.michael.email.util.Consts;
import com.michael.email.util.ImageUtils;
import com.michael.email.util.L;
import com.michael.email.util.StatusThemeUtil;
import com.michael.email.util.TimeUtils;

import java.io.File;
import java.util.List;

/**
 * 邮件详情
 *
 * Created by michael on 16/6/5.
 */
public class EmailDetailActivity extends AppCompatActivity implements DialogResultListener
{
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StatusThemeUtil.setMeizuStatusBarDarkIcon(this, true);
        StatusThemeUtil.setMiuiStatusBarDarkMode(this, true);
        setContentView(R.layout.activity_email_detail);
        iniComponent();
        emailId = getIntent().getStringExtra("emailId");
        if(emailId != null && !emailId.isEmpty())
        {
            iniView(DBManagerEmail.getInstance().getEmail(emailId));
        }
        else
        {
            L.e(TAG, "emailId is empty");
        }
    }

    private String emailId;

    private RelativeLayout rlAttach;
    private LinearLayout llAttach;
    private TextView tvReceiver;
    private TextView tvSubject;
    private TextView tvContent;
    private TextView tvTime;
    private ImageView ivAvatar;

    private void iniComponent()
    {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_gray);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rlAttach = (RelativeLayout) findViewById(R.id.rlAttach);
        llAttach = (LinearLayout) findViewById(R.id.llAttach);
        tvReceiver = (TextView) findViewById(R.id.tvReceiver);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvTime = (TextView) findViewById(R.id.tvTime);
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
    }

    private void iniView(final Email email)
    {
        if(email != null)
        {
            String receiver = email.receiver;
            receiver = "发给:" + receiver;
            tvReceiver.setText(receiver);
            String subject = email.subject;
            tvSubject.setText(subject == null ? "" : subject);
            String content = email.content;
            tvContent.setText(content == null ? "" :content);
            String time = email.sendTime+"";
            tvTime.setText(time == null ? "" : TimeUtils.getFormatTime(email.sendTime));
            List<String> attachPaths = email.attachPaths;
            if(attachPaths == null || attachPaths.isEmpty())
            {
                rlAttach.setVisibility(View.GONE);
            }
            else
            {
                rlAttach.setVisibility(View.VISIBLE);
                for(String attachPath : attachPaths)
                {
                    AttachItem attachItem = new AttachItem(EmailDetailActivity.this);
                    attachItem.setAttach(0, attachPath);
                    attachItem.setDeleteIconVisibility(View.GONE);
                    L.d(TAG, "addView:"+attachPath);
                    llAttach.addView(attachItem);
                }
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    updateStar(email.isStar);
                }
            }, 200);
            displayAvatar();
        }
    }

    /**
     * 如果有头像，就显示头像
     */
    private void displayAvatar()
    {
        String AVATAR_PATH = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
        File imageFile = new File(AVATAR_PATH + Consts.AVATAR_NAME);
        if (imageFile.exists())
        {
            Bitmap bitmap = ImageUtils.getCroppedBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            ivAvatar.setImageBitmap(bitmap);
        }
    }

    private void updateStar(boolean isStar)
    {
        if(menuStar != null)
        {
            menuStar.setIcon(isStar ? R.drawable.ic_star_yellow : R.drawable.ic_star_gray);
        }
    }

    private MenuItem menuStar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_detail_actionbar, menu);
        menuStar = menu.findItem(R.id.action_star);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_star:
                addOrCancelStar();
                break;
            case R.id.action_delete:
                showConfirmDialog();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加或取消星星
     * */
    private void addOrCancelStar()
    {
        if(emailId != null && !emailId.isEmpty())
        {
            Email email = DBManagerEmail.getInstance().getEmail(emailId);
            if(email.isStar)
            {
                email.isStar = false;
            }
            else
            {
                email.isStar = true;
            }
            DBManagerEmail.getInstance().updateEmail(emailId, email);
            updateStar(email.isStar);
        }
    }


    /**
     * 放弃编写邮件的提醒对话框
     * */
    private int REQUEST_CONFIRM_DELETE = 0;

    private void showConfirmDialog()
    {
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(this)
                .setRequestCode(REQUEST_CONFIRM_DELETE)
                .setMessage(R.string.dialog_delete_email_tip)
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
        if (requestCode == REQUEST_CONFIRM_DELETE)
        {
            if (resultCode == RESULT_OK)
            {
                deleteEmail();
                finish();
            }
        }
    }

    /**
     * 删除Email
     * */
    private void deleteEmail()
    {
        if(emailId != null && !emailId.isEmpty())
        {
            DBManagerEmail.getInstance().deleteEmail(emailId);
        }
    }
}
