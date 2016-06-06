package com.michael.email.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.michael.email.R;

/**
 * Created by michael on 16/6/6.
 */
public class QuestionActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        iniComponent();
    }

    private void iniComponent()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_bar_text_question));

        TextView tvQuestion = (TextView)findViewById(R.id.tvQuestion);
        tvQuestion.setText(Html.fromHtml(
                "<h3>为什么邮件发送失败？</h3>" +
                "<p>1.请检查邮箱是否是163的邮箱，并且确定密码输入正确</p>"+
                "<p>2.请注意发送的内容是否违规，如随意输入标题，可能会被163认为是爬虫或垃圾邮件，导致发送失败</p>"+
                "<h3>为什么只能使用163邮箱？</h3>" +
                "<p>不同的邮件平台都有自己的服务器地址和端口，因为时间紧迫，只实现了在163邮箱发送邮件的功能</p>"
        ));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
