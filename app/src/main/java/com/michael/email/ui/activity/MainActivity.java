package com.michael.email.ui.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.michael.email.R;
import com.michael.email.ui.fragment.FlagFragment;
import com.michael.email.ui.fragment.PendingFragment;
import com.michael.email.ui.fragment.SendFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 程序的主界面
 *
 * @author michael
 */
public class MainActivity extends AppCompatActivity
{
    private String TAG = this.getClass().getName();
    private Toolbar toolBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SystemBarTintManager tintManager;
    /**存放Fragment*/
    private FrameLayout flContainer;
    /**发送过的邮件*/
    private SendFragment sendFragment;
    /**加星星的邮件*/
    private FlagFragment flagFragment;
    /**准备发送中的邮件*/
    private PendingFragment pendingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniComponent();
    }

    private void iniComponent()
    {
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                drawerLayout.closeDrawers();
                if (menuItem.isChecked())
                {
                    return false;
                }
                resetMenuTextColor();
                switch (menuItem.getItemId())
                {
                    case R.id.menuSend:
                        navigationView.setCheckedItem(R.id.menuSend);
                        resetMenuTextColor();
                        changeMenuItemTextColor(menuItem, R.color.menuColorGreen);
                        changeToolbarColor(R.color.menuColorGreen);
                        if(sendFragment == null)
                        {
                            sendFragment = new SendFragment();
                        }
                        changeFragment(sendFragment);
                        break;
                    case R.id.menuFlag:
                        navigationView.setCheckedItem(R.id.menuFlag);
                        resetMenuTextColor();
                        changeMenuItemTextColor(menuItem, R.color.menuColorRed);
                        changeToolbarColor(R.color.menuColorRed);
                        if(flagFragment == null)
                        {
                            flagFragment = new FlagFragment();
                        }
                        changeFragment(flagFragment);
                        break;
                    case R.id.menuPending:
                        navigationView.setCheckedItem(R.id.menuPending);
                        resetMenuTextColor();
                        changeMenuItemTextColor(menuItem, R.color.menuColorORange);
                        changeToolbarColor(R.color.menuColorORange);
                        if(pendingFragment == null)
                        {
                            pendingFragment = new PendingFragment();
                        }
                        changeFragment(pendingFragment);
                        break;
                    case R.id.menuSetting:
                        break;
                    case R.id.menuAbout:
                        break;
                }
                return true;
            }
        });
        iniToolBar();
        setDefaultCheckedMenu();
    }

    /**
     * Fragment之间的切换
     * */
    private void changeFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.flContainer, fragment);
        transaction.commit();
    }

    /**
     * Toolbar里面的内容是自己定制的
     * */
    private void iniToolBar()
    {
        View viewToolBar = getLayoutInflater().inflate(R.layout.layout_tool_bar, null);
        toolBar.addView(viewToolBar);
        viewToolBar.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
        RelativeLayout rlMenu = (RelativeLayout)viewToolBar.findViewById(R.id.rlMenu);
        rlMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 默认选中的MenuItem
     */
    private void setDefaultCheckedMenu()
    {
        navigationView.getMenu().performIdentifierAction(R.id.menuSend, 0);
    }

    /**
     * 重置Menu的文本颜色为黑色
     */
    private void resetMenuTextColor()
    {
        Menu menu = navigationView.getMenu();
        if (menu.findItem(R.id.menuSend).isChecked())
        {
            changeMenuItemTextColor(menu.findItem(R.id.menuSend), R.color.menuColorBlack);
        }
        if (menu.findItem(R.id.menuFlag).isChecked())
        {
            changeMenuItemTextColor(menu.findItem(R.id.menuFlag), R.color.menuColorBlack);
        }
        if (menu.findItem(R.id.menuPending).isChecked())
        {
            changeMenuItemTextColor(menu.findItem(R.id.menuPending), R.color.menuColorBlack);
        }
    }

    /**
     * 改变Menu的文本颜色
     */
    private void changeMenuItemTextColor(MenuItem menuItem, int color)
    {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    private Integer currentToolBarColor = 0;

    private int getDefaultToolBarColor()
    {
        return R.color.menuColorGreen;
    }

    /**
     * 改变ToolBar的颜色
     *
     * 这里同时改变了Toolbar和statusbar的颜色
     * */
    private void changeToolbarColor(final int newToolBarColor)
    {
        Integer colorTo = Color.parseColor(getResources().getString(newToolBarColor));
        if(currentToolBarColor == 0)
        {
            currentToolBarColor = Color.parseColor(getResources().getString(getDefaultToolBarColor()));
        }
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), currentToolBarColor, colorTo);
        ValueAnimator colorStatusAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), currentToolBarColor, colorTo);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            @Override
            public void onAnimationUpdate(ValueAnimator animator)
            {
                toolBar.setBackgroundColor((Integer) animator.getAnimatedValue());
                currentToolBarColor = newToolBarColor;
            }
        });

        colorStatusAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animator)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//这里只能这样设置，否则statusbar会和侧滑菜单冲突
                    MainActivity.this.getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                }
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                {
                    tintManager.setStatusBarTintColor((Integer) animator.getAnimatedValue());
                }
                currentToolBarColor = newToolBarColor;
            }
        });
        colorAnimation.setDuration(300);
        colorAnimation.setStartDelay(0);
        colorAnimation.start();
        colorStatusAnimation.setDuration(300);
        colorStatusAnimation.setStartDelay(0);
        colorStatusAnimation.start();
    }
}
