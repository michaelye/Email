package com.michael.email.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.michael.email.R;

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
                Intent mainIntent = new Intent(WelComeActivity.this, MainActivity.class);
                WelComeActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                WelComeActivity.this.finish();
            }
        });
        animation.setFillAfter(true);
        ivImage.startAnimation(animation);
    }
}
