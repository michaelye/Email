package com.michael.email.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.michael.email.R;
import com.michael.email.util.Consts;
import com.michael.email.util.SharedPreferenceUtils;
import com.michael.email.util.UIUtil;

/**
 * 欢迎界面
 *
 * Created by michael on 16/6/2.
 */
public class WelComeActivity extends AppCompatActivity
{
    /**
     * 旋转动画结束的时候跳转到MainActivity
     * */
    private ImageView ivImage;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_rotate);
        animation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                String userEmail = SharedPreferenceUtils.getString(WelComeActivity.this, Consts.USER_EMAIL, "");
                if(userEmail == null || userEmail.isEmpty())
                {
                    UIUtil.startUserInfoSettingActivity(WelComeActivity.this, false);
                }
                else
                {
                    UIUtil.startMainActivity(WelComeActivity.this);
                }
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                WelComeActivity.this.finish();
            }
        });
        animation.setFillAfter(true);
        ivImage.startAnimation(animation);
    }
}
