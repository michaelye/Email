package com.michael.email.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.michael.email.R;

/**
 * 用来设置和修改用户的邮箱
 *
 * Created by michael on 16/6/2.
 */
public class UserInfoSettingActivity extends AppCompatActivity
{

    private RelativeLayout rlUserName;

    private EditText etUserName;

    private ImageView ivClearName;

    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_setting);
        iniComponent();
    }

    private void iniComponent()
    {
        rlUserName = (RelativeLayout) findViewById(R.id.rlUserName);
        etUserName = (EditText) findViewById(R.id.etUserName);
        ivClearName = (ImageView)findViewById(R.id.ivClearName);
        btnOk = (Button)findViewById(R.id.btnOk);

        etUserName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().trim().equalsIgnoreCase(""))
                {
                    ivClearName.setVisibility(View.INVISIBLE);
                } else
                {
                    ivClearName.setVisibility(View.VISIBLE);
                }
            }
        });

        ivClearName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                etUserName.setText("");
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = etUserName.getText().toString();
                if(email != null && !email.isEmpty() && isEmailFormat(email))
                {
                    //save and Intent
                }
                else
                {
                    //shake animation
                    rlUserName.startAnimation(AnimationUtils.loadAnimation(UserInfoSettingActivity.this, R.anim.shake));
                }
            }
        });
    }

    /**
     * 是否符合邮箱格式
     * */
    private boolean isEmailFormat(String email)
    {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
