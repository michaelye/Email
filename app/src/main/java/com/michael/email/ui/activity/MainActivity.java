package com.michael.email.ui.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michael.email.R;
import com.michael.email.ui.fragment.ContactsFragment;
import com.michael.email.ui.fragment.FlagFragment;
import com.michael.email.ui.fragment.PendingFragment;
import com.michael.email.ui.fragment.SendFragment;
import com.michael.email.util.Consts;
import com.michael.email.util.EmailBus;
import com.michael.email.util.ImageUtils;
import com.michael.email.util.SharedPreferenceUtils;
import com.michael.email.util.UIUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;

/**
 * 程序的主界面
 *
 * @author michael
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private String TAG = this.getClass().getName();
    private Toolbar toolBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SystemBarTintManager tintManager;
    /**发送过的邮件*/
    private SendFragment sendFragment;
    /**加星星的邮件*/
    private FlagFragment flagFragment;
    /**准备发送中的邮件*/
    private PendingFragment pendingFragment;
    /**联系人*/
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmailBus.getInstance().register(this);
        iniComponent();
    }

    public void onEventMainThread(EmailBus.BusEvent busEvent)
    {
        if (busEvent.eventId == EmailBus.BUS_ID_REFRESH_USER_INFO)
        {
            iniNavigationViewHeader();//刷新一下
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EmailBus.getInstance().unregister(this);
    }

    private void iniComponent()
    {
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
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
                        tvTitle.setText(getResources().getString(R.string.menu_send));
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
                        tvTitle.setText(getResources().getString(R.string.menu_flag));
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
                        tvTitle.setText(getResources().getString(R.string.menu_pending));
                        resetMenuTextColor();
                        changeMenuItemTextColor(menuItem, R.color.menuColorOrange);
                        changeToolbarColor(R.color.menuColorOrange);
                        if(pendingFragment == null)
                        {
                            pendingFragment = new PendingFragment();
                        }
                        changeFragment(pendingFragment);
                        break;
                    case R.id.menuContacts:
                        navigationView.setCheckedItem(R.id.menuContacts);
                        tvTitle.setText(getResources().getString(R.string.menu_contacts));
                        resetMenuTextColor();
                        changeMenuItemTextColor(menuItem, R.color.menuColorBlue);
                        changeToolbarColor(R.color.menuColorBlue);
                        if(contactsFragment == null)
                        {
                            contactsFragment = new ContactsFragment();
                        }
                        changeFragment(contactsFragment);
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
        iniNavigationViewHeader();
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

    private TextView tvTitle;

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
        tvTitle = (TextView)viewToolBar.findViewById(R.id.tvTitle);
    }

    private ImageView ivAvatar;
    /**
     * 头像和Email所在的Header
     * */
    private void iniNavigationViewHeader()
    {
        View headerLayout = navigationView.getHeaderView(0);
        ivAvatar = (ImageView)headerLayout.findViewById(R.id.ivAvatar);
        displayAvatar();
        TextView tvEmail = (TextView)headerLayout.findViewById(R.id.tvEmail);
        tvEmail.setText(SharedPreferenceUtils.getString(this, Consts.USER_EMAIL, ""));
        tvEmail.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEmail:
            case R.id.ivAvatar:
                UIUtil.startUserInfoSettingActivity(MainActivity.this, true);
                break;
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
