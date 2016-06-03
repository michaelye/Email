package com.michael.email.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.michael.email.R;
import com.michael.email.util.StatusThemeUtil;

/**
 * 写邮件界面
 * <p/>
 * Created by michael on 16/6/3.
 */
public class NewLetterActivity extends AppCompatActivity
{

    private EditText etEmailTo;
    private EditText etSubject;
    private EditText etContent;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etEmailTo = (EditText)findViewById(R.id.etEmailTo);
        etSubject = (EditText)findViewById(R.id.etSubject);
        etContent = (EditText)findViewById(R.id.etContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_letter_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_attach:

                break;
            case R.id.action_send:

                break;
            case android.R.id.home:
                //TODO checkEmptyAndfinish
                back();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 页面关闭的时候需要有一个动画
     */
    private void back()
    {
        finish();
    }

}
